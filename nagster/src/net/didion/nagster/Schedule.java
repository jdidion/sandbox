/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import net.didion.nagster.util.CalendarUtils;
import net.didion.nagster.util.Time;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.StringTokenizer;

public class Schedule implements Serializable {
    public static final int NO_LIMIT = 0;
    public static final int FIRST = -1;
    public static final int LAST = Integer.MAX_VALUE;

    private static final String CHAR_FIRST = "^";
    private static final String CHAR_LAST = "$";
    private static final String CHAR_ALL = "*";
    private static final String CHAR_INTERVAL_SEP = "/";
    private static final String CHAR_RANGE_SEP = "-";
    private static final String CHAR_VALUE_SEP = ",";

    public static enum Field {
        MINUTE(Calendar.MINUTE, 0, 59),
        HOUR(Calendar.HOUR_OF_DAY, 0, 23),
        DAY_OF_MONTH(Calendar.DAY_OF_MONTH, 1, 31),
        MONTH(Calendar.MONTH, 0, 11),
        DAY_OF_WEEK(Calendar.DAY_OF_WEEK, 1, 7),
        YEAR(Calendar.YEAR, 1970, 2069);

        public static Field nextCalendarField(Field f) {
            int index = f.ordinal() + 1;
            Field[] calendarFields = getCalendarFields().toArray(new Field[0]);
            return (calendarFields.length > index) ? calendarFields[index] : null;
        }

        public static EnumSet<Field> getCalendarFields() {
            return EnumSet.of(MINUTE, HOUR, DAY_OF_MONTH, MONTH, YEAR);
        }

        public static EnumSet<Field> getCalendarFieldRange(Field to, boolean inclusive) {
            EnumSet<Field> e = EnumSet.range(MINUTE, to);
            e.remove(DAY_OF_WEEK);
            if (!inclusive) {
                e.remove(to);
            }
            return e;
        }

        private int _calendarField;
        private int _minValue;
        private int _maxValue;

        Field(int calendarField, int minValue, int maxValue) {
            _calendarField = calendarField;
            _minValue = minValue;
            _maxValue = maxValue;
        }

        public int getCalendarField() {
            return _calendarField;
        }

        public int getMinValue() {
            return _minValue;
        }

        public int getMaxValue() {
            return _maxValue;
        }

        public int getNumValues() {
            return getMaxValue() - getMinValue() + 1;
        }

        public boolean isInRange(int value) {
            return value >= getMinValue() && value <= getMaxValue();
        }

        public int normalize(int value) {
            return value - getMinValue();
        }

        public int denormalize(int value) {
            return value + getMinValue();
        }
    }

    private int _id;
    private Calendar _startDate;
    private Time _startTime;
    private Calendar _endDate;
    private int _maxOccurances;
    private EnumMap<Field, BitSet> _fields;
    private Calendar _lastOccurance;

    public Schedule() {
        this(CalendarUtils.now(), null);
    }

    public Schedule(String schedule) {
        this(CalendarUtils.now(), schedule);
    }

    public Schedule(Calendar start) {
        this(start, null);
    }

    public Schedule(Calendar start, String schedule) {
        _id = Nagster.nextId(Schedule.class);
        setStartDate(start);
        setMaxOccurances(1);
        _fields = new EnumMap<Field, BitSet>(Field.class);
        for (Field f : Field.values()) {
            _fields.put(f, new BitSet(f.getNumValues() + 1));
        }
        if (schedule != null) {
            setFromString(schedule);
        }
    }

    public int getId() {
        return _id;
    }

    public Calendar getStartDate() {
        return _startDate;
    }

    public String getStartDateAsString() {
        return CalendarUtils.formatDate(getStartDate());
    }

    public void setStartDate(Calendar startDate) {
        _startDate = CalendarUtils.
                keep(startDate, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
        _lastOccurance = null;
    }

    public Time getStartTime() {
        return _startTime;
    }

    public void setStartTime(Time startTime) {
        _startTime = startTime;
    }

    public Calendar getStartDateTime() {
        if (getStartDate() == null) {
            return null;
        }
        Time startTime = getStartTime();
        if (startTime == null) {
            startTime = CalendarUtils.getTime();
            setStartTime(startTime);
        }
        Calendar startDate = CalendarUtils.clone(getStartDate());
        CalendarUtils.at(startDate, startTime);
        return startDate;
    }

    public Calendar getEndDate() {
        return _endDate;
    }

    public void setEndDate(Calendar endDate) {
        _endDate = endDate;
        _lastOccurance = null;
    }

    public int getMaxOccurances() {
        return _maxOccurances;
    }

    public void setMaxOccurances(int maxOccurances) {
        _maxOccurances = maxOccurances;
        _lastOccurance = null;
    }

    public void set(Field field, int... values) {
        set(field, true, values);
    }

    public void set(Field field, boolean enabled, int... values) {
        for (int value : values) {
            if (value == FIRST) {
                setFirst(field, enabled);
            } else if (value == LAST) {
                setLast(field, enabled);
            } else {
                if (!field.isInRange(value)) {
                    throw new IllegalArgumentException(
                            "Value " + value + " is not in range for field " + field);
                }
                get(field).set(field.normalize(value), enabled);
            }
        }
        checkAll(field);
        _lastOccurance = null;
    }

    public void setRange(Field field, int start, int end, int interval) {
        setRange(field, start, end, interval, true);
    }

    public void setRange(Field field, int start, int end, int interval, boolean enabled) {
        if (start == FIRST) {
            start = field.getMinValue();
        }
        if (end == LAST) {
            end = field.getMaxValue();
            setLast(field, enabled);
        }
        if (end < start) {
            throw new IllegalArgumentException(
                    "Ending value of range is less than starting value.");
        }
        if (interval < 1) {
            throw new IllegalArgumentException("Invalid interval: " + interval);
        }
        for (int i = start; i <= end; i += interval) {
            if (!field.isInRange(i)) {
                throw new IllegalArgumentException(
                        "Value " + i + " is not in range for field " + field);
            }
            get(field).set(field.normalize(i), enabled);
        }
        checkAll(field);
        _lastOccurance = null;
    }

    public void setAll(Field field) {
        BitSet bs = get(field);
        bs.set(0, field.getNumValues()+1, false);
        _lastOccurance = null;
    }

    public void setFields(Calendar c) {
        for (Field f : Field.getCalendarFields()) {
            set(f, c.get(f.getCalendarField()));
        }
    }

    public void setFromString(String schedule) {
        StringTokenizer tok = new StringTokenizer(schedule, " \t");
        if (tok.countTokens() != 6) {
            throw new RuntimeException("invalid schedule: " + schedule);
        }

        EnumSet<Schedule.Field> fields = EnumSet.allOf(Schedule.Field.class);
        for (Schedule.Field f : fields) {
            setFromString(f, tok.nextToken());
        }
    }

    public void setFromString(Field field, String val) {
        setAll(field);
        if (val == null) {
            return;
        }
        String[] parts = val.split(CHAR_VALUE_SEP);
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.equals(CHAR_FIRST)) {
                setFirst(field);
            } else if (part.equals(CHAR_LAST)) {
                setLast(field);
            } else {
                String[] s = part.split(CHAR_INTERVAL_SEP);
                part = s[0];
                int interval = 1;
                if (s.length > 1) {
                    interval = Integer.parseInt(s[1]);
                }
                if (part.equals(CHAR_ALL)) {
                    setRange(field, field.getMinValue(), field.getMaxValue(), interval);
                } else if (part.indexOf(CHAR_RANGE_SEP) > 0) {
                    int[] range = parseRange(part);
                    setRange(field, range[0], range[1], interval);
                } else {
                    try {
                        set(field, Integer.parseInt(part));
                    } catch (Exception ex) {
                        throw new RuntimeException("invalid field value: " + val, ex);
                    }
                }
            }
        }
    }

    private static int[] parseRange(String range) {
        String[] strArry = range.split(CHAR_RANGE_SEP);
        int[] intArry = new int[strArry.length];
        for (int i = 0; i < strArry.length; i++) {
            String s = strArry[i];
            if (s.equals(CHAR_FIRST)) {
                intArry[i] = FIRST;
            } else if (s.equals(CHAR_LAST)) {
                intArry[i] = LAST;
            } else {
                intArry[i] = Integer.parseInt(s);
            }
        }
        return intArry;
    }

    public void setFirst(Field field) {
        setFirst(field, true);
    }

    public void setFirst(Field field, boolean enabled) {
        get(field).set(0, enabled);
        checkAll(field);
        _lastOccurance = null;
    }

    public boolean getFirst(Field field) {
        if (get(field).cardinality() == 0) {
            return true;
        }
        return get(field).get(0);
    }

    public void setLast(Field field) {
        setLast(field, true);
    }

    public void setLast(Field field, boolean enabled) {
        get(field).set(field.getNumValues(), enabled);
        checkAll(field);
        _lastOccurance = null;
    }

    public boolean getLast(Field field){
        if (get(field).cardinality() == 0) {
            return true;
        }
        return get(field).get(field.getNumValues());
    }

    public boolean get(Field field, int index) {
        if (get(field).cardinality() == 0) {
            return true;
        }
        return get(field).get(field.normalize(index));
    }

    public int getCount(Field field, Calendar timestamp) {
        int count = get(field).cardinality();
        if (count == 0) {
            return field.getNumValues();
        }
        // don't double count the last field (relative to the current date/time)
        if (getLast(field) && get(field, field.normalize(
                                timestamp.getActualMaximum(field.getCalendarField())))) {
            count--;
        }
        return count;
    }

    private BitSet get(Field field) {
        return _fields.get(field);
    }

    private void checkAll(Field field) {
        if (get(field).cardinality() >= field.getNumValues()) {
            setAll(field);
        }
    }

    public Calendar nextScheduledTime(Calendar test) {
        if (notNullAndAfter(test, getEndDate())) {
            return null;
        }
        Calendar startDateTime = getStartDateTime();
        Calendar start = (startDateTime != null) ?
                CalendarUtils.max(test, startDateTime) : test;
        if (getLastScheduledTime() != null && getLastScheduledTime().before(start)) {
            return null;
        }
        return nextScheduledTime0(start);
    }

    public Calendar getLastScheduledTime() {
        if (_maxOccurances == 0) {
            return null;
        }
        if (_lastOccurance == null) {
            Calendar start = getStartDateTime();
            Calendar c = CalendarUtils.clone(start);
            Calendar end = getEndDate();
            Calendar previous = null;
            for (int occurances = 0;
                 (end == null || c.before(end)) && occurances < _maxOccurances;
                 occurances++) {
                if (!c.equals(start)) {
                    previous = c;
                }
                c = nextScheduledTime0(c);
            }
            if (c.after(end)) {
                c = previous;
            }
            _lastOccurance = c;
        }
        return _lastOccurance;
    }

    private Calendar nextScheduledTime0(Calendar start) {
        Calendar end = getEndDate();

        Calendar c = CalendarUtils.clone(start);
        c.add(Calendar.MINUTE, 1);

        for (Field f : Field.getCalendarFields()) {
            roll(f, c);
            if (notNullAndAfter(c, end)) {
                return null;
            }
        }

        int curDayOfWeek = currentValue(Field.DAY_OF_WEEK, c);
        int nextDayOfWeek = nextValue(Field.DAY_OF_WEEK, c);
        if (curDayOfWeek != nextDayOfWeek) {
            // go to the end of the day and try again
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.HOUR, 23);
            return nextScheduledTime0(c);
        }

        return c;
    }

    /**
     * Generates a summary string in cron format.
     * @return
     */
    public String getSummaryString() {
        StringBuffer buf = new StringBuffer();
        for (Field f : Field.values()) {
            if (buf.length() != 0) {
                buf.append(' ');
            }
            formatField(f, buf);
        }
        return buf.toString();
    }

    public String formatField(Field f) {
        StringBuffer buf = new StringBuffer();
        formatField(f, buf);
        return buf.toString();
    }

    private void formatField(Field f, StringBuffer buf) {
        BitSet bs = get(f);
        if (bs.cardinality() == 0) {
            buf.append('*');
        }
        int length = buf.length();
        for (int i = 0; i < f.normalize(f.getMaxValue());) {
            int start = i;
            if (bs.get(start)) {
                int end = start;
                while (bs.get(++end)) {
                ;
                }
                if (end == start + 1) {
                    if (buf.length() > length) {
                        buf.append(',');
                    }
                    buf.append(f.denormalize(start));
                } else if (start == 0 && end == f.getNumValues()) {
                    buf.append('*');
                } else {
                    if (buf.length() > length) {
                        buf.append(',');
                    }
                    buf.append(f.denormalize(start)).append('-').append(f.denormalize(end-1));
                }
                i = end + 1;
            } else {
                i++;
            }
        }
    }

    private boolean notNullAndAfter(Calendar c1, Calendar c2) {
        return c2 != null && c1.after(c2);
    }

    private void roll(Field f, Calendar c) {
        int curValue = currentValue(f, c);
        int nextValue = nextValue(f, c);
        if (curValue != nextValue) {
            resetPrevious(f, c);
            if (nextValue == -1) {
                nextValue = firstValue(f, c);
                Field next = Field.nextCalendarField(f);
                if (next != null) {
                    c.add(next.getCalendarField(), 1);
                }
            }
        }
        c.set(f.getCalendarField(), nextValue);
    }

    private void resetPrevious(Field f, Calendar c) {
        EnumSet<Field> e = Field.getCalendarFieldRange(f, false);
        for (Field field : e) {
            c.set(field.getCalendarField(), firstValue(field, c));
        }
    }

    private int currentValue(Field f, Calendar c) {
        return c.get(f.getCalendarField());
    }

    private int firstValue(Field f, Calendar c) {
        BitSet bs = get(f);
        int nextBit = (bs.cardinality() == 0) ? 0 : bs.nextSetBit(0);
        return resolveValue(nextBit, f, c);
    }

    private int nextValue(Field f, Calendar c) {
        BitSet bs = get(f);
        int currentBit = f.normalize(currentValue(f, c));
        int nextBit = (bs.cardinality() == 0) ?
                currentBit : bs.nextSetBit(currentBit);
        return resolveValue(nextBit, f, c);
    }

    private int resolveValue(int index, Field f, Calendar c) {
        if (index < 0) {
            return -1;
        }
        int firstValue = (index == f.getNumValues()) ?
            c.getActualMaximum(f.getCalendarField()) : f.denormalize(index);
        return Math.min(firstValue, f.getMaxValue());
    }
}