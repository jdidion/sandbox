/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

public class UserExistsException extends Exception {
    public UserExistsException(String userName) {
        super("User " + userName + " already exists.");
    }
}