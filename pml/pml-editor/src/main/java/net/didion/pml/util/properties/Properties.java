package net.didion.pml.util.properties;

import static net.didion.pml.util.StringUtils.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.util.Collections;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This is an alternative to java.util.Properties. It is *not* a drop-in replacement. Some methods
 * have different names. The following are the major differences:
 * <ul>
 * <li>Support for references. ${key} references another property, @{key} references a system
 * property, and #{key} references an environment variable.</li>
 * <li>Better support for property hierarchy. A Properties object can have a parent Properties
 * object from which it obtains values for properties it does not contain. The parent Properties
 * can be set after the object has been created as well as in the constructor.</li>
 * <li>Support for property metadata. Every property can have a comment and a group. These are
 * used when storing a property file.</li>
 * </li>
 * <li>Setting a property to null does not throw a NullPointerException. Instead it simply 
 * removes the property.</li>
 * </ul>
 * 
 * Note: This class is generic in order to support future enhancement for type support. 
 * Convenience methods are provided to get an instance that stores only String values.
 * 
 * @author johndidion
 */
public class Properties<T> implements Serializable, Cloneable {
    public static final String DEFAULT_GROUP_NAME = "General";
    private static final Pattern REF_PATTERN = Pattern.compile("([@#\\$])\\{([^\\}]*)\\}");
    
    private static final String EXTERNAL_XML_VERSION = "1.0";
    private static final String PROPS_DTD_URI = "http://java.sun.com/dtd/properties.dtd";
    private static final String PROPS_DTD =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<!-- DTD for properties -->"                +
        "<!ELEMENT properties ( comment?, entry* ) >"+
        "<!ATTLIST properties"                       +
            " version CDATA #FIXED \"1.0\">"         +
        "<!ELEMENT comment (#PCDATA) >"              +
        "<!ELEMENT entry (#PCDATA) >"                +
        "<!ATTLIST entry "                           +
            " key CDATA #REQUIRED>";
    
    public static Properties<String> getStringInstance() {
        return new Properties<String>(String.class);
    }
    
    public static Properties<String> getStringInstance(Properties<String> parent) {
        return new Properties<String>(String.class, parent);
    }
    
    private Class<T> valueType;
    private Map<String, Property> properties;
    private Properties<T> parent;
    private String comment;
    private transient PropertyFileParser<T> defaultPropertyFileParser;
    private transient PropertyFileFormatter<T> defaultPropertyFileFormatter;
    private transient XmlPropertyFileParser<T> defaultXmlPropertyFileParser;
    private transient XmlPropertyFileFormatter<T> defaultXmlPropertyFileFormatter;
    
    public Properties(Class<T> valueType) {
        this(valueType, null);
    }
    
    public Properties(Class<T> valueType, Properties<T> parent) {
        this.valueType = valueType;
        this.properties = new LinkedHashMap<String, Property>();
        this.parent = parent;
    }
    
    public Class<T> getValueType() {
        return this.valueType;
    }
    
    public Properties<T> getParent() {
        return this.parent;
    }
    
    public synchronized void setParent(Properties<T> parent) {
        this.parent = parent;
    }
    
    public PropertyFileParser<T> getDefaultPropertyFileParser() {
        if (null == this.defaultPropertyFileParser) {
            this.defaultPropertyFileParser = new DefaultPropertyFileFormat<T>();
        }
        return defaultPropertyFileParser;
    }

    public void setDefaultPropertyFileParser(PropertyFileParser<T> defaultPropetyFileParser) {
        this.defaultPropertyFileParser = defaultPropetyFileParser;
    }

    /**
     * Load a property file from a Reader. See the equivalent method 
     * in java.util.Properties for a detailed explanation of how
     * property file loading is done. This method differs in the
     * following ways:
     * 
     * 
     * @param  reader   the input character stream.
     * @throws IOException  if an error occurred when reading from the
     *         input stream.
     * @throws IllegalArgumentException if a malformed Unicode escape
     *         appears in the input.
     */
    public synchronized void load(Reader reader) throws IOException {
        load(reader, getDefaultPropertyFileParser());
    }
    
    /**
     * Load a property file from a Reader. See the equivalent method 
     * in java.util.Properties for a detailed explanation of how
     * property file loading is done. This method differs in the
     * following ways:
     * 
     * @param  reader   the input character stream.
     * @param  parser
     * @throws IOException  if an error occurred when reading from the
     *         input stream.
     * @throws IllegalArgumentException if a malformed Unicode escape
     *         appears in the input.
     */
    public synchronized void load(Reader reader, PropertyFileParser<T> parser) throws IOException {
        load(new LineReader(reader), parser);
    }
    
   /**
    * Reads a property list (key and element pairs) from the input
    * byte stream. The input stream is in a simple line-oriented
    * format as specified in
    * {@link #load(java.io.Reader) load(Reader)} and is assumed to use
    * the ISO 8859-1 character encoding; that is each byte is one Latin1
    * character. Characters not in Latin1, and certain special characters,
    * are represented in keys and elements using
    * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.3">Unicode escapes</a>.
    * <p>
    * The specified stream remains open after this method returns.
    *
    * @param      in   the input stream.
    * @exception  IOException  if an error occurred when reading from the
    *             input stream.
    * @throws     IllegalArgumentException if the input stream contains a
    *             malformed Unicode escape sequence.
    */
    public synchronized void load(InputStream in) throws IOException {
        load(in, getDefaultPropertyFileParser());
    }
    
    /**
     * Reads a property list (key and element pairs) from the input
     * byte stream. The input stream is in a simple line-oriented
     * format as specified in
     * {@link #load(java.io.Reader) load(Reader)} and is assumed to use
     * the ISO 8859-1 character encoding; that is each byte is one Latin1
     * character. Characters not in Latin1, and certain special characters,
     * are represented in keys and elements using
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.3">Unicode escapes</a>.
     * <p>
     * The specified stream remains open after this method returns.
     *
     * @param      in   the input stream.
     * @param      parser
     * @exception  IOException  if an error occurred when reading from the
     *             input stream.
     * @throws     IllegalArgumentException if the input stream contains a
     *             malformed Unicode escape sequence.
     */
     public synchronized void load(InputStream in, PropertyFileParser<T> parser) throws IOException {
         load(new LineReader(in), parser);
     }
    
    private synchronized void load(LineReader reader, PropertyFileParser<T> parser) throws IOException {
        if (reader.readLine() < 0) {
            return;
        }
        
        String group = null;
        String propertyComment = null; 
        
        if (reader.isComment) {
            final Comment comment = parser.parseComment(reader);
            if (null != comment) {
                switch (comment.type) {
                case FILE_HEADER:
                    this.comment = comment.comment;
                    break;
                case GROUP_NAME:
                    group = comment.comment;
                    break;
                case PROPERTY_COMMENT:
                    propertyComment = comment.comment;
                    break;
                case COMMENTED_PROPERTY:
                    final KeyValue<T> property = parser.parseProperty(comment.comment);
                    if (null == property) {
                        throw new InvalidPropertiesFormatException("Invalid property");
                    }
                    if (!hasProperty(property.key)) {
                        setProperty(property.key, property.value, group, propertyComment);
                    }
                    propertyComment = null;
                    break;
                }
            }
        }
        
        while (reader.limit >= 0) {
            if (reader.isComment) {
                final Comment comment = parser.parseComment(reader);
                if (null != comment) {
                    switch(comment.type) {
                    case GROUP_NAME:
                        group = comment.comment;
                        break;
                    case PROPERTY_COMMENT:
                        propertyComment = comment.comment;
                        break;
                    case COMMENTED_PROPERTY:
                        final KeyValue<T> property = parser.parseProperty(comment.comment);
                        if (null == property) {
                            throw new InvalidPropertiesFormatException("Invalid property");
                        }
                        if (!hasProperty(property.key)) {
                            setProperty(property.key, property.value, group, propertyComment);
                        }
                        propertyComment = null;
                        break;
                    }
                }
            }
            else {
                final KeyValue<T> kv = parser.parseProperty(reader);
                setProperty(kv.key, kv.value, group, propertyComment);
                propertyComment = null;
            }
        }
    }
    
    public XmlPropertyFileParser<T> getDefaultXmlPropertyFileParser() {
        if (null == this.defaultXmlPropertyFileParser) {
            this.defaultXmlPropertyFileParser = new DefaultXmlPropertyFileFormat<T>();
        }
        return this.defaultXmlPropertyFileParser;
    }

    public void setDefaultXmlPropertyFileParser(XmlPropertyFileParser<T> defaultXmlPropertyFileParser) {
        this.defaultXmlPropertyFileParser = defaultXmlPropertyFileParser;
    }

    /**
     * Loads all of the properties represented by the XML document on the
     * specified input stream into this properties table.
     *
     * <p>The XML document must have the following DOCTYPE declaration:
     * <pre>
     * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
     * </pre>
     * Furthermore, the document must satisfy the properties DTD described
     * above.
     *
     * @param in the input stream from which to read the XML document.
     * @throws IOException if reading from the specified input stream
     *         results in an <tt>IOException</tt>.
     * @throws InvalidPropertiesFormatException Data on input stream does not
     *         constitute a valid XML document with the mandated document type.
     * @throws NullPointerException if <code>in</code> is null.
     * @see    #storeToXML(OutputStream, String, String)
     */
    public synchronized void loadFromXml(InputStream in) throws IOException, InvalidPropertiesFormatException {
        loadFromXml(in, getDefaultXmlPropertyFileParser());
    }
    
    /**
     * Loads all of the properties represented by the XML document on the
     * specified input stream into this properties table.
     *
     * <p>The XML document must have the following DOCTYPE declaration:
     * <pre>
     * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
     * </pre>
     * Furthermore, the document must satisfy the properties DTD described
     * above.
     *
     * @param in the input stream from which to read the XML document.
     * @param parser
     * @throws IOException if reading from the specified input stream
     *         results in an <tt>IOException</tt>.
     * @throws InvalidPropertiesFormatException Data on input stream does not
     *         constitute a valid XML document with the mandated document type.
     * @throws NullPointerException if <code>in</code> is null.
     * @see    #storeToXML(OutputStream, String, String)
     */
    public synchronized void loadFromXml(InputStream in, XmlPropertyFileParser<T> parser) 
            throws IOException, InvalidPropertiesFormatException {
        loadFromXml(new InputSource(in), parser);
    }
    
    public synchronized void loadFromXml(Reader reader) throws IOException, InvalidPropertiesFormatException  {
        loadFromXml(reader, getDefaultXmlPropertyFileParser());
    }
    
    public synchronized void loadFromXml(Reader reader, XmlPropertyFileParser<T> parser) 
            throws IOException, InvalidPropertiesFormatException  {
        loadFromXml(new InputSource(reader), parser);
    }
    
    public synchronized void loadFromXml(InputSource is, XmlPropertyFileParser<T> parser) 
            throws IOException, InvalidPropertiesFormatException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setValidating(true);
        dbf.setCoalescing(true);
        dbf.setIgnoringComments(false);
            
        final DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException x) {
            throw new Error(x);
        } 
        db.setEntityResolver(new Resolver());
        db.setErrorHandler(new EH());
            
        final Document doc;
        try {
            doc = db.parse(is);
        } 
        catch (SAXException saxe) {
            throw new InvalidPropertiesFormatException(saxe);
        }
        
        final Element propertiesElement = (Element) doc.getChildNodes().item(1);
        final String xmlVersion = propertiesElement.getAttribute("version");
        if (xmlVersion.compareTo(EXTERNAL_XML_VERSION) > 0) {
            throw new InvalidPropertiesFormatException(
                "Exported Properties file format version " + xmlVersion +
                " is not supported. This java installation can read" +
                " versions " + EXTERNAL_XML_VERSION + " or older. You" +
                " may need to install a newer version of JDK.");
        }
        
        String group = null;
        String propertyComment = null;
        
        final NodeList children = propertiesElement.getChildNodes();
        
        Node n = children.item(0);
        int start = 0;
        if (n.getNodeName().equals("comment")) {
            this.comment = n.getFirstChild().getNodeValue();
            start = 1;
        }
        for (int i = start; i < children.getLength(); i++) {
            n = children.item(i);
            if (n.getNodeType() == Node.COMMENT_NODE) {
                final Comment c = parser.parseComment((org.w3c.dom.Comment) n);
                if (null != c) {
                    switch (c.type) {
                    case GROUP_NAME:
                        group = c.comment;
                        break;
                    case PROPERTY_COMMENT:
                        propertyComment = c.comment;
                        break;
                    case COMMENTED_PROPERTY:
                        final Document frag;
                        try {
                            frag = db.parse(new InputSource(c.comment));
                        } 
                        catch (SAXException saxe) {
                            throw new InvalidPropertiesFormatException(saxe);
                        }
                        final KeyValue<T> property = parser.parseProperty((Element) frag.getChildNodes().item(0));
                        if (null == property) {
                            throw new InvalidPropertiesFormatException("Invalid property element: " + n);
                        }
                        if (!hasProperty(property.key)) {
                            setProperty(property.key, property.value, group, propertyComment);
                        }
                        propertyComment = null;
                        break;
                    }
                }
            }
            else if (n.getNodeType() == Node.ELEMENT_NODE) {
                final KeyValue<T> property = parser.parseProperty((Element) n);
                if (null == property) {
                    throw new InvalidPropertiesFormatException("Invalid property element: " + n);
                }
                setProperty(property.key, property.value, group, propertyComment);
                propertyComment = null;
            }
            else {
                throw new InvalidPropertiesFormatException("Invalid property element: " + n);
            }
        }
    }
    
    public PropertyFileFormatter<T> getDefaultPropertyFileFormatter() {
        if (null == this.defaultPropertyFileFormatter) {
            this.defaultPropertyFileFormatter = new DefaultPropertyFileFormat<T>();
        }
        return this.defaultPropertyFileFormatter;
    }

    public void setDefaultPropertyFileFormatter(PropertyFileFormatter<T> defaultPropertyFileFormatter) {
        this.defaultPropertyFileFormatter = defaultPropertyFileFormatter;
    }

    /**
     * Writes this property list to the output stream in a format suitable
     * for loading into a <code>Properties</code> table using the
     * {@link #load(Reader) load(Reader)} method. See the equivalent method in
     * java.util.Properties for details. 
     *
     * @param   writer an output character stream writer.
     * @exception  IOException if writing this property list to the specified
     *             output stream throws an <tt>IOException</tt>.
     * @exception  NullPointerException  if <code>writer</code> is null.
     */
    public void store(Writer writer) throws IOException {
        store(writer, getDefaultPropertyFileFormatter());
    }

    /**
     * Writes this property list to the output stream in a format suitable
     * for loading into a <code>Properties</code> table using the
     * {@link #load(Reader) load(Reader)} method. See the equivalent method in
     * java.util.Properties for details. 
     *
     * @param   writer an output character stream writer.
     * @param   propertyFileFormatter
     * @exception  IOException if writing this property list to the specified
     *             output stream throws an <tt>IOException</tt>.
     * @exception  NullPointerException  if <code>writer</code> is null.
     */
    public void store(Writer writer, PropertyFileFormatter<T> propertyFileFormatter) throws IOException {
        store((writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer), 
            propertyFileFormatter, false);
    }
    
    /**
     * Writes this property list to the output stream in a format suitable
     * for loading into a <code>Properties</code> table using the
     * {@link #load(InputStream) load(InputStream)} method. See the equivalent 
     * method in java.util.Properties for details. 
     * 
     * @param   out an output stream.
     * @exception  IOException if writing this property list to the specified
     *             output stream throws an <tt>IOException</tt>.
     * @exception  NullPointerException  if <code>out</code> is null.
     */
    public void store(OutputStream out) throws IOException {
        store(out, getDefaultPropertyFileFormatter());
    }
    
    /**
     * Writes this property list to the output stream in a format suitable
     * for loading into a <code>Properties</code> table using the
     * {@link #load(InputStream) load(InputStream)} method. See the equivalent 
     * method in java.util.Properties for details. 
     * 
     * @param   out an output stream.
     * @param   propertyFileFormatter
     * @exception  IOException if writing this property list to the specified
     *             output stream throws an <tt>IOException</tt>.
     * @exception  NullPointerException  if <code>out</code> is null.
     */
    public void store(OutputStream out, PropertyFileFormatter<T> propertyFileFormatter) throws IOException {
        store(new BufferedWriter(new OutputStreamWriter(out, "8859_1")), propertyFileFormatter, true);
    }
    
    /**
     * Writes properties to the specified writer in the following format:
     * <pre>
     * 
     * 
     * @param writer
     * @param escUnicode
     */
    private void store(BufferedWriter writer, PropertyFileFormatter<T> propertyFileFormatter, boolean escUnicode) 
            throws IOException {
        if (isEmpty()) {
            return;
        }
        
        final Map<String, Map<String, Property>> groups = getGroups();
        final Map<String, Property> defaultGroup = groups.remove(DEFAULT_GROUP_NAME);
        
        boolean writePropertyBreak = false;
        boolean writeGroupBreak = false;
        
        if (propertyFileFormatter.writeFileHeader(writer, this.comment)) {
            writePropertyBreak = true;
            writeGroupBreak = true;
        }
        
        if (null != defaultGroup) {
            writeProperties(defaultGroup, writer, propertyFileFormatter, escUnicode, writePropertyBreak);
        }
        
        for (String groupName : groups.keySet()) {
            if (writeGroupBreak) {
                propertyFileFormatter.writeBreakBeforeGroup(writer);
            }
            else {
                writeGroupBreak = true;
            }
            
            writePropertyBreak = propertyFileFormatter.writeGroupHeader(writer, groupName);
            writeProperties(groups.get(groupName), writer, propertyFileFormatter, escUnicode, writePropertyBreak);
        }
        
        writer.flush();
    }
    
    private void writeProperties(Map<String, Property> properties, BufferedWriter writer, 
                                 PropertyFileFormatter<T> propertyFileFormatter, boolean escUnicode, 
                                 boolean writePropertyBreak)  throws IOException {
        for (String key : properties.keySet()) {
            if (writePropertyBreak) {
                propertyFileFormatter.writeBreakBeforeProperty(writer);
            }
            else {
                writePropertyBreak = true;
            }
            
            final Property p = properties.get(key);
            propertyFileFormatter.writePropertyComment(writer, p.getComment());
            propertyFileFormatter.writeProperty(writer, key, p.getValue(), escUnicode);
        }
    }
    
    public XmlPropertyFileFormatter<T> getDefaultXmlPropertyFileFormatter() {
        if (null == this.defaultXmlPropertyFileFormatter) {
            this.defaultXmlPropertyFileFormatter = new DefaultXmlPropertyFileFormat<T>();
        }
        return this.defaultXmlPropertyFileFormatter;
    }

    public void setDefaultXmlPropertyFileFormatter(XmlPropertyFileFormatter<T> defaultXmlPropertyFileFormatter) {
        this.defaultXmlPropertyFileFormatter = defaultXmlPropertyFileFormatter;
    }

    /**
     * Emits an XML document representing all of the properties contained
     * in this table.
     *
     * <p> An invocation of this method of the form <tt>props.storeToXML(os))</tt> 
     * behaves in exactly the same way as the invocation 
     * <tt>props.storeToXML(os, "UTF-8");</tt>.
     *
     * @param os the output stream on which to emit the XML document.
     * @throws IOException if writing to the specified output stream
     *         results in an <tt>IOException</tt>.
     * @throws NullPointerException if <code>os</code> is null.
     * @see    #loadFromXML(InputStream)
     */
    public synchronized void storeToXml(OutputStream os) throws IOException {
        storeToXml(new StreamResult(os), getDefaultXmlPropertyFileFormatter(), "UTF-8");
    }
    
    /**
     * Emits an XML document representing all of the properties contained
     * in this table.
     *
     * <p> An invocation of this method of the form <tt>props.storeToXML(os))</tt> 
     * behaves in exactly the same way as the invocation 
     * <tt>props.storeToXML(os, "UTF-8");</tt>.
     *
     * @param os the output stream on which to emit the XML document.
     * @param formatter
     * @throws IOException if writing to the specified output stream
     *         results in an <tt>IOException</tt>.
     * @throws NullPointerException if <code>os</code> is null.
     * @see    #loadFromXML(InputStream)
     */
    public synchronized void storeToXml(OutputStream os, XmlPropertyFileFormatter<T> formatter) throws IOException {
        storeToXml(new StreamResult(os), formatter, "UTF-8");
    }
    
    /**
     * Emits an XML document representing all of the properties contained
     * in this table, using the specified encoding.
     *
     * <p>The XML document will have the following DOCTYPE declaration:
     * <pre>
     * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
     * </pre>
     *
     *<p>If the specified comment is <code>null</code> then no comment
     * will be stored in the document.
     *
     * <p>The specified stream remains open after this method returns.
     *
     * @param os the output stream on which to emit the XML document.
     * @param encoding
     * @throws IOException if writing to the specified output stream
     *         results in an <tt>IOException</tt>.
     * @throws NullPointerException if <code>os</code> is <code>null</code>,
     *         or if <code>encoding</code> is <code>null</code>.
     * @see    #loadFromXML(InputStream)
     */ 
    public synchronized void storeToXml(OutputStream os, String encoding) throws IOException {
        storeToXml(new StreamResult(os), getDefaultXmlPropertyFileFormatter(), encoding);
    }
    
    public synchronized void storeToXml(Writer writer) throws IOException {
        storeToXml(writer, getDefaultXmlPropertyFileFormatter());
    }
    
    public synchronized void storeToXml(Writer writer, String encoding) throws IOException {
        storeToXml(new StreamResult(writer), getDefaultXmlPropertyFileFormatter(), encoding);
    }
    
    public synchronized void storeToXml(Writer writer, XmlPropertyFileFormatter<T> formatter) throws IOException {
        final String encoding = (writer instanceof OutputStreamWriter) ? ((OutputStreamWriter) writer).getEncoding() : "UTF-8";
        storeToXml(new StreamResult(writer), formatter, encoding);
    }
    
    /**
     * Emits an XML document representing all of the properties contained
     * in this table, using the specified encoding.
     *
     * <p>The XML document will have the following DOCTYPE declaration:
     * <pre>
     * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
     * </pre>
     *
     *<p>If the specified comment is <code>null</code> then no comment
     * will be stored in the document.
     *
     * <p>The specified stream remains open after this method returns.
     *
     * @param result the result to which to emit the XML document.
     * @param formatter
     * @param encoding
     * @throws IOException if writing to the specified output stream
     *         results in an <tt>IOException</tt>.
     * @throws NullPointerException if <code>result</code> is <code>null</code>,
     *         or if <code>encoding</code> is <code>null</code>.
     * @see    #loadFromXML(InputStream)
     */ 
    public synchronized void storeToXml(Result result, XmlPropertyFileFormatter<T> formatter, String encoding) 
            throws IOException {
        if (isEmpty()) {
            return;
        }
        
        final Map<String, Map<String, Property>> groups = getGroups();
        final Map<String, Property> defaultGroup = groups.remove(DEFAULT_GROUP_NAME);
        
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
        } 
        catch (ParserConfigurationException pce) {
            throw new RuntimeException(pce);
        }
        final Document doc = db.newDocument();
        
        final Element properties = (Element) doc.appendChild(doc.createElement("properties"));

        if (null != this.comment) {
            final Element comments = (Element)properties.appendChild(doc.createElement("comment"));
            comments.appendChild(doc.createTextNode(comment));
        }

        writeXmlProperties(defaultGroup, formatter, doc, properties);
        
        for (String group : groups.keySet()) {
            formatter.writeGroupName(doc, properties, group);
            writeXmlProperties(groups.get(group), formatter, doc, properties);
        } 
        
        final TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = null;
        try {
            t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, PROPS_DTD_URI);
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.setOutputProperty(OutputKeys.ENCODING, encoding);
        } 
        catch (TransformerConfigurationException tce) {
            throw new RuntimeException(tce);
        }
        
        try {
            t.transform(new DOMSource(doc), result);
        } 
        catch (TransformerException te) {
            throw new IOException(te);
        }
    }
    
    private void writeXmlProperties(Map<String, Property> properties, XmlPropertyFileFormatter<T> formatter, 
                                    Document doc, Element parent) {
        for (Map.Entry<String, Property> property : properties.entrySet()) {
            final Property p = property.getValue();
            if (null != p.getComment()) {
                formatter.writePropertyComment(doc, parent, p.getComment());
            }
            formatter.writeProperty(doc, parent, property.getKey(), property.getValue().getValue());
        }
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public boolean isEmpty() {
        return isEmpty(true);
    }
    
    public boolean isEmpty(boolean includeParent) {
        return this.properties.isEmpty() && (!includeParent || null == this.parent || this.parent.isEmpty());
    }
    
    public int size() {
        return size(true);
    }
    
    public int size(boolean includeParent) {
        int size = getPropertyNames().size();
        if (includeParent && null != this.parent) {
            size += this.parent.size(true);
        }
        return size;
    }
    
    public boolean hasProperty(String key) {
        return this.properties.containsKey(key) 
            || (null != this.parent && this.parent.hasProperty(key));
    }
    
    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the parent property list,
     * and its parents, recursively, are then checked. The method returns
     * <code>null</code> if the property is not found. If the property is not
     * null, is a String, and contains references, the references are replaced
     * with their values.
     *
     * @param   key   the property key.
     * @return  the value in this property list with the specified key value.
     * @see     #setProperty
     */
    public synchronized T getProperty(String key) {
        return getProperty(key, true);
    }

    @SuppressWarnings("unchecked")
    public synchronized T getProperty(String key, boolean resolveReferences) {
        final Property e = getPropertyEntry(key);
        
        T value = null;
        if (null != e) {
            value = e.getValue();
        }
        else if (null != this.parent) {
            value = this.parent.getProperty(key, false);
        }
        
        if (resolveReferences && null != value && String.class.equals(getValueType())) {
            final String s = (String) value;
            final Matcher m = REF_PATTERN.matcher(s);
            
            StringBuilder sb = null;
            int start = 0;
            while (m.find()) {
                if (sb == null) {
                    sb = new StringBuilder();
                }
                
                final String refKey = m.group(2);
                final int end = m.start(); 
                String refValue = null;
                    
                switch (m.group(1).charAt(0)) {
                // local property
                case '$':
                    refValue = (String) getProperty(refKey);
                    break;
                // system property    
                case '@':
                    refValue = System.getProperty(refKey);
                    break;
                // environment variable
                case '#':
                    refValue = System.getenv(refKey);
                    break; 
                }
                
                if (null != refValue) {
                    sb.append(s.substring(start, end)).append(refValue);
                    start = m.end();
                }
            }
            
            if (null != sb) {
                if (start < s.length()) {
                    sb.append(s.substring(start, s.length()));
                }
                value = (T) sb.toString();
            }
        }
        
        return value;
    }
    
    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the parent property list,
     * and its parent, recursively, are then checked. The method returns the
     * default value argument if the property is not found.
     *
     * @param   key            the property key.
     * @param   defaultValue   a default value.
     * @return  the value in this property list with the specified key value.
     * @see     #setProperty
     */
    public T getProperty(String key, T defaultValue) {
        final T value = getProperty(key);
        return (value == null) ? defaultValue : value;
    }
    
    /**
     * Sets the value of <tt>key</tt> to <tt>value</tt>. If <tt>value</tt> is
     * null, the property is removed.
     * 
     * @param key the key to be placed into this property list.
     * @param value the value corresponding to <tt>key</tt>.
     * @return     the previous value of the specified key in this property
     *             list, or <code>null</code> if it did not have one.
     * @see #getProperty
     */
    public synchronized T setProperty(String key, T value) {
        return setProperty(key, value, null, null);
    }
    
    public synchronized T setProperty(String key, T value, String group, String comment) {
        T old = null;
        if (null == value) {
            old = removeProperty(key);
        }
        else {
            final Property e = getPropertyEntry(key, true);
            old = e.getValue();
            e.setValue(value);
            e.setComment(comment);
        
            final String oldGroup = e.getGroup();
            if (null == oldGroup || (null != group && !group.equals(oldGroup))) {
                if (null == group) {
                    group = DEFAULT_GROUP_NAME;
                }
                e.setGroup(group);
            }
        }
        return old;
    }
    
    public void setAll(Properties<T> other) {
        if (null != other.parent) {
            setAll(other.parent);
        }
        for (Map.Entry<String,Property> e : other.properties.entrySet()) {
            final Property entry = e.getValue();
            setProperty(e.getKey(), entry.getValue(), entry.getGroup(), entry.getComment());
        }   
    }
    
    public T removeProperty(String key) {
        return removeProperty(key, false);
    }
    
    public synchronized T removeProperty(String key, boolean includeParent) {
        T oldValue = null;
        
        if (includeParent && null != this.parent) {
            oldValue = this.parent.removeProperty(key);
        }
        
        if (this.properties.containsKey(key)) {
            final Property e = this.properties.remove(key);
            if (null != e) {
                oldValue = e.getValue();
            }
        }
        
        return oldValue;
    }
    
    public synchronized void clear() {
        clear(false);
    }
    
    public synchronized void clear(boolean includeParent) {
        this.properties.clear();
        if (includeParent && null != this.parent) {
            this.parent.clear(true);
        }
    }
    
    public String getComment(String key) {
        return this.properties.containsKey(key) ? getPropertyEntry(key).getComment() : 
            (null == this.parent) ? null : this.parent.getComment(key);
    }
    
    public synchronized String setComment(String key, String comment) {
        String old = null;
        if (this.properties.containsKey(key)) {
            final Property e = getPropertyEntry(key);
            old = e.getComment();
            e.setComment(comment);
        }
        else if (null != this.parent) {
            old = this.parent.setComment(key, comment);
        }
        return old;
    }
    
    public synchronized String getGroup(String key) {
        String group = null;
        if (this.properties.containsKey(key)) {
            group = this.properties.get(key).getGroup();
        }
        else if (null != this.parent) {
            group = this.parent.getGroup(key);
        }
        return group;
    }
    
    public synchronized String setGroup(String key, String group) {
        String old = null;
        if (this.properties.containsKey(key)) {
            final Property e = getPropertyEntry(key);
            old = e.getGroup();
            e.setGroup(group);
        }
        else if (null != this.parent) {
            old = this.parent.setGroup(key, group);
        }
        return old;
    }
    
    public Property getPropertyEntry(String key) {
        return getPropertyEntry(key, false);
    }
    
    private synchronized Property getPropertyEntry(String key, boolean create) {
        Property newProperty = null;
        if (this.properties.containsKey(key)) {
            newProperty = this.properties.get(key);
        }
        if (create) {
            Properties<T> p = this.parent;
            Property e = null;
            while (null != p && null == e) {
                e = p.getPropertyEntry(key, false);
                p = p.parent;
            }
            newProperty = (null == e) ? new Property() : (Property) e.clone();
            this.properties.put(key, newProperty);
        }
        return newProperty;
    }
    
    /**
     * Stores all key/value pairs, distinct parent keys, in the specified Map.
     * 
     * @param sink the Map in which the enumerated key/value pairs are stored
     */
    public synchronized void enumerate(Map<String, T> sink) {
        if (this.parent != null) {
            this.parent.enumerate(sink);
        }
        for (String key : this.properties.keySet()) {
            sink.put(key, this.properties.get(key).getValue());
        }
    }
    
    /**
     * Returns a set of keys in this property list,
     * including distinct keys in the parent property list if a key
     * of the same name has not already been found from the main
     * properties list. The returned set is not modifiable.
     *
     * @return  a set of keys in this property list,
     *          including the keys in the parent property list.
     */
    public Set<String> getPropertyNames() {
        final Map<String, T> map = new LinkedHashMap<String, T>();
        enumerate(map);
        return Collections.unmodifiableSet(map.keySet());
    }
    
    public Set<String> getGroupNames() {
        final Set<String> propertyNames = getPropertyNames();
        final Set<String> groupNames = new LinkedHashSet<String>();
        
        for (String key : propertyNames) {
            groupNames.add(getGroup(key));
        }
        
        return groupNames;
    }
    
    public Map<String, Map<String, Property>> getGroups() {
        final Map<String, Map<String, Property>> groups = new LinkedHashMap<String, Map<String, Property>>();
        enumerateGroups(groups);
        return groups;
    }
    
    public Map<String, Map<String, Property>> enumerateGroups(Map<String, Map<String, Property>> groups) {
        final Set<String> propertyNames = getPropertyNames();
        
        for (String key : propertyNames) {
            final Property property = getPropertyEntry(key);
            final String groupName = property.getGroup();
            
            Map<String, Property> group = groups.get(groupName);
            if (null == group) {
                group = new LinkedHashMap<String, Property>();
                groups.put(groupName, group);
            }
            
            group.put(key, property);
        }
        
        return groups;
    }
    
    /**
     * Prints this property list out to the specified output stream.
     * This method is useful for debugging.
     *
     * @param   out an output stream.
     */
    public void list(PrintStream out) {
        list(out, 40);
    }
    
    /**
     * Prints this property list out to the specified output stream.
     * This method is useful for debugging.
     *
     * @param out an output stream.
     * @param maxWidth the maximum width of the output
     */
    public void list(PrintStream out, int maxWidth) {
        out.println("-- listing properties --");
        visitAll(new PrintVisitor(out, maxWidth));
    }
    
    /**
     * Prints this property list out to the specified writer.
     * This method is useful for debugging.
     *
     * @param out a PrintWriter.
     */
    public void list(PrintWriter out) {
        list(out, 40);
    }
    
    /**
     * Prints this property list out to the specified writer.
     * This method is useful for debugging.
     *
     * @param out a PrintWriter.
     * @param maxWidth the maximum width of the output
     */
    public void list(PrintWriter out, int maxWidth) {
        out.println("-- listing properties --");
        visitAll(new PrintVisitor(out, maxWidth));
    }
    
    public void visitAll(Visitor visitor) {
        final Set<String> propertyNames = getPropertyNames();
        for (String key : propertyNames) {
            visitor.visit(key, this.properties.get(key));
        }
    }
    
    @Override
    public synchronized Object clone() {
        final Properties<T> properties =  new Properties<T>(getValueType());
        properties.setAll(this);
        properties.setParent(getParent());
        return properties;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public synchronized boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Properties)) {
            return false;
        }
        Properties<T> other = (Properties<T>) obj;
        if (!getValueType().equals(other.getValueType())) {
            return false;
        }
        if (other.size() != size()) {
            return false;
        }

        final Iterator<Map.Entry<String, Property>> i = this.properties.entrySet().iterator();
        while (i.hasNext()) {
            final Map.Entry<String, Property> e = i.next();
            
            if (!e.getValue().equals(other.properties.get(e.getKey()))) {
                return false;
            }
        }
        
        return (null != this.parent || null != other.parent || this.parent.equals(other.parent));
    }

    /**
     * Generates a hash code from the properties table. Does not include
     * properties from parent.
     * 
     * @return hash code
     */
    @Override
    public synchronized int hashCode() {
        return this.properties.hashCode();
    }

    @Override
    public String toString() {
        int max = size() - 1;
        if (max < 0) {
            return "{}";
        }

        final StringBuilder sb = new StringBuilder("{");
        
        toString(sb, 0, max);
        
        return sb.toString();
    }
    
    private synchronized void toString(StringBuilder sb, int i, int max) {
        for (Iterator<Map.Entry<String, Property>> it = this.properties.entrySet().iterator(); it.hasNext(); i++) {
            final Map.Entry<String, Property> e = it.next();
            sb.append(e.getKey())
              .append('=')
              .append(e.getValue().getValue());

            if (i == max) {
                sb.append('}');
                return;
            }
            
            sb.append(", ");
        }
        
        this.parent.toString(sb, i, max);
    }
    
    private static class Resolver implements EntityResolver {
        public InputSource resolveEntity(String pid, String sid) throws SAXException {
            if (sid.equals(PROPS_DTD_URI)) {
                InputSource is;
                is = new InputSource(new StringReader(PROPS_DTD));
                is.setSystemId(PROPS_DTD_URI);
                return is;
            }
            throw new SAXException("Invalid system identifier: " + sid);
        }
    }

    private static class EH implements ErrorHandler {
        public void error(SAXParseException x) throws SAXException {
            throw x;
        }
        public void fatalError(SAXParseException x) throws SAXException {
            throw x;
        }
        public void warning(SAXParseException x) throws SAXException {
            throw x;
        }
    }
    
    public abstract class Visitor {
        public abstract void visit(String property, Property entry);
    }
    
    private class PrintVisitor extends Visitor {
        private PrintStream ps;
        private PrintWriter pw;
        private int maxWidth;
        
        public PrintVisitor(PrintStream ps, int maxWidth) {
            this.ps = ps;
            this.maxWidth = maxWidth;
        }

        public PrintVisitor(PrintWriter pw, int maxWidth) {
            this.pw = pw;
            this.maxWidth = maxWidth;
        }

        @Override
        public void visit(String key, Property value) {
            String strVal = value.getValue().toString();
            if (strVal.length() > this.maxWidth) {
                strVal = strVal.substring(0, this.maxWidth-3) + "...";
            }
            if (null != ps) {
                this.ps.println(key + "=" + value);
            }
            else {
                this.pw.println(key + "=" + value);
            }
        }
    }
    
    public class Property implements Cloneable {
        private T value;
        private String comment;
        private String group;
        
        public T getValue() {
            return value;
        }

        void setValue(T value) {
            this.value = value;
        }

        public String getComment() {
            return comment;
        }
        
        void setComment(String comment) {
            this.comment = comment;
        }
        
        public String getGroup() {
            return group;
        }
        
        void setGroup(String group) {
            this.group = group;
        }
        
        public boolean equals(Object obj) {
            if (!Property.class.isInstance(obj)) {
                return false;
            }
            final Property other = (Property) obj;
            return this.value.equals(other.value)
                && nullOrEqual(this.comment, other.comment)
                && (null == this.group) ? (null == other.group) : this.group.equals(other.group);
        }

        @Override
        public Object clone() {
            final Property newProperty = new Property();
            newProperty.setValue(getValue());
            newProperty.setComment(getComment());
            newProperty.setGroup(getGroup());
            return newProperty;
        }
    }
}
