/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.util.Collection;
import java.util.Map;

public interface ActionManager {
    void setActions(Collection<Action> actions);
    Collection<ActionDescriptor> listActionDescriptors();
    boolean execute(
            ActionDescriptor descriptor, Map<ParameterDescriptor, Parameter> parameters)
            throws Exception;
}