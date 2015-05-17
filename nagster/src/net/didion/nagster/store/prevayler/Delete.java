/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.store.prevayler;

import org.prevayler.Transaction;

import java.util.Date;

public class Delete implements Transaction {
    private String _table;
    private Object _obj;

    public Delete(String table, Object obj) {
        _table = table;
        _obj = obj;
    }

    public void executeOn(Object object, Date date) {
        ((Database) object).remove(_table, _obj);
    }
}