/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.tapestry.page;

import net.didion.nagster.AlertGroup;
import net.didion.nagster.AlertImpl;
import net.didion.nagster.Event;
import net.didion.nagster.Nagster;
import net.didion.nagster.ParameterDescriptor;
import net.didion.nagster.Resource;
import net.didion.nagster.Application;
import net.didion.nagster.Parameter;
import net.didion.nagster.ActionDescriptor;
import net.didion.nagster.tapestry.util.ArrayPropertySelectionModel;
import net.didion.nagster.tapestry.Visit;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.html.BasePage;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class AlertPage extends BasePage {
    private static final String SUBMIT = "submit";
    private static final String RELOAD = "reload";

    private Integer _eventId;
    private Integer _alertGroupId;
    private Integer _alertId;
    private AlertImpl _alert;
    private transient ParameterDescriptor[] _parameterDescriptors;
    private transient ActionDescriptor[] _actionDescriptors;
    private transient String _operation = SUBMIT;

    public void detach() {
        super.detach();
        _eventId = 0;
        _alertGroupId = 0;
        _alertId = 0;
        _alert = null;
        _parameterDescriptors = null;
        _actionDescriptors = null;
        _operation = SUBMIT;
    }

    public Integer getEventId() {
        return _eventId;
    }

    public void setEventId(Integer eventId) {
        _eventId = eventId;
    }

    public Integer getAlertGroupId() {
        return _alertGroupId;
    }

    public void setAlertGroupId(Integer alertGroupId) {
        _alertGroupId = alertGroupId;
    }

    public Integer getAlertId() {
        return _alertId;
    }

    public void setAlertId(Integer alertId) {
        _alertId = alertId;
    }

    public ActionDescriptor getActionDescriptor() {
        return getAlert().getActionDescriptor();
    }

    public void setActionDescriptor(ActionDescriptor descriptor) {
        getAlert().setActionDescriptor(descriptor);
    }

    private AlertImpl getAlert() {
        if (_alert == null) {
            if (_alertId != null) {
                AlertGroup ag = getAlertGroup();
                _alert = (AlertImpl) ag.getAlert(_alertId);
                if (_alert == null) {
                    _alert = ((Visit) getVisit()).getAlert(_alertId);
                }
            }
            if (_alert == null) {
                _alert = ((Visit) getVisit()).createAlert();
                _alertId = _alert.getId();
            }
        }
        if (_alert.getActionDescriptor() == null) {
            ActionDescriptor[] descriptors = getSortedActionDescriptorArray();
            if (descriptors.length > 0) {
                _alert.setActionDescriptor(descriptors[0]);
            }
        }
        return _alert;
    }

    private AlertGroup getAlertGroup() {
        Event e = ((Visit) getVisit()).getEvent(_eventId);
        if (e == null) {
            e = Nagster.getStore().selectEventById(_eventId);
        }
        return e.getAlertGroup(_alertGroupId);
    }

    public IPropertySelectionModel getActionDescriptorModel() {
        return new ArrayPropertySelectionModel<ActionDescriptor>(getSortedActionDescriptorArray());
    }

    private ActionDescriptor[] getSortedActionDescriptorArray() {
        if (_actionDescriptors == null) {
            Collection<ActionDescriptor> actions =
                    Nagster.getActionManager().listActionDescriptors();
            ActionDescriptor[] actionArray = actions.toArray(new ActionDescriptor[actions.size()]);
            Arrays.sort(actionArray);
            _actionDescriptors = actionArray;
        }
        return _actionDescriptors;
    }

    public Collection<ParameterDescriptor> getParameterDescriptors() {
        if (getActionDescriptor() == null) {
            return Collections.EMPTY_LIST;
        }
        return getActionDescriptor().getParameterDescriptors();
    }

    private ParameterDescriptor[] getParameterDescriptorArray() {
        if (_parameterDescriptors == null) {
            Collection<ParameterDescriptor> descriptors = getParameterDescriptors();
            _parameterDescriptors = descriptors.
                    toArray(new ParameterDescriptor[descriptors.size()]);
        }
        return _parameterDescriptors;
    }

    public Parameter getParameter(int index) {
        ParameterDescriptor[] descriptors = getParameterDescriptorArray();
        if (index >= descriptors.length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        Parameter param = getAlert().getParameter(descriptors[index]);
        if (param == null) {
            param = new Parameter(descriptors[index]);
            getAlert().setParameter(param);
        }
        return param;
    }

    public IPropertySelectionModel getAcceptedValuesModel(int index) {
        ParameterDescriptor descriptor = getParameterDescriptorArray()[index];
        Collection<String> acceptedValues = descriptor.getAcceptedValues();
        return new ArrayPropertySelectionModel<String>(
                acceptedValues.toArray(new String[acceptedValues.size()]));
    }

    public IPropertySelectionModel getResourceModel(int index) {
        ParameterDescriptor descriptor = getParameterDescriptorArray()[index];
        Set<Resource> acceptedResources = new HashSet<Resource>();
        if (descriptor.isAcceptsResource()) {
            Collection<Resource> resources =
                    Nagster.getResourceManager().listResources();
            for (Resource r : resources) {
                if (descriptor.isAcceptsResource(r)) {
                    acceptedResources.add(r);
                }
            }
        }
        Resource[] resourceArray = acceptedResources.
                toArray(new Resource[acceptedResources.size()]);
        Arrays.sort(resourceArray);
        return new ArrayPropertySelectionModel<Resource>(resourceArray);
    }

    public IPropertySelectionModel getApplicationModel(int index) {
        ParameterDescriptor descriptor = getParameterDescriptorArray()[index];
        Collection<Application> applications =
                (descriptor.isAcceptsApplication()) ?
                        Nagster.getApplicationManager().listApplications() :
                        Collections.EMPTY_LIST;
        Application[] applicationArray = applications.
                toArray(new Application[applications.size()]);
        Arrays.sort(applicationArray);
        return new ArrayPropertySelectionModel<Application>(applicationArray);
    }

    public ActionDescriptor[] getActionDescriptors() {
        return _actionDescriptors;
    }

    public void setActionDescriptors(ActionDescriptor[] actionDescriptors) {
        _actionDescriptors = actionDescriptors;
    }

    public String getOperation() {
        return _operation;
    }

    public void setOperation(String operation) {
        _operation = operation;
    }

    public void updateAlert(IRequestCycle cycle) {
        if (RELOAD.equals(getOperation())) {
            setOperation(SUBMIT);
            return;
        }
        if (getAlertGroup().getAlert(_alert.getId()) == null) {
            getAlertGroup().addAlert(_alert);
        }
        ((Visit) getVisit()).removeAlert(_alert);
        EventPage page = (EventPage) cycle.getPage(Constants.Pages.EVENT.getName());
        page.setEventId(_eventId);
        cycle.activate(page);
    }
}