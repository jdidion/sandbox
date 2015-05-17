module Lime
  # A Sink that stores data rows in an array.
  class ArraySink < Sink
    attr_reader :table
    
    def initialize(name)
      super(name)
      @table = []
    end
  end
  
  # Right now this is just a proxy to DelimitedFileSink.new since
  # that is the only supported file type.
  def Lime.create_file_sink(name, file, opt)
    DelimitedFileSink.new(name, file, opt)
  end
  
  class DelimitedFileSink < Sink
    attr_reader :file, :delim, :opt
    
    # Create a DelimitedFileSource. Options are:
    # :type => :csv, :tsv, :delim. 
    # :delim => When :type == :delim, specifies the delimiter character
    # :line_endings => line terminator character; defaults to system default
    # :escape_char => character that escapes special characters in the source 
    # file, defaults to \
    # :quote_char => character that quotes field values, defaults to nil
    def initialize(name, file, opt={:escape_char => '\\'})
      super(name)
      @file = file
      @delim = case opt[:type]
        when :csv  then ','
        when :tsv  then "\t"
        else opt[:delim]
      end
      @opt = opt
    end
  end
end