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

public class User {
    private String _name;
    private List<Group> _groups;

    public User(String name) {
        _name = name;
        _groups = new ArrayList<Group>();
    }

    public String getName() {
        return _name;
    }

    public List<Group> getGroups() {
        return _groups;
    }

    void addGroup(Group group) {
        _groups.add(group);
    }
}