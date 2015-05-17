/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.util.Collection;

public interface ApplicationManager {
    void addApplication(String name, String executable, String... params);
    Collection<Application> listApplications();
}