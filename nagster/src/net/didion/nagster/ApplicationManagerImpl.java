/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.util.Collection;

public class ApplicationManagerImpl implements ApplicationManager {
    private Store _store;

    public ApplicationManagerImpl(Store store) {
        _store = store;
    }

    public void addApplication(
            String name, String executable, String... params) {
        Application app = new Application(name, executable);
        for (int i = 0; i < params.length; i++) {
            String[] nameValue = params[i].split("=");
            ParameterDescriptor pd = ParameterDescriptor.
                    createValueDescriptor(nameValue[0], true, false);
            Parameter param = new Parameter(pd, nameValue[1]);
            app.addParameter(param);
        }
        _store.insertApplication(app);
    }

    public Collection<Application> listApplications() {
        return _store.selectAllApplications();
    }
}