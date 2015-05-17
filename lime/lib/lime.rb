require 'sources'
require 'sinks'
require 'operations'
require 'options'
require 'util'

module Lime
  # Lime::Builder implements a DSL for manipulating textual tables of data
  # (such as CSV files).
  
  class Script < BlankSlate
    attr_reader :tables, :operations
    
    # Parse a Lime script.
    def self.parse_file(script_file, opt_parser=Options.new)
      script_txt = ::File.read(script_file)
      
      # Parse command line options from the first line of the script 
      # if it is a comment
      options = {}
      if script_txt[/^#\\(.*)/] && opt
        options = opt_parser.parse! $1.split(/\s+/)
      end
      
      # Parse the script
      script = eval "Lime::Builder.new(opt) {( " + script_txt + "\n )}",
        TOPLEVEL_BINDING, script_file
      
      return script, options
    end
    
    # Create a Lime::Builder programatically. Sources, Sinks and
    # Operations can be passed in as opt[:sources], opt[:sinks] and
    # opt[:operations].
    def initialize(opt={}, &block)
      @tables = Tables.new(opt[:sources], opt[:sinks])
      @operations = opt.key?(:operations) ? opt[:operations] : []
      instance_eval(&block) if block_given?
    end
    
    # Execute the configured set of Operations.
    def run
      # Open Sources and Sinks
      @sources.values.each {|s| s.open }
      @sinks.values.each {|s| s.open }
      
      # Iterate over Sinks until all are exhausted
      while true
        # Advance all Sources that haven't been exhausted. We're done when all
        # Sources are exhausted.
        break unless (@sources.values.collect do |s| 
          s.exhausted? ? false : s.advance
        end).any?
        
        # Execute all operations
        @operations.each {|o| o.run(@tables) }
        
        # Commit all Sinks
        @sinks.values.each {|s| s.commit! }
      end
      
      # Close all sinks
      @sinks.values.each {|s| s.close }
    end
    
    # When columns are declared using the DSL, this is the method by
    # which they are handled.
    def method_missing(sym, *args)
      @tables.to_columns(sym, args)
    end
    
    # Add an operation. Returns the index of the operation.
    def add_operation(op)
      @operations << op
      @operations.size - 1
    end
    
    # Declare an input file.
    def infile(name, file, opt={:type => :auto, :escape_char => '\\'})
      @sources[name] = Lime.create_file_source(name, file, opt)
    end
    
    # Declare an output file.
    def outfile(name, file, opt={:type => :auto, :escape_char => '\\'})
      @sinks[name] = Lime.create_file_sink(name, file, opt)
    end
    
    # Copy one or more columns of a Source to a Sink. If sink is nil, the
    # columns are just appended to source.
    def copy(source, sink=nil)
      raise 'Source cannot be nil' if source.nil?
      if source.is_a?(Symbol)
        source = Columns.new(:source, source, :all)
      end
      if sink.nil?
        sink = Columns.new(:sink, source.table_name, :end)
      elsif sink.is_a?(Symbol)
        sink = Columns.new(:sink, sink, :end)
      end
      add_operation(CopyOperation.new(source, sink))
    end
    
    # Apply a function to one or more columns. The columns argument must be a
    # fully qualified column (or columns). Additional arguments to the function
    # besides the column value can be specified in args.
    def apply(columns, fn, *args)
      add_operation(ApplyOperation.new(columns, fn, *args))
    end
  end
end