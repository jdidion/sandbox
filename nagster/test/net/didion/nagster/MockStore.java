/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.util.Collection;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

public class MockStore implements Store {
    private List<Event> _events = new ArrayList();

    public Integer nextId(Class c) {
        throw new UnsupportedOperationException();
    }

    public void insertEvent(Event event) {
        _events.add(event);
    }

    public void deleteEvent(Event event) {
        throw new UnsupportedOperationException();
    }

    public Collection<Event> selectAllEvents() {
        return _events;
    }

    public Collection<Event> selectEventsByDate(Calendar cal) {
        throw new UnsupportedOperationException();
    }

    public Event selectEventById(Integer id) {
        throw new UnsupportedOperationException();
    }

    public boolean containsEvent(Event e) {
        return _events.contains(e);
    }

    public void insertResource(Resource resource) {
        throw new UnsupportedOperationException();
    }

    public void deleteResource(Resource resource) {
        throw new UnsupportedOperationException();
    }

    public Collection<Resource> selectAllResources() {
        throw new UnsupportedOperationException();
    }

    public Resource selectResourceById(Integer id) {
        throw new UnsupportedOperationException();
    }

    public void insertApplication(Application application) {
        throw new UnsupportedOperationException();
    }

    public void deleteApplication(Application application) {
        throw new UnsupportedOperationException();
    }

    public Collection<Application> selectAllApplications() {
        throw new UnsupportedOperationException();
    }

    public Application selectApplicationById(Integer id) {
        throw new UnsupportedOperationException();
    }
}