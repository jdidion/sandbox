/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

public class GroupExistsException extends Exception {
    public GroupExistsException(String groupName) {
        super("Group " + groupName + " already exists.");
    }
}