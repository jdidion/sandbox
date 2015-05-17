package net.didion.pml.editor.dialogs;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.text.JTextComponent;

import net.didion.pml.model.AllowedValues;
import net.didion.pml.model.Property;
import net.didion.pml.model.Value;
import net.didion.pml.type.Type;
import net.didion.pml.type.TypeMapper;
import net.didion.pml.type.TypeValidationException;
import net.didion.pml.type.XsdTypes;
import net.didion.pml.util.StringUtils;

public abstract class AbstractFieldEditorDialog extends AbstractMultiValueEditorDialog {
    protected JPanel fieldPanel;
    protected GroupLayout fieldLayout;
    protected Group rowGroups;
    protected List<Group> columnGroups;
    protected List<JTextComponent> fields;
    protected Map<JTextComponent,List<Component>> rowComponents;
    
    public AbstractFieldEditorDialog(JFrame parent, Property property) {
        super(parent, property);
    }

    @Override
    public void createSingleValueUi(String currentValue) {
        createSingleValueUi(currentValue, 1);
    }
    
    protected void createSingleValueUi(String currentValue, int columns) {
        super.initUi();
        
        addSingleValueComponentsBeforeFields(currentValue != null);
        
        createFieldPanel(columns);
        
        this.fields = new ArrayList<JTextComponent>(1);
        if (columns > 1) {
            this.rowComponents = new HashMap<JTextComponent,List<Component>>(1);
        }
        
        createFieldRow(currentValue, true);
    }
    
    @Override
    public void createMultiValueUi(List<String> currentValues) {
        createMultiValueUi(currentValues, 2);
    }
    
    protected void createMultiValueUi(List<String> currentValues, int columns) {
        super.initUi();
        
        final boolean hasValues = (null != currentValues && !currentValues.isEmpty());
       
        addMultiValueComponentsBeforeFields(hasValues);
        
        createFieldPanel(columns);
        
        if (!hasValues) {
            this.fields = new ArrayList<JTextComponent>();
            if (columns > 1) {
                this.rowComponents = new HashMap<JTextComponent,List<Component>>();
            }
            createFieldRow(null, false);
        }
        else {
            this.fields = new ArrayList<JTextComponent>(currentValues.size());
            if (columns > 1) {
                this.rowComponents = new HashMap<JTextComponent,List<Component>>(currentValues.size());
            }
            for (String value : currentValues) {
                createFieldRow(value, false);
            }
        }
        
        final JButton add = new JButton("+");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createFieldRow(null, false);
                AbstractFieldEditorDialog.super.contentPanel.validate();
            }
        });
        super.contentPanel.add(add);
        super.layout.putConstraint(SpringLayout.WEST, add, 0, SpringLayout.WEST, super.contentPanel);
        super.layout.putConstraint(SpringLayout.NORTH, add, 0, SpringLayout.SOUTH, this.fieldPanel);
    }

    protected void addSingleValueComponentsBeforeFields(boolean hasValues) {
    }
    
    protected void addMultiValueComponentsBeforeFields(boolean hasValues) {
    }
    
    protected void createFieldPanel(int columns) {
        this.fieldPanel = new JPanel();
        this.fieldLayout = new GroupLayout(this.fieldPanel);
        this.fieldLayout.setAutoCreateGaps(true);
        this.rowGroups = this.fieldLayout.createSequentialGroup();
        this.fieldLayout.setVerticalGroup(this.rowGroups);
        final Group columnGroup = this.fieldLayout.createSequentialGroup();
        this.fieldLayout.setHorizontalGroup(columnGroup);
        this.columnGroups = new ArrayList<Group>(columns);
        for (int i = 0; i < columns; i++) {
            final Group g = this.fieldLayout.createParallelGroup();
            columnGroup.addGroup(g);
            this.columnGroups.add(g);
        }
        this.fieldPanel.setLayout(this.fieldLayout);
        super.contentPanel.add(this.fieldPanel);
        super.layout.putConstraint(SpringLayout.WEST, this.fieldPanel, 0, SpringLayout.NORTH, super.contentPanel);
        super.layout.putConstraint(SpringLayout.NORTH, this.fieldPanel, 5, SpringLayout.SOUTH, super.northAnchor);
    }
    
    protected JTextComponent createFieldRow(String currentValue, boolean singleValue) {
        final JTextComponent field = createField(currentValue, 550);
        this.fields.add(field);
        
        final List<Component> components = (singleValue) ? 
            createSingleValueRowComponents(field) :
            createMultiValueRowComponents(field);
        
        final Group rowGroup = this.fieldLayout.createParallelGroup(Alignment.BASELINE);
        rowGroup.addComponent(field);
        this.rowGroups.addGroup(rowGroup);
        this.columnGroups.get(0).addComponent(field);
        
        if (null != components) {
            this.rowComponents.put(field, components);
            for (int i = 0; i < components.size(); i++) {
                final Component c = components.get(i);
                rowGroup.addComponent(c);
                this.columnGroups.get(i+1).addComponent(c);
            }
        }
        
        return field;
    }
    
    protected JTextComponent createField(String currentValue, int width) {
        final JTextField field = new JTextField();
        // TODO: set columns to width / font width
        field.setColumns(40);
        if (null != currentValue) {
            field.setText(currentValue);
        }
        return field;
    }
    
    protected List<Component> createSingleValueRowComponents(JTextComponent field) {
        return null;
    }
    
    protected List<Component> createMultiValueRowComponents(JTextComponent field) {
        return Arrays.asList((Component) createRemoveButton(field));
    }
    
    protected JButton createRemoveButton(final JTextComponent field) {
        final JButton remove = new JButton("-");
        remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AbstractFieldEditorDialog.this.fields.remove(field);
                AbstractFieldEditorDialog.this.fieldPanel.remove(field);
                
                final List<Component> rowComponents = getRowComponents(field);
                if (null != rowComponents) {
                    for (Component c : rowComponents) {
                        AbstractFieldEditorDialog.this.fieldPanel.remove(c);
                    }
                }
                
                AbstractFieldEditorDialog.this.contentPanel.validate();
            }
        });
        return remove;
    }
    
    @Override
    protected boolean okPressed() {
        for (JTextComponent field : this.fields) {
            if (!validate(field)) {
                return false;
            }
        }
        return true;
    }

    protected boolean validate(JTextComponent field) {
        final String text = field.getText();
        final String[] values = (null == text) ? null : StringUtils.clean(text.split(this.property.getValueDelimiter()));
        return validateFieldValues(field, values);
    }
    
    protected boolean validateFieldValues(JTextComponent field, String[] values) {
        final int minNumValues = (super.property.getMinNumValues() == null) ? 
            1 : super.property.getMinNumValues().intValue();
        final int maxNumValues = (super.property.getMaxNumValues() == null) ?
            1 : (super.property.getMaxNumValues().equals("unbounded")) ? 
                    Integer.MAX_VALUE : Integer.valueOf(super.property.getMaxNumValues());
        final Type type = (null == super.property.getType()) ? 
            XsdTypes.ANY : TypeMapper.getType(super.property.getType()); 
        
        return validateMultiplicity(field, values, minNumValues, maxNumValues) 
            && validateType(field, values, type)
            && (null == super.property.getAllowedValues() || super.property.getAllowedValues().isEmpty() 
                    || validateAllowedValues(field, values, super.property.getAllowedValues()))
            && ((null == super.property.getMinValue() && null == super.property.getMaxValue())
                    || validateValueRange(field, values, super.property.getMinValue(), super.property.getMaxValue()));
    }
    
    protected boolean validateMultiplicity(JTextComponent field, String[] values, int minNumValues, int maxNumValues) {
        if (values.length < minNumValues || values.length > maxNumValues) {
            showValidationError("Property {0} must have between {1} and {2} values.", getPropertyDisplayName(),
                minNumValues, maxNumValues);
            return false;
        }
        return true;
    }

    protected boolean validateType(JTextComponent field, String[] values, Type type) {
        for (String value : values) {
            try {
                type.validate(value, super.property.getType().getFormat());
            }
            catch (TypeValidationException ex) {
                showValidationError("Property {0} validation error: {1}", getPropertyDisplayName(), 
                    ex.getMessage());
                return false;
            }
        }
        return true;
    }
    
    protected boolean validateAllowedValues(JTextComponent field, String[] values, List<AllowedValues> allowedValues) {
        outer: for (String value : values) {
            for (AllowedValues av : allowedValues) {
                if (av.getValues().equals(value)) {
                    continue outer;
                }
            }
            showValidationError("Property {0} may only have one of the allowed values.", getPropertyDisplayName());
            return false;
        }
        return true;
    }

    protected boolean validateValueRange(JTextComponent field, String[] values, Value minValue, Value maxValue) {
        throw new UnsupportedOperationException("Min and max values are not supported for properties of this type");
    }

    protected void showValueRangeValidationError() {
        final StringBuilder sb = new StringBuilder();
        addMinMaxValueText(sb);
        showValidationError(sb.toString());
    }
    
    protected void showValidationError(String message, Object... args) {
        JOptionPane.showMessageDialog(this, MessageFormat.format(message, args), "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    public String getStringValue() {
        return this.fields.get(0).getText();
    }
    
    public List<String> getStringValues() {
        final List<String> values = new ArrayList<String>(this.fields.size());
        for (JTextComponent field : this.fields) {
            values.add(field.getText());
        }
        return values;
    }
    
    protected JTextComponent getField(int row) {
        return this.fields.get(row);
    }
    
    protected List<Component> getRowComponents(JTextComponent field) {
        return this.rowComponents.get(field);
    }
    
    protected int getRows() {
        return this.fields.size();
    }
}
