/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import java.util.List;
import java.util.ArrayList;

public class PermissionSet {
    public static final boolean GRANT_TAKES_PRECEDENCE = true;

    private List<Permission> _permissions = new ArrayList<Permission>();
    private Permission _defaultPermission;

    public PermissionSet() {
    }

    public PermissionSet(Permission defaultPermission) {
        _defaultPermission = defaultPermission;
    }

    public void add(Permission permission) {
        _permissions.add(permission);
    }

    public boolean grant(User user) {
        boolean grant = false;
        boolean deny = false;
        for (Permission p : _permissions) {
            if (p.grant(user)) {
                grant = true;
            }
            if (p.deny(user)) {
                deny = true;
            }
        }

        return test(grant, deny, false) ||
               (_defaultPermission == null && GRANT_TAKES_PRECEDENCE) ||
               test(_defaultPermission.grant(user), _defaultPermission.deny(user), GRANT_TAKES_PRECEDENCE);
    }

    private boolean test(boolean grant, boolean deny, boolean def) {
        if (GRANT_TAKES_PRECEDENCE && grant) {
            return true;
        }
        if (!GRANT_TAKES_PRECEDENCE && deny) {
            return false;
        }
        if (grant) {
            return true;
        }
        if (deny) {
            return false;
        }
        return def;
    }
}