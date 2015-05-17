/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.action;

import net.didion.nagster.ActionBase;
import net.didion.nagster.Application;
import net.didion.nagster.Parameter;
import net.didion.nagster.ParameterDescriptor;
import static net.didion.nagster.ParameterUtils.*;

import java.util.Map;

public class ExecAction extends ActionBase {
    public static final ParameterDescriptor PARAM_APPLICATION =
            ParameterDescriptor.createApplicationDescriptor("application", true);

    public ExecAction() {
        super("Execute a Program", PARAM_APPLICATION, PARAM_RETURN);
    }

    public boolean execute(
            Map<ParameterDescriptor, Parameter> parameters)
            throws Exception {
        Application app = getApplicationValue(parameters, PARAM_APPLICATION);
        Integer retVal = app.execute(app.getParameters());
        if (retVal == null || parameters.containsKey(PARAM_RETURN)) {
            return getDefaultReturnValue(parameters);
        }
        return retVal.intValue() == 0;
    }
}