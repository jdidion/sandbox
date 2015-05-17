package net.didion.pml.editor.dialogs;

import javax.swing.JFrame;

import net.didion.pml.model.Property;

public class FixedValueEditorDialogFactory extends AbstractEditorDialogFactory {
	public boolean edits(Property property) {
		return property.getAllowedValues() != null && !property.getAllowedValues().isEmpty();
	}

    @Override
    protected AbstractEditorDialog createEditorDialog(JFrame parent, Property property) {
        return new FixedValueEditorDialog(parent, property);
    }
}
