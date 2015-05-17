/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.tapestry.page;

public interface Constants {
    String ATTR_EVENT_NAME = "eventName";

    enum Pages {
        INDEX, EVENT, SCHEDULE, ALERT;

        public String getName() {
            return name().toLowerCase();
        }
    }

    // modes
    String NEW = "new";
    String EDIT = "edit";
}