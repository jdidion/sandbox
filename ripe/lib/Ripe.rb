require 'rubygems'
require 'mechanize'

module Ripe
  # == Synopsis 
  #   Ripe is a document processing library for Ruby. It is inspired by
  #   Unix pipes, as well as powerful web tools like Yahoo Pipes. The
  #   name Ripe simply comes from "Ruby pIPEs". 
  #
  #   The basic use case for Ripe is that you want to transform one or
  #   several documents into some other format, possibly combining them
  #   or extracting data into objects. Documents can be local or on the 
  #   web, in plain text, HTML, XML, or any other supported format. There 
  #   are many types of transformations you can do. And if Ripe doesn't do 
  #   what you want out of the box, it can easily be extended by plugins.
  #
  # == Examples
  #
  #   The following example crawls EZTV for all the episodes of all the
  #   TV shows on the site. This example illustrates several methods in
  #   the HTML context. By setting the :context attribute to :html when
  #   requiring the Ripe library, shorthand names for all HTML methods
  #   may be used (otherwise each method would be prefixed with html_).
  #   A Pipeline is created with the define method, and executed with
  #   the run method. These two methods can be comined into one by using
  #   the eval method. A quick summary of this pipeline:
  #   1. Retrieve the EZTV show list web page and make it the current
  #      document.
  #   2. Focus in on the rows of the first table. Now we can proceed as
  #      if that this fragment is the current document.
  #   3. Iterate over the table rows. By not specifying the first 
  #      argument to the each method, it is assumed that we want to
  #      iterate over the nodes of the current document. The :skip
  #      attribute tells the each method to skip the first row, which
  #      consists of the table headers. The :save_to attribute sets the
  #      default object in the current context: for each iteration, a new
  #      TvShow object is created, stored to the show variable, and also
  #      set as the default object. At the end of each iteration, the
  #      TvShow object is stored to the shows hash using show.name as
  #      the key.
  #   4. Extract information from the current document, which is the
  #      row currently being iterated over. The :attr attribute causes
  #      the extracted value to be saved to the specified property of
  #      the current object (show).
  #   5. After extracting the required data, we set the current document
  #      to the page listing all the episodes of the TV show in the
  #      current row, then focus in on the episode table.
  #   6. Iterate over the rows of the episode table, again skipping the
  #      header row. An Episode object is created for each row, and
  #      stored to a hash, which is in turn stored to the episodes
  #      property of the current show object.
  #   7. Parse the current row into a DOM tree. The hash passed to the
  #      parse method maps properties of the current object (ep) to
  #      xpath statements that extract data from the tree. The mirrors
  #      property is a special case whose value is the result of 
  #      iterating over the a elements of the third td. This demonstrates
  #      that :save_to causes the destination container (mirrors) to be
  #      returned as well as added to the current context.
  #   8. Since shows was the last variable named in the outermost context,
  #      it is returned from the define method.
  #
  #   require 'Ripe' qw(:context => :html)
  #
  #   show_list = Ripe::Pipeline.eval do
  #     fetch 'http://eztv.it/index.php?main=showlist'
  #     focus '/html/body/center/table/table/tr'
  #     each :skip => 1, :save_to => 'shows{:name}=TvShow show' do
  #       extr '/td[1]/a'       :attr => :name 
  #       extr '/td[1]/a[href]' :attr => :url  
  #       
  #       fetch :url
  #       focus '/html/body/center/table/table/tr'
  #       each :skip => 1, :save_to => 'show.episodes{:name}=Episode ep' do
  #         parse
  #           :name    => '/td[2]/b/a[title]',
  #           :url     => '/td[2]/b/a[href]',
  #           :mirrors => each '/td[3]/a' :save_to => 'mirrors{:name}=Mirror' do
  #             extr '[title]' :attr => :name 
  #             extr '[href]'  :attr => :url   
  #           end
  #       end
  #     end
  #   end
  #
  # == Author
  #   John Didion
  #
  # == Copyright
  #   
  class Pipeline
    
  end
end