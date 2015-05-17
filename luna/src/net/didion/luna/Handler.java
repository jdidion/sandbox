/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.luna;

import java.net.URLStreamHandler;
import java.net.URLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;

public class Handler extends URLStreamHandler {
    protected URLConnection openConnection(URL url) throws IOException {
        return new LunaURLConnection(url);
    }

    public static class LunaURLConnection extends URLConnection {
        private URL _httpUrl;

        public LunaURLConnection(URL url) {
            super(url);
        }

        public void connect() throws IOException {
            _httpUrl = new URL("http", url.getHost(), url.getPort(), url.getFile());
        }

        public InputStream getInputStream() throws IOException {
            InputStream in = _httpUrl.openStream();
            return new LunaObjectInputStream(in);
        }
    }
}