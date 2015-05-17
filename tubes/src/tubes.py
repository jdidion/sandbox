"""Tubes: a workflow framework.

Tubes is a framework for implementing workflows. A workflow (called a Tube) is composed of Modules.
A Module can do anything you can dream up. Tubes is inspired by Yahoo! Pipes (tm).
"""
import networkx as nx

OUTPUT_NODE = 'output_node'
"""Attribute name of the output node of a connection."""

INPUT_NODE = 'input_node'
"""Attribute name of the input node of a connection."""

def parse_path(path):
    """Validate a path, and convert it to a tuple if necessary.
    
    A path is the location of a module within a Tube or hierarchy of Tubes. A path can be 
    represented as a tuple or a string. Path components are separated by a period when the path 
    is in string format.
    """
    assert path is not None and len(path) > 0, "Invalid path: %s." % str(path)
    if not isinstance(path, tuple):
        path = str(path).split('.')
    return path

def path_to_str(path):
    assert isinstance(path, tuple), "Invalid path"
    return '.'.join(path)

def path_child(path):
    """Remove the first component from a path and return it as a string."""
    return path_to_str(parse_path(path)[1:])

class Module(object):
    """A basic unit of work.

    This is intended as an interface and should not be instantiated directly.

    Modules are intended to be combined into workflows using :class:`Tube`s. In general, a Module 
    should perform a single non-decomposable function, taking as few inputs and producing as few 
    outputs as possible. 

    For example, rather than a single module that fetches an HTML page and extracts table data, it
    is better to instead have two modules - one that takes a URL as input, fetches an HTML page and 
    returns HTML as output, and a second that takes HTML as input, extracts a table and returns, 
    say, a matrix as output.

    Module input and output types enable a Tube to know if two modules can be connected. In the
    previous example, the "fetch HTML" module could choose to output HTML either as a string or
    a DOM object (although a Tube will connect any output with a __repr__ attribute to a string
    input).

    A module must have a name. A Module must also be hashable and each instance must have a hash 
    value that is unique within a Tube. A description of a Module and help text should be put in 
    the Module's doc string.
    """

    def __init__(self, name):
        self.name = name
    
    def input_nodes(self):
        """Returns a list of paths of the input nodes of this module."""
        pass
    
    def validate(self):
        """Raise an exception if this Module is not in a valid state to be executed."""
        pass
        
    def execute(self, inputs={}):
        """Execute this module on the given inputs and return the outputs.
        
        Args:
            inputs (dict): key is node name, value is the input for that node.
        
        Returns:
            dict: key is output node name, value is the output for that node
        """
        self.validate()

class ModuleDecorator(Module):
    """A Module that contains another Module and (presumably) augments its behavior."""

    __slots__ = ['delegate']

    def __init__(self, delegate):
        assert isinstance(delegate, Module)
        self.delegate = delegate
        
class Tube(Module):
    """A set of :class:`Module`s that are executed in a specific sequence.
    
    A Tube is a DAG (Directed Acyclic Graph) in which nodes are :class:`Module`s. Every module has
    zero or more inputs and one or more outputs. When a Tube is executed, the outputs of any 
    terminal leaf nodes (i.e. nodes with inputs but not outputs) are considered the output of the
    Tube. A Tube is itself a subclass of :class:`Module` and so can be used as a Module in another
    Tube.
    """
    
    __slots__ = ['graph', 'modules']
    
    def __init__(self, name):
        Module.__init__(self, name)
        self.graph = nx.MultiDiGraph(name=name)
        self.modules = {}
        
    def add(self, module, *from_nodes, **connections):
        """Add a component, and optionally connect it to other components.
        
        Args:
            module: A module with a name that is unique within this Tube.
            from_nodes: Nodes to connect to module's inputs.
            connections: input_node, output_node pairs.
            
        If both from_nodes and connections are specified, from_nodes is ignored.
        
        Returns: self
        """
        assert module.name not in self.modules, "There is already a module named %s" % module.name
        self.modules[module.name] = comp

        if connections:
            for innode, outnode in connections.iteritems():
                self.connect(outnode, tuple(module.name, innode))
        elif from_nodes:
            for outnode, innode in zip(from_nodes, module.input_nodes()):
                self.connect(outnode, innode)

        return self
        
    def connect(self, outnode, innode, **attr):
        """Connect the output of a module to the input of another module.
        
        Args:
            outnode (string): The output node.
            innode (string): The input node.
            attr: An arbitrary number of attributes to attach to the connection.
            
        If outnode or innode is a module, it is assumed the module has a single output/input.
        
        Returns: self
        """
        outpath = parse_path(outnode)
        inpath = parse_path(innode)
        assert outpath != inpath, "Output and input nodes must be different."
        assert outpath[0] != inpath[0], "Output and input modules must be different."
        
        if len(outpath) > 1:
            assert OUTPUT_NODE not in attr, "%s is a reserved attribute" % OUTPUT_NODE
            attr[OUTPUT_NODE] = outpath[1:]
        if len(inpath) > 1:
            assert INPUT_NODE not in attr, "%s is a reserved attribute" % INPUT_NODE
            attr[INPUT_NODE] = inpath[1:]
        self.graph.add_edge(outpath[0], inpath[0], **attr)
        
        return self
    
    def validate(self):
        """Check that this Tube is valid and can be executed."""
        assert nx.is_directed_acyclic_graph(self.graph),
            "This Tube does not have a valid graph structure."
        
    def input_nodes(self, prefix=None):
        self.validate()
        prefix = (prefix,) if prefix else ()
        return [
            prefix + path
            for m in self.input_modules()
            for path in m.input_nodes(self.name)
        ]
    
    def input_modules(self):
        """
        Returns a list of modules that have no upstream connections from other 
        modules in this Tube.
        """
        return [k for k,v in self.graph.in_degree().iteritems() if v == 0]
    
    def output_modules(self, modules):
        """Returns a set of the modules connected to the outputs of `modules`."""   
        return set([
            names.add(n)
            for m in modules
            for n in self.graph.successors(m.name)
        ])
        
    def execute(self, inputs={}):
        self.validate()
        
        modules = [self.modules[m] for m in self.input_modules()]
        
        while True:
            outputs = {}
            
            for m in modules:
                if m.name in inputs:
                    outputs[m.name] = m.execute(inputs[m.name])
                else:
                    outputs.update(dict(
                        [(path_child(n), inputs[n]) for n in m.input_nodes() if n in inputs]))
            
            modules = self.output_modules(modules)
            if modules:
                inputs = outputs
            else:
                return outputs