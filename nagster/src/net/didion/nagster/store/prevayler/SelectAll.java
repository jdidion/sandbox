/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.store.prevayler;

import org.prevayler.Query;

import java.util.Date;

public class SelectAll implements Query {
    private String _table;

    public SelectAll(String table) {
        _table = table;
    }

    public Object query(Object object, Date date) throws Exception {
        return ((Database) object).list(_table);
    }
}