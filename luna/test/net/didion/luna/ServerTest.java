/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import junit.framework.TestCase;

public class ServerTest extends TestCase {
    private RootEnvironment _server;

    public ServerTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        _server = RootEnvironment.init("test");
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void testInit() {
        assertEquals("test", _server.getDomain());
    }
}