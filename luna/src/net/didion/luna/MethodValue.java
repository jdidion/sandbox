/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import java.math.BigDecimal;

public class MethodValue implements Value {
    private Method _method;

    public MethodValue(Method method) {
        _method = method;
    }

    public String getString() {
        try {
            Value val = execute();
            return val.getString();
        } catch (Exception ex) {
            return getAsPrimitive().getString();
        }
    }

    public BigDecimal getNumber() {
        try {
            Value val = execute();
            return val.getNumber();
        } catch (Exception ex) {
            return getAsPrimitive().getNumber();
        }
    }

    public boolean getBoolean() {
        try {
            Value val = execute();
            return val.getBoolean();
        } catch (Exception ex) {
            return getAsPrimitive().getBoolean();
        }
    }

    public LunaObject getObject() {
        try {
            Value val = execute();
            return val.getObject();
        } catch (Exception ex) {
            return getAsPrimitive().getObject();
        }
    }

    public Value execute(Value... args) {
        return _method.execute(args);
    }

    private PrimitiveValue getAsPrimitive() {
        return new PrimitiveValue(_method.toString());
    }

    protected Object clone() {
        return new MethodValue(_method);
    }
}