/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import junit.framework.TestCase;

public class ObjectGUIDTest extends TestCase {
    public ObjectGUIDTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void testToString() {
        ObjectLocator id = new ObjectLocator("localhost", "test", 0);
        assertEquals(ObjectLocator.LUNA_PROTOCOL + "://localhost/test/0", id.toString());
    }
}