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
import static net.didion.nagster.ParameterUtils.PARAM_RETURN;
import static net.didion.nagster.ParameterUtils.getDefaultReturnValue;

import java.awt.Toolkit;
import java.util.Map;

public class BeepAction extends ActionBase {
    public BeepAction() {
        super("Beep", PARAM_RETURN);
    }

    public boolean execute(Map<ParameterDescriptor, Parameter> parameters)
            throws Exception {
        Toolkit.getDefaultToolkit().beep();
        return getDefaultReturnValue(parameters);
    }
}