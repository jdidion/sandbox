/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.tapestry.util;

import org.apache.tapestry.form.IPropertySelectionModel;

public class ArrayPropertySelectionModel<T> implements IPropertySelectionModel {
    private T[] _objects;

    public ArrayPropertySelectionModel(T[] descriptors) {
        _objects = descriptors;
    }

    public int getOptionCount() {
        return _objects.length;
    }

    public Object getOption(int i) {
        return _objects[i];
    }

    public String getLabel(int i) {
        return _objects[i].toString();
    }

    public String getValue(int i) {
        return _objects[i].toString();
    }

    public Object translateValue(String string) {
        for (T t : _objects) {
            if (string.equals(t.toString())) {
                return t;
            }
        }
        return null;
    }
}