/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import junit.framework.TestCase;

import java.math.BigDecimal;

public class ObjectTest extends TestCase {
    private Environment _env;
    private LunaObject _obj;

    public ObjectTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        RootEnvironment server = RootEnvironment.init("test");
        _env = server.createEnvironment("test");
        _obj = _env.createObject("test", "test", _env.getRootUser());
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void testCreate() {
        assertEquals("test", _obj.getName());
        assertEquals("test", _obj.getDescription());
        assertEquals(_env.getRootObject().getId(), _obj.getParentId());
        assertEquals(_env.getRootObject(), _obj.getParent());
        assertEquals(_env.getRootUser(), _obj.getOwner());
    }

    public void testCreateSlot() {
        Slot slot = _obj.createSlot("test", "test");
        assertEquals("test", slot.getName());
        assertEquals("test", slot.getDescription());
        assertEquals(_env.getRootUser(), slot.getOwner());
    }
}