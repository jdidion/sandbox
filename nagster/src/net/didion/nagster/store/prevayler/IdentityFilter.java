/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.store.prevayler;

import net.didion.nagster.Identity;

public class IdentityFilter implements Filter {
    private Integer _id;

    public IdentityFilter(Integer id) {
        _id = id;
    }

    public boolean accept(Object obj) {
        if (_id.equals(((Identity) obj).getId())) {
            return true;
        }
        return false;
    }
}