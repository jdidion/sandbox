/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

public class SimplePermission implements Permission {
    private boolean _grant;

    public SimplePermission(boolean grant) {
        _grant = grant;
    }

    public boolean grant(User user) {
        return _grant;
    }

    public boolean deny(User user) {
        return !_grant;
    }
}