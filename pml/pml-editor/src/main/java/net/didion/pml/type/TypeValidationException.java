package net.didion.pml.type;

public class TypeValidationException extends Exception {
    public TypeValidationException(String message) {
        super(message);
    }

    public TypeValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
