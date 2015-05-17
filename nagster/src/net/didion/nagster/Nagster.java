/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import org.apache.hivemind.Registry;
import org.apache.hivemind.impl.RegistryBuilder;

public final class Nagster {
    private static Registry _registry;

    public static Scheduler getScheduler() {
        return getService(Scheduler.class);
    }

    public static ActionManager getActionManager() {
        return getService(ActionManager.class);
    }

    public static ResourceManager getResourceManager() {
        return getService(ResourceManager.class);
    }

    public static ApplicationManager getApplicationManager() {
        return getService(ApplicationManager.class);
    }

    public static Store getStore() {
        return getService(Store.class);
    }

    public static int nextId(Class c) {
        return getStore().nextId(c);
    }

    private synchronized static Registry getRegistry() {
        if (_registry == null) {
            _registry = RegistryBuilder.constructDefaultRegistry();
        }
        return _registry;
    }

    private static <T> T getService(Class<T> clazz) {
        return (T) getRegistry().getService(clazz);
    }

    private Nagster() {
    }
}