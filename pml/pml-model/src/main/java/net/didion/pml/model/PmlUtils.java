package net.didion.pml.model;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import net.didion.pml.model.PropertyDescriptor;

public final class PmlUtils {
	public static PropertyDescriptor parse(File file) throws JAXBException {
		final JAXBContext ctx = JAXBContext.newInstance(PropertyDescriptor.class);
		final Unmarshaller um = ctx.createUnmarshaller();
		return um.unmarshal(new StreamSource(file), PropertyDescriptor.class).getValue();
	}
	
	public static final void write(PropertyDescriptor pd, File file) throws JAXBException {
	    final JAXBContext ctx = JAXBContext.newInstance(PropertyDescriptor.class);
        final Marshaller m = ctx.createMarshaller();
        m.setProperty("jaxb.formatted.output", Boolean.TRUE);
        m.marshal(new JAXBElement<PropertyDescriptor>(new QName("","properties"), PropertyDescriptor.class, pd), file);
	}
	
	private PmlUtils() {
	}
}
