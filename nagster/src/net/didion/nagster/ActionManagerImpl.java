/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

public class ActionManagerImpl implements ActionManager {
    private static Logger _log = Logger.getLogger(ActionManagerImpl.class);

    private Map<ActionDescriptor, Action> _actions;

    public ActionManagerImpl() {
        _actions = new HashMap<ActionDescriptor, Action>();
    }

    public void setActions(Collection<Action> actions) {
        _actions.clear();
        for (Action a : actions) {
            _actions.put(a.getActionDescriptor(), a);
        }
    }

    public Collection<ActionDescriptor> listActionDescriptors() {
        return _actions.keySet();
    }

    public boolean execute(
            ActionDescriptor descriptor, Map<ParameterDescriptor, Parameter> parameters)
            throws Exception {
        Action action = _actions.get(descriptor);
        if (action == null) {
            _log.warn("undefined action: " + descriptor.getName());
            return false;
        }
        return action.execute(parameters);
    }
}