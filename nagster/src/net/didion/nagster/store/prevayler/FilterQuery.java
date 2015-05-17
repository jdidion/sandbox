/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.store.prevayler;

import net.didion.nagster.Event;
import org.prevayler.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class FilterQuery implements Query {
    private String _table;
    private Filter _filter;

    public FilterQuery(String table, Filter filter) {
        _table = table;
        _filter = filter;
    }

    public Object query(Object object, Date date) throws Exception {
        Database database = (Database) object;
        Collection filtered = new ArrayList();
        for (Object obj : database.list(_table)) {
            if (_filter.accept(obj)) {
                filtered.add(obj);
            }
        }
        return filtered;
    }
}