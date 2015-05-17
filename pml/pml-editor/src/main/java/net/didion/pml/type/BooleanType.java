package net.didion.pml.type;

public class BooleanType extends AbstractType {
    public BooleanType() {
        super("boolean", Boolean.class);
    }

    @Override
    protected Object doParse(String value, String format) throws TypeValidationException {
        return super.doParse(value.toLowerCase(), "true|false");
    }
}
