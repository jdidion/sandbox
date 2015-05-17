package net.didion.pml.type;

import java.math.BigInteger;

public class IntegerType extends AbstractType {
    private final BigInteger minValue;
    private final BigInteger maxValue;
    
    public IntegerType() {
        this(BigInteger.class);
    }
    
    public IntegerType(Class<?> javaType) {
        this(javaType, true);
    }

    public IntegerType(boolean signed) {
        this(BigInteger.class, signed);
    }
    
    public IntegerType(Class<?> type, boolean signed) {
        this(type, null, null, signed);
    }
    
    public IntegerType(BigInteger minValue, BigInteger maxValue) {
        this(BigInteger.class, minValue, maxValue, true);
    }
    
    public IntegerType(Class<?> javaType, BigInteger minValue, BigInteger maxValue) {
        this(javaType, minValue, maxValue, true);
    }
    
    public IntegerType(Class<?> javaType, BigInteger minValue, BigInteger maxValue, boolean signed) {
        super("integer", javaType);
        if (BigInteger.class.equals(javaType)) {
            this.minValue = this.maxValue = null;
        }
        else {
            try {
                final BigInteger min = new BigInteger(javaType.getField("MIN_VALUE").get(null).toString());
                final BigInteger max = new BigInteger(javaType.getField("MAX_VALUE").get(null).toString());
                if (signed) {
                    this.minValue = min;
                    this.maxValue = max;
                }
                else {
                    this.minValue = BigInteger.ZERO;
                    this.maxValue = max.add(min.abs());
                }
            }
            catch (Exception ex) {
                throw new RuntimeException("Java type must have MIN_VALUE and MAX_VALUE fields", ex);
            }
        }
    }

    public BigInteger getMinValue() {
        return minValue;
    }

    public BigInteger getMaxValue() {
        return maxValue;
    }
}
