/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.action;

import net.didion.nagster.ActionBase;
import net.didion.nagster.ParameterDescriptor;
import net.didion.nagster.Parameter;
import net.didion.nagster.Resource;
import net.didion.nagster.Player;
import static net.didion.nagster.ParameterUtils.*;

import java.io.File;
import java.util.Map;
import java.net.URL;

public class PlayCDAction extends ActionBase {
    public static final ParameterDescriptor PARAM_DRIVE =
            ParameterDescriptor.createValueDescriptor("drive", true, getSystemRoots());

    private static String[] getSystemRoots() {
        File[] roots = File.listRoots();
        String[] rootPaths = new String[roots.length];
        for (int i = 0; i < roots.length; i++) {
            rootPaths[i] = roots[i].getAbsolutePath();
        }
        return rootPaths;
    }

    public PlayCDAction() {
        super("Play a CD", PARAM_DRIVE, PARAM_RETURN, PARAM_WAIT);
    }

    public boolean execute(
            Map<ParameterDescriptor, Parameter> parameters)
            throws Exception {
        String drive = getStringValue(parameters, PARAM_DRIVE);
        int trackCount = new File(drive).listFiles().length;

        // This class will not work without the following line. A windows
        // version of the Tritonus CDDA plugin is required.
        // URL.setURLStreamHandlerFactory(new CddaURLStreamHandlerFactory());

        for (int i = 0; i < trackCount; i++) {
            URL url = new URL("cdda://" + drive + "#" + i);
            Resource resource = new Resource("track", url);
            Player.play(resource, true);
        }

        return getDefaultReturnValue(parameters);
    }
}