/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.io.InputStream;
import java.io.IOException;
import java.util.Collection;

public interface ResourceManager {
    void addResource(
            String name, String fileName, InputStream stream)
            throws IOException;
    Collection<Resource> listResources();
}