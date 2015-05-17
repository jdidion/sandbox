package net.didion.pml.editor.dialogs;

import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import net.didion.pml.editor.EditorDialogFactory;
import net.didion.pml.model.Property;
import net.didion.pml.type.TypeMapper;

public abstract class AbstractEditorDialogFactory implements EditorDialogFactory {
	private final List<String> types;
	
	protected AbstractEditorDialogFactory() {
		this(null);
	}
	
	protected AbstractEditorDialogFactory(String[] types) {
	    this.types = (null == types) ? null : Arrays.asList(types);
	}
	
	public boolean edits(Property property) {
		return null == this.types || this.types.contains(TypeMapper.getType(property.getType()));
	}

	@Override
    public String editSingleValue(JFrame parent, Property property, String currentValue) {
	    final AbstractEditorDialog dialog = createEditorDialog(parent, property);
	    dialog.createSingleValueUi(currentValue);
        if (AbstractEditorDialog.OK == dialog.open()) {
            return dialog.getStringValue();
        }
        return null;
    }
	
    @Override
    public List<String> editMultipleValues(JFrame parent, Property property, List<String> currentValues) {
        final AbstractMultiValueEditorDialog dialog = (AbstractMultiValueEditorDialog) createEditorDialog(parent, property);
        dialog.createMultiValueUi(currentValues);
        if (AbstractEditorDialog.OK == dialog.open()) {
            return dialog.getStringValues();
        }
        return null;
    }
    
    protected abstract AbstractEditorDialog createEditorDialog(JFrame parent, Property property);
}
