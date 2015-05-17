/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.tapestry;

import net.didion.nagster.Event;
import net.didion.nagster.AlertImpl;
import net.didion.nagster.Schedule;

import java.util.Map;
import java.util.HashMap;

public class Visit {
    private Map<Integer, Event> _events = new HashMap<Integer, Event>();
    private Map<Integer, AlertImpl> _alerts = new HashMap<Integer, AlertImpl>();
    private Map<Integer, Schedule> _schedules = new HashMap<Integer, Schedule>();

    public Event createEvent(String name) {
        Event e = new Event(name);
        _events.put(e.getId(), e);
        return e;
    }

    public void removeEvent(Event event) {
        _events.remove(event.getId());
    }

    public Event getEvent(Integer eventId) {
        return _events.get(eventId);
    }

    public AlertImpl createAlert() {
        AlertImpl a = new AlertImpl();
        _alerts.put(a.getId(), a);
        return a;
    }

    public void removeAlert(AlertImpl alert) {
        _alerts.remove(alert.getId());
    }

    public AlertImpl getAlert(Integer alertId)  {
        return _alerts.get(alertId);
    }

    public Schedule createSchedule() {
        Schedule s = new Schedule();
        _schedules.put(s.getId(), s);
        return s;
    }

    public void removeSchedule(Schedule schedule) {
        _schedules.remove(schedule.getId());
    }

    public Schedule getSchedule(Integer scheduleId) {
        return _schedules.get(scheduleId);
    }
}