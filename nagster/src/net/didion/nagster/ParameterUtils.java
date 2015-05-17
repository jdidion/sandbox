/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.util.Map;

public final class ParameterUtils {
    public static final ParameterDescriptor PARAM_RETURN =
            ParameterDescriptor.createValueDescriptor("return", false, "true", "false");
    public static final ParameterDescriptor PARAM_WAIT =
            ParameterDescriptor.createValueDescriptor("wait", false, "true", "false");

    public static String getStringValue(
            Map<ParameterDescriptor, Parameter> parameters,
            ParameterDescriptor descriptor)
            throws Exception {
        return getStringValue(parameters, descriptor, null);
    }

    public static String getStringValue(
            Map<ParameterDescriptor, Parameter> parameters,
            ParameterDescriptor descriptor, String defaultValue)
            throws Exception {
        Parameter param = getAndValidateParameter(parameters, descriptor);
        if (param == null) {
            return defaultValue;
        } else if (param.getValue() != null) {
            return param.getValue();
        } else if (param.getResource() != null) {
            return param.getResource().getAsString();
        } else if (param.getApplication() != null) {
            return param.getApplication().getExecutable();
        }
        return defaultValue;
    }

    public static boolean getBooleanValue(
            Map<ParameterDescriptor, Parameter> parameters,
            ParameterDescriptor descriptor)
            throws Exception {
        return getBooleanValue(parameters, descriptor, false);
    }

    public static boolean getBooleanValue(
            Map<ParameterDescriptor, Parameter> parameters,
            ParameterDescriptor descriptor, boolean defaultValue)
            throws Exception {
        String strVal = getStringValue(parameters, descriptor);
        if (strVal != null) {
            return new Boolean(strVal).booleanValue();
        }
        return defaultValue;
    }

    public static Resource getResourceValue(
            Map<ParameterDescriptor, Parameter> parameters,
            ParameterDescriptor descriptor)
            throws Exception {
        Parameter param = getAndValidateParameter(parameters, descriptor);
        return (param == null) ? null : param.getResource();
    }

    public static Application getApplicationValue(
            Map<ParameterDescriptor, Parameter> parameters,
            ParameterDescriptor descriptor)
            throws Exception {
        Parameter param = getAndValidateParameter(parameters, descriptor);
        return (param == null) ? null : param.getApplication();
    }

    public static boolean getDefaultReturnValue(
            Map<ParameterDescriptor, Parameter> parameters)
            throws Exception {
        return getBooleanValue(parameters, PARAM_RETURN, false);
    }

    public static boolean wait(
            Map<ParameterDescriptor, Parameter> parameters)
            throws Exception {
        return getBooleanValue(parameters, PARAM_WAIT, false);
    }

    private static Parameter getAndValidateParameter(
            Map<ParameterDescriptor, Parameter> parameters,
            ParameterDescriptor descriptor) {
        Parameter param = parameters.get(descriptor);
        if (param == null && descriptor.isRequired()) {
            throw new RuntimeException(
                    "missing required parameter " + descriptor.getName());
        }
        if (param != null && !param.validate()) {
            throw new RuntimeException("invalid parameter");
        }
        return param;
    }

    private ParameterUtils() {
    }
}