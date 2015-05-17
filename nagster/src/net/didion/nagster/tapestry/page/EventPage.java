/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.tapestry.page;

import net.didion.nagster.AlertGroup;
import net.didion.nagster.Event;
import net.didion.nagster.Nagster;
import net.didion.nagster.tapestry.Visit;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import static net.didion.nagster.tapestry.page.Constants.*;

public class EventPage extends BasePage {

    private Event _event;
    private String _alertGroupName;
    private boolean _firstSuccessfulOnly = false;
    private String _mode;

    public void detach() {
        super.detach();
        _event = null;
        _alertGroupName = null;
        _firstSuccessfulOnly = false;
    }

    public String getEventName() {
        return _event.getName();
    }

    public void setEventName(String eventName) {
        _mode = NEW;
        _event = ((Visit) getVisit()).createEvent(eventName);
    }

    public void setEventId(Integer eventId) {
        _mode = EDIT;
        _event = ((Visit) getVisit()).getEvent(eventId);
        if (_event == null) {
            _event = Nagster.getStore().selectEventById(eventId);
        }
    }

    public Integer getEventId() {
        return _event.getId();
    }

    public Event getEvent() {
        return _event;
    }

    public void createSchedule(IRequestCycle cycle) {
        Integer eventId = (Integer) cycle.getServiceParameters()[0];
        SchedulePage schedulePage = (SchedulePage)
                cycle.getPage(Constants.Pages.SCHEDULE.getName());
        schedulePage.setEventId(eventId);
        schedulePage.setMode(_mode);
        cycle.activate(schedulePage);
    }

    public void editSchedule(IRequestCycle cycle) {
        Integer eventId = (Integer) cycle.getServiceParameters()[0];
        Integer scheduleId = (Integer) cycle.getServiceParameters()[1];
        SchedulePage schedulePage = (SchedulePage)
                cycle.getPage(Constants.Pages.SCHEDULE.getName());
        schedulePage.setEventId(eventId);
        schedulePage.setScheduleId(scheduleId);
        schedulePage.setMode(_mode);
        cycle.activate(schedulePage);
    }

    public void deleteSchedule(IRequestCycle cycle) {
        Integer eventId = (Integer) cycle.getServiceParameters()[0];
        Integer scheduleId = (Integer) cycle.getServiceParameters()[1];
        setEventId(eventId);
        getEvent().deleteSchedule(scheduleId);
        if (_mode == EDIT) {
            Nagster.getScheduler().scheduleEvent(getEvent());
        }
    }

    public void createAlert(IRequestCycle cycle) {
        Integer eventId = (Integer) cycle.getServiceParameters()[0];
        Integer alertGroupId = (Integer) cycle.getServiceParameters()[1];
        AlertPage alertPage = (AlertPage)
                cycle.getPage(Constants.Pages.ALERT.getName());
        alertPage.setEventId(eventId);
        alertPage.setAlertGroupId(alertGroupId);
        cycle.activate(alertPage);
    }

    public void editAlert(IRequestCycle cycle) {
        Integer eventId = (Integer) cycle.getServiceParameters()[0];
        Integer alertGroupId = (Integer) cycle.getServiceParameters()[1];
        Integer alertId = (Integer) cycle.getServiceParameters()[2];
        AlertPage alertPage = (AlertPage)
                cycle.getPage(Constants.Pages.ALERT.getName());
        alertPage.setEventId(eventId);
        alertPage.setAlertGroupId(alertGroupId);
        alertPage.setAlertId(alertId);
        cycle.activate(alertPage);
    }

    public void deleteAlert(IRequestCycle cycle) {
        Integer eventId = (Integer) cycle.getServiceParameters()[0];
        Integer alertGroupId = (Integer) cycle.getServiceParameters()[1];
        Integer alertId = (Integer) cycle.getServiceParameters()[2];
        setEventId(eventId);
        getEvent().getAlertGroup(alertGroupId).deleteAlert(alertId);
    }

    public String getAlertGroupName() {
        return "";
    }

    public void setAlertGroupName(String alertGroupName) {
        _alertGroupName = alertGroupName;
    }

    public boolean isFirstSuccessfulOnly() {
        return false;
    }

    public void setFirstSuccessfulOnly(boolean firstSuccessfulOnly) {
        _firstSuccessfulOnly = firstSuccessfulOnly;
    }

    public void addAlertGroup(IRequestCycle cycle) {
        getEvent().addAlertGroup(
                new AlertGroup(_alertGroupName, _firstSuccessfulOnly));
    }

    public void deleteAlertGroup(IRequestCycle cycle) {
        Integer eventId = (Integer) cycle.getServiceParameters()[0];
        Integer alertGroupId = (Integer) cycle.getServiceParameters()[1];
        setEventId(eventId);
        getEvent().deleteAlertGroup(alertGroupId);
    }

    public void scheduleEvent(IRequestCycle cycle) {
        getEvent().validate();
        Nagster.getScheduler().scheduleEvent(getEvent());
        ((Visit) getVisit()).removeEvent(getEvent());
        cycle.activate(Constants.Pages.INDEX.getName());
    }

    public void cancel(IRequestCycle cycle) {
        ((Visit) getVisit()).removeEvent(getEvent());
        cycle.activate(Constants.Pages.INDEX.getName());
    }
}