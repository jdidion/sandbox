package net.didion.pml.type;

import java.math.BigDecimal;

public class DecimalType extends AbstractType {
    private final BigDecimal minValue;
    private final BigDecimal maxValue;
    
    public DecimalType() {
        this(BigDecimal.class);
    }
    
    public DecimalType(Class<?> javaType) {
        this(javaType, true);
    }

    public DecimalType(boolean signed) {
        this(BigDecimal.class, signed);
    }
    
    public DecimalType(Class<?> type, boolean signed) {
        this(type, null, null, signed);
    }
    
    public DecimalType(BigDecimal minValue, BigDecimal maxValue) {
        this(BigDecimal.class, minValue, maxValue, true);
    }
    
    public DecimalType(Class<?> javaType, BigDecimal minValue, BigDecimal maxValue) {
        this(javaType, minValue, maxValue, true);
    }
    
    public DecimalType(Class<?> javaType, BigDecimal minValue, BigDecimal maxValue, boolean signed) {
        super("decimal", javaType);
        if (BigDecimal.class.equals(javaType)) {
            this.minValue = this.maxValue = null;
        }
        else {
            try {
                final BigDecimal min = new BigDecimal(javaType.getField("MIN_VALUE").get(null).toString());
                final BigDecimal max = new BigDecimal(javaType.getField("MAX_VALUE").get(null).toString());
                if (signed) {
                    this.minValue = min;
                    this.maxValue = max;
                }
                else {
                    this.minValue = BigDecimal.ZERO;
                    this.maxValue = max.add(min.abs());
                }
            }
            catch (Exception ex) {
                throw new RuntimeException("Java type must have MIN_VALUE and MAX_VALUE fields", ex);
            }
        }
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }
}
