module Lime
  # An Operation is a manipulation of a Source (or Sources), possibly
  # with a destination Sink (or Sinks).
  
  # Copy one or more columns from a Source to a Sink.
  class CopyOperation
    def initialize(source, sink)
      @source = source
      @sink = sink
    end
    
    def run(tables)
      values = @source.values(tables)
      @sink.insert(tables, *values)
    end
  end
  
  # Apply a function to one or more columns.
  class ApplyOperation
    def initialize(columns, fn, *args)
      @columns = columns
      @fn = fn
      @args = args
    end
    
    def run(tables)
      @columns.update_each(tables) do |v|
        @fn.call(v, *args)
      end
    end
  end
end