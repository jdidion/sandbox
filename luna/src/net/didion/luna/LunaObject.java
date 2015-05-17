/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import netscape.security.PrivilegeTable;

import java.util.*;

public class LunaObject {
    public static LunaObject createAnonymous() {
        return new LunaObject(null, null, null, null, null);
    }

    // attributes
    private ObjectLocator _id;
    private String _name;
    private String _description;
    private User _owner;
    private ObjectLocator _parentId;
    private Set<ObjectLocator> _childIds;

    // permissions
    private PermissionSet _canRead;
    private PermissionSet _canWrite;
    private PermissionSet _canInherit;
    private PermissionSet _canChangePermissions;

    // slots
    private Map<String, Slot> _slots;

    public LunaObject(
            ObjectLocator id, String name, String description, User owner) {
        this(id, name, description, owner, null);
    }

    public LunaObject(
            ObjectLocator id, String name, String description, User owner,
            ObjectLocator parentId) {
        // init attributes
        _id = id;
        _name = name;
        _description = description;
        _owner = owner;
        _parentId = parentId;
        if (_parentId != null) {
            getParent().addChild(_id);
        }
        _childIds = new HashSet<ObjectLocator>();

        // init permissions
        _canRead = new PermissionSet(new SimplePermission(true));
        _canWrite = new PermissionSet(new SimplePermission(true));
        _canInherit = new PermissionSet(new SimplePermission(true));
        _canChangePermissions = new PermissionSet(new SimplePermission(true));

        // init slots
        _slots = new HashMap<String, Slot>();
    }

    public ObjectLocator getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getDescription() {
        return _description;
    }

    public User getOwner() {
        return _owner;
    }

    public ObjectLocator getParentId() {
        return _parentId;
    }

    public LunaObject getParent() {
        if (getParentId() == null) {
            throw new ObjectNotFoundException();
        }
        return getParentId().getObject();
    }

    void addChild(ObjectLocator childId) {
        _childIds.add(childId);
    }

    public Set<ObjectLocator> getChildIds() {
        return Collections.unmodifiableSet(_childIds);
    }

    public boolean isReadableBy(User user) {
        return _canRead.grant(user);
    }

    public boolean isWritableBy(User user) {
        return _canWrite.grant(user);
    }

    public boolean isInheritableBy(User user) {
        return _canInherit.grant(user);
    }

    public boolean isPermissionsChangeableBy(User user) {
        return _canChangePermissions.grant(user);
    }

    public Slot createSlot(String name) {
        return createSlot(name, null);
    }

    public Slot createSlot(String name, String description) {
        Slot slot = new Slot(name, description, getOwner());
        _slots.put(slot.getName(), slot);
        return slot;
    }

    public Set<String> getSlotNames() {
        return Collections.unmodifiableSet(_slots.keySet());
    }

    public Value getSlotValue(String name) throws SlotNotFoundException {
        Slot slot = _slots.get(name);
        if (slot == null) {
            throw new SlotNotFoundException(name);
        }
        return slot.getValue();
    }
}