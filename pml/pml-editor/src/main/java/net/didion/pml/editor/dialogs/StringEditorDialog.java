package net.didion.pml.editor.dialogs;

import javax.swing.JFrame;

import net.didion.pml.model.Property;
import net.didion.pml.type.TypeMapper;

public class StringEditorDialog extends AbstractFieldEditorDialog {
	public StringEditorDialog(JFrame parent, Property property) {
		super(parent, property);
	}
	
	protected String getExplanationText() {
        final StringBuilder sb = new StringBuilder();
        sb.append("This is a ")
          .append(TypeMapper.getType(super.property.getType()).getName())
          .append(" property. ");
        addMinMaxNumValuesText(sb);
        return sb.toString();
    }
}
