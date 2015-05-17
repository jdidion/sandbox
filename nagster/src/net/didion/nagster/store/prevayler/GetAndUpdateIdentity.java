/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.store.prevayler;

import org.prevayler.TransactionWithQuery;

import java.util.Date;

public class GetAndUpdateIdentity implements TransactionWithQuery {
    private Class _class;

    public GetAndUpdateIdentity(Class c) {
        _class = c;
    }

    public Object executeAndQuery(Object object, Date date) throws Exception {
        return ((Database) object).getAndIncrementId(_class);
    }
}