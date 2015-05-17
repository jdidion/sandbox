/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.net.MalformedURLException;

public class FileResource extends Resource {
    private File _file;

    public FileResource(String name, File file) throws MalformedURLException {
        super(name, file.toURL());
        _file = file;
    }

    public String getContentType() {
        return MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(_file);
    }
}