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

public class ValueTest extends TestCase {
    public ValueTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void testPrimitiveValue() throws Exception {
        PrimitiveValue v = new PrimitiveValue("test");
        LunaObject obj = v.getObject();
        assertNull(obj.getId());
        assertNull(obj.getName());
        assertNull(obj.getDescription());
        assertNull(obj.getOwner());
        assertNull(obj.getParentId());
        assertEquals(1, obj.getSlotNames().size());
        assertEquals(Value.VALUE_SLOT_NAME, obj.getSlotNames().iterator().next());
        assertEquals("test", obj.getSlotValue(Value.VALUE_SLOT_NAME).getString());

        Value execVal = v.execute();
        assertTrue(execVal instanceof PrimitiveValue);
        assertEquals("test", execVal.getString());
    }

    public void testStringValue() {
        PrimitiveValue v = new PrimitiveValue("test");
        assertEquals("test", v.getString());
        assertEquals(BigDecimal.ZERO, v.getNumber());
        assertEquals(true, v.getBoolean());
    }

    public void testNumberValue() {
        PrimitiveValue v = new PrimitiveValue("1.0");
        assertEquals("1.0", v.getString());
        assertEquals(new BigDecimal("1.0"), v.getNumber());
        assertEquals(true, v.getBoolean());
    }

    public void testBooleanValue() {
        PrimitiveValue v = new PrimitiveValue("");
        assertEquals("", v.getString());
        assertEquals(BigDecimal.ZERO, v.getNumber());
        assertEquals(false, v.getBoolean());

        PrimitiveValue v1 = new PrimitiveValue("false");
        assertEquals("false", v1.getString());
        assertEquals(BigDecimal.ZERO, v1.getNumber());
        assertEquals(true, v1.getBoolean());
    }

    public void testObjectValue() throws Exception {
        RootEnvironment s = RootEnvironment.init("test");
        Environment env = s.createEnvironment("test");
        LunaObject obj = env.createObject("test", env.getRootUser());

        ObjectValue v = new ObjectValue(obj.getId());
        assertEquals(obj.getId().toString(), v.getString());
        assertEquals(new BigDecimal(obj.getId().getObjectId()), v.getNumber());
        assertEquals(true, v.getBoolean());
        assertEquals(obj, v.getObject());
        assertEquals(obj, v.execute().getObject());
    }

    public void testMethodValue() throws Exception {
        Method m = new Method();
        MethodValue v = new MethodValue(m);
    }
}