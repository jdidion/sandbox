package net.didion.pml.util.properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XmlPropertyFileFormatter<TT> {
    void writeGroupName(Document doc, Element parent, String name);
    
    void writePropertyComment(Document doc, Element parent, String comment);
    
    void writeProperty(Document doc, Element parent, String key, TT value);
}
