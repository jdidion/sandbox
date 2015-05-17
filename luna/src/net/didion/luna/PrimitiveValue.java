/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import java.math.BigDecimal;

public class PrimitiveValue implements Value {
    private String _value;

    public PrimitiveValue(String value) {
        _value = value;
    }

    public String getString() {
        return _value;
    }

    public BigDecimal getNumber() {
        try {
            return new BigDecimal(_value);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    public boolean getBoolean() {
        return _value != null && _value.length() > 0;
    }

    public LunaObject getObject() {
        LunaObject obj = LunaObject.createAnonymous();
        Slot slot = obj.createSlot(VALUE_SLOT_NAME);
        slot.setValue((Value) clone());
        return obj;
    }

    public Value execute(Value... args) {
        return (Value) clone();
    }

    protected Object clone() {
        return new PrimitiveValue(_value);
    }
}