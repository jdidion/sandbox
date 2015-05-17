/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import java.math.BigDecimal;

public class Slot {
    private String _name;
    private String _description;
    private User _owner;
    private Value _value;

    public Slot(String name, User owner) {
        this(name, null, owner);
    }

    public Slot(String name, String description, User owner) {
        _name = name;
        _description = description;
        _owner = owner;
    }

    public String getName() {
        return _name;
    }

    public String getDescription() {
        return _description;
    }

    public void setValue(Value value) {
        _value = value;
    }

    public Value getValue() {
        return _value;
    }

    public Object getOwner() {
        return _owner;
    }
}