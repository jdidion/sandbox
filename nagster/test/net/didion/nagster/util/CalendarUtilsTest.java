/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.util;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarUtilsTest extends TestCase {
    public CalendarUtilsTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void testShiftToGMT() {
        Calendar cal = CalendarUtils.now(TimeZone.getTimeZone("GMT-8"));
        cal.set(Calendar.HOUR_OF_DAY, 12);
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));

        CalendarUtils.shiftToGMT(cal);
        assertEquals(20, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(CalendarUtils.GMT, cal.getTimeZone());
    }

    public void testFirstWeekInMonth() {
        Calendar now = CalendarUtils.now();
        now = CalendarUtils.at(now.get(Calendar.YEAR), now.get(Calendar.MONTH));
        Week week = CalendarUtils.firstWeekInMonth(now);
        assertEquals(now, week.getStartDay());
        now.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        assertEquals(now, week.getEndDay());
    }

    public void testNextWeek() {
        Week firstWeek = CalendarUtils.firstWeekInMonth(CalendarUtils.now());
        Week next = CalendarUtils.next(firstWeek, false);
        assertEquals(Calendar.SUNDAY, next.getStartDay().get(Calendar.DAY_OF_WEEK));
        assertEquals(Calendar.SATURDAY, next.getEndDay().get(Calendar.DAY_OF_WEEK));
        assertEquals(6, next.getEndDay().get(Calendar.DAY_OF_MONTH) - next.getStartDay().get(Calendar.DAY_OF_MONTH));
        assertEquals(firstWeek.getStartDay().get(Calendar.MONTH), next.getStartDay().get(Calendar.MONTH));
        assertEquals(firstWeek.getStartDay().get(Calendar.YEAR), next.getStartDay().get(Calendar.YEAR));
        assertEquals(firstWeek.getStartDay().get(Calendar.MONTH), next.getEndDay().get(Calendar.MONTH));
        assertEquals(firstWeek.getStartDay().get(Calendar.YEAR), next.getEndDay().get(Calendar.YEAR));
    }

    public void testLastWeekOfMonth() {
        Calendar feb05 = CalendarUtils.at(2005, Calendar.FEBRUARY); // feb 05's last week is the 27th-28th (sunday & monday)
        Week week = CalendarUtils.firstWeekInMonth(feb05);
        week = CalendarUtils.next(week, false);
        week = CalendarUtils.next(week, false);
        week = CalendarUtils.next(week, false);

        Week spanMonths = CalendarUtils.next(week, true);
        assertEquals(CalendarUtils.at(2005, Calendar.FEBRUARY, 27, 0, 0), spanMonths.getStartDay());
        assertEquals(CalendarUtils.at(2005, Calendar.MARCH, 5, 0, 0), spanMonths.getEndDay());

        Week dontSpanMonths = CalendarUtils.next(week, false);
        assertEquals(CalendarUtils.at(2005, Calendar.FEBRUARY, 27, 0, 0), dontSpanMonths.getStartDay());
        assertEquals(CalendarUtils.at(2005, Calendar.FEBRUARY, 28, 0, 0), dontSpanMonths.getEndDay());
    }

    private void assertEquals(Calendar c1, Calendar c2) {
        c1.get(Calendar.YEAR);
        c2.get(Calendar.YEAR);
        assertEquals(
                "not equal: " + CalendarUtils.format(c1) + " " + CalendarUtils.format(c2),
                c1, c2);
    }
}