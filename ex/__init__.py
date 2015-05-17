"""Library for creating and running command scripts.

A command script is one or more operations that can be repeated for a set of variables. Examples
would be running a shell script or executing SQL statements. Scripts are defined within python
modules (and assigned to the 'script' variable if they are to be run from exec.py).
"""
import logging
import util.cl
import util.collections
import util.fork

import ex.options

__all__ = ['Script', 'Subscript', 'Component', 'ExecError']

class Component(object):
    __slots__ = ['desc', 'options']
    
    def __init__(self, desc=None, options=None):
        self.desc = desc
        self.options = options or []
        
    def add_option(self, option):
        self.options.append(option)    

    def configure_option_parser(self, parser, names=OptionNames()):
        for o in self.options:
            o.configure_option_parser(parser, names)
    
    def process_options(self, options, var_args):
        for o in self.options:
            o.process_options(options, var_args)
        
class BaseScript(Component):
    __slots__ = ['components','fixed_args']
    
    def __init__(self, desc=None, options=None, components=None, fixed_args=None):
        Component.__init__(self, desc, options)
        self.components = components or []
        self.fixed_args = fixed_args
    
    def add_component(self, component):
        self.components.append(component)
        
    def configure_option_parser(self, parser, names=OptionNames()):
        Component.configure_option_parser(self, parser, names)
        for c in self.components:
            c.configure_option_parser(parser, names.child())
                
    def process_results(self, results):
        for c in self.components:
            c.process_results(results)

class Script(BaseScript):
    def __init__(self, desc=None, options=None, components=None, forking=True, fixed_args=None):
        """A top-level command script.
        
        Args:
            desc (str): A description of the script that will be printed with the help text.
            options (iterable): :class:`OptionHandler`s that define script-level options.
            components (iterable): :class:`ex.Subscript`s or :class:`ex.component.Component`s to 
                execute in order.
            forking (bool or dict): If False, no parallelization occurs. If True, options are
                automatically added for specifying forking configuration. If a dict, no options
                are added and the values in the dict are used to configure forking.
            fixed_args (:class:`util.collections.VarArgGenerator`): Can be used to hard-code any option 
                values that may be required by components but should not be user-settable.
        """
        # Convert components into Subscripts
        subscripts = []
        sub = None
        for c in components:
            if isinstance(c, Subscript):
                subscript = None
                subscripts.append(c)
            else:
                if sub is None:
                    sub = Subscript()
                    subscripts.append(sub)
                subscript.add_component(c)
                
        if forking:
            if isintance(forking, dict)
                if fixed_args is None: fixed_args = util.collections.VarArgGenerator()
                fixed_args.add_constant('forking', forking)
            else:
                if options is None: options = []
                options.append(ex.options.ForkOptionsHandler())

        BaseScript.__init__(self, desc, options, subscripts, fixed_args)
    
    def __call__(self, args=sys.argv[1:]):
        """Parse command-line arguments and execute the script.
        
        Components are executed serially and depth-first. For example, say you create and run 
        the following script::
            
            # create the script
            name_opt = ...TODO...
            script = Script(
                Subscript(ComponentA()),
                ComponentB(),
                ComponentC(),
                Subscript(ComponentD(), ComponentE()),
                options=[name_opt])
                
            # execute the script
            script("--names", "Bob,Sarah,Jim")
        
        1. ``ComponentA`` executes once for each name (Bob,Sarah,Jim)
        2. For each name:
            ``ComponentB`` executes
            ``ComponentC`` executes
        3. For each name:
            ``ComponentD`` executes
            ``ComponentE`` executes
        
        Essentially, consecutive components execute as if they were a subscript.
        
        Args:
            args (iterable): Command-line arguments (typically from sys.argv).
            
        Returns:
            int: The exit code. 0 indicates success, anything else indicates failure.
        """
        try:
            options = util.cl.parse_options(self.configure_option_parser, args=args or [], 
                desc=self.desc, usage="usage: %prog script_file [options]", allow_unprocessed=False)
            
            # Create the script-level var_args and lock it so subscripts cannot modify it
            var_args = util.collections.VarArgGenerator(self.fixed_args)
            self.process_options(options, var_args)
            var_args.locked = True
            
            # context can be used by components to store results
            context = {}
            for sub in self.components:
                sub.exec_all(options, var_args, context)
        
            self.process_results(context)
            
            return 0
        except ExecError as ex:
            logging.critical("Error executing script.", ex)
            return ex.return_code
    
class Subscript(BaseScript):
    def __init__(self, desc=None, options=None, components=None, fixed_args=None):
        BaseScript.__init__(self, desc, options, components, fixed_args)
    
    def __call__(self, args):
        results = {}
        for c in self.components:
            r = c(args)
            if r is not None: 
                results.update(r)
        return results
        
    def add_component(self, c):
        self.components.append(c)
    
    def process_options(self, options, var_args):
        Component.process_options(self, options, var_args)
        for c in self.components:
            c.process_options(options, var_args)
    
    def exec_all(self, options, var_args, context):
        """Execute the subscript.
        
        The option values in ``options`` are processed into a 
        :class:`util.collections.VarListGenerator`, which generates a set of variables for each 
        iteration. The subscript is executed once for each set of variables generated by args. 
        All work is done via :func:`util.fork.run_jobs`, which parallelizes jobs.
        
        Args:
            options (:class:`util.cl.IndexableValues`): option values
            var_args (:class:`util.collections.VarArgGenerator`): args from the parent script
            context (dict): stores results
        """
        # The parent will have locked var_args, so we need to make a copy before modifying
        var_args = var_args.copy()
        var_args.update(self.fixed_args)
        self.process_options(options, var_args)
        
        fork_opt = var_args.constants['forking'] if 'forking' in var_args.constants else {}
        util.fork.distribute(self, var_args, result_handler=lambda r: context.update(r), **fork_opt)

class ExecError(Exception):
    def __init__(self, reason, return_code=1):
        Exception.__init__(self, reason)
        self.return_code = 1
            
class OptionNames(object):
    """
    Simple tree structure to hold option names while configuring the option parser. If an option
    name exists already, it's place in the hierarchy can be found using the compare methods.
    """
    def __init__(self, parent=None):
        self.parent = parent
        self.short = set()
        self.long = set()
        self.children = []
    
    def child(self):
        child = OptionNames(self)
        self.children.append(child)
        return child
    
    def add_pair(self, short, long):
        self.short.add(short)
        self.long.add(long)
        
    def compare_short(self, name):
        return self._find_holder(name, lambda o: o.short)
    
    def compare_long(self, name):
        return self._find_holder(name, lambda o: o.long)
    
    def _find_holder(self, name, accessor, level=0, exclude=None):
        """Returns: 
        * -1 if name exists at a higher level of the tree
        *  0 if name exists at a the current level of the tree
        *  1 if name exists at a lower level of the tree
        * Else None
        """
        # look in self
        if name in accessor(self):
            return level
        # look in children
        for c in self.children:
            if c == exclude:
                continue
            l = c._find_holder(name, accessor, level+1)
            if l is not None:
                return l
        # look in parent
        if self.parent is None:
            return None
        else:
            return self.parent._find_holder(name, accessor, level-1, self)