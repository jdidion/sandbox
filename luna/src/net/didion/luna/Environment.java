/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import java.util.*;

public class Environment {
    private String _name;
    private List<String> _path;
    private long _nextId;
    private Map<String, User> _users;
    private Map<String, Group> _groups;
    private Map<String, Environment> _environments;
    private Map<Long, LunaObject> _objects;

    public Environment(String name, List<String> parentPath) {
        _name = name;
        _path = new ArrayList<String>();
        _path.addAll(parentPath);
        _path.add(name);
        _nextId = (parentPath.size() == 0) ? 0 : 1;
        _groups = new HashMap<String, Group>();
        _users = new HashMap<String, User>();
        _environments = new HashMap<String, Environment>();
        _objects = new HashMap<Long, LunaObject>();
    }

    public String getName() {
        return _name;
    }

    public List<String> getPath() {
        return _path;
    }

    public LunaObject createObject(String name, User owner)
            throws NotInheritableException {
        return createObject(name, null, owner);
    }

    public LunaObject createObject(String name, String description, User owner)
            throws NotInheritableException {
        return createObject(
                name, description, owner,
                RootEnvironment.getInstance().getRootObject().getId());
    }

    public LunaObject createObject(String name, User owner, ObjectLocator parentId)
            throws NotInheritableException {
        return createObject(name, null, owner, parentId);
    }

    public LunaObject createObject(
            String name, String description, User owner, ObjectLocator parentId)
            throws NotInheritableException {
        if (parentId != null && !parentId.getObject().isInheritableBy(owner)) {
            throw new NotInheritableException();
        }
        LunaObject obj = new LunaObject(
                nextGUID(), name, description, owner, parentId);
        _objects.put(obj.getId().getObjectId(), obj);
        return obj;
    }

    public LunaObject getObject(long id) {
        return _objects.get(id);
    }

    private synchronized ObjectLocator nextGUID() {
        long objectId = _nextId++;
        return new ObjectLocator(getPath(), objectId);
    }

    public User createUser(String name) throws UserExistsException {
        if (_users.containsKey(name)) {
            throw new UserExistsException(name);
        }
        User user = new User(name);
        _users.put(user.getName(), user);
        return user;
    }

    public User createUser(String name, Group... userGroups)
            throws UserExistsException {
        User user = createUser(name);
        for(Group group : userGroups) {
            group.addUser(user);
        }
        return user;
    }

    public Group createGroup(String name) throws GroupExistsException {
        if (_groups.containsKey(name)) {
            throw new GroupExistsException(name);
        }
        Group group = new Group(name);
        _groups.put(group.getName(), group);
        return group;
    }

    public Environment createEnvironment(String name)
            throws EnvironmentExistsException {
        if (_environments.containsKey(name)) {
            throw new EnvironmentExistsException(name);
        }
        Environment env = new Environment(name, getPath());
        _environments.put(name, env);
        return env;
    }

    public Environment resolveEnvironment(Iterator<String> envItr) {
        String envName = envItr.next();
        Environment env = _environments.get(envName);
        if (env == null || !envItr.hasNext()) {
            return env;
        }
        return env.resolveEnvironment(envItr);
    }
}