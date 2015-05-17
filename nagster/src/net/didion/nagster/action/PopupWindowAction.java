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
import static net.didion.nagster.ParameterUtils.getStringValue;

import javax.swing.JOptionPane;
import java.util.Map;

public class PopupWindowAction extends ActionBase {
    public static final ParameterDescriptor PARAM_MESSAGE =
            ParameterDescriptor.createValueDescriptor("message", false, true);
    public static final String DEFAULT_MESSAGE = "Alert!";

    public PopupWindowAction() {
        super("Pop up a Window", PARAM_MESSAGE);
    }

    public boolean execute(
            Map<ParameterDescriptor, Parameter> parameters)
            throws Exception {
        String message = getStringValue(
                parameters, PARAM_MESSAGE, DEFAULT_MESSAGE);
        JOptionPane.showConfirmDialog(
                null, message, "Alert!", JOptionPane.OK_OPTION,
                JOptionPane.WARNING_MESSAGE);
        return true;
    }
}