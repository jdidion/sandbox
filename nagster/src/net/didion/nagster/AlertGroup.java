/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AlertGroup implements Alert {
    private Integer _id;
    private String _name;
    private List<Alert> _alerts;
    private boolean _firstSuccessfulOnly;

    public AlertGroup(String name) {
        this(name, false);
    }

    public AlertGroup(String name, boolean firstSuccessfulOnly) {
        _id = Nagster.nextId(AlertGroup.class);
        _name = name;
        _firstSuccessfulOnly = firstSuccessfulOnly;
        _alerts = new ArrayList<Alert>();
    }

    public Integer getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public void addAlert(Alert alert) {
        _alerts.add(alert);
    }

    public Alert getAlert(Integer id) {
        for (Alert a : _alerts) {
            if (id.equals(a.getId())) {
                return a;
            }
        }
        return null;
    }

    public Collection<Alert> getAlerts() {
        return Collections.unmodifiableCollection(_alerts);
    }

    public void deleteAlert(Integer id) {
        Alert a = getAlert(id);
        if (a != null) {
            deleteAlert(a);
        }
    }

    public void deleteAlert(Alert alert) {
        _alerts.remove(alert);
    }

    public boolean isFirstSuccessfulOnly() {
        return _firstSuccessfulOnly;
    }

    public void setFirstSuccessfulOnly(boolean firstSuccessfulOnly) {
        _firstSuccessfulOnly = firstSuccessfulOnly;
    }

    public boolean execute(ActionManager executor) throws Exception {
        boolean success = false;
        for (Alert a : _alerts) {
            if (a.execute(executor)) {
                success = true;
                if (_firstSuccessfulOnly) {
                    break;
                }
            }
        }
        return success;
    }
}