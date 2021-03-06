//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-661 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.04.08 at 12:20:49 PM EDT 
//


package net.didion.pml.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Property complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Property">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="displayName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.didion.net/pml/1.0}DataType"/>
 *         &lt;element name="editable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="minValue" type="{http://www.didion.net/pml/1.0}Value" minOccurs="0"/>
 *         &lt;element name="maxValue" type="{http://www.didion.net/pml/1.0}Value" minOccurs="0"/>
 *         &lt;element name="minNumValues" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="maxNumValues" type="{http://www.didion.net/pml/1.0}multiplicity" minOccurs="0"/>
 *         &lt;element name="valueDelimiter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="defaultValues" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="allowedValues" type="{http://www.didion.net/pml/1.0}AllowedValues" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Property", propOrder = {
    "name",
    "displayName",
    "description",
    "type",
    "editable",
    "minValue",
    "maxValue",
    "minNumValues",
    "maxNumValues",
    "valueDelimiter",
    "defaultValues",
    "allowedValues"
})
public class Property {

    @XmlElement(required = true)
    protected String name;
    protected String displayName;
    protected String description;
    @XmlElement(required = true)
    protected DataType type;
    @XmlElement(defaultValue = "true")
    protected Boolean editable;
    protected Value minValue;
    protected Value maxValue;
    @XmlElement(defaultValue = "1")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger minNumValues;
    @XmlElement(defaultValue = "1")
    protected String maxNumValues;
    @XmlElement(defaultValue = ",")
    protected String valueDelimiter;
    protected String defaultValues;
    protected List<AllowedValues> allowedValues;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the displayName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link DataType }
     *     
     */
    public DataType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataType }
     *     
     */
    public void setType(DataType value) {
        this.type = value;
    }

    /**
     * Sets the value of the editable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEditable(Boolean value) {
        this.editable = value;
    }

    /**
     * Gets the value of the minValue property.
     * 
     * @return
     *     possible object is
     *     {@link Value }
     *     
     */
    public Value getMinValue() {
        return minValue;
    }

    /**
     * Sets the value of the minValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Value }
     *     
     */
    public void setMinValue(Value value) {
        this.minValue = value;
    }

    /**
     * Gets the value of the maxValue property.
     * 
     * @return
     *     possible object is
     *     {@link Value }
     *     
     */
    public Value getMaxValue() {
        return maxValue;
    }

    /**
     * Sets the value of the maxValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Value }
     *     
     */
    public void setMaxValue(Value value) {
        this.maxValue = value;
    }

    /**
     * Sets the value of the minNumValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMinNumValues(BigInteger value) {
        this.minNumValues = value;
    }

    /**
     * Sets the value of the maxNumValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaxNumValues(String value) {
        this.maxNumValues = value;
    }

    /**
     * Sets the value of the valueDelimiter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueDelimiter(String value) {
        this.valueDelimiter = value;
    }

    /**
     * Gets the value of the defaultValues property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultValues() {
        return defaultValues;
    }

    /**
     * Sets the value of the defaultValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultValues(String value) {
        this.defaultValues = value;
    }

    /**
     * Gets the value of the allowedValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allowedValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllowedValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AllowedValues }
     * 
     * 
     */
    public List<AllowedValues> getAllowedValues() {
        if (allowedValues == null) {
            allowedValues = new ArrayList<AllowedValues>();
        }
        return this.allowedValues;
    }

    /**
     * Gets the value of the editable property.
     * 
     */
    public Boolean isEditable() {
        if (null == editable) {
            return (Boolean.TRUE);
        } else {
            return editable;
        }
    }

    /**
     * Gets the value of the minNumValues property.
     * 
     */
    public BigInteger getMinNumValues() {
        if (null == minNumValues) {
            return new BigInteger("1");
        } else {
            return minNumValues;
        }
    }

    /**
     * Gets the value of the maxNumValues property.
     * 
     */
    public String getMaxNumValues() {
        if (null == maxNumValues) {
            return "1";
        } else {
            return maxNumValues;
        }
    }

    /**
     * Gets the value of the valueDelimiter property.
     * 
     */
    public String getValueDelimiter() {
        if (null == valueDelimiter) {
            return ",";
        } else {
            return valueDelimiter;
        }
    }

}
