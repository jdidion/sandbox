package net.didion.pml.editor.dialogs;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;

import net.didion.pml.model.Property;

public class BooleanEditorDialog extends AbstractEditorDialog {
	private JRadioButton trueButton;
	private JRadioButton falseButton;
	
	public BooleanEditorDialog(JFrame parent, Property property) {
		super(parent, property);
	}
	
	@Override
    public void createSingleValueUi(String currentValue) {
	    super.initUi();
    
	    final boolean value;
		if (null != currentValue) {
			value = new Boolean(currentValue).booleanValue();
		}
		else if (null != property.getDefaultValues()) {
			value = new Boolean(property.getDefaultValues()).booleanValue();
		}
		else {
			value = true;
		}
		
		final ButtonGroup bg = new ButtonGroup();
		
		this.trueButton = new JRadioButton("true", value);
		super.contentPanel.add(this.trueButton);
		super.layout.putConstraint(SpringLayout.WEST, this.trueButton, 0, SpringLayout.WEST, super.contentPanel);
		super.layout.putConstraint(SpringLayout.NORTH, this.trueButton, 0, SpringLayout.SOUTH, super.northAnchor);
		bg.add(this.trueButton);
		
		this.falseButton = new JRadioButton("false", !value);
		super.contentPanel.add(this.falseButton);
		super.layout.putConstraint(SpringLayout.WEST, this.falseButton, 0, SpringLayout.WEST, super.contentPanel);
        super.layout.putConstraint(SpringLayout.NORTH, this.falseButton, 5, SpringLayout.SOUTH, this.trueButton);
		bg.add(this.falseButton);
	}
	
	public String getStringValue() {
		return String.valueOf(this.trueButton.isSelected());
	}
	
	protected String getExplanationText() {
        return "This is a boolean property.";
    }
}
