/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class CalendarUtils {
    public static final String DEFAULT_DATE_FORMAT = "MM/dd/yyyy";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String DEFAULT_DATE_TIME_FORMAT =
            DEFAULT_DATE_FORMAT + " " + DEFAULT_TIME_FORMAT;
    private static final int[] DEFAULT_KEEP_FIELDS = new int[] {
                Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH,
                Calendar.HOUR, Calendar.MINUTE
            };

    public static Calendar createEmptyCalendar() {
        Calendar c = Calendar.getInstance();
        c.set(1, 0, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    public static Calendar now() {
        return keep(Calendar.getInstance());
    }

    public static String nowString() {
        return format(now());
    }

    public static Calendar toCalendar(Date date) {
        Calendar c = createEmptyCalendar();
        c.setTimeInMillis(date.getTime());
        return keep(c);
    }

    public static Date toDate(Calendar cal) {
        return new Date(cal.getTimeInMillis());
    }

    public static Calendar at(int year, int month) {
        return at(year, month, 1, 0, 0);
    }

    public static Calendar at(int year, int month, int day, int hour, int minute) {
        Calendar cal = createEmptyCalendar();
        cal.set(year, month, day, hour, minute);
        return cal;
    }

    public static Calendar keep(Calendar source, int... fields) {
        Calendar c = createEmptyCalendar();
        if (fields.length == 0) {
            fields = DEFAULT_KEEP_FIELDS;
        }
        for (int field : fields) {
            c.set(field, source.get(field));
        }
        return c;
    }

    public static Calendar max(Calendar c1, Calendar c2) {
        return c1.compareTo(c2) >= 0 ? c1 : c2;
    }

    public static Calendar min(Calendar c1, Calendar c2) {
        return c1.compareTo(c2) <= 0 ? c1 : c2;
    }

    public static DateFormat createFormat(String format) {
        return new SimpleDateFormat(format);
    }

    public static String format(Calendar cal) {
        return createFormat(DEFAULT_DATE_TIME_FORMAT).format(cal.getTime());
    }

    public static String formatDate(Calendar cal) {
        return createFormat(DEFAULT_DATE_FORMAT).format(cal.getTime());
    }

    public static String formatTime(Calendar cal) {
        return createFormat(DEFAULT_TIME_FORMAT).format(cal.getTime());
    }

    public static Calendar parseDate(String dateString) throws ParseException {
        Date d = DateFormat.getDateInstance().parse(dateString);
        return toCalendar(d);
    }

    public static Week firstWeekInMonth(Calendar monthYear) {
        Calendar start = at(monthYear.get(Calendar.YEAR), monthYear.get(Calendar.MONTH));
        Calendar end = clone(start);
        end.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        return new Week(start, end);
    }

    public static Week next(Week week, boolean spanMonths) {
        Calendar start = week.getEndDay();
        if (!spanMonths &&
                start.getActualMaximum(Calendar.DAY_OF_MONTH) == start.get(Calendar.DAY_OF_MONTH)) {
            return null;
        }
        start = clone(start);
        start.add(Calendar.DAY_OF_MONTH, 1);

        Calendar end = clone(start);
        end.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        if (!spanMonths && start.get(Calendar.MONTH) != end.get(Calendar.MONTH)) {
            end = clone(start);
            end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        return new Week(start, end);
    }

    public static boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
               c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
               c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    public static Calendar clone(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.get(Calendar.YEAR);
        return c;
    }

    public static boolean onOrAfter(Calendar d1, Calendar d2) {
        return d1.equals(d2) || d1.after(d2);
    }

    public static boolean onOrBefore(Calendar d1, Calendar d2) {
        return d1.equals(d2) || d1.before(d2);
    }

    public static Time getTime() {
        return getTime(now());
    }

    public static Time getTime(Calendar cal) {
        return new Time(
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE));
    }

    public static void at(Time time) {
        at(now(), time);
    }

    public static void at(Calendar cal, Time time) {
        cal.set(Calendar.HOUR_OF_DAY, time.getHourOfDay());
        cal.set(Calendar.MINUTE, time.getMinute());
        cal.get(Calendar.MINUTE);
    }

    private CalendarUtils() {
    }

    /**
    public static String format(Calendar cal, Locale locale) {
        return format(
                cal, cal.getTimeZone(), locale,
                DateFormat.DEFAULT, DateFormat.DEFAULT);
    }

    public static String format(
            Calendar cal, TimeZone timeZone, Locale locale,
            int dateStyle, int timeStyle) {
        return createFormat(dateStyle, timeStyle, locale, timeZone).format(cal.getTime());
    }
    */

    /*public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    public static void shiftTo(Calendar cal, TimeZone tz) {
        cal.setTimeZone(tz);
    }

    public static void shiftToGMT(Calendar cal) {
        shiftTo(cal, GMT);
    }

    public static Calendar now() {
        return now(GMT);
    }

    public static Calendar now(TimeZone tz) {
        return Calendar.getInstance(tz);
    }*/

    /*public static enum DayOfWeek {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

        public static DayOfWeek forOrdinal(int i) {
            for (DayOfWeek dow : DayOfWeek.values()) {
                if (dow.ordinal() == i) {
                    return dow;
                }
            }
            return null;
        }
    }*/

    /*public static DayOfWeek dayOfWeek(Calendar cal) {
        return DayOfWeek.forOrdinal(cal.get(Calendar.DAY_OF_WEEK) - 1);
    }*/

    /*public static enum Month {
        JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE,
        JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER;

        public static Month forOrdinal(int i) {
            for (Month month : Month.values()) {
                if (month.ordinal() == i) {
                    return month;
                }
            }
            return null;
        }
    }*/

    /*public static Month month(Calendar cal) {
        return Month.forOrdinal(cal.get(Calendar.MONTH));
    }*/

    /*public static Calendar createCalendar(boolean withTime) {
        Calendar now = now();
        if (withTime) {
            return now;
        } else {
            Calendar cal = createEmptyCalendar();
            copyDateFields(cal, now);
            return cal;
        }
    }*/

    /*public static void future(int years, int months, int days) {
        future(years, months, days, 0, 0, 0);
    }*/

    /*public static void future(Calendar cal, int years, int months, int days) {
        future(cal, years, months, days, 0, 0, 0);
    }*/
    
    /*public static void future(
            int years, int months, int days,
            int hours, int minutes, int seconds) {
        Calendar cal = createCalendar(true);
        future(cal, years, months, days, hours, minutes, seconds);
    }*/
    
    /*public static void future(
            Calendar cal,
            int years, int months, int days,
            int hours, int minutes, int seconds) {
        cal.add(Calendar.YEAR, years);
        cal.add(Calendar.MONTH, months);
        cal.add(Calendar.DATE, days);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        cal.add(Calendar.MINUTE, minutes);
        cal.add(Calendar.SECOND, seconds);
    }*/

    /*public static Calendar dateOnly(Calendar src) {
        Calendar cal = createEmptyCalendar();
        copyDateFields(cal, src);
        return cal;
    }*/

    /*public static void setMin(Calendar cal, int... fields) {
        for (int field : fields) {
            cal.set(field, cal.getMinimum(field));
        }
    }*/

    /*public static void setMax(Calendar cal, int... fields) {
        for (int field : fields) {
            cal.set(field, cal.getMaximum(field));
        }
    }*/

    /*public static void lastDayOf(Calendar c, int field) {
        c.add(field, 1);
        c.add(Calendar.DATE, -1);
    }*/

    /*public static boolean between(
            Calendar c,
            Calendar start, boolean startInclusive,
            Calendar end, boolean endInclusive) {
        int startComparison = (start != null) ? c.compareTo(start) : 1;
        int endComparison = (end != null) ? c.compareTo(end) : -1;
        return ((startInclusive) ? startComparison >= 0 : startComparison > 0) &&
               ((endInclusive) ? endComparison <= 0 : endComparison < 0);
    }*/

    /*public static boolean isWeekday(Calendar cal) {
        return !isWeekendDay(cal);
    }

    public static boolean isWeekendDay(Calendar cal) {
        int dow = cal.get(Calendar.DAY_OF_WEEK);
        return dow == Calendar.SATURDAY || dow == Calendar.SUNDAY;
    }

    public static void firstWeekday(Calendar c, int field) {
        setMin(c, field);
        nextWeekday(c, false);
    }

    public static void firstWeekendDay(Calendar c, int field) {
        setMin(c, field);
        nextWeekendDay(c, false);
    }

    public static void lastWeekday(Calendar c, int field) {
        lastDayOf(c, field);
        nextWeekday(c, false);
    }

    public static void lastWeekendDay(Calendar c, int field) {
        lastDayOf(c, field);
        nextWeekendDay(c, false);
    }

    public static void nextWeekday(Calendar c) {
        nextWeekday(c, true);
    }

    public static void nextWeekday(Calendar c, boolean forward) {
        while (!isWeekday(c)) {
            c.add(Calendar.DATE, (forward) ? 1 : -1);
        }
    }

    public static void nextWeekendDay(Calendar c) {
        nextWeekendDay(c, true);
    }

    public static void nextWeekendDay(Calendar c, boolean forward) {
        while (!isWeekendDay(c)) {
            c.add(Calendar.DATE, (forward) ? 1 : -1);
        }
    }*/

    /*public static void next(
            Calendar c, int testField, int testFor, int incrementField) {
        while (c.get(testField) != testFor) {
            c.add(incrementField, 1);
        }
    }*/

    /*public static int difference(Calendar c1, Calendar c2, int field) {
        if (c1.equals(c2)) {
            return 0;
        }
        if (c2.before(c1)) {
            Calendar temp = c1;
            c1 = c2;
            c2 = temp;
        }
        c1 = clone(c1);
        int diff = 0;
        for (;;) {
            c1.add(field, 1);
            if (c1.compareTo(c2) <= 0) {
                diff++;
            } else {
                break;
            }
        }
        return diff;
    }*/

    /*private static void copyDateFields(Calendar dest, Calendar src) {
        copyField(dest, src, Calendar.YEAR);
        copyField(dest, src, Calendar.MONTH);
        copyField(dest, src, Calendar.DATE);
    }*/

    /*private static void copyField(Calendar dest, Calendar src, int field) {
        dest.set(field, src.get(field));
    }*/
}