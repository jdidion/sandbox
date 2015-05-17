package net.didion.pml.util.properties;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DefaultXmlPropertyFileFormat<T> implements XmlPropertyFileFormatter<T>, XmlPropertyFileParser<T> {
    private final boolean handleCommentedProperties;
    
    public DefaultXmlPropertyFileFormat() {
        this(false);
    }
    
    public DefaultXmlPropertyFileFormat(boolean handleCommentedProperties) {
        this.handleCommentedProperties = handleCommentedProperties;
    }
    
    @Override
    public Comment parseComment(org.w3c.dom.Comment n) {
        final String comment = n.getTextContent();
        if (comment.startsWith("\n")) {
            return new Comment(Comment.Type.GROUP_NAME, comment.trim());
        }
        else if (comment.startsWith("<property")) {
            return (this.handleCommentedProperties) ? new Comment(Comment.Type.COMMENTED_PROPERTY, comment) : null;
        }
        else {
            return new Comment(Comment.Type.PROPERTY_COMMENT, comment);
        }
    }

    @Override
    public KeyValue<T> parseProperty(Element e) {
        if (e.hasAttribute("key")) {
            final String key = e.getAttribute("key");
            final Node n = e.getFirstChild();
            final String val = (n == null) ? "" : n.getNodeValue();
            return new KeyValue<T>(key, fromString(val));
        }
        return null;
    }

    @Override
    public void writeGroupName(Document doc, Element parent, String name) {
        final String comment = "\n" + name + "\n";
        parent.appendChild(doc.createComment(comment));
    }

    @Override
    public void writePropertyComment(Document doc, Element parent, String comment) {
        parent.appendChild(doc.createComment(comment));
    }
    
    @Override
    public void writeProperty(Document doc, Element parent, String key, T value) {
        final Element entry = (Element) parent.appendChild(doc.createElement("entry"));
        entry.setAttribute("key", key);
        entry.appendChild(doc.createTextNode(toString(value)));
    }        
    
    protected String toString(T value) {
        return value.toString();
    }
    
    @SuppressWarnings("unchecked")
    protected T fromString(String s) {
        return (T) s;
    }
}
