/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.io.Serializable;

public class Application implements Identity, Comparable, Serializable {
    private Integer _id;
    private String _name;
    private String _executable;
    private List<ParameterDescriptor> _parameterDescriptors;
    private Map<ParameterDescriptor, Parameter> _parameters;

    public Application(
            String name, String executable, ParameterDescriptor... parameters) {
        _id = Nagster.nextId(Application.class);
        _name = name;
        _executable = executable;
        _parameterDescriptors = new ArrayList<ParameterDescriptor>();
        _parameterDescriptors.addAll(Arrays.asList(parameters));
        _parameters = new HashMap<ParameterDescriptor, Parameter>();
    }

    public void addParameter(Parameter param) {
        _parameterDescriptors.add(param.getParameterDescriptor());
        _parameters.put(param.getParameterDescriptor(), param);
    }

    public Map<ParameterDescriptor, Parameter> getParameters() {
        return Collections.unmodifiableMap(_parameters);
    }

    public Integer getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getExecutable() {
        return _executable;
    }

    public Collection<ParameterDescriptor> getParameterDescriptors() {
        return Collections.unmodifiableCollection(_parameterDescriptors);
    }

    public Integer execute(
            Map<ParameterDescriptor, Parameter>  parameters)
            throws Exception {
        Collection<ParameterDescriptor> descriptors = getParameterDescriptors();
        List<String> cmdArray = new ArrayList<String>(descriptors.size());
        cmdArray.add(getExecutable());
        for (ParameterDescriptor descriptor : descriptors) {
            String value = ParameterUtils.getStringValue(parameters, descriptor);
            if (value != null) {
                cmdArray.add(value);
            }
        }
        Process proc = Runtime.getRuntime().exec(
                cmdArray.toArray(new String[cmdArray.size()]));
        boolean wait = false;
        if (parameters.containsKey(ParameterUtils.PARAM_WAIT)) {
            wait = ParameterUtils.wait(parameters);
        }
        Integer retVal = null;
        if (wait) {
            retVal = new Integer(proc.waitFor());
        }
        return retVal;
    }

    public int compareTo(Object o) {
        return getName().compareTo(((Application) o).getName());
    }

    public String toString() {
        return getName();
    }
}