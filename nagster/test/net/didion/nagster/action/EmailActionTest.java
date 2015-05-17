/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.action;

import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import net.didion.nagster.ParameterDescriptor;
import net.didion.nagster.Parameter;
import net.didion.nagster.util.NameValuePair;

public class EmailActionTest extends TestCase {
    public EmailActionTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void test() throws Exception {
        EmailAction email = new EmailAction();
        email.addConfigurationParameter(new NameValuePair("host", "foo.com"));
        email.addConfigurationParameter(new NameValuePair("username", "monkey"));
        email.addConfigurationParameter(new NameValuePair("password", "lover"));
        email.addConfigurationParameter(new NameValuePair("smtp.provider", "net.didion.nagster.action.MockTransport"));
        Map<ParameterDescriptor, Parameter> params =
                new HashMap<ParameterDescriptor, Parameter>();
        params.put(EmailAction.PARAM_TO, new Parameter(EmailAction.PARAM_TO, "my@ass.com"));
        email.execute(params);
        Collection<MockTransport.SentMessage> messages = MockTransport.getSentMessages();
        assertEquals(1, messages.size());
    }
}