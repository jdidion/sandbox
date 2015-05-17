/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import java.util.Iterator;
import java.util.List;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

public class ObjectLocator {
    public static final String LUNA_PROTOCOL = "luna";

    private List<String> _environments;
    private long _objectId;

    public ObjectLocator(List<String> environments, long objectId) {
        _environments = environments;
        _objectId = objectId;
    }

    public Iterator<String> getEnvironmentIterator() {
        return _environments.iterator();
    }

    public long getObjectId() {
        return _objectId;
    }

    public LunaObject getObject() {
        Iterator<String> itr = getEnvironmentIterator();
        String rootEnvName = itr.next();
        if (RootEnvironment.getInstance().getName().equals(rootEnvName)) {
            Environment env = RootEnvironment.getInstance().resolveEnvironment(itr);
            if (env == null) {
                throw new ObjectNotFoundException(this);
            }
            return env.getObject(getObjectId());
        } else {
            LunaObjectInputStream stream = null;
            try {
                URL url = new URL(toString());
                stream = (LunaObjectInputStream) url.openStream();
                return stream.getLunaObject();
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid luna URL: " + toString(), e);
            } catch (IOException e) {
                throw new RuntimeException(
                        "Communication error retrieving object " + toString(), e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(LUNA_PROTOCOL).append(":/");
        for (String env : _environments) {
            buf.append("/").append(env);
        }
        buf.append("/").append(_objectId);
        return buf.toString();
    }
}