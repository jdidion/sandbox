package net.didion.pml.util;

import static net.didion.pml.util.StringUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import net.didion.pml.model.DataType;
import net.didion.pml.model.ObjectFactory;
import net.didion.pml.model.PmlUtils;
import net.didion.pml.model.Property;
import net.didion.pml.model.PropertyDescriptor;
import net.didion.pml.model.PropertyGroup;
import net.didion.pml.type.TypeMapper;
import net.didion.pml.util.properties.DefaultPropertyFileFormat;
import net.didion.pml.util.properties.DefaultXmlPropertyFileFormat;
import net.didion.pml.util.properties.Properties;
import net.didion.pml.util.properties.PropertyFileParser;
import net.didion.pml.util.properties.XmlPropertyFileParser;

/**
 * This class generates a PML document for a given property file. It
 * produces a best attempt, and it is likely that the resulting PML
 * file will require hand editing.
 * 
 * @author johndidion
 */
public final class PmlHelper {
    public static void main(String[] args) throws JAXBException, IOException {
        final Options options = new Options();
        options.addOption("i", true,  "The property file.");
        options.addOption("o", true,  "The output file.");
        options.addOption("x", false, "The property file is in XML format.");
        options.addOption("c", true,  "The comment character.");
        options.addOption("v", false, "Property values should be used as defaults.");
        options.addOption("m", false, "Look for commented-out properties.");
        
        final CommandLineParser parser = new PosixParser();
        CommandLine cl = null;
        try {
            cl = parser.parse(options, args);
        }
        catch (ParseException ex) {
            System.exit(1);
        }
        final File in = new File(cl.getOptionValue('i'));
        final File out = new File(cl.getOptionValue('o'));
        final boolean propertyValuesAsDefaults = cl.hasOption('v');
        final boolean addCommentedOutProperties = cl.hasOption('m');
        
        if (cl.hasOption('x')) {
            generatePmlFromXmlProperties(in, out, propertyValuesAsDefaults);
        }
        else if (cl.hasOption('c')) {
            generatePmlFromProperties(in, out, propertyValuesAsDefaults, 
                new DefaultPropertyFileFormat<String>(1, 80, '!', true, addCommentedOutProperties));
        }
        else {
            generatePmlFromProperties(in, out, propertyValuesAsDefaults);
        }
    }
    
    public static void generatePmlFromProperties(File propertiesFile, File outputFile, boolean valuesAsDefaults) 
            throws JAXBException, IOException {
        generatePmlFromProperties(propertiesFile, outputFile, valuesAsDefaults, new DefaultPropertyFileFormat<String>());
    }
    
    public static void generatePmlFromProperties(File propertiesFile, File outputFile, boolean valuesAsDefaults, 
                                                 PropertyFileParser<String> parser) 
            throws JAXBException, IOException {
        final InputStream is = new FileInputStream(propertiesFile);
        final Properties<String> props = Properties.getStringInstance();
        
        props.load(is, parser);
        
        generatePml(props, valuesAsDefaults, outputFile);
    }
    
    public static void generatePmlFromXmlProperties(File propertiesFile, File outputFile, boolean valuesAsDefaults) 
            throws JAXBException, IOException {
        generatePmlFromXmlProperties(propertiesFile, outputFile, valuesAsDefaults, new DefaultXmlPropertyFileFormat<String>());
    }
    
    public static void generatePmlFromXmlProperties(File propertiesFile, File outputFile, boolean valuesAsDefaults,
                                                    XmlPropertyFileParser<String> parser) 
            throws JAXBException, IOException {
        final InputStream is = new FileInputStream(propertiesFile);
        final Properties<String> props = Properties.getStringInstance();
        
        props.loadFromXml(is, parser);
        
        generatePml(props, valuesAsDefaults, outputFile);
    }
    
    public static void generatePml(Properties<String> props,  boolean valuesAsDefaults, File outputFile) 
            throws JAXBException, IOException {
        final ObjectFactory of = new ObjectFactory(); 
        final PropertyDescriptor desc = of.createPropertyDescriptor();
        final Map<String, Map<String, Properties<String>.Property>> groups  = props.getGroups();
        
        final Map<String, Properties<String>.Property> defaultGroup = groups.remove(Properties.DEFAULT_GROUP_NAME);
        addProperties(defaultGroup, valuesAsDefaults, desc.getProperty(), of);
        
        for (String group : groups.keySet()) {
            final PropertyGroup pg = of.createPropertyGroup();
            pg.setName(group);
            desc.getGroup().add(pg);
            addProperties(groups.get(group), valuesAsDefaults, pg.getProperty(), of);
        }
        
        PmlUtils.write(desc, outputFile);
    }
    
    private static void addProperties(Map<String, Properties<String>.Property> group, boolean valuesAsDefaults,
                                      List<Property> dest, ObjectFactory of) {
        if (null == group || group.isEmpty()) {
            return;
        }
        for (String key : group.keySet()) {
            final Properties<String>.Property from = group.get(key);
            final Property to = of.createProperty();
            to.setName(key);
            to.setDisplayName(camelCaseToTitle(key));
            to.setDescription(from.getComment());
            
            final String value = from.getValue();
            if (valuesAsDefaults) {
                to.setDefaultValues(value);
            }
            final DataType dt = of.createDataType();
            dt.setValue(TypeMapper.findTypeForValue(value, null));
            to.setType(dt);
            
            dest.add(to);
        }
    }
    
    private PmlHelper() {
    }
}
