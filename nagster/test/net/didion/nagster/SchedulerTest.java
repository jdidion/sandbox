/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import junit.framework.TestCase;

import java.util.Arrays;

import net.didion.nagster.action.BeepAction;

public class SchedulerTest extends TestCase {
    public SchedulerTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void test() {
        Store store = new MockStore();
        ActionManager actionManager = new ActionManagerImpl();
        BeepAction action = new BeepAction();
        actionManager.setActions(Arrays.asList(new Action[] { action }));
        Scheduler scheduler = new SchedulerImpl(store, actionManager, 1);
        Event event = new Event("test");
        event.addSchedule(new Schedule("29 18 * * * *"));
        AlertGroup ag = new AlertGroup("foo");
        ag.addAlert(new AlertImpl(action.getActionDescriptor()));
        event.addAlertGroup(ag);
        scheduler.scheduleEvent(event);
        try {
            Thread.sleep(300000);
        } catch (InterruptedException ex) {
        }
    }
}