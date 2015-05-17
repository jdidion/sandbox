/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import java.math.BigDecimal;

public interface Value extends Cloneable {
    String VALUE_SLOT_NAME = "value";

    String getString();
    BigDecimal getNumber();
    boolean getBoolean();
    LunaObject getObject();
    Value execute(Value... args);
}