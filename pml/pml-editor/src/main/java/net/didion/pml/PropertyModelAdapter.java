package net.didion.pml;

import java.util.List;

import net.didion.pml.model.Property;

public class PropertyModelAdapter implements PropertyModelListener {
    @Override
    public void propertyValueChanged(Property property, String oldValue, String newValue) {
    }

    @Override
    public void variablePropertyAdded(Property property, String oldValue, List<String> newValue) {
    }

    @Override
    public void variablePropertyRemoved(Property property, List<String> oldValue, String newValue) {
    }

    @Override
    public void variablePropertyValueChanged(Property property, List<String> oldValue, List<String> newValue) {
    }

    @Override
    public void propertyDisabled(Property property) {
    }

    @Override
    public void propertyEnabled(Property property) {
    }
}
