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
import net.didion.nagster.ParameterUtils;
import static net.didion.nagster.ParameterUtils.*;

import java.util.Map;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.Chat;

public class IMAction extends ActionBase {
    public static enum Network {
        YAHOO,
        AIM,
        MSN,
        JABBER;
        
        public static String[] stringValues() {
            String[] values = new String[Network.values().length];
            for (int i = 0; i < values().length; i++) {
                values[i] = values()[i].name();                
            }
            return values;
        }

        public String formatAddress(String username) {
            return username;
        }
    }

    public static final ParameterDescriptor PARAM_NETWORK =
            ParameterDescriptor.createValueDescriptor("network", true, Network.JABBER.name()); //Network.stringValues());
    public static final ParameterDescriptor PARAM_USERNAME =
            ParameterDescriptor.createValueDescriptor("username", true, false);
    public static final ParameterDescriptor PARAM_MESSAGE =
            ParameterDescriptor.createValueDescriptor("message", false, true);

    public static final String DEFAULT_MESSAGE = "Alert!";

    public IMAction() {
        super("Send an Instant Message",
              PARAM_NETWORK, PARAM_USERNAME, PARAM_MESSAGE, PARAM_WAIT, PARAM_RETURN);
    }

    public boolean execute(
            Map<ParameterDescriptor, Parameter> parameters)
            throws Exception {
        Network network = Network.valueOf(getStringValue(parameters, PARAM_NETWORK));
        String username = getStringValue(parameters, PARAM_USERNAME);
        String message = getStringValue(parameters, PARAM_MESSAGE);
        if (message == null) {
            message = DEFAULT_MESSAGE;
        }
        boolean waitForReply = ParameterUtils.wait(parameters);
        sendMessage(network, username, message, waitForReply);
        if (waitForReply) {
            return true;
        }
        return getDefaultReturnValue(parameters);
    }

    // TODO - implement gateways for non-Jabber addresses
    private void sendMessage(
            Network network, String username, String message, boolean waitForReply)
            throws Exception {
        String host = getConfigurationParameters().get("host");
        String login = getConfigurationParameters().get("username");
        String password = getConfigurationParameters().get("password");

        XMPPConnection connection = new XMPPConnection(host);
        connection.login(login, password);

        if (network == Network.JABBER) {
            Chat chat = connection.createChat(network.formatAddress(username));
            chat.sendMessage(message);
            if (waitForReply) {
                chat.nextMessage();
            }
        }
    }
}