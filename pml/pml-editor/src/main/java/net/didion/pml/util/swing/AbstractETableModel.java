package net.didion.pml.util.swing;

import javax.swing.table.AbstractTableModel;

public abstract class AbstractETableModel extends AbstractTableModel {
    public boolean isEnabled(int row) {
        return true;
    }
}
