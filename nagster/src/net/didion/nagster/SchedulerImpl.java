/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import org.apache.log4j.Logger;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

public class SchedulerImpl implements Scheduler {
    public static final int DEFAULT_THREADS = 3;
    private static Logger _log = Logger.getLogger(SchedulerImpl.class);

    private class EventExecutor extends ScheduledThreadPoolExecutor {
        private Map<Integer, ScheduledFuture<Event>> _scheduledFutureMap =
                new HashMap<Integer, ScheduledFuture<Event>>();

        EventExecutor(int corePoolSize) {
            super(corePoolSize);
        }

        boolean schedule(final Event e) {
            long delay = e.delay(TimeUnit.MILLISECONDS);
            if (delay < 0) {
                _log.debug("deleting event " + e.getId());
                _store.deleteEvent(e);
                return false;
            }
            ScheduledFuture<Event> sf = super.schedule(
                    new EventCallable(e),
                    delay, TimeUnit.MILLISECONDS);
            _scheduledFutureMap.put(e.getId(), sf);
            return true;
        }

        boolean isScheduled(Event e) {
            return _scheduledFutureMap.containsKey(e.getId());
        }

        boolean cancel(Event e) {
            if (isScheduled(e)) {
                ScheduledFuture<Event> sf = _scheduledFutureMap.remove(e.getId());
                super.remove((Runnable) sf);
                return true;
            }
            return false;
        }

        /**
         * Attempt to reinsert all events back into the queue. Events will
         * only be reinserted if they have a nextScheduledTime after the
         * time at which the insertion attempt is made.
         * @param r
         * @param t
         */
        protected void afterExecute(Runnable r, Throwable t) {
            ScheduledFuture<Event> sf = (ScheduledFuture<Event>) r;
            Event e = null;
            try {
                e = sf.get();
            } catch (Exception ex) {
                _log.error("Exception occurred during execution of event", ex);
            }
            schedule(e);
        }
    }

    private class EventCallable implements Callable<Event> {
        private Event _event;

        public EventCallable(Event event) {
            _event = event;
        }

        public Event call() throws Exception {
            _event.execute(_actionManager);
            return _event;
        }
    }

    private EventExecutor _executor;
    private Store _store;
    private ActionManager _actionManager;

    public SchedulerImpl(Store store, ActionManager actionManager) {
        this(store, actionManager, DEFAULT_THREADS);
    }

    public SchedulerImpl(Store store, ActionManager actionManager, int executorThreads) {
        _store = store;
        _actionManager = actionManager;
        _executor = new EventExecutor(executorThreads);
        Collection<Event> events = _store.selectAllEvents();
        Event[] eventArray = events.toArray(new Event[events.size()]);
        for (Event e : eventArray) {
            scheduleEvent(e);
        }
    }

    public boolean scheduleEvent(Event e) {
        if (_executor.isScheduled(e)) {
            _log.debug("cancelling event " + e.getId());
            _executor.cancel(e);
        } else if (!_store.containsEvent(e)) {
            _log.debug("inserting event " + e.getId());
            _store.insertEvent(e);
        }
        boolean success = _executor.schedule(e);
        return success;
    }
}