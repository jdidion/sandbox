/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import junit.framework.TestCase;

public class AlertGroupTest extends TestCase {
    public AlertGroupTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void testAll() throws Exception {
        AlertGroup group = new AlertGroup(false);
        group.addAlert(new AlertImpl(new ActionDescriptor("foo")));
        group.addAlert(new AlertImpl(new ActionDescriptor("bar")));
        MockNagster client = new MockNagster();
        assertTrue(group.execute(client));
        assertEquals(2, client.getFired().size());
        assertTrue(client.getFired().contains("foo"));
        assertTrue(client.getFired().contains("bar"));
    }

    public void testFirstSuccessfulOnly1() throws Exception {
        AlertGroup group = new AlertGroup(true);
        group.addAlert(new AlertImpl(new ActionDescriptor("foo")));
        group.addAlert(new AlertImpl(new ActionDescriptor("bar")));
        MockNagster client = new MockNagster();
        assertTrue(group.execute(client));
        assertEquals(1, client.getFired().size());
        assertTrue(client.getFired().contains("foo"));
    }

    public void testFirstSuccessfulOnly2() throws Exception {
        AlertGroup group = new AlertGroup(true);
        group.addAlert(new AlertImpl(new ActionDescriptor("foo")));
        group.addAlert(new AlertImpl(new ActionDescriptor("bar")));
        MockNagster client = new MockNagster("bar");
        assertTrue(group.execute(client));
        assertEquals(1, client.getFired().size());
        assertTrue(client.getFired().contains("bar"));
    }
}