/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.store.prevayler;

import net.didion.nagster.Event;

import java.util.Calendar;

public class EventDateFilter implements Filter {
    private Calendar _calendar;

    public EventDateFilter(Calendar calendar) {
        _calendar = calendar;
    }

    public boolean accept(Object obj) {
        return ((Event) obj).occursOn(_calendar);
    }
}