package net.didion.pml.type;

public class StringType extends AbstractType {
    public StringType() {
        this(String.class);
    }
    
    public StringType(Class<?> javaType) {
        super("string", javaType);
    }

    @Override
    public Object doParse(String value, String format) throws TypeValidationException {
        if (null != format) {
            validateRegexp(value, format);
        }
        return value;
    }
}
