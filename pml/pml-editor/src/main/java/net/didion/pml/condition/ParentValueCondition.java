package net.didion.pml.condition;

import net.didion.pml.PropertyCondition;
import net.didion.pml.PropertyModel;
import net.didion.pml.model.Property;
import net.didion.pml.util.StringUtils;

/**
 * This condition is satisfied when the specified parent has the specified value.
 * 
 * @author johndidion
 */
public class ParentValueCondition implements PropertyCondition {
    private Property parent;
    private String value;
    
    public ParentValueCondition(Property parent, String value) {
        this.parent = parent;
        this.value = value;
    }

    public Property getParent() {
        return parent;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean isSatisfied(PropertyModel model) {
        final String currentValue = model.getValue(this.parent);
        return (StringUtils.nullOrEqual(this.value, currentValue));
    }    
}
