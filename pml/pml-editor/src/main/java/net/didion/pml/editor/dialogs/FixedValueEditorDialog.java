package net.didion.pml.editor.dialogs;

import static net.didion.pml.util.StringUtils.*;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.text.JTextComponent;

import net.didion.pml.model.AllowedValues;
import net.didion.pml.model.Property;
import net.didion.pml.type.TypeMapper;

public class FixedValueEditorDialog extends AbstractFieldEditorDialog {
    private JList list;
    
    private JTextComponent editField;
    private JButton editButton;
    private JButton saveButton;
    private JButton clearButton;
    
    public FixedValueEditorDialog(JFrame parent, final Property property) {
        super(parent, property);
    }
    
    @Override
    public void createSingleValueUi(String currentValue) {
        super.createSingleValueUi(currentValue, 2);
    }
    
    @Override
    public void createMultiValueUi(List<String> currentValues) {
        super.createMultiValueUi(currentValues, 3);
    }
    
    @Override
    protected void addSingleValueComponentsBeforeFields(boolean hasValues) {
        addComponentsBeforeFields(hasValues);
    }
    
    @Override
    protected void addMultiValueComponentsBeforeFields(boolean hasValues) {
        addComponentsBeforeFields(hasValues);
    }

    private void addComponentsBeforeFields(boolean hasValues) {
        final List<AllowedValues> allowed = super.property.getAllowedValues();
        final String[] values = new String[allowed.size()];
        for (int i = 0; i < allowed.size(); i++) {
            values[i] = allowed.get(i).getValues();
        }
        final Integer[] minMax = getMinMaxNumValues();
        final int selectionMode = (minMax[1] != null && minMax[1] > 1) ? 
            ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION;
        
        this.list = new JList(values);
        this.list.setEnabled(false);
        this.list.setSelectionMode(selectionMode);
        
        final JScrollPane scroll = new JScrollPane(this.list);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        super.contentPanel.add(scroll);
        super.layout.putConstraint(SpringLayout.WEST, scroll, 0, SpringLayout.WEST, super.contentPanel);
        super.layout.putConstraint(SpringLayout.NORTH, scroll, 5, SpringLayout.SOUTH, super.northAnchor);
        
        this.saveButton = new JButton("Save");
        this.saveButton.setEnabled(false);
        this.saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FixedValueEditorDialog.this.editField.setText(concat(FixedValueEditorDialog.this.list.getSelectedValues()));
                FixedValueEditorDialog.this.list.clearSelection();
                FixedValueEditorDialog.this.editField = null;
                FixedValueEditorDialog.this.editButton.setEnabled(true);
                FixedValueEditorDialog.this.editButton = null;
                FixedValueEditorDialog.this.list.setEnabled(false);
                FixedValueEditorDialog.this.saveButton.setEnabled(false);
                FixedValueEditorDialog.this.clearButton.setEnabled(false);
            }
        });
        this.contentPanel.add(saveButton);
        super.northAnchor = saveButton;
        super.layout.putConstraint(SpringLayout.WEST, saveButton, 0, SpringLayout.WEST, super.contentPanel);
        super.layout.putConstraint(SpringLayout.NORTH, saveButton, 5, SpringLayout.SOUTH, scroll);
        
        this.clearButton = new JButton("Clear");
        this.clearButton.setEnabled(false);
        this.clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FixedValueEditorDialog.this.list.clearSelection();
                FixedValueEditorDialog.this.editField = null;
                FixedValueEditorDialog.this.editButton.setEnabled(true);
                FixedValueEditorDialog.this.editButton = null;
                FixedValueEditorDialog.this.list.setEnabled(false);
                FixedValueEditorDialog.this.saveButton.setEnabled(false);
                FixedValueEditorDialog.this.clearButton.setEnabled(false);
            }
        });
        this.contentPanel.add(clearButton);
        super.layout.putConstraint(SpringLayout.WEST, clearButton, 0, SpringLayout.EAST, saveButton);
        super.layout.putConstraint(SpringLayout.NORTH, clearButton, 5, SpringLayout.SOUTH, scroll);
    }
    
    @Override
    protected JTextComponent createField(String currentValue, int row) {
        final JTextComponent field = super.createField(currentValue, row);
        field.setEditable(false);
        return field;
    }

    @Override
    protected List<Component> createSingleValueRowComponents(JTextComponent field) {
        final JButton edit = createEditButton(field);
        if (nullOrEmpty(field.getText())) {
            this.editField = (JTextField) field;
            this.editButton = edit;
            this.list.setEnabled(true);
            this.saveButton.setEnabled(true);
            this.clearButton.setEnabled(true);
            edit.setEnabled(false);
        }
        return Arrays.asList((Component) edit);
    }
    
    @Override
    protected List<Component> createMultiValueRowComponents(JTextComponent field) {
        return Arrays.asList((Component) createEditButton(field), (Component) createRemoveButton(field));
    }

    private JButton createEditButton(final JTextComponent field) {
        final JButton edit = new JButton("Edit");
        edit.setEnabled(true);
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String valueStr = field.getText();
                if (null != valueStr && valueStr.trim().length() > 0) {
                    final String[] values = valueStr.split(FixedValueEditorDialog.super.property.getValueDelimiter());
                    final List<AllowedValues> allowedValues = property.getAllowedValues();
                    final int[] selectionIndices = new int[values.length];
                    for (int i = 0; i < values.length; i++) {
                        selectionIndices[i] = allowedValues.indexOf(values[i]);
                    }
                    FixedValueEditorDialog.this.list.setSelectedIndices(selectionIndices);
                }
                FixedValueEditorDialog.this.editField = field;
                FixedValueEditorDialog.this.editButton = edit;
                edit.setEnabled(false);
                FixedValueEditorDialog.this.list.setEnabled(true);
                FixedValueEditorDialog.this.saveButton.setEnabled(true);
                FixedValueEditorDialog.this.clearButton.setEnabled(true);
            }
        });
        return edit;
    }
    
    @Override
    protected String getExplanationText() {
        final StringBuilder sb = new StringBuilder();
        sb.append("This is a ")
          .append(TypeMapper.getType(super.property.getType()).getName())
          .append(" property. You must chose the value(s) from those listed below.");
        addMinMaxNumValuesText(sb);
        return sb.toString();
    }
}
