# Lime::Builder implements a DSL for manipulating textual tables of data
  # (such as CSV files). Input/Output are abstracted as Sources (see source.rb)
  # and Sinks (see sink.rb). Any Source can be used as a Sink, meaning the
  # source will be overwritten.
  #
  # Source/Sink columns can be referenced in several ways depending on what
  # you want to do. The most formal way is <pre>io_name(:col_name)</pre> or
  # <pre>io_name(col_num)</pre>, where column numbers are 1-indexed. Column
  # name symbols must follow Ruby's symbol naming rules, so only alphanumerics
  # and underscores (_) are allowed. You can also specify column names as
  # strings. Also allowed are ranges: <pre>io_name(start_num..end_num)</pre>
  # or <pre>io_name(:start_name..:end_name)</pre>, and lists: 
  # <pre>io_name(col_num_1, col_num_2, ...)</pre> or 
  # <pre>io_name(:col_name_1, :col_name_2)</pre>. There are also two special
  # symbols: :all specifies all columns, and :rest specifies remaining columns
  # that have not been specified earlier in a list or range. The string 
  # equivalents are '*' and '...'. Use of any of these special symbols/strings
  # can be disabled via the options passed to the constructor.
  #
  # If you omit the IO name the default is used: input() for Sources and 
  # output() for Sinks. If there are no Sinks defined, input() is used as the 
  # Sink (meaning you're modifying the Source in-place). You can also omit the
  # brackets if you omit the IO name, except in the case of lists.