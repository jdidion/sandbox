package net.didion.pml.type;

import java.math.BigInteger;

public interface XsdTypes {
    Type BOOLEAN = new BooleanType();
    Type DATE = new DateType();
    Type INTEGER = new IntegerType();
    Type UNSIGNED_INTEGER = new IntegerType(false);
    Type BYTE = new IntegerType(Byte.class);
    Type UNSIGNED_BYTE = new IntegerType(Byte.class, false);
    Type SHORT = new IntegerType(Short.class);
    Type UNSIGNED_SHORT = new IntegerType(Short.class, false);
    Type NEGATIVE_INTEGER = new IntegerType(null, new BigInteger("-1"));
    Type NON_POSITIVE_INTEGER = new IntegerType(null, BigInteger.ZERO);
    Type POSITIVE_INTEGER = new IntegerType(BigInteger.ONE, null);
    Type NON_NEGATIVE_INTEGER = new IntegerType(BigInteger.ZERO, null);;
    Type DECIMAL = new DecimalType(Double.class);
    Type ANY = new StringType();
}
