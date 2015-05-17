/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import junit.framework.TestSuite;

public class TestAll {
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(ServerTest.class);
        suite.addTestSuite(EnvironmentTest.class);
        suite.addTestSuite(ObjectTest.class);
        suite.addTestSuite(ObjectGUIDTest.class);
        suite.addTestSuite(SlotTest.class);
        suite.addTestSuite(ValueTest.class);
        return suite;
    }
}