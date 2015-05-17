package net.didion.pml.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class AbstractType implements Type {
    protected static Throwable unwrap(InvocationTargetException ex) {
        Throwable t = ex.getTargetException();
        while (t instanceof InvocationTargetException) {
            t = ((InvocationTargetException) t).getTargetException();
        }
        return t;
    }
    
    private String name;
    private final Class<?> javaType;
    
    public AbstractType(String name, Class<?> javaType) {
        this.name = name;
        this.javaType = javaType;
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<?> getJavaType() {
        return this.javaType;
    }

    @Override
    public Object parse(String value, String format) {
        if (null == value) {
            return null;
        }
        try {
            return doParse(value, format);
        }
        catch (TypeValidationException ex) {
            return null;
        }
    }

    @Override
    public void validate(String value, String format) throws TypeValidationException {
        doParse(value, format);
    }    
    
    protected Object doParse(String value, String format) throws TypeValidationException {
        if (null != format) {
            validateRegexp(value, format);
        }
        
        // first try valueOf method
        try {
            final Method m = this.javaType.getMethod("valueOf", String.class);
            return m.invoke(null, value);
        }
        catch (InvocationTargetException ex) {
            throw new TypeValidationException("Invalid value: " + value, unwrap(ex));
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        catch (NoSuchMethodException ex) {
            // ignore
        }
        
        // next try constructor
        try {
            final Constructor<?> c = this.javaType.getConstructor(String.class);
            return c.newInstance(value);
        }
        catch (InvocationTargetException ex) {
            throw new TypeValidationException("Invalid value: " + value, unwrap(ex));
        }
        catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        catch (NoSuchMethodException ex) {
            // ignore
        }
        
        // it's up to the subclass
        throw new UnsupportedOperationException("Validation not implemented for this Type");
    }
    
    protected void validateRegexp(String value, String regexp) throws TypeValidationException {
        if (!Pattern.matches(regexp, value)) {
            throw new TypeValidationException("Value does not match regular expression");
        }
    }
}
