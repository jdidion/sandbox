package net.didion.pml.editor.dialogs;

import java.util.List;

import javax.swing.JFrame;

import net.didion.pml.model.Property;

public abstract class AbstractMultiValueEditorDialog extends AbstractEditorDialog {
    public AbstractMultiValueEditorDialog(JFrame parent, Property property) {
        super(parent, property);
    }

    public abstract void createMultiValueUi(List<String> currentValues);
    
    public abstract List<String> getStringValues();
}
