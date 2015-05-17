/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class LunaObjectInputStream extends InputStream {
    private ObjectInputStream _objectInputStream;

    public LunaObjectInputStream(InputStream in) throws IOException {
        _objectInputStream = new ObjectInputStream(in);
    }

    public int read() throws IOException {
        return _objectInputStream.read();
    }

    public LunaObject getLunaObject() {
        return null;
    }
}