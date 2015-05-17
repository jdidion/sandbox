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
import static net.didion.nagster.ParameterUtils.getStringValue;
import net.didion.nagster.util.SimpleAuthenticator;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class EmailAction extends ActionBase {
    public static final ParameterDescriptor PARAM_TO =
            ParameterDescriptor.createValueDescriptor("to", true, false);
    public static final ParameterDescriptor PARAM_FROM =
            ParameterDescriptor.createValueDescriptor("from", false, false);
    public static final ParameterDescriptor PARAM_SUBJECT =
            ParameterDescriptor.createValueDescriptor("from", false, false);
    public static final ParameterDescriptor PARAM_BODY =
            ParameterDescriptor.createValueDescriptor("from", false, true);

    public static final String DEFAULT_FROM = "alert@nagster.com";
    public static final String DEFAULT_SUBJECT = "Alert!";
    public static final String DEFAULT_BODY = "Alert!";

    public EmailAction() {
        super("Send E-Mail", PARAM_TO, PARAM_FROM, PARAM_SUBJECT, PARAM_BODY, PARAM_RETURN);
    }

    public boolean execute(
            Map<ParameterDescriptor, Parameter> parameters)
            throws Exception {
        String user = getConfigurationParameters().get("username");
        if (user == null) {
            throw new RuntimeException(
                    "Required configuration parameter 'username' is not defined.");
        }
        String password = getConfigurationParameters().get("password");
        if (password == null) {
            throw new RuntimeException(
                    "Required configuration parameter 'password' is not defined.");
        }
        Authenticator auth = new SimpleAuthenticator(
                new PasswordAuthentication(user, password));
        String host = getConfigurationParameters().get("host");
        if (host == null) {
            host = "localhost";
        }
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", user);
        props.put("mail.smtp.auth", "true");
        if (getConfigurationParameters().get("smtp.provider") != null) {
            props.put(
                    "mail.smtp.class",
                    getConfigurationParameters().get("smtp.provider"));
        }
        Session session = Session.getInstance(props, auth);

        String to = getStringValue(parameters, PARAM_TO);
        String from = getStringValue(parameters, PARAM_FROM, DEFAULT_FROM);
        String subject = getStringValue(parameters, PARAM_SUBJECT, DEFAULT_SUBJECT);
        String body = getStringValue(parameters, PARAM_BODY, DEFAULT_BODY);

        // create a message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(body);

        // send the message
        Transport.send(msg);

        return false;
    }
}