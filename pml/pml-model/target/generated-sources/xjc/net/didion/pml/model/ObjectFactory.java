//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-661 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.04.08 at 12:20:49 PM EDT 
//


package net.didion.pml.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.didion.pml.model package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Properties_QNAME = new QName("http://www.didion.net/pml/1.0", "properties");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.didion.pml.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link AllowedValues }
     * 
     */
    public AllowedValues createAllowedValues() {
        return new AllowedValues();
    }

    /**
     * Create an instance of {@link DataType }
     * 
     */
    public DataType createDataType() {
        return new DataType();
    }

    /**
     * Create an instance of {@link PropertyDescriptor }
     * 
     */
    public PropertyDescriptor createPropertyDescriptor() {
        return new PropertyDescriptor();
    }

    /**
     * Create an instance of {@link PropertyGroup }
     * 
     */
    public PropertyGroup createPropertyGroup() {
        return new PropertyGroup();
    }

    /**
     * Create an instance of {@link Value }
     * 
     */
    public Value createValue() {
        return new Value();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyDescriptor }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.didion.net/pml/1.0", name = "properties")
    public JAXBElement<PropertyDescriptor> createProperties(PropertyDescriptor value) {
        return new JAXBElement<PropertyDescriptor>(_Properties_QNAME, PropertyDescriptor.class, null, value);
    }

}