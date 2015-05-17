package net.didion.pml.util.properties;

public class Comment {
    enum Type { FILE_HEADER, GROUP_NAME, PROPERTY_COMMENT, COMMENTED_PROPERTY, IGNORE };
    
    public Type type;
    public String comment;
        
    public Comment(Type type, String comment) {
        this.type = type;
        this.comment = comment;
    }
}
