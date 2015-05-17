/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class AlertImpl implements Alert {
    private Integer _id;
    private ActionDescriptor _actionDescriptor;
    private Map<ParameterDescriptor, Parameter> _parameters;

    public AlertImpl() {
        _id = Nagster.nextId(AlertImpl.class);
        _parameters = new HashMap<ParameterDescriptor, Parameter>();
    }

    public AlertImpl(ActionDescriptor action, Parameter... parameters) {
        this();
        setActionDescriptor(action);
        for (Parameter p : parameters) {
            setParameter(p);
        }
    }

    public Integer getId() {
        return _id;
    }

    public ActionDescriptor getActionDescriptor() {
        return _actionDescriptor;
    }

    public void setActionDescriptor(ActionDescriptor action) {
        _actionDescriptor = action;
    }

    public void setParameter(Parameter parameter) {
        ParameterDescriptor descriptor = parameter.getParameterDescriptor();
        if (!getActionDescriptor().getParameterDescriptors().contains(descriptor)) {
            throw new RuntimeException("invalid parameter");
        }
        _parameters.put(parameter.getParameterDescriptor(), parameter);
    }

    public Map<ParameterDescriptor, Parameter> getParameters() {
        return Collections.unmodifiableMap(_parameters);
    }

    public String getParametersAsString() {
        StringBuffer buf = new StringBuffer();
        for (Parameter p : getParameters().values()) {
            if (buf.length() > 0) {
                buf.append(",");
            }
            buf.append(p.getParameterDescriptor().getName());
            buf.append("=");
            if (p.getType() == null) {
                buf.append("null");
            } else if (p.getType().equals(Parameter.VALUE)) {
                buf.append(p.getValue());
            } else if (p.getType().equals(Parameter.RESOURCE)) {
                buf.append("Resource[").append(p.getResource().getName()).append("]");
            } else if (p.getType().equals(Parameter.APPLICATION)) {
                buf.append("Application[").append(p.getApplication().getName()).append("]");
            } else {
                buf.append("[unknown]");
            }
        }
        return buf.toString();
    }

    public Parameter getParameter(ParameterDescriptor descriptor) {
        return _parameters.get(descriptor);
    }

    public boolean execute(ActionManager executor) throws Exception {
        return executor.execute(_actionDescriptor, getParameters());
    }
}