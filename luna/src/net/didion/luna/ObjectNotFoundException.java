/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(ObjectLocator id) {
        super("Object with id " + id + " cannot be found.");
    }

    public ObjectNotFoundException() {
        super("Object id is null.");
    }
}