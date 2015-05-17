/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import net.didion.nagster.util.NameValuePair;

import java.util.Map;
import java.util.HashMap;

public abstract class ActionBase implements Action {
    private ActionDescriptor _actionDescriptor;
    private Map<String, String> _configurationParameters;

    protected ActionBase(String name, ParameterDescriptor... parameters) {
        _actionDescriptor = new ActionDescriptor(name, parameters);
    }

    public ActionDescriptor getActionDescriptor() {
        return _actionDescriptor;
    }

    public void addConfigurationParameter(NameValuePair param) {
        if (_configurationParameters == null) {
            _configurationParameters = new HashMap<String, String>();
        }
        _configurationParameters.put(param.getName(), param.getValue());
    }

    protected Map<String, String> getConfigurationParameters() {
        return _configurationParameters;
    }
}