/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

public class SlotNotFoundException extends Exception {
    public SlotNotFoundException(String name) {
        super("Slot " + name + " could not be found.");
    }
}