/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import junit.framework.TestCase;

public class SlotTest extends TestCase {
    private Environment _env;
    private Slot _slot;

    public SlotTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        RootEnvironment server = RootEnvironment.init("test");
        _env = server.createEnvironment("test");
        LunaObject obj = _env.createObject("test", "test", _env.getRootUser());
        _slot = obj.createSlot("test", "test");
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void testCreate() {
        assertEquals("test", _slot.getName());
        assertEquals("test", _slot.getDescription());
        assertEquals(_env.getRootUser(), _slot.getOwner());
    }

    public void testValue() {
        Value v = new PrimitiveValue("test");
        _slot.setValue(v);
        assertEquals(v, _slot.getValue());
    }
}