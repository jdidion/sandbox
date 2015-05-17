package net.didion.pml.editor.dialogs;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.JXDatePicker;

import net.didion.pml.model.Property;
import net.didion.pml.model.Value;

public class DateEditorDialog extends AbstractFieldEditorDialog {
    public static final DateFormat DEFAULT_DATE_FORMAT = DateFormat.getDateInstance();
    
    private DateFormat format;
    
    public DateEditorDialog(JFrame parent, Property property) {
        super(parent, property);
        this.format = (null == property.getType().getFormat()) ? 
            DEFAULT_DATE_FORMAT : new SimpleDateFormat(property.getType().getFormat());
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
    protected List<Component> createSingleValueRowComponents(JTextComponent field) {
        return Arrays.asList((Component) createChooseButton(field));
    }
    
    @Override
    protected List<Component> createMultiValueRowComponents(JTextComponent field) {
        return Arrays.asList((Component) createChooseButton(field), (Component) createRemoveButton(field));
    }

    private JButton createChooseButton(final JTextComponent field) {
        final JButton choose = new JButton("Choose");
        choose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JXDatePicker picker = new JXDatePicker();
                picker.setFormats(DateEditorDialog.this.format);
                picker.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (JXDatePicker.COMMIT_KEY.equals(e.getActionCommand())) {
                            final Date date = picker.getDate();
                            field.setText(format.format(date));
                        }
                    }
                });
            }
        });
        return choose;
    }
    
    @Override
    protected String getExplanationText() {
        final StringBuilder sb = new StringBuilder();
        sb.append("This is a date property.");
        addMinMaxValueText(sb);
        addMinMaxNumValuesText(sb);
        return sb.toString();
    }
    
    @Override
    protected boolean validateValueRange(JTextComponent field, String[] values, Value minValue, Value maxValue) {
        try {
            final Date min = (null == minValue) ? null : this.format.parse(minValue.getValue());
            final Date max = (null == maxValue) ? null : this.format.parse(maxValue.getValue());
            
            for (String value : values) {
                final Date date = this.format.parse(value);
                final int minCompare = (null == min) ? -1 : min.compareTo(date);
                final boolean minInclusive = (null == min) || minValue.isInclusive();
                final int maxCompare = (null == max) ? 1 : max.compareTo(date);
                final boolean maxInclusive = (null == max) || maxValue.isInclusive();
                if (!(minInclusive ? minCompare <= 0 : minCompare < 0)
                 || !(maxInclusive ? maxCompare >= 0 : maxCompare > 0)) {
                    showValueRangeValidationError();
                    return false;
                }
            }
        }
        catch (Exception ex) {
            return false;
        }
        
        return true;
    }
}
