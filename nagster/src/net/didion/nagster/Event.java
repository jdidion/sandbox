/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import net.didion.nagster.util.CalendarUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Event extends AlertGroup implements Identity, Serializable {
    private Integer _id;
    private List<Schedule> _schedules;
    private transient Calendar _cachedNext;

    public Event(String name) {
        super(name);
        _id = Nagster.nextId(Event.class);
        _schedules = new ArrayList<Schedule>();
    }

    public Integer getId() {
        return _id;
    }

    public void addSchedule(Schedule schedule) {
        _schedules.add(schedule);
    }

    public Collection<Schedule> getSchedules() {
        return Collections.unmodifiableCollection(_schedules);
    }

    public Schedule getSchedule(Integer id) {
        for (Schedule s : _schedules) {
            if (id.equals(s.getId())) {
                return s;
            }
        }
        return null;
    }

    public synchronized void deleteSchedule(Integer id) {
        Schedule s = getSchedule(id);
        if (s != null) {
            _schedules.remove(s);
            _cachedNext = null;
        }
    }

    public void addAlert(Alert alert) {
        if (alert instanceof AlertGroup) {
            super.addAlert(alert);
        } else {
            throw new IllegalArgumentException("Only alert groups are allowed");
        }
    }

    public void addAlertGroup(AlertGroup group) {
        addAlert(group);
    }

    public Collection<AlertGroup> getAlertGroups() {
        List<AlertGroup> l = new ArrayList<AlertGroup>();
        for (Alert a : getAlerts()) {
            l.add((AlertGroup) a);
        }
        return l;
    }

    public AlertGroup getAlertGroup(Integer id) {
        for (AlertGroup a : getAlertGroups()) {
            if (id.equals(a.getId())) {
                return a;
            }
        }
        return null;
    }

    public void deleteAlertGroup(Integer id) {
        deleteAlert(id);
    }

    public void validate() {
        if (getSchedules().size() == 0) {
            throw new RuntimeException("at least one schedule must be defined");
        }
        if (getAlertGroups().size() == 0) {
            throw new RuntimeException("at least one alert group must be defined");
        }
        for (AlertGroup group : getAlertGroups()) {
            if (group.getAlerts().size() == 0) {
                throw new RuntimeException("at least one alert must be defined for each alert group");
            }
        }
    }

    public long delay(TimeUnit unit) {
        return delay(CalendarUtils.now(), unit);
    }

    public synchronized long delay(Calendar from, TimeUnit unit) {
        if (_cachedNext == null || CalendarUtils.onOrBefore(_cachedNext, from)) {
            _cachedNext = nextScheduledTime(from);
        }
        if (_cachedNext == null) {
            return -1;
        }
        long delayMillis = _cachedNext.getTimeInMillis() - from.getTimeInMillis();
        return unit.convert(delayMillis, TimeUnit.MILLISECONDS);
    }

    public Calendar nextScheduledTime(Calendar from) {
        Calendar next = null;
        for (Schedule s : _schedules) {
            Calendar c = s.nextScheduledTime(from);
            if (next == null || (c != null && c.before(next))) {
                next = c;
            }
        }
        return next;
    }

    public boolean occursOn(Calendar calendar) {
        Calendar test = CalendarUtils.keep(
                calendar, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
        Calendar next = nextScheduledTime(test);
        return next != null && (CalendarUtils.isSameDay(test, next));
    }
}