require 'fastercsv'

module Lime
  # A Source backed by a one- or two-dimensional array. 1D arrays
  # are treated as a table with a single column.
  class ArraySource < Source
    attr_reader :table, :row
    
    def initialize(name, table, headers)
      raise "Table must have at least one row and one column"
        if table.nil? || table.size == 0
      super(name)
      @table = table
      @headers = headers
      @row = -1
    end
    
    def open
      status = :open
    end
    
    def ncols
      @table[0].is_a?(Array) ? @table[0].size : 1
    end
    
    def headers?
      !@headers.nil?
    end
      
    def header(col)
      @headers[resolve_column(col)]
    end
    
    def advance
      assert_open
      @row += 1
      if @row == @table.size
        status = :exhausted
        return false
      else
        return true
      end
    end
      
    def [](col)
      assert_open
      col = resolve_column(col)
      row = @table[@row]
      row.is_a?(Array) ? row[col] : row
    end
    
    def []=(col, value)
      assert_open
      col = resolve_column(col)
      row = @table[@row]
      if row.is_a?(Array)
        row[col] = value
      else
        @table[@row] = value
      end
    end
  end
  
  # Right now this is just a proxy to DelimitedFileSource.new since
  # that is the only supported file type.
  def Lime.create_file_source(name, file, opt)
    DelimitedFileSource.new(name, file, opt)
  end
  
  # A source backed by a delimited text file (i.e. CSV, TSV).
  class DelimitedFileSource < Source
    attr_reader :file, :delim, :opt, :row
    
    # Create a DelimitedFileSource. Options are:
    # :type => :csv, :tsv, :delim. 
    # :delim => When :type == :delim, specifies the delimiter character
    # :line_endings => line terminator character; defaults to system default
    # :escape_char => character that escapes special characters in the source 
    # file, defaults to \
    # :quote_char => character that quotes field values, defaults to nil
    def initialize(name, file, opt={:type => :auto, :escape_char => '\\'})
      super(name)
      @file = file
      @delim = case opt[:type]
        when :auto then raise "File-type auto-detection is not yet supported"
        when :csv  then ','
        when :tsv  then "\t"
        else opt[:delim]
      end
      @opt = opt
    end
    
    def open
      @fh = ::File.open(file)
      @parser = FasterCSV.new(@fh, :col_sep => @delim, 
        :headers => opt.key?(:headers) ? opt[:headers] : false)
      @headers = @parser.header_row? ? @parser.shift : nil
      @row = nil
    end
    
    def ncols
      assert_open
      if !@header.nil?
        @headers.row.size
      elsif !@row.nil?
        @row.row.size
      else
        unless instance_variable_defined? :@preview
          @preview = @parser.shift
          raise "Empty file" if @preview.nil?
        end
        @preview.row.size
      end
    end
    
    def headers?
      assert_open
      !@headers.nil?
    end
    
    def headers
      assert_open
      raise "No headers defined" unless headers?
      @headers.fields
    end
    
    def header(col)
      assert_open
     @headers[resolve_column(col)]
    end
     
    def advance
      assert_open
      if instance_variable_defined? :@preview
        @row = @preview
        @preview = nil
      else
        @row = @parser.shift
        if @row.nil?
          @fh.close
          status = :exhausted
        end
        return false
      end
      true
    end
    
    def [](col)
      assert_open
      raise "No current row" if @row.nil?
      @row[col]
    end
    
    def []=(col, value)
      assert_open
      raise "No current row" if @row.nil?
      @row[col] = value
    end
  end
end