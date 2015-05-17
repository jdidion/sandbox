/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import java.math.BigDecimal;

public class ObjectValue implements Value {
    private ObjectLocator _objectId;

    public ObjectValue(ObjectLocator objectId) {
        _objectId = objectId;
    }

    public String getString() {
        return _objectId.toString();
    }

    public BigDecimal getNumber() {
        return new BigDecimal(_objectId.getObjectId());
    }

    public boolean getBoolean() {
        return true;
    }

    public LunaObject getObject() {
        return _objectId.getObject();
    }

    public Value execute(Value... args) {
        return (Value) clone();
    }

    protected Object clone() {
        return new ObjectValue(_objectId);
    }
}