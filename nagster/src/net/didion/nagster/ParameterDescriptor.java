/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import javax.activation.MimeType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

public class ParameterDescriptor implements Serializable {
    public static final MimeType TEXT = createMimeType("text/*");

    private static MimeType createMimeType(String mimeType) {
        try {
            return new MimeType(mimeType);
        } catch (Exception ex) {
            return null;
        }
    }

    public static ParameterDescriptor createValueDescriptor(
            String name, boolean required, String... acceptedValues) {
        if (acceptedValues.length == 0) {
            acceptedValues = null;
        }
        boolean acceptsResource = (acceptedValues == null);
        return new ParameterDescriptor(
                name, true, acceptedValues, acceptsResource, false, required);
    }

    public static ParameterDescriptor createValueDescriptor(
            String name, boolean required, boolean acceptsResource) {
        return new ParameterDescriptor(
                name, true, null, acceptsResource, false, required);
    }

    public static ParameterDescriptor createResourceDescriptor(
            String name, boolean required) {
        return new ParameterDescriptor(
                name, false, null, true, false, required);
    }

    public static ParameterDescriptor createApplicationDescriptor(
            String name, boolean required) {
        return new ParameterDescriptor(
                name, false, null, false, true, required);
    }

    private String _name;
    private boolean _acceptsValue;
    private Collection<String> _acceptedValues;
    private boolean _acceptsResource;
    private boolean _acceptsApplication;
    private boolean _required;

    private ParameterDescriptor(
            String name, boolean acceptsValue, String[] acceptedValues,
            boolean acceptsResource, boolean acceptsApplication, boolean required) {
        _name = name;
        _acceptsValue = acceptsValue;
        if (acceptedValues != null) {
            _acceptedValues = Arrays.asList(acceptedValues);
        }
        _acceptsResource = acceptsResource;
        _acceptsApplication = acceptsApplication;
        _required = required;
    }

    public String getName() {
        return _name;
    }

    public boolean isAcceptsValue() {
        return _acceptsValue;
    }

    public Collection getAcceptedValues() {
        return _acceptedValues;
    }

    public boolean isAcceptsResource() {
        return _acceptsResource;
    }

    public boolean isAcceptsResource(Resource resource) {
        if (isAcceptsValue()) {
            return TEXT.match(resource.getMimeType());
        }
        return isAcceptsResource();
    }

    public boolean isAcceptsApplication() {
        return _acceptsApplication;
    }

    public boolean isRequired() {
        return _required;
    }

    public boolean validate(Parameter param) {
        if (!equals(param.getParameterDescriptor())) {
            return false;
        }
        if (param.getValue() != null) {
            if (!isAcceptsValue()) {
                return false;
            }
            if (getAcceptedValues() != null &&
                    !getAcceptedValues().contains(param.getValue())) {
                return false;
            }
        } else if (param.getResource() != null) {
            if (!isAcceptsResource()) {
                return false;
            }
        } else if (param.getApplication() != null) {
            if (!isAcceptsApplication()) {
                return false;
            }
        } else if (isRequired()) {
            return false;
        }
        return true;
    }
}