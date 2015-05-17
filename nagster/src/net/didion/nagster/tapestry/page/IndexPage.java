/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.tapestry.page;

import net.didion.nagster.Event;
import net.didion.nagster.Nagster;
import net.didion.nagster.tapestry.util.EnumSelectionModel;
import net.didion.nagster.util.CalendarUtils;
import net.didion.nagster.util.Week;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.IEngine;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.html.BasePage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.io.IOException;
import java.io.InputStream;

public class IndexPage extends BasePage {
    public static enum Month {
        JANUARY, FEBRARY, MARCH, APRIL, MAY, JUNE,
        JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER;
    }

    public class CalendarRow {
        private Week _week;
        private Collection<CalendarCell> _cells;

        public CalendarRow(Week week) {
            _week = week;
        }

        public Collection<CalendarCell> getCells() {
            if (_cells == null) {
                _cells = new ArrayList<CalendarCell>(7);
                int emptyDays =
                        _week.getStartDay().get(Calendar.DAY_OF_WEEK) -
                        Calendar.SUNDAY;
                for (int i = 0; i < emptyDays; i++) {
                    _cells.add(new CalendarCell(null));
                }
                for (Iterator<Calendar> itr = _week.dayIterator(); itr.hasNext();) {
                    _cells.add(new CalendarCell(itr.next()));
                }
                emptyDays =
                        Calendar.SATURDAY -
                        _week.getEndDay().get(Calendar.DAY_OF_WEEK);
                for (int i = 0; i < emptyDays; i++) {
                    _cells.add(new CalendarCell(null));
                }
            }
            return _cells;
        }
    }

    public class CalendarCell {
        private Calendar _calendar;

        public CalendarCell(Calendar calendar) {
            _calendar = calendar;
        }

        public Calendar getCalendar() {
            return _calendar;
        }

        public String getDate() {
            return (_calendar != null) ?
                    String.valueOf(_calendar.get(Calendar.DAY_OF_MONTH)) : "";
        }

        public Collection<Event> getEvents() {
            if (_calendar == null) {
                return Collections.EMPTY_LIST;
            }
            return Nagster.getStore().selectEventsByDate(_calendar);
        }
    }

    private Month _month;
    private int _year;
    private Collection<CalendarRow> _calendarRows;
    private String _error;
    private String _eventName;
    private String _resourceName;
    private IUploadFile _resourceFile;
    private String _applicationName;
    private String _applicationExecutable;
    private String _applicationParameters;

    public IndexPage() {
        this(CalendarUtils.now());
    }

    IndexPage(Calendar c) {
        setMonthYear(c);
    }

    public void attach(IEngine iEngine) {
        super.attach(iEngine);
        if (_month == null || _year == 0) {
            setMonthYear(CalendarUtils.now());
        }
    }

    public void detach() {
        super.detach();
        _month = null;
        _year = 0;
        _calendarRows = null;
        _error = null;
        _eventName = null;
        _resourceName = null;
        _resourceFile = null;
        _applicationName = null;
        _applicationExecutable = null;
        _applicationParameters = null;
    }

    private void setMonthYear(Calendar calendar) {
        setMonth(Month.values()[calendar.get(Calendar.MONTH)]);
        setYear(calendar.get(Calendar.YEAR));
    }

    public Month getMonth() {
        return _month;
    }

    public void setMonth(Month month) {
        _month = month;
        _calendarRows = null;
    }

    public IPropertySelectionModel getMonthModel() {
        return new EnumSelectionModel<Month>(Month.values());
    }

    public int getYear() {
        return _year;
    }

    public void setYear(int year) {
        _year = year;
        _calendarRows = null;
    }

    public int getCurrentYear() {
        return CalendarUtils.now().get(Calendar.YEAR);
    }

    public int getPreviousMonth() {
        return getPreviousMonthCalendar().get(Calendar.MONTH);
    }

    public int getPreviousMonthYear() {
        return getPreviousMonthCalendar().get(Calendar.YEAR);
    }

    private Calendar getPreviousMonthCalendar() {
        Calendar c = getCurrentMonthCalendar();
        c.add(Calendar.MONTH, -1);
        return c;
    }

    public int getNextMonth() {
        return getNextMonthCalendar().get(Calendar.MONTH);
    }

    public int getNextMonthYear() {
        return getNextMonthCalendar().get(Calendar.YEAR);
    }

    private Calendar getNextMonthCalendar() {
        Calendar c = getCurrentMonthCalendar();
        c.add(Calendar.MONTH, 1);
        return c;
    }

    public Collection<CalendarRow> getCalendarRows() {
        if (_calendarRows == null) {
            Calendar c = getCurrentMonthCalendar();
            _calendarRows = new ArrayList<CalendarRow>(
                    c.getActualMaximum(Calendar.WEEK_OF_MONTH));

            Week week = CalendarUtils.firstWeekInMonth(c);
            do {
                _calendarRows.add(new CalendarRow(week));
                week = CalendarUtils.next(week, false);
            } while (week != null);
        }
        return _calendarRows;
    }

    public void previousMonth(IRequestCycle cycle) {
        setMonthYear(getPreviousMonthCalendar());
    }

    public void nextMonth(IRequestCycle cycle) {
        setMonthYear(getNextMonthCalendar());
    }

    public String getError() {
        return _error;
    }

    public void setError(String error) {
        _error = error;
    }

    public boolean hasError() {
        return _error != null;
    }

    public String getEventName() {
        return _eventName;
    }

    public void setEventName(String eventName) {
        _eventName = eventName;
    }

    public void createEvent(IRequestCycle cycle) {
        EventPage page = (EventPage) cycle.getPage(Constants.Pages.EVENT.getName());
        page.setEventName(getEventName());
        cycle.activate(page);
    }

    public void editEvent(IRequestCycle cycle) {
        EventPage page = (EventPage) cycle.getPage(Constants.Pages.EVENT.getName());
        page.setEventId((Integer) cycle.getServiceParameters()[0]);
        cycle.activate(page);
    }

    private Calendar getCurrentMonthCalendar() {
        return CalendarUtils.at(_year, _month.ordinal());
    }

    public String getResourceName() {
        return _resourceName;
    }

    public void setResourceName(String resourceName) {
        _resourceName = resourceName;
    }

    public IUploadFile getResourceFile() {
        return _resourceFile;
    }

    public void setResourceFile(IUploadFile resourceFile) {
        _resourceFile = resourceFile;
    }

    public void addResource(IRequestCycle cycle) throws IOException {
        InputStream stream = null;
        try {
            stream = getResourceFile().getStream();
            Nagster.getResourceManager().addResource(
                    getResourceName(),
                    getResourceFile().getFileName(),
                    stream);
        } finally {
            if (stream != null) try { stream.close(); } catch (Exception ex) {}
            _resourceName = null;
            _resourceFile = null;
        }
    }

    public String getApplicationName() {
        return _applicationName;
    }

    public void setApplicationName(String applicationName) {
        _applicationName = applicationName;
    }

    public String getApplicationExecutable() {
        return _applicationExecutable;
    }

    public void setApplicationExecutable(String applicationExecutable) {
        _applicationExecutable = applicationExecutable;
    }

    public String getApplicationParameters() {
        return _applicationParameters;
    }

    public void setApplicationParameters(String applicationParameters) {
        _applicationParameters = applicationParameters;
    }

    public void addApplication(IRequestCycle cycle) {
        String[] params = (getApplicationParameters() == null) ?
                new String[0] : getApplicationParameters().split(",");
        Nagster.getApplicationManager().addApplication(
                getApplicationName(),
                getApplicationExecutable(),
                params);
        _applicationName = null;
        _applicationExecutable = null;
        _applicationParameters = null;        
    }
}