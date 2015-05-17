/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import javax.activation.DataSource;
import javax.activation.MimeType;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class Resource implements DataSource, Identity, Comparable, Serializable {
    private Integer _id;
    private String _name;
    private URL _url;
    private transient URLConnection _urlConnection;
    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

    public Resource(String name, URL url) {
        _id = Nagster.nextId(Resource.class);
        _name = name;
        _url = url;
    }

    public Integer getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public MimeType getMimeType() {
        try {
            return new MimeType(getContentType());
        } catch (Exception ex) {
            throw new RuntimeException("Invalid mime type: " + getContentType());
        }
    }

    public String getContentType() {
        if (_urlConnection == null) {
            try {
                _urlConnection = _url.openConnection();
            } catch(IOException ex) {
            }
        }
        if (_urlConnection != null) {
            return _urlConnection.getContentType();
        }
        return DEFAULT_MIME_TYPE;
    }

    public InputStream getInputStream() throws IOException {
        return _url.openStream();
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    public String getAsString() throws IOException {
        if (!"text".equals(getMimeType().getPrimaryType())) {
            throw new RuntimeException(
                    "Content of type " + getContentType() +
                    " cannot be converted to a string.");
        }
        InputStream in = getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    public int compareTo(Object o) {
        return getName().compareTo(((Resource) o).getName());
    }

    public String toString() {
        return getName();
    }
}