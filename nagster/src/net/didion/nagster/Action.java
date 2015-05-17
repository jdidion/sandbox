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

public interface Action {
    void addConfigurationParameter(NameValuePair param);
    ActionDescriptor getActionDescriptor();
    boolean execute(Map<ParameterDescriptor, Parameter>  parameters) throws Exception;
}