/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

public class RediException extends Exception {
    private Object[] _args;

    public RediException(Throwable cause) {
        super(cause);
    }

    public RediException(String message, Object... args) {
        super(message);
        _args = args;
    }

    public RediException(String message, Throwable cause, Object... args) {
        super(message, cause);
        _args = args;
    }

    public String getLocalizedMessage() {
        return Utils.format(getMessage(), _args);
    }
}