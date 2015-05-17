package net.didion.pml.util.properties;

import org.w3c.dom.Element;

public interface XmlPropertyFileParser<T> {
    Comment parseComment(org.w3c.dom.Comment comment);
    
    KeyValue<T> parseProperty(Element property);
}
