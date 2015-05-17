/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.action;

import javax.mail.Transport;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.MessagingException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MockTransport extends Transport {
    public static class SentMessage {
        private Message _message;
        private Address[] _addresses;

        public SentMessage(Message message, Address[] addresses) {
            _message = message;
            _addresses = addresses;
        }

        public Message getMessage() {
            return _message;
        }

        public Address[] getAddresses() {
            return _addresses;
        }
    }

    private static ThreadLocal<List<SentMessage>> _sentMessages =
            new ThreadLocal<List<SentMessage>>() {
                public List<SentMessage> initialValue() {
                    return new ArrayList<SentMessage>();
                }
            };

    public static Collection<SentMessage> getSentMessages() {
        List<SentMessage> messages = _sentMessages.get();
        return Collections.unmodifiableCollection(messages);
    }

    public MockTransport(Session session, URLName urlName) {
        super(session, urlName);
    }

    public void connect(String host, int port, String user, String password)
            throws MessagingException {        
    }

    public void sendMessage(Message message, Address[] addresses)
            throws MessagingException {
        List<SentMessage> messages = _sentMessages.get();
        messages.add(new SentMessage(message, addresses));    
    }
}