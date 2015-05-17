package net.didion.pml.editor.dialogs;

import static net.didion.pml.util.swing.SwingUtils.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.text.JTextComponent;

import net.didion.pml.model.Property;
import net.didion.pml.model.Value;
import net.didion.pml.type.TypeMapper;

public class NumberEditorDialog extends AbstractFieldEditorDialog {
    private JRadioButton manualButton;
    private JRadioButton rangeButton;
    private JTextField rangeMin;
    private JTextField rangeMax;
    private JTextField rangeStep;
    
    public NumberEditorDialog(JFrame parent, Property property) {
		super(parent, property);
    }
    
    @Override
    protected void addMultiValueComponentsBeforeFields(boolean hasValues) {
        if (super.property.getMinNumValues().compareTo(BigInteger.ONE) > 0) {
            return;
        }
        
        final ButtonGroup bg = new ButtonGroup();
        
        this.rangeButton = new JRadioButton("Range:", !hasValues);
        this.rangeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NumberEditorDialog.this.rangeMin.setEnabled(true);
                NumberEditorDialog.this.rangeMax.setEnabled(true);
                NumberEditorDialog.this.rangeStep.setEnabled(true);
                for (JTextComponent field : NumberEditorDialog.super.fields) {
                    field.setEnabled(false);
                }
            }
        });
        super.contentPanel.add(this.rangeButton);
        super.layout.putConstraint(SpringLayout.WEST, this.rangeButton, 0, SpringLayout.WEST, super.contentPanel);
        super.layout.putConstraint(SpringLayout.NORTH, this.rangeButton, 5, SpringLayout.SOUTH, super.northAnchor);
        bg.add(this.rangeButton);
        
        this.rangeMin = new JTextField();
        this.rangeMin.setColumns(5);
        this.rangeMin.setEnabled(!hasValues);
        super.contentPanel.add(this.rangeMin);
        super.layout.putConstraint(SpringLayout.WEST, this.rangeMin, 5, SpringLayout.EAST, this.rangeButton);
        super.layout.putConstraint(SpringLayout.NORTH, this.rangeMin, 5, SpringLayout.SOUTH, super.northAnchor);
        
        final JLabel to = new JLabel("to");
        super.contentPanel.add(to);
        super.layout.putConstraint(SpringLayout.WEST, to, 5, SpringLayout.EAST, this.rangeMin);
        super.layout.putConstraint(SpringLayout.BASELINE, to, 0, SpringLayout.BASELINE, this.rangeMin);
        
        this.rangeMax = new JTextField();
        this.rangeMax.setColumns(5);
        this.rangeMax.setEnabled(!hasValues);
        super.contentPanel.add(this.rangeMax);
        super.layout.putConstraint(SpringLayout.WEST, this.rangeMax, 5, SpringLayout.EAST, to);
        super.layout.putConstraint(SpringLayout.NORTH, this.rangeMax, 5, SpringLayout.SOUTH, super.northAnchor);
        
        final JLabel step = new JLabel("Step:");
        super.contentPanel.add(step);
        super.layout.putConstraint(SpringLayout.WEST, step, 5, SpringLayout.EAST, this.rangeMax);
        super.layout.putConstraint(SpringLayout.BASELINE, step, 0, SpringLayout.BASELINE, this.rangeMax);
        
        this.rangeStep = new JTextField();
        this.rangeStep.setColumns(5);
        this.rangeStep.setEnabled(!hasValues);
        this.rangeStep.setText("1");
        super.contentPanel.add(this.rangeStep);
        super.layout.putConstraint(SpringLayout.WEST, this.rangeStep, 5, SpringLayout.EAST, step);
        super.layout.putConstraint(SpringLayout.NORTH, this.rangeStep, 5, SpringLayout.SOUTH, super.northAnchor);
        
        this.manualButton = new JRadioButton("Manual:", hasValues);
        this.manualButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NumberEditorDialog.this.rangeMin.setEnabled(false);
                NumberEditorDialog.this.rangeMax.setEnabled(false);
                NumberEditorDialog.this.rangeStep.setEnabled(false);
                //for (JTextField field : NumberEditorDialog.this.manualFields) {
                //    field.setEnabled(true);
                //}
                NumberEditorDialog.super.fieldPanel.setEnabled(true);
            }
        });
        super.contentPanel.add(this.manualButton);
        super.layout.putConstraint(SpringLayout.WEST, this.manualButton, 0, SpringLayout.WEST, super.contentPanel);
        super.layout.putConstraint(SpringLayout.NORTH, this.manualButton, 5, SpringLayout.SOUTH, this.rangeButton);
        bg.add(this.manualButton);
        
        super.northAnchor = this.manualButton;
    }

    @Override
    protected String getExplanationText() {
        final StringBuilder sb = new StringBuilder("This is a ");
        
        if (isDecimal()) {
            sb.append("decimal");
        }
        else {
            sb.append("integer");
        }
        
        sb.append(" property. ");
        
        addMinMaxValueText(sb);
        addMinMaxNumValuesText(sb);
        
        return sb.toString();
    }
    
    private boolean isDecimal() {
        final Class<?> c = TypeMapper.getType(super.property.getType()).getJavaType();
        return c.equals(Double.class) || c.equals(Float.class);
    }
    
    @Override
    protected boolean okPressed() {
        if (null != this.rangeButton && this.rangeButton.isSelected()) {
            if (isEmpty(this.rangeMin) || isEmpty(this.rangeMax) || isEmpty(this.rangeStep)) {
                showValidationError("Range min, max, and step must be specified.");
                return false;
            }
            
            final Value minValue = super.property.getMinValue();
            final BigDecimal min = (null == minValue || null == minValue.getValue()) ? null : new BigDecimal(minValue.getValue());
            final Value maxValue = super.property.getMaxValue();
            final BigDecimal max = (null == maxValue || null == maxValue.getValue()) ? null : new BigDecimal(maxValue.getValue());
            
            final BigDecimal rangeMin = new BigDecimal(this.rangeMin.getText());
            final BigDecimal rangeMax = new BigDecimal(this.rangeMax.getText());
            
            if (min != null && min.compareTo(rangeMin) > 0) {
                showValidationError("The range minimum must be at least {0}", min);
            }
            if (max != null && max.compareTo(rangeMax) < 0) {
                showValidationError("The range maximum must be at most {0}", max);
            }
            
            return true;
        }
        else {
            return super.okPressed();
        }
    }
    
    @Override
    protected boolean validateValueRange(JTextComponent field, String[] values, Value minValue, Value maxValue) {
        final BigDecimal min = (null == minValue || null == minValue.getValue()) ? null : new BigDecimal(minValue.getValue());
        final BigDecimal max = (null == maxValue || null == maxValue.getValue()) ? null : new BigDecimal(maxValue.getValue());
        
        for (String value : values) {
            final BigDecimal i = new BigDecimal(value);
            final int minCompare = (null == min) ? -1 : min.compareTo(i);
            final boolean minInclusive = (null == min) || minValue.isInclusive();
            final int maxCompare = (null == max) ? 1 : max.compareTo(i);
            final boolean maxInclusive = (null == max) || maxValue.isInclusive();
            if (!(minInclusive ? minCompare <= 0 : minCompare < 0)
             || !(maxInclusive ? maxCompare >= 0 : maxCompare > 0)) {
                showValueRangeValidationError();
                return false;
            }    
        }
        
        return true;
    }

    public List<String> getStringValues() {
        if (this.rangeButton.isSelected()) {
            final BigDecimal rangeMin = new BigDecimal(this.rangeMin.getText());
            final BigDecimal rangeMax = new BigDecimal(this.rangeMax.getText());
            final BigDecimal rangeStep = new BigDecimal(this.rangeStep.getText());
            
            BigDecimal value = rangeMin;
            final List<String> values = new ArrayList<String>(rangeMax.subtract(value).divide(rangeStep).intValue()+1);
            while (value.compareTo(rangeMax) <= 0) {
                values.add(value.toString());
                value = value.add(rangeStep);
            }
            
            return values;
        }
        else {
            return super.getStringValues();
        }
    }    
}
