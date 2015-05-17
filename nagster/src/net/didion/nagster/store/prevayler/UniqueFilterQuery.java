/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.store.prevayler;

import java.util.Collection;
import java.util.Date;

public class UniqueFilterQuery extends FilterQuery {
    public UniqueFilterQuery(String table, Filter filter) {
        super(table, filter);
    }

    public Object query(Object object, Date date) throws Exception {
        Collection objects = (Collection) super.query(object, date);
        if (objects.size() == 0) {
            return null;
        } else if (objects.size() > 1) {
            throw new RuntimeException("more than one event matches filter criteria");
        } else {
            return objects.iterator().next();
        }
    }
}