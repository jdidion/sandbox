/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.store.prevayler;

import net.didion.nagster.Event;
import net.didion.nagster.Resource;
import net.didion.nagster.Application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database implements Serializable {
    public static final String EVENT = "events";
    public static final String RESOURCE = "resources";
    public static final String APPLICATION = "applications";

    private Map<String, Integer> _ids;
    private Map<String, List> _tables;

    public Database() {
        _ids = new HashMap<String, Integer>();
        _tables = new HashMap<String, List>();
        _tables.put(EVENT, new ArrayList<Event>());
        _tables.put(RESOURCE, new ArrayList<Resource>());
        _tables.put(APPLICATION, new ArrayList<Application>());
    }


    public Object getAndIncrementId(Class c) {
        synchronized (c) {
            Integer i = _ids.get(c.getName());
            if (i == null) {
                i = new Integer(1);
            }
            _ids.put(c.getName(), new Integer(i+1));
            return i;
        }
    }

    public void add(String table, Object obj) {
        getTable(table).add(obj);
    }

    public void remove(String table, Object obj) {
        getTable(table).remove(obj);
    }

    public Collection list(String table) {
        return Collections.unmodifiableCollection(_tables.get(table));
    }

    private List getTable(String name) {
        List tab = _tables.get(name);
        if (tab == null) {
            throw new RuntimeException("no such table: " + name);
        }
        return tab;
    }
}