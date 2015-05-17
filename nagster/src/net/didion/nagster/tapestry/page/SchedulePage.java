/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.tapestry.page;

import net.didion.nagster.Event;
import net.didion.nagster.Schedule;
import net.didion.nagster.Nagster;
import net.didion.nagster.tapestry.Visit;
import net.didion.nagster.util.CalendarUtils;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import static net.didion.nagster.tapestry.page.Constants.*;

import java.util.Date;

public class SchedulePage extends BasePage {
    private Integer _eventId;
    private Integer _scheduleId;
    private Schedule _schedule;
    private String _mode;

    public void detach() {
        super.detach();
        _eventId = null;
        _scheduleId = null;
        _schedule = null;
    }

    public void setMode(String mode) {
        _mode = mode;
    }

    public void setEventId(Integer eventId) {
        _eventId = eventId;
    }

    public Integer getEventId() {
        return _eventId;
    }

    public void setScheduleId(Integer scheduleId) {
        _scheduleId = scheduleId;
    }

    private Schedule getSchedule() {
        if (_schedule == null) {
            if (_scheduleId != null) {
                Event e = getEvent();
                _schedule = e.getSchedule(_scheduleId);
                if (_schedule == null) {
                    _schedule = ((Visit) getVisit()).getSchedule(_scheduleId);
                }
            }
            if (_schedule == null) {
                _schedule = ((Visit) getVisit()).createSchedule();
                _scheduleId = _schedule.getId();
            }
        }
        return _schedule;
    }

    public Integer getScheduleId() {
        return _scheduleId;
    }

    public Date getStartDate() {
        if (getSchedule().getStartDate() == null) {
            return CalendarUtils.toDate(CalendarUtils.now());
        }
        return CalendarUtils.toDate(getSchedule().getStartDate());
    }

    public void setStartDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("start date cannot be null");
        }
        getSchedule().setStartDate(CalendarUtils.toCalendar(date));
    }

    public Date getEndDate() {
        if (getSchedule().getEndDate() == null) {
            return null;
        }
        return CalendarUtils.toDate(getSchedule().getEndDate());
    }

    public void setEndDate(Date endDate) {
        if (endDate == null) {
            getSchedule().setEndDate(null);
        } else {
            getSchedule().setEndDate(CalendarUtils.toCalendar(endDate));
        }
    }

    public int getMaxOccurances() {
        return getSchedule().getMaxOccurances();
    }

    public void setMaxOccurances(int maxOccurances) {
        getSchedule().setMaxOccurances(maxOccurances);
    }

    public String getMinutes() {
        return getSchedule().formatField(Schedule.Field.MINUTE);
    }

    public void setMinutes(String minutes) {
        getSchedule().setFromString(Schedule.Field.MINUTE, minutes);
    }

    public String getHours() {
        return getSchedule().formatField(Schedule.Field.HOUR);
    }

    public void setHours(String hours) {
        getSchedule().setFromString(Schedule.Field.HOUR, hours);
    }

    public String getDays() {
        return getSchedule().formatField(Schedule.Field.DAY_OF_MONTH);
    }

    public void setDays(String days) {
        getSchedule().setFromString(Schedule.Field.DAY_OF_MONTH, days);
    }

    public String getMonths() {
        return getSchedule().formatField(Schedule.Field.MONTH);
    }

    public void setMonths(String months) {
        getSchedule().setFromString(Schedule.Field.MONTH, months);
    }

    public String getDaysOfWeek() {
        return getSchedule().formatField(Schedule.Field.DAY_OF_WEEK);
    }

    public void setDaysOfWeek(String daysOfWeek) {
        getSchedule().setFromString(Schedule.Field.DAY_OF_WEEK, daysOfWeek);
    }

    public String getYears() {
        return getSchedule().formatField(Schedule.Field.YEAR);
    }

    public void setYears(String years) {
        getSchedule().setFromString(Schedule.Field.YEAR, years);
    }

    public void updateSchedule(IRequestCycle cycle) {
        if (getEvent().getSchedule(getScheduleId()) == null) {
            getEvent().addSchedule(_schedule);
        }
        ((Visit) getVisit()).removeSchedule(_schedule);
        if (_mode == EDIT) {
            Nagster.getScheduler().scheduleEvent(getEvent());
        }
        EventPage page = (EventPage) cycle.getPage(Constants.Pages.EVENT.getName());
        page.setEventId(_eventId);
        cycle.activate(page);
    }

    private Event getEvent() {
        Event e = ((Visit) getVisit()).getEvent(_eventId);
        if (e == null) {
            e = Nagster.getStore().selectEventById(_eventId);
        }
        return e;
    }
}