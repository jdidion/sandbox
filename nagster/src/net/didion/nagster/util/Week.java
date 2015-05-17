/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.util;

import java.util.Calendar;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Week {
    private class CalendarIterator implements Iterator<Calendar> {
        private Calendar _current = CalendarUtils.clone(_startDay);

        public boolean hasNext() {
            return !_current.after(_endDay);
        }

        public Calendar next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Calendar retVal = _current;
            _current = CalendarUtils.clone(_current);
            _current.add(Calendar.DATE, 1);
            return retVal;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private Calendar _startDay;
    private Calendar _endDay;

    public Week(Calendar startDay, Calendar endDay) {
        _startDay = startDay;
        _endDay = endDay;
    }

    public Calendar getStartDay() {
        return _startDay;
    }

    public Calendar getEndDay() {
        return _endDay;
    }

    public int days() {
        int startDay = _startDay.get(Calendar.DAY_OF_MONTH);
        int endDay = _endDay.get(Calendar.DAY_OF_MONTH);
        return (startDay <= endDay) ?
                endDay - startDay + 1 :
                _startDay.getActualMaximum(Calendar.DAY_OF_MONTH) - startDay + 1 + endDay;
    }

    public Iterator<Calendar> dayIterator() {
        return new CalendarIterator();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("Week<").
                append(CalendarUtils.format(getStartDay())).
                append(" - ").
                append(CalendarUtils.format(getEndDay())).
                append(">");
        return buf.toString();
    }
}