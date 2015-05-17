package net.didion.pml.editor;

import java.util.List;

import javax.swing.JFrame;

import net.didion.pml.model.Property;

public interface EditorDialogFactory {
	boolean edits(Property property);
	
	String editSingleValue(JFrame parent, Property property, String currentValue);
	
	List<String> editMultipleValues(JFrame parent, Property property, List<String> currentValues);
}
