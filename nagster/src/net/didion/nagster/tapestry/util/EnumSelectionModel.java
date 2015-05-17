/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.tapestry.util;

import org.apache.tapestry.form.IPropertySelectionModel;

public class EnumSelectionModel<T extends Enum<T>> implements IPropertySelectionModel {
    private Enum<T>[] _items;

    public EnumSelectionModel(Enum<T>[] items) {
        _items = items;
    }

    public int getOptionCount() {
        return _items.length;
    }

    public Object getOption(int index) {
        return _items[index];
    }

    public String getLabel(int index) {
        return _items[index].name();
    }

    public String getValue(int index) {
        return _items[index].name();
    }

    public Object translateValue(String value) {
        for (Enum<T> item : _items) {
            if (item.name().equals(value)) {
                return item;
            }
        }
        return null;
    }
}