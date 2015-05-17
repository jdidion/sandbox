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
import net.didion.nagster.ParameterUtils;

import java.util.Map;
import java.util.HashMap;

public class BeepActionTest extends TestCase {
    public BeepActionTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void test() throws Exception {
        BeepAction beep = new BeepAction();
        Map<ParameterDescriptor, Parameter> params =
                new HashMap<ParameterDescriptor, Parameter>();
        params.put(ParameterUtils.PARAM_RETURN, new Parameter(ParameterUtils.PARAM_RETURN, "true"));
        assertTrue(beep.execute(params));
    }
}