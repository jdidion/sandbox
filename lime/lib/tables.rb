module Lime
  class Tables
    attr_reader :sources, :sinks
    
    def initialize(sources, sinks)
      @sources = sources.nil? ? {} : sources.to_h(:name)
      @sinks = sinks.nil? ? {} : sinks.to_h(:name)
    end
    
    def source(name)
      @sources[name]
    end
    
    def sink(name)
      @sinks[name]
    end
    
    def to_columns(table_name, args)
      if args.nil? || args.empty?
        table_name
      elsif @sources.key?(table_name)
        Columns.new(:source, table_name, args)
      elsif @sinks.key?(table_name)
        Columns.new(:sink, table_name, args)
      elsif table_name == :input && @sources.size == 1
        Columns.new(:source, @sources.values.first, args)
      elsif table_name == :output && @sinks.size == 1
        Columns.new(:sink, @sinks.values.first, args)
      elsif table_name == :output && @sinks.empty? && @sources.key?(:input)
        Columns.new(:sink, :input, args)
      elsif table_name == :output && @sinks.empty? && @sources.size == 1
        Columns.new(:sink, @sources.values.first, args)
      else
        raise NameError("Invalid table: #{table_name}", table_name.to_s)
      end
    end
  end
  
  class Columns
    attr_reader :type, :table_name
    
    def initialize(type, table_name, columns)
      @type = type
      @table_name = table_name
      @columns = columns
    end
    
    def values(tables)
      table = get_table(tables)
      @columns.collect {|c| table[c] }
    end
    
    def insert(tables, *values)
      table = get_table(tables)
      @columns.zip(values.flatten).each do |c, v|
        
      end
    end
    
    def update_each(tables, &block)
      table = get_table(tables)
      @columns.each {|c| table[c] = yield table[c] }
    end
    
    protected
    
    def get_table(tables)
      (@type == :source ? tables.sources : tables.sinks)[@table_name]
    end
  end
  
  # ncols
  class Table
    attr_reader :name
    
    def initialize(name)
      @name = name
    end
    
    def column_index(name)
      for i in 0..(ncols-1)
        return i if header(i) eq name
      end
      raise "Invalid column #{name}"
    end
    
    def resolve_column(col)
      if col.is_a?(Integer)
        raise "Invalid column #{col}" unless col >= 0 && col < ncols
        col
      else
        column_index(col)
      end
    end
  end
  
  # A Source is an input data table. Subclasses are required to implement
  # the following methods: headers?, [], []=, header(col), advance, open
  class Source < Table
    attr_reader :status
    
    def initialize(name)
      super(name)
      @status = :unopened
    end
    
    def open?;      :status == :open; end
    def exhausted?; :status == :exhausted; end
    
    def headers
      raise "No headers defined" unless headers?
      headers = []
      for h in 0..ncols
        headers << header(h)
      end
      headers
    end
    
    def column?(col)
      if col.is_a?(Integer)
        col >= 0 && col < ncols
      else
        headers.include?(col)
      end
    end
        
    protected
    attr_writer :status
    
    def assert_open
      raise "Source is not open" unless status == :open
    end
  end
  
  # A Sink is an output data table.
  class Sink < Table
    attr_reader :headers
    
    def initialize(name)
      super(name)
    end
  end
end