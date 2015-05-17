package net.didion.pml.util.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

public final class SwingUtils {
    private static final Color MAC_OS_ALTERNATE_ROW_COLOR = new Color(0.92f, 0.95f, 0.99f);

    public static void center(Window window) {
        final Toolkit tk = Toolkit.getDefaultToolkit();
        final Dimension screenSize = tk.getScreenSize();
        final int x = (screenSize.width - window.getWidth()) / 2;
        final int y = (screenSize.height - window.getHeight()) / 2;
        window.setLocation(new Point(x, y));
    }
    
    /**
     * Tests whether we're running on Mac OS. The Mac is quite
     * different from Linux and Windows, and it's sometimes
     * necessary to put in special-case behavior if you're running
     * on the Mac.
     */
    public static boolean isMacOs() {
        return System.getProperty("os.name").contains("Mac");
    }
    
    /**
     * Tests whether we're using the GTK+ LAF (and so are probably on Linux or Solaris).
     */
    public static boolean isGtk() {
        return UIManager.getLookAndFeel().getClass().getName().contains("GTK");
    }
    
    /**
     * Returns the appropriate background color for the given row index.
     */
    public static Color backgroundColorForRow(int row) {
        if (isGtk()) {
            return (row % 2 == 0) ? Color.WHITE : UIManager.getColor("Table.background");
        } else if (isMacOs()) {
            return (row % 2 == 0) ? Color.WHITE : MAC_OS_ALTERNATE_ROW_COLOR;
        }
        return UIManager.getColor("Table.background");
    }
    
    public static boolean isEmpty(JTextComponent text) {
        return (null == text.getText() || text.getText().trim().length() == 0);
    }
    
    private SwingUtils() {
    }
}
