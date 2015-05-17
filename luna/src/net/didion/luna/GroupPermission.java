/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

public class GroupPermission extends SimplePermission {
    private Group _group;

    public GroupPermission(Group group, boolean grant) {
        super(grant);
        _group = group;
    }

    public boolean grant(User user) {
        return _group.contains(user) && super.grant(user);
    }

    public boolean deny(User user) {
        return _group.contains(user) && super.deny(user);
    }
}