/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.scheduler;

import net.didion.nagster.util.BlockingQueueDecorator;
import net.didion.nagster.util.CalendarUtils;
import net.didion.nagster.util.Adapter;

import java.util.concurrent.*;
import java.util.Calendar;

public class EventExecutor extends ScheduledThreadPoolExecutor {
    private static class EventNode implements Delayed {
        static EventNode create(Event e) {
            Calendar next = e.nextScheduledTime(CalendarUtils.now());
            if (e == null) {
                return null;
            }
            return new EventNode(e, next);
        }

        private Event _event;
        private Calendar _nextTime;

        private EventNode(Event event, Calendar nextTime) {
            _event = event;
            _nextTime = nextTime;
        }

        public Event getEvent() {
            return _event;
        }

        public long getDelay(TimeUnit unit) {
            Calendar now = CalendarUtils.now();
            long delayMillis =
                    _nextTime.getTimeInMillis() - now.getTimeInMillis();
            return unit.convert(delayMillis, TimeUnit.MILLISECONDS);
        }

        public int compareTo(Delayed delayed) {
            return _nextTime.compareTo(((EventNode) delayed)._nextTime);
        }

        public boolean equals(Object obj) {
            return obj instanceof EventNode && _event.equals(((EventNode) obj)._event);
        }
    }

    private static class EventAdapter implements Adapter<EventNode, Runnable> {
        public Runnable convert(EventNode eventNode) {
            return eventNode.getEvent();
        }

        public EventNode create(Runnable event) {
            return EventNode.create((Event) event);
        }
    }

    public EventExecutor() {
        super(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new BlockingQueueDecorator<EventNode, Runnable>(
                        new DelayQueue<EventNode>(), new EventAdapter()));
    }

    public void execute(Runnable command) {
        if (!(command instanceof Event)) {
            throw new UnsupportedOperationException(
                    "This class only supports Event objects.");
        }
        super.execute(command);
    }


}