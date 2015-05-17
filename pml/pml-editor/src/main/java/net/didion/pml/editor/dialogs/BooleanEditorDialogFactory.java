package net.didion.pml.editor.dialogs;

import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import net.didion.pml.model.Property;

public class BooleanEditorDialogFactory extends AbstractEditorDialogFactory {
	private static final String[] TYPES = { "boolean" };
	
	public BooleanEditorDialogFactory() {
		super(TYPES);
	}
	
	@Override
    protected AbstractEditorDialog createEditorDialog(JFrame parent, Property property) {
	    return new BooleanEditorDialog(parent, property);
    }

    public List<String> editMultipleValues(JFrame parent, Property property, List<String> currentVaues) {
		return Arrays.asList("true", "false");
	}	
}
