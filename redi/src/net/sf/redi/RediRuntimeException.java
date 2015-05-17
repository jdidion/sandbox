/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

public class RediRuntimeException extends RuntimeException {
    private Object[] _args;

    public RediRuntimeException(Throwable cause) {
        super(cause);
    }

    public RediRuntimeException(String message, Object... args) {
        super(message);
        _args = args;
    }

    public RediRuntimeException(String message, Throwable cause, Object... args) {
        super(message, cause);
        _args = args;
    }

    public String getLocalizedMessage() {
        return Utils.format(getMessage(), _args);
    }
}