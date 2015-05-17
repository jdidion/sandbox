/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

public class EnvironmentExistsException extends Exception {
    public EnvironmentExistsException(String name) {
        super("Environment " + name + " already exists.");
    }
}