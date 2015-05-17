/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.util;

import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.impl.ConfigurationObjectProvider;

public class CastingConfigurationObjectProvider extends ConfigurationObjectProvider {
    public Object provideObject(
            Module contributingModule, Class propertyType,
            String locator, Location location) {
        Object obj = super.provideObject(contributingModule, null, locator, null);
        if (propertyType.isAssignableFrom(obj.getClass())) {
            return propertyType.cast(obj);
        }
        return obj;
    }
}