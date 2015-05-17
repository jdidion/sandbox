/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.action;

import junit.framework.TestCase;

import javax.mail.Session;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import java.util.Properties;
import java.util.Date;

import net.didion.nagster.util.SimpleAuthenticator;

public class EmailTest extends TestCase {
    public EmailTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void test() throws Exception {
        Authenticator auth = new SimpleAuthenticator(
                new PasswordAuthentication("john", "fat.dog"));
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.didion.net");
        props.put("mail.smtp.user", "john");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(props, auth);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("john@didion.net"));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress("johndidion@yahoo.com"));
        msg.setSubject("testing");
        msg.setSentDate(new Date());
        msg.setText("hi");

        // send the message
        Transport.send(msg);
    }
}