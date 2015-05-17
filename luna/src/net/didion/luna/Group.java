/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import netscape.security.PrivilegeTable;

import java.util.List;
import java.util.ArrayList;

public class Group {
    private String _name;
    private List<User> _users;

    public Group(String name) {
        _name = name;
        _users = new ArrayList<User>();
    }

    public String getName() {
        return _name;
    }

    public List<User> getUsers() {
        return _users;
    }

    public void addUser(User user) {
        if (!_users.contains(user)) {
            _users.add(user);
            user.addGroup(this);
        }
    }

    public boolean contains(User user) {
        return _users.contains(user);
    }
}