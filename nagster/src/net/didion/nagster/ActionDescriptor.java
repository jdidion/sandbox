/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.io.Serializable;

public class ActionDescriptor implements Comparable, Serializable {
    private String _name;
    private List<ParameterDescriptor> _parameters;

    public ActionDescriptor(String name, ParameterDescriptor... parameters) {
        _name = name;
        _parameters = Arrays.asList(parameters);
    }

    public String getName() {
        return _name;
    }

    public Collection<ParameterDescriptor> getParameterDescriptors() {
        return _parameters;
    }

    public String toString() {
        return getName();
    }

    public int compareTo(Object o) {
        return getName().compareTo(((ActionDescriptor) o).getName());
    }
}