/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.tapestry.page;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import net.didion.nagster.util.CalendarUtils;

public class IndexPageTest extends TestCase {
    public IndexPageTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void testInit() throws Exception {
        Calendar now = CalendarUtils.now();
        IndexPage page = new IndexPage(now);
        assertEquals(now.get(Calendar.MONTH), page.getMonth());
        assertEquals(now.get(Calendar.YEAR), page.getYear());
        assertEquals(CalendarUtils.format(now), page.getSearchDate());
        assertNull(page.getError());
    }

    public void testPrevious() {
        Calendar now = CalendarUtils.now();
        now = CalendarUtils.at(now.get(Calendar.YEAR), now.get(Calendar.MONTH));
        IndexPage page = new IndexPage(now);
        int previousMonth = page.getPreviousMonth();
        int previousYear = page.getPreviousMonthYear();
        Calendar expected = (Calendar) now.clone();
        expected.add(Calendar.MONTH, -1);
        assertEquals(expected.get(Calendar.MONTH), previousMonth);
        assertEquals(expected.get(Calendar.YEAR), previousYear);
        page.previousMonth(null);
        assertEquals(expected.get(Calendar.MONTH), page.getMonth());
        assertEquals(expected.get(Calendar.YEAR), page.getYear());
    }

    public void testNext() {
        Calendar now = CalendarUtils.now();
        now = CalendarUtils.at(now.get(Calendar.YEAR), now.get(Calendar.MONTH));
        IndexPage page = new IndexPage(now);
        int nextMonth = page.getNextMonth();
        int nextYear = page.getNextMonthYear();
        Calendar expected = (Calendar) now.clone();
        expected.add(Calendar.MONTH, 1);
        assertEquals(expected.get(Calendar.MONTH), nextMonth);
        assertEquals(expected.get(Calendar.YEAR), nextYear);
        page.nextMonth(null);
        assertEquals(expected.get(Calendar.MONTH), page.getMonth());
        assertEquals(expected.get(Calendar.YEAR), page.getYear());
    }

    public void testGetCalendarRows() {
        Calendar jan05 = CalendarUtils.at(2005, Calendar.JANUARY);
        IndexPage page = new IndexPage(jan05);
        Collection<IndexPage.CalendarRow> rows = page.getCalendarRows();
        assertEquals(6, rows.size());
        Iterator<IndexPage.CalendarRow> rowItr = rows.iterator();
        Collection<IndexPage.CalendarCell> cells = rowItr.next().getCells();
        assertEquals(7, cells.size());
        Iterator<IndexPage.CalendarCell> cellItr = cells.iterator();
        for (int i = 0; i < 6; i++) {
            assertNull(cellItr.next().getCalendar());
        }
        assertEquals(jan05, cellItr.next().getCalendar());
        for (int i = 0; i < 4; i++) {
            cells = rowItr.next().getCells();
            assertEquals(7, cells.size());
        }
        cells = rowItr.next().getCells();
        assertEquals(7, cells.size());
        cellItr = cells.iterator();
        assertEquals(CalendarUtils.at(2005, Calendar.JANUARY, 30, 0, 0), cellItr.next().getCalendar());
        assertEquals(CalendarUtils.at(2005, Calendar.JANUARY, 31, 0, 0), cellItr.next().getCalendar());
        for (int i = 0; i < 5; i++) {
            assertNull(cellItr.next().getCalendar());
        }
    }
}