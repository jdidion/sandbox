package net.didion.pml.type;

public interface Type {
    String getName();
    
    Class<?> getJavaType();
    
    /**
     * Attempts to parse <tt>stringValue</tt>. Returns null if a) stringValue is null
     * or b) stringValue cannot be parsed.
     * 
     * @param stringValue
     * @param format
     * @return
     */
    Object parse(String value, String format);
    
    /**
     * Validate that the specified string value can be converted to this type. If <var>format</var>
     * is not null, further validate that <var>value</var> matches the format. 
     * 
     * @param value
     * @param format
     * @throws TypeValidationException
     */
    void validate(String value, String format) throws TypeValidationException;
}
