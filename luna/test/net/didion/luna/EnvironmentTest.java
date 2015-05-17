/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import junit.framework.TestCase;

public class EnvironmentTest extends TestCase {
    private Environment _env;

    public EnvironmentTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        RootEnvironment server = RootEnvironment.init("localhost");
        _env = server.createEnvironment("test");
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void testCreateEnvironment() {
        assertEquals("test", _env.getName());
    }

    public void testRootUser() {
        User rootUser = _env.getRootUser();
        assertNotNull(rootUser);
        assertEquals(Environment.ROOT_USER_NAME, rootUser.getName());
        assertEquals(1, rootUser.getGroups().size());
        assertEquals(_env.getRootGroup(), rootUser.getGroups().get(0));
    }

    public void testCreateUser() throws Exception {
        User user = _env.createUser("foo");
        assertNotNull(user);
        assertEquals("foo", user.getName());
    }

    public void testCreateDuplicateUserFails() throws Exception {
        _env.createUser("foo");
        try {
            _env.createUser("foo");
            fail("expected exception");
        } catch (UserExistsException ex) {
            // expected
        }
    }

    public void testRootGroup() {
        Group rootGroup = _env.getRootGroup();
        assertNotNull(rootGroup);
        assertEquals(Environment.ROOT_GROUP_NAME, rootGroup.getName());
        assertEquals(1, rootGroup.getUsers().size());
        assertEquals(_env.getRootUser(), rootGroup.getUsers().get(0));
    }

    public void testCreateGroup() throws Exception {
        Group group = _env.createGroup("foo");
        assertNotNull(group);
        assertEquals("foo", group.getName());
    }

    public void testCreateDuplicateGroupFails() throws Exception {
        _env.createGroup("foo");
        try {
            _env.createGroup("foo");
            fail("expected exception");
        } catch (GroupExistsException ex) {
            // expected
        }
    }

    public void testRootObject() {
        LunaObject rootObject = _env.getRootObject();
        assertNotNull(rootObject);
        assertEquals(Environment.FIRST_OBJECT_ID, rootObject.getId().getObjectId());
        assertEquals(Environment.ROOT_OBJECT_NAME, rootObject.getName());
        assertEquals(Environment.ROOT_OBJECT_DESCRIPTION, rootObject.getDescription());
    }

    public void testCreateObject() throws Exception {
        Group group = _env.createGroup("test");
        User user = _env.createUser("test", group);
        LunaObject obj1 = _env.createObject("test", "A test object.", user);
        assertNotNull(obj1);
        assertTrue(obj1.getId().getObjectId() > 0);
        assertEquals("test", obj1.getName());
        assertEquals("A test object.", obj1.getDescription());
        assertEquals(0, obj1.getParentId().getObjectId());
        assertEquals(_env.getRootObject(), obj1.getParent());
        assertEquals(user, obj1.getOwner());

        LunaObject obj2 = _env.createObject("test2", "Another test object.", user);
        assertNotNull(obj2);
        assertTrue(obj2.getId().getObjectId() > 0);
        assertEquals(obj1.getId().getObjectId() + 1, (long) obj2.getId().getObjectId());
        assertEquals("test2", obj2.getName());
        assertEquals("Another test object.", obj2.getDescription());
        assertEquals(0, obj2.getParentId().getObjectId());
        assertEquals(_env.getRootObject(), obj2.getParent());
        assertEquals(user, obj2.getOwner());
    }

    public void testGetObject() throws Exception {
        Group group = _env.createGroup("test");
        User user = _env.createUser("test", group);
        LunaObject obj = _env.createObject("test", "A test object.", user);
        LunaObject got = _env.getObject(obj.getId().getObjectId());
        assertEquals(obj, got);
    }

    public void testCreateChildObject() throws Exception {
        Group group = _env.createGroup("test");
        User user = _env.createUser("test", group);
        LunaObject parent = _env.createObject("test", "test object", user);
        LunaObject child = _env.createObject("test2", "another test object", user, parent.getId());
        assertNotNull(child);
        assertEquals(parent.getId(), child.getParentId());
        assertEquals(parent, child.getParent());
        assertEquals(1, parent.getChildIds().size());
        assertEquals(child.getId(), parent.getChildIds().iterator().next());
    }

    public void testInheritablePermission() throws Exception {
        Group group = _env.createGroup("test");
        User user = _env.createUser("test", group);
        LunaObject parent = _env.createObject("test", "test object", user);
        LunaObject child = _env.createObject("test2", "another test object", user, parent.getId());
    }
}