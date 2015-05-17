package net.didion.pml.editor.dialogs;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import net.didion.pml.model.Property;
import net.didion.pml.type.Type;
import net.didion.pml.type.TypeMapper;

public class DefaultEditorDialogFactory extends AbstractEditorDialogFactory {
    private static final Map<Class<?>,Class<? extends AbstractEditorDialog>> typeToDialogClass = 
        new HashMap<Class<?>,Class<? extends AbstractEditorDialog>>();
    
    static {
        typeToDialogClass.put(Date.class, DateEditorDialog.class);
        typeToDialogClass.put(Calendar.class, DateEditorDialog.class);
        typeToDialogClass.put(Integer.class, NumberEditorDialog.class);
        typeToDialogClass.put(Long.class, NumberEditorDialog.class);
        typeToDialogClass.put(Short.class, NumberEditorDialog.class);
        typeToDialogClass.put(Byte.class, NumberEditorDialog.class);
        typeToDialogClass.put(Double.class, NumberEditorDialog.class);
        typeToDialogClass.put(Float.class, NumberEditorDialog.class);
        typeToDialogClass.put(BigInteger.class, NumberEditorDialog.class);
        typeToDialogClass.put(BigDecimal.class, NumberEditorDialog.class);
        typeToDialogClass.put(Character.class, StringEditorDialog.class);
        typeToDialogClass.put(String.class, StringEditorDialog.class);
        typeToDialogClass.put(Boolean.class, BooleanEditorDialog.class);
    }
    
    @Override
    public boolean edits(Property property) {
        final Type type = TypeMapper.getType(property.getType());
        return typeToDialogClass.containsKey(type.getJavaType());
    }

    @Override
    protected AbstractEditorDialog createEditorDialog(JFrame parent, Property property) {
        final Type type = TypeMapper.getType(property.getType());
        final Class<? extends AbstractEditorDialog> dialogClass = typeToDialogClass.get(type.getJavaType());
        
        try {
            final Constructor<? extends AbstractEditorDialog> c = dialogClass.getConstructor(JFrame.class, Property.class);
            return c.newInstance(parent, property);
        }
        catch (Exception ex) {
            throw new RuntimeException("No dialog class for type " + property.getType(), ex);
        }
    }
}
