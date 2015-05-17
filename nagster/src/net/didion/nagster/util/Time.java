/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.util;

import java.util.Formatter;
import java.io.Serializable;

public class Time implements Serializable, Cloneable {
    private int _hourOfDay;
    private int _minute;

    public Time(int hourOfDay, int minute) {
        _hourOfDay = hourOfDay;
        _minute = minute;
    }

    public int getHourOfDay() {
        return _hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        _hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return _minute;
    }

    public void setMinute(int minute) {
        _minute = minute;
    }

    public Object clone() {
        return new Time(_hourOfDay, _minute);
    }

    public String toString() {
        return (new Formatter().format("Time<%d:%d>", _hourOfDay, _minute).out()).toString();
    }
}