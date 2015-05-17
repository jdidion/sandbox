package net.didion.pml.values;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

public final class PmlValuesUtils {
	public static PropertyValueDescriptor parse(File file) throws JAXBException {
		final JAXBContext ctx = JAXBContext.newInstance(PropertyValueDescriptor.class);
		final Unmarshaller um = ctx.createUnmarshaller();
		return um.unmarshal(new StreamSource(file), PropertyValueDescriptor.class).getValue();
	}
	
	public static final void write(PropertyValueDescriptor pd, File file) throws JAXBException {
        final JAXBContext ctx = JAXBContext.newInstance(PropertyValueDescriptor.class);
        final Marshaller m = ctx.createMarshaller();
        m.setProperty("jaxb.formatted.output", Boolean.TRUE);
        m.marshal(new JAXBElement<PropertyValueDescriptor>(
            new QName("","propertyValues"), PropertyValueDescriptor.class, pd), file);
    }
	
    public static Map<String, Object> descriptorToMap(PropertyValueDescriptor pvd) {
        final Map<String, Object> map = new HashMap<String, Object>();
        for (Property p : pvd.getProperty()) {
            Object value = p.getValue();
            if (null == value) {
                value = p.getMultivalue();
            }
            if (null != value) {
                map.put(p.getName(), value);
            }
        }
        return map;
    }
	
	private PmlValuesUtils() {
	}
}
