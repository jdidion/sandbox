/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.store.prevayler;

import net.didion.nagster.Event;
import org.prevayler.Transaction;

import java.util.Date;

public class Insert implements Transaction {
    private String _table;
    private Object _object;

    public Insert(String table, Object object) {
        _table = table;
        _object = object;
    }

    public void executeOn(Object object, Date date) {
        ((Database) object).add(_table, _object);
    }
}