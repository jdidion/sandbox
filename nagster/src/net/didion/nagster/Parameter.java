/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.io.Serializable;

public class Parameter implements Serializable {
    public static final String VALUE = "value";
    public static final String RESOURCE = "resource";
    public static final String APPLICATION = "application";

    private ParameterDescriptor _descriptor;
    private String _type;
    private String _value;
    private Resource _resource;
    private Application _application;

    public Parameter(ParameterDescriptor descriptor) {
        _descriptor = descriptor;
    }

    public Parameter(ParameterDescriptor descriptor, String value) {
        _descriptor = descriptor;
        setType(VALUE);
        setValue(value);
    }

    public Parameter(ParameterDescriptor descriptor, Resource resource) {
        _descriptor = descriptor;
        setType(RESOURCE);
        setResource(resource);
    }

    public Parameter(ParameterDescriptor descriptor, Application application) {
        _descriptor = descriptor;
        setType(APPLICATION);
        setApplication(application);
    }

    public ParameterDescriptor getParameterDescriptor() {
        return _descriptor;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String value) {
        checkCanSet(VALUE);
        _value = value;
    }

    public Resource getResource() {
        return _resource;
    }

    public void setResource(Resource resource) {
        checkCanSet(RESOURCE);
        _resource = resource;
    }

    public Application getApplication() {
        return _application;
    }

    public void setApplication(Application application) {
        checkCanSet(APPLICATION);
        _application = application;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    private void checkCanSet(String type) {
        if (_type == null) {
            throw new RuntimeException("no type has been specified");
        }
        if (!_type.equals(type)) {
            throw new RuntimeException("invalid type");
        }
    }

    public boolean validate() {
        return getParameterDescriptor().validate(this);
    }
}