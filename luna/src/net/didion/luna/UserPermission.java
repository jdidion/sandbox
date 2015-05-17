/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

public class UserPermission extends SimplePermission {
    private User _user;

    public UserPermission(User user, boolean grant) {
        super(grant);
        _user = user;
    }

    public boolean grant(User user) {
        return _user.equals(user) && super.grant(user);
    }

    public boolean deny(User user) {
        return _user.equals(user) && super.deny(user);
    }
}