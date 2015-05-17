package net.didion.pml;

import java.util.List;

import net.didion.pml.model.Property;

public interface PropertyModelListener {
    void propertyEnabled(Property property);
    
    void propertyDisabled(Property property);
    
    void propertyValueChanged(Property property, String oldValue, String newValue);
    
    void variablePropertyValueChanged(Property property, List<String> oldValue, List<String> newValue);
    
    void variablePropertyAdded(Property property, String oldValue, List<String> newValue);
    
    void variablePropertyRemoved(Property property, List<String> oldValue, String newValue);
}
