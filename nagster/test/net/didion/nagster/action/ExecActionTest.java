/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.action;

import junit.framework.TestCase;
import net.didion.nagster.Parameter;
import net.didion.nagster.ParameterDescriptor;
import net.didion.nagster.Application;

import java.util.Map;
import java.util.HashMap;

public class ExecActionTest extends TestCase {
    public ExecActionTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void test() throws Exception {
        ExecAction action = new ExecAction();
        Map<ParameterDescriptor, Parameter> params =
                new HashMap<ParameterDescriptor, Parameter>();
        Application app = new Application("google", "\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\"");
        app.addParameter(new Parameter(ParameterDescriptor.createValueDescriptor("site", true, false), "www.google.com"));
        params.put(ExecAction.PARAM_APPLICATION, new Parameter(ExecAction.PARAM_APPLICATION, app));
        action.execute(params);
    }
}