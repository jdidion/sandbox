# Array mixin methods
class Array
  # Convert an array to a hash using an attribute of each object
  # in the array as its key.
  def to_h(key_attr)
    h = {}
    self.each {|e| h[e.method(key_attr).call] = e }
    h
  end
  
  # Returns true if any element is true, otherwise false.
  def any?
    self.each {|e| return true if e }
    false
  end
end

class Symbol
  # Allow usage of brackets to specify columns when symbols are used
  # to identify sources/sinks.
  def [](*args)
    [self, args]
  end
end

module Lime
  # Delete all but the essential Object methods to reduce the chance
  # of collision with dynamically-defined method names.
  class BlankSlate
    instance_methods.each { |m| undef_method(m) unless %w(
      __send__ __id__ send class 
      inspect instance_eval instance_variables 
      ).include?(m)
    }
  end
end