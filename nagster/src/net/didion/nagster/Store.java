/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.util.Calendar;
import java.util.Collection;

public interface Store {
    Integer nextId(Class c);

    void insertEvent(Event event);
    void deleteEvent(Event event);
    Collection<Event> selectAllEvents();
    Collection<Event> selectEventsByDate(Calendar cal);
    Event selectEventById(Integer id);
    boolean containsEvent(Event e);

    void insertResource(Resource resource);
    void deleteResource(Resource resource);
    Collection<Resource> selectAllResources();
    Resource selectResourceById(Integer id);

    void insertApplication(Application application);
    void deleteApplication(Application application);
    Collection<Application> selectAllApplications();
    Application selectApplicationById(Integer id);
}