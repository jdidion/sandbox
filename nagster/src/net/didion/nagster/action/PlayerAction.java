/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.action;

import net.didion.nagster.ActionBase;
import net.didion.nagster.Parameter;
import net.didion.nagster.ParameterDescriptor;
import net.didion.nagster.Player;
import net.didion.nagster.Resource;
import net.didion.nagster.ParameterUtils;
import static net.didion.nagster.ParameterUtils.*;

import java.util.Map;

public class PlayerAction extends ActionBase {
    public static final ParameterDescriptor PARAM_RESOURCE =
            ParameterDescriptor.createResourceDescriptor("resource", true);

    public PlayerAction() {
        super("Play Resource", PARAM_RESOURCE, PARAM_RETURN, PARAM_WAIT);
    }

    public boolean execute(
            Map<ParameterDescriptor, Parameter> parameters)
            throws Exception {
        Resource resource = getResourceValue(parameters, PARAM_RESOURCE);
        Player.play(resource, ParameterUtils.wait(parameters));
        return getDefaultReturnValue(parameters);
    }
}