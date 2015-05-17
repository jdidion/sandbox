/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import static net.didion.nagster.Schedule.Field.*;
import junit.framework.TestCase;
import java.util.Calendar;

import net.didion.nagster.util.CalendarUtils;

public class ScheduleTest extends TestCase {
    private Schedule _schedule;

    public ScheduleTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        _schedule = new Schedule();
        _schedule.setStartDate(CalendarUtils.at(2005, 0, 1, 0, 0));
        _schedule.setEndDate(CalendarUtils.at(2008, 11, 31, 23, 59));
        _schedule.setMaxOccurances(Schedule.NO_LIMIT);
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void testSetNone() {
        assertTrue(_schedule.get(MINUTE, 1));
    }

    public void testSet() {
        _schedule.set(MINUTE, 1, 3, 5);
        //_schedule.complete();
        validateField(MINUTE, CalendarUtils.now(), 1, 3, 5);
    }

    public void testSetAll() {
        _schedule.setAll(MINUTE);
        assertEquals(60, _schedule.getCount(MINUTE, CalendarUtils.now()));
        assertTrue(_schedule.getLast(MINUTE));
    }

    public void testSetFieldsFromCalendar() {
        Calendar cal = CalendarUtils.at(2006, 0, 1, 0, 0);
        _schedule.setFields(cal);
        //_schedule.complete();
        validateField(YEAR, cal, 2006);
        validateField(MONTH, cal, 0);
        validateField(DAY_OF_MONTH, cal, 1);
        validateField(HOUR, cal, 0);
        validateField(MINUTE, cal, 0);
    }

    public void testSetRange() {
        _schedule.setRange(MINUTE, 1, 3, 1);
        //_schedule.complete();
        validateField(MINUTE, CalendarUtils.now(), 1, 2, 3);
    }

    public void testSetRangeWithInterval() {
        _schedule.setRange(MINUTE, 1, 5, 2);
        //_schedule.complete();
        validateField(MINUTE, CalendarUtils.now(), 1, 3, 5);
    }

    public void testSetOddRangeWithInterval() {
        _schedule.setRange(MINUTE, 1, 6, 2);
        //_schedule.complete();
        validateField(MINUTE, CalendarUtils.now(), 1, 3, 5);
    }

    public void testSetWithAndOddInterval() {
        _schedule.setRange(MINUTE, 1, 5, 3);
        //_schedule.complete();
        validateField(MINUTE, CalendarUtils.now(), 1, 4);
    }

    public void testSetInvalidRange() {
        try {
            _schedule.setRange(MINUTE, 5, 1, 1);
            fail("expected exception");
        } catch (Exception ex) {
            // expected
        }
    }

    public void testNextScheduledTimeSingleDate() {
        doTestNextScheduledTime(
                "0 0 1 0 * 2006", CalendarUtils.at(2006, 0, 1, 0, 0));
    }

    public void testNextScheduledTimeAll() {
        doTestNextScheduledTime(
                "* * * * * *",
                CalendarUtils.at(2005, 0, 1, 0, 1),
                CalendarUtils.at(2005, 0, 1, 0, 2));
    }

    public void testNextScheduledTimeAllWithInterval() {
        doTestNextScheduledTime(
                "*/12 * * * * *",
                CalendarUtils.at(2005, 0, 1, 0, 12),
                CalendarUtils.at(2005, 0, 1, 0, 24));
    }

    public void testNextScheduledTimeRange() {
        doTestNextScheduledTime(
                "2-4 * * * * *",
                CalendarUtils.at(2005, 0, 1, 0, 2),
                CalendarUtils.at(2005, 0, 1, 0, 3),
                CalendarUtils.at(2005, 0, 1, 0, 4),
                CalendarUtils.at(2005, 0, 1, 1, 2));
    }

    public void testNextScheduledTimeRangeWithInterval() {
        doTestNextScheduledTime(
                "2-6/2 * * * * *",
                CalendarUtils.at(2005, 0, 1, 0, 2),
                CalendarUtils.at(2005, 0, 1, 0, 4),
                CalendarUtils.at(2005, 0, 1, 0, 6),
                CalendarUtils.at(2005, 0, 1, 1, 2));
    }

    public void testNextScheduledTimeValues() {
        doTestNextScheduledTime(
                "2,4,6 * * * * *",
                CalendarUtils.at(2005, 0, 1, 0, 2),
                CalendarUtils.at(2005, 0, 1, 0, 4),
                CalendarUtils.at(2005, 0, 1, 0, 6),
                CalendarUtils.at(2005, 0, 1, 1, 2));
    }

    public void testNextScheduledTimeLast() {
        doTestNextScheduledTime(
                "0 0 $ * * *", // 12 AM, last day of the month
                CalendarUtils.at(2005, 0, 31, 0, 0),
                CalendarUtils.at(2005, 1, 28, 0, 0));
    }

    public void testNextScheduledTimeDayOfWeek() {
        doTestNextScheduledTime(
                "0 0 * * 2-6 *", // 12 am every weekday
                CalendarUtils.at(2005, 0, 3, 0, 0),
                CalendarUtils.at(2005, 0, 4, 0, 0),
                CalendarUtils.at(2005, 0, 5, 0, 0),
                CalendarUtils.at(2005, 0, 6, 0, 0),
                CalendarUtils.at(2005, 0, 7, 0, 0),
                CalendarUtils.at(2005, 0, 10, 0, 0));
    }

    public void testNextScheduledTimeComplex() {
        doTestNextScheduledTime(
                "0 9 1-7 * 2 *", // first monday of each month, 9AM
                CalendarUtils.at(2005, 0, 3, 9, 0),
                CalendarUtils.at(2005, 1, 7, 9, 0));
    }

    public void testAfterEndDate() {
        doTestNextScheduledTime("0 0 1 0 * 2009", (Calendar) null);
    }

    public void testLeapYear() {
        doTestNextScheduledTime(
                "0 0 29 1 * *",
                CalendarUtils.at(2005, 2, 1, 0, 0),
                CalendarUtils.at(2006, 2, 1, 0, 0),
                CalendarUtils.at(2007, 2, 1, 0, 0),
                CalendarUtils.at(2008, 1, 29, 0, 0));
    }

    public void testSummaryString() {
        _schedule.setFromString("0 1-7 3,14,25 2,5-11 * *");
        assertEquals("0 1-7 3,14,25 2,5-11 * *", _schedule.getSummaryString());
    }

    private void doTestNextScheduledTime(String format, Calendar... expected) {
        _schedule.setFromString(format);
        Calendar test = CalendarUtils.at(2005, 0, 1, 0, 0);
        for (Calendar c : expected) {
            assertEquals(
                    "Next scheduled time does not match expected.",
                    c, _schedule.nextScheduledTime(test));
            test = c;
        }
    }

    private void validateField(Schedule.Field field, Calendar c, int... values) {
        assertEquals(values.length, _schedule.getCount(field, c));
        for (int i = 0; i < values.length; i++) {
            assertTrue(_schedule.get(field, values[i]));
        }
    }

    private void assertEquals(String msg, Calendar c1, Calendar c2) {
        // make sure the calendars have been completed
        if (c1 != null) {
            c1.get(Calendar.SECOND);
        }
        if (c2 != null) {
            c2.get(Calendar.SECOND);
        }
        if (c1 == null && c2 == null) {
            return;
        }
        if (c1 == null || !c1.equals(c2)) {
            fail(msg + " expected <" + CalendarUtils.format(c1) + "> but was <" +
                    CalendarUtils.format(c2) + ">");
        }
    }

    /*
    private void assertEquals(int[] a1, int[] a2) {
        if (a1 == null || a2 == null) {
            super.assertEquals(a1, a2);
        } else {
            assertEquals("array lengths are different", a1.length, a2.length);
            for (int i = 0; i < a1.length; i++) {
                assertEquals("element " + i + "'s are different", a1[i], a2[i]);
            }
        }
    }
     */
}