package net.didion.pml.type;

import static net.didion.pml.type.XsdTypes.*;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import net.didion.pml.model.DataType;

public final class TypeMapper {
    private static final String NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";
    private static final Map<QName,Type> xsdToJavaType = new LinkedHashMap<QName,Type>();
    private static final QName ANY_TYPE = new QName(NAMESPACE_URI, "any");
    
    static {
        xsdToJavaType.put(new QName(NAMESPACE_URI, "boolean"), BOOLEAN);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "date"), DATE);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "time"), DATE);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "datetime"), DATE);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "integer"), INTEGER);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "int"), INTEGER);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "long"), INTEGER);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "byte"), BYTE);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "short"), SHORT);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "negativeInteger"), NEGATIVE_INTEGER);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "positiveInteger"), POSITIVE_INTEGER);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "nonNegativeInteger"), NON_NEGATIVE_INTEGER);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "nonPositiveInteger"), NON_POSITIVE_INTEGER);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "unsignedInt"), UNSIGNED_INTEGER);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "unsignedLong"), UNSIGNED_INTEGER);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "unsignedByte"), UNSIGNED_BYTE);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "unsignedShort"), UNSIGNED_SHORT);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "decimal"), DECIMAL);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "double"), DECIMAL);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "string"), ANY);
        xsdToJavaType.put(new QName(NAMESPACE_URI, "any"), ANY);
    }
    
    public static Type getType(String xsdType) {
        if (null == xsdType) {
            return ANY;
        }
        return xsdToJavaType.get(new QName(NAMESPACE_URI, xsdType));
    }
    
    public static Type getType(DataType dataType) {
        if (null == dataType || null == dataType.getValue()) {
            return ANY;
        }
        return getType(dataType.getValue());
    }
    
    public static Type getType(QName qname) {
        if (null == qname) {
            return ANY;
        }
        return xsdToJavaType.get(qname);
    }
    
    public static QName findTypeForValue(String value, String format) {
        QName type = ANY_TYPE;
        if (null != value) {
            for (QName t : xsdToJavaType.keySet()) {
                if (xsdToJavaType.get(t).parse(value, format) != null) {
                    type = t;
                    break;
                }
            }
        }
        return type;
    }
    
    private TypeMapper() {
    }    
}
