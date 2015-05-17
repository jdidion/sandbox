/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class RootEnvironment extends Environment {
    public static final String ROOT_USER_NAME = "root";
    public static final String ROOT_GROUP_NAME = "root";
    public static final String ROOT_OBJECT_NAME = "object";
    public static final String ROOT_OBJECT_DESCRIPTION = "the root object";

    private static RootEnvironment _instance;

    public static RootEnvironment init(String domain) {
        if (_instance != null) {
            throw new RuntimeException("RootEnvironment already initialized.");
        }
        _instance = new RootEnvironment(domain);
        return _instance;
    }

    public static RootEnvironment getInstance() {
        return _instance;
    }

    private final User _rootUser;
    private final Group _rootGroup;
    private final LunaObject _rootObject;

    private RootEnvironment(String domain) {
        super(domain, new ArrayList<String>());

        try {
            _rootGroup = createGroup(ROOT_GROUP_NAME);
        } catch (GroupExistsException ex) {
            throw new RuntimeException(ex);
        }

        try {
            _rootUser = createUser(ROOT_USER_NAME, _rootGroup);
        } catch (UserExistsException ex) {
            throw new RuntimeException(ex);
        }

        try {
            _rootObject = createObject(
                    ROOT_OBJECT_NAME, ROOT_OBJECT_DESCRIPTION, getRootUser(), null);
        } catch (NotInheritableException ex) {
            throw new RuntimeException(ex);
        }
    }

    public User getRootUser() {
        return _rootUser;
    }

    public Group getRootGroup() {
        return _rootGroup;
    }

    public LunaObject getRootObject() {
        return _rootObject;
    }
}