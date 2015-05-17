/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class ResourceManagerImpl implements ResourceManager {
    private Store _store;
    private File _resourceDir;

    public ResourceManagerImpl(Store store, String resourcePath) {
        _store = store;
        _resourceDir = new File(resourcePath);
        if (!_resourceDir.exists()) {
            _resourceDir.mkdirs();
        }
    }

    public void addResource(
            String name, String fileName, InputStream stream)
            throws IOException {
        String outputFileName = uniqueFileName(_resourceDir, fileName);
        File outputFile = new File(_resourceDir, outputFileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
            for (byte b; (b = (byte) stream.read()) != -1;) {
                out.write(b);
            }
            out.flush();
        } finally {
            if (out != null) try { out.close(); } catch (Exception ex) {}
        }
        _store.insertResource(new FileResource(name, outputFile));
    }

    public Collection<Resource> listResources() {
        return _store.selectAllResources();
    }

    private String uniqueFileName(File dir, String file) {
        int index = file.lastIndexOf('.');
        String name;
        String extension;
        if (index < 0) {
            name = file;
            extension = ".txt";
        } else {
            name = file.substring(0, index);
            extension = file.substring(index);
        }
        File f;
        for (int i = 0; (f = makeFile(dir, name, i, extension)).exists(); i++);
        return f.getName();
    }

    private File makeFile(File dir, String name, int i, String extension) {
        return new File(dir, name + ((i == 0) ? "" : i) + extension);
    }
}