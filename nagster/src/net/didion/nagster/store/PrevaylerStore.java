/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.store;

import net.didion.nagster.Event;
import net.didion.nagster.Store;
import net.didion.nagster.Resource;
import net.didion.nagster.Application;
import net.didion.nagster.store.prevayler.Database;
import net.didion.nagster.store.prevayler.EventDateFilter;
import net.didion.nagster.store.prevayler.Delete;
import net.didion.nagster.store.prevayler.FilterQuery;
import net.didion.nagster.store.prevayler.GetAndUpdateIdentity;
import net.didion.nagster.store.prevayler.IdentityFilter;
import net.didion.nagster.store.prevayler.Insert;
import net.didion.nagster.store.prevayler.SelectAll;
import net.didion.nagster.store.prevayler.UniqueFilterQuery;
import net.didion.nagster.util.CalendarUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

public class PrevaylerStore implements Store {
    private static Log _log = LogFactory.getLog(PrevaylerStore.class);

    private Prevayler _prevayler;

    public PrevaylerStore(String databaseDir) {
        PrevaylerFactory factory = new PrevaylerFactory();
        factory.configurePrevalenceBase(databaseDir);
        factory.configurePrevalentSystem(new Database());
        try {
            _prevayler = factory.create();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Integer nextId(Class c) {
        try {
            return (Integer) _prevayler.execute(new GetAndUpdateIdentity(c));
        } catch (Exception ex) {
            _log.error("Error selecting and/or updating id", ex);
            return null;
        }
    }

    public void insertEvent(Event event) {
        _prevayler.execute(new Insert(Database.EVENT, event));
    }

    public void deleteEvent(Event event) {
        _prevayler.execute(new Delete(Database.EVENT, event));
    }

    public Collection<Event> selectAllEvents() {
        try {
            return (Collection<Event>) _prevayler.
                    execute(new SelectAll(Database.EVENT));
        } catch (Exception ex) {
            _log.error("Error selecting all events", ex);
            return Collections.EMPTY_LIST;
        }
    }

    public Collection<Event> selectEventsByDate(Calendar cal) {
        try {
            return (Collection<Event>) _prevayler.execute(
                    new FilterQuery(Database.EVENT, new EventDateFilter(cal)));
        } catch (Exception ex) {
            _log.error("Error selecting events on date " + CalendarUtils.format(cal), ex);
            return Collections.EMPTY_LIST;
        }
    }

    public Event selectEventById(Integer id) {
        try {
            return (Event) _prevayler.execute(
                    new UniqueFilterQuery(Database.EVENT, new IdentityFilter(id)));
        } catch (Exception ex) {
            _log.error("Error selecting event for id " + id);
            return null;
        }
    }

    public boolean containsEvent(Event e) {
        return selectEventById(e.getId()) != null;
    }

    public void insertResource(Resource resource) {
        _prevayler.execute(new Insert(Database.RESOURCE, resource));
    }

    public void deleteResource(Resource resource) {
        _prevayler.execute(new Delete(Database.RESOURCE, resource));
    }

    public Collection<Resource> selectAllResources() {
        try {
            return (Collection<Resource>) _prevayler.
                    execute(new SelectAll(Database.RESOURCE));
        } catch (Exception e) {
            _log.error("Error selecting all resources", e);
            return null;
        }
    }

    public Resource selectResourceById(Integer id) {
        try {
            return (Resource) _prevayler.execute(
                    new UniqueFilterQuery(Database.RESOURCE, new IdentityFilter(id)));
        } catch (Exception ex) {
            _log.error("Error selecting resource by id " + id, ex);
            return null;
        }
    }

    public void insertApplication(Application application) {
        _prevayler.execute(new Insert(Database.APPLICATION, application));
    }

    public void deleteApplication(Application application) {
        _prevayler.execute(new Delete(Database.APPLICATION, application));
    }

    public Collection<Application> selectAllApplications() {
        try {
            return (Collection<Application>) _prevayler.
                    execute(new SelectAll(Database.APPLICATION));
        } catch (Exception ex) {
            _log.error("Error selecting all applications", ex);
            return null;
        }
    }

    public Application selectApplicationById(Integer id) {
        try {
            return (Application) _prevayler.execute(
                    new UniqueFilterQuery(Database.APPLICATION, new IdentityFilter(id)));
        } catch (Exception ex) {
            _log.error("Error selecting applications by id " + id, ex);
            return null;
        }
    }
}