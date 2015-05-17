import os

class OptionAppender(object):
    __slots__ = [ 'short_name', 'long_name', 'args', 'variable' ]
    
    def __init__(self, name, short_name=None, variable=False, **kwargs):
        '''
        :variable:
            Whether the values should be considered variable (i.e. one used per iteration) or
            static (the entire list used every iteration).
        '''
        assert name is not None
        self.long_name = name
        self.long_opt = "--%s" % name
        if short_name is None:
            self.short_name = name
            self.short_opt = None
        else:
            self.short_name = short_name
            self.short_opt = "-%s" % short_name
        self.args = kwargs
    
    def add_options(self, parser):
        parser.add_option(self.short_opt, self.long_opt, **self.args)
    
    def names(self):
        return dict(self.short_name: self.long_name)


        def configure_option_parser(parser, names, defaults=None):

                opt_names = o.names

                # Defaults override both short and long names
                if defaults is not None:
                    for short_name, long_name in opt_names.iteritems():
                        if short_name in defaults[0]:
                            logging.debug("Component option overridden by default: %s", short_name)
                            continue
                        if long_name in defaults[1]:
                            logging.debug("Component option overridden by default: %s", long_name)
                            continue

                # Short names (that haven't been overridden) must be unique
                for short_name, long_name in opt_names.iteritems():
                    if short_name in names[0]:
                        logging.critical("Multiple options with short name %s", short_name)
                        sys.exit(1)
                    # Options with the same long name are considered duplicates: the first
                    # occurrence is accepted.
                    if long_name in names[1]:
                        logging.debug("Multiple options with long name %s", long_name)
                        continue

                    names.short.add(short_name)
                    names.long.add(long_name)

                # Allow the OptionAppender to add options to the parser
                o.configure_option_parser(parser)


class OptionSetAppender(object):
    __slots__ = [ 'appenders', 'variable' ]
    
    def __init__(self, *args, variable=False):
        '''
        :variable:
            Whether the values should be considered variable (i.e. one used per iteration) or
            static (the entire list used every iteration).
        '''
        self.appenders = args
        self.variable = variable
    
    def add_appender(self, appender):
        self.appenders.append(appender)
        
    def add_options(self, parser):
        for a in self.appenders:
            a.add_options(parser)
    
    def names(self):
        names = {}
        for a in self.appenders:
            names[a.short_name] = a.long_name
        return names

class ValuesAppender(OptionSetAppender):
    '''
    Parses either or both of:
    * values specified on the command line
    * values specified in a file
    Values can either be simple (in which case value_class must be None) or complex (in which case
    value_class must be either dict or a class). Simple values specified on the command-line can be
    specified using a single flag (comma-delimited values), but complex options specified on the
    command line must use separate flags.
    '''
    __slots__ = [ 'values_opt', 'file_opt', 'mode', 'value_class', 'header', 'force_list', 'cmp', 'parser_opts' ]
    
    CMD_LINE = 1
    FILE = 2
    
    # Option attributes for csv parser configuration options.
    csv_parser_options = dict(
        delimiter=dict(type="string", metavar="CHAR", help="Field terminator character.")
        terminator=dict(type="string", metavar="CHAR(S)", default="\r\n", help="End of line character")
    )
    
    def __init__(self, name, short_opt=None, mode=CMD_LINE | FILE, value_class=None, header=True,
                 default_values=None, default_file=None, force_list=False, comparator=cmp, 
                 variable=False, parser_options=None):
        '''
        :mode:
            Bitwise combination of CMD_LINE and FILE. Determines which option(s) are defined.
        :value_class:
            A class used to store complex row values from the values file. If None, values are
            assumed to be simple.
        :header:
            If value_class is None, ignored. If value_class is dict, must be True. Specifies
            whether the file has a header that should be used for initializing value_class using
            kwargs (otherwise positional arguments are used).
        :default:
            A file of values to use by default.
        :force_list:
            If true, the option is set to a list of values regardless of whether they are simple or
            complex. Otherwise a dict is used for complex values. 
        :comparator:
            A function used in sorting values. Only applies to simple values (or when force_list is
            True).
        :parser_options:
            Used to specify additional arguments to csv.reader (for reading the values file). This
            can either be a list or a dictionary (with values being defaults). Available options
            are: delimiter (the field delimiter), terminator (the line terminator)
        '''
        OptionSetAppender.__init__(self, variable=variable)
        self.values_opt = name
        self.file_opt = "%s_file" % name
        self.mode = mode
        self.value_class = value_class
        self.header = header
        self.force_list = force_list
        self.cmp = comparator
        self.parser_opts = {}
        if parser_options is not None:
            for name in parser_options:
                if name not in csv_parser_options:
                    logger.critical("Invalid CSV parser option: %s", name)
                    sys.exit(1)
                kwargs = csv_parser_options[name].copy()
                if typeof(parser_options) == 'dict':
                    kwargs['default'] = parser_options[name]
                opt_name = "%s_%s" % (self.values_opt, name)
                self.add_appender(OptionAppender(None, opt_name, kwargs))
                self.parser_opts[name] = opt_name
        
        change_case = (self.accepts_cmd_line() and self.accepts_file())
        if self.accepts_cmd_line():
            cmd_opt = short_opt.lower() if short_opt is not None and change_case else short_opt
            delim = "," if value_class is None else None
            self.add_appender(OptionAppender(self.values_opt, cmd_opt, type="string",
                metavar="LIST", action="overwrite", default=default_values, delim=delim,
                help="List of values." % (long_opt, self.file_opt)))
        if self.accepts_file():
            file_opt = short_opt.upper() if short_opt is not None and change_case else short_opt
            self.add_appender(OptionAppender(self.file_opt, file_opt, type="string", metavar=FILE, 
                action="callback", callback=parseopt.readable_file, default=default_file,
                help="File with one value per line."))
    
    def accepts_cmd_line(self):
        return self.mode & CMD_LINE > 0
    
    def accepts_file(self):
        return self.mode & FILE > 0
    
    def simple_values(self):
        return self.value_class is None
        
    def process_options(self, options, args):
        # TODO: should probably do something with comments
        cl_values = None
        file_values = None
        comments = None
        
        # parse command-line values
        if self.accepts_cmd_line():
            cl_values = self._parse_values(options[self.values_opt], options)
            
        # parse file values
        if self.accepts_file():   
            (file_values, comments) = self._parse_file(options[self.file_opt], options)
            
        # merge values
        merged = self._merge_values(cl_values, file_values, comments)
        if (self.force_list or self.variable) and typeof(merged) == 'dict':
            merged = merged.values()
        
        # Set the option to the merged list/dict (sorting if required and only if merged is a list)
        if typeof(merged) == 'list' and self.cmp is not None:
            merged = sorted(set(merged), cmp=self.cmp)
        options[self.values_opt] = merged
    
    def _parse_file(self, fname, options):
        """
        A values file is an optional header of commented python code that should yield a dict named
        MACROS when exec'd, followed by rows of values, each of which can be simple or a comma-
        delimited list. If the latter, rows are parsed into objects (of class self.row_class) or
        dicts if self.row_class is None (in this case, a header is required).
        """
        lines = util.self_read_file_array(fname)
        if lines is None:
            raise IOError("%s is not a valid values file", fname)
        
        i = 0
        comments = []
        while lines[i][0] == '#':
            m = re.match('#\s*(.*)', lines[i])
            comments.append(m.group(1))
            i += i
        
        rows = self._parse_values(lines[i:], options)
        
        return (rows, comments)    
    
    def _parse_values(lines, options):
        parser_opts = {}
        for k,v in self.parser_opts.iteritems():
            parser_opts[k] = options[v]
        
        header = None
        if self.header or typeof(self.value_class) == 'dict':
            rows = {}
        else:
            rows = []
        
        rows = None
        for row in csv.reader(lines, **parser_opts):
            if self.simple_values():
                assert len(row) == 1
                rows.append(row[0])
            else
                if self.header:
                    if header is None:
                        header = row
                        continue
                    else:
                        row_dict = {}
                        for i in range(0,len(header)):
                            row_dict[header[i]] = row[i]
                        obj = self.value_class(**row_dict)
                else:
                    obj = self.row_class(*row)
                
                key = val.key() if 'key' in dir(val) else row[0]
                rows[key] = val
    
    def _merge_values(self, cl_values, file_values, comments):
        '''
        Merge command-line and file values. In the default implementation, when cl_values and 
        file_values are dicts, command-line values overwrite file values with the same name.
        '''
        merged = None
        if cl_values is None
            return file_values
        elif file_values is None:
            return cl_values
        elif self.simple_values():
            return cl_values + file_values
        else:
            return dict(file_values, **cl_values)
        
class ConstrainedValuesOptionAppender(ValuesAppender):
    '''
    A ValuesAppender that can take both command-line and file values. If both options are set, the 
    file is treated as the set of valid values (hence the "constrained" part of the class name) and 
    the values list as the ones to actually use.
    '''
    def __init__(self, name, short_opt, value_class=None, header=True, default_values=None, 
                 default_file=None, force_list=False, comparator=cmp, variable=False, 
                 parser_options=None):
        DelimitedFileAppender.__init__(self, name, short_opt, CMD_LINE | FILE, value_class, 
            default_values, default_file, force_list, comparator, variable, parser_options)
            
    def _merge_values(self, cl_values, file_values, comments):
        macros = None
        if comments is not None:
            ns = eval_util.exec_return_locals("\n".join(comments))
            if 'MACROS' in ns:
                macros = ns['MACROS']
     
        if cl_values is None
            return file_values
        elif file_values is None:
            return cl_values
        elif self.simple_values():
            return cl_values & file_values
        else:
            def resolve_value(value, valid, macros):
                if macros is not None and value in macros:
                    if typeof(valid) == 'list':
                        return valid & macros[value]
                    else:
                        return [valid[i] for i in macros[value]]
                elif valid is None or value in valid:
                    return value
                else:
                    raise Exception("Invalid value: %s" % value)
                        
            return [resolve_value(v, file_values, macros) for v in cl_values]

class InfilePattern(OptionSetAppender):
    __slots__ = [ 'file_opt', 'dir_opt' ]
    
    def __init__(self, short_opt='i', long_opt_prefix='in', default_dir=None, 
                 default_file=None):
        self.file_opt = "%sfile" % long_opt_prefix.lower()
        self.dir_opt = "%sdir" % long_opt_prefix.lower()
        OptionSetAppender.__init__(self,
            OptionAppender("-%s" % short_opt.lower(), "--%s" % self.file_opt,
                type="string", metavar="FILE", default=default_file, 
                help="Input file or pattern (if --%s is specified)." % self.dir_opt),
            OptionAppender("-%s" % short_opt.upper(), "--%s" % self.dir_opt,
                type="string", metavar="DIR", default=default_dir, 
                help="Directory containing input files"))
      
    def process_options(self, options, args):
        if self.file_opt not in options and self.dir_opt not in options:
            raise OptionValueError("At least one of --%s, --%s is required" % 
                (self.file_opt, self.dir_opt))
    
    def interpolate_options(self, options, args):
        path = None
        if self.dir_opt in options:
            path = self.dir_opt % options
            parseopt.readable_dir(path)
        if self.file_opt in options:
            fname = self.file_opt % options
            if path is None:
                path = fname
            else:
                path = os.path.join(path, fname)
            parseopt.readable_file(path)
        options[self.file_opt] = path