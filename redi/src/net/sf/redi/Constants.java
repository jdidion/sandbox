/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

public interface Constants {
    // system properties
    String PROP_DEFAULT_NAME = "net.sf.redi.defaultType";
    String PROP_CONFIGURATION_RESOURCE_NAME =  "net.sf.redi.resourceName";
    String PROP_DEFAULT_DICTIONARY_CLASS = "net.sf.redi.defaultTypeClass";
    String PROP_DEFAULT_DICTIONARY_FACTORY_CLASS = "net.sf.redi.defaultDictionaryFactoryClass";
    String PROP_BUNDLE_CONFIGURATION_RESOURCE_NAME = "net.sf.redi.bundleResourceName";

    // default values
    String DEFAULT_DICTIONARY_NAME = "default";
    String DEFAULT_DICTIONARY_CLASS = "net.sf.redi.wordnet.WNDictionary";
    String DEFAULT_DICTIONARY_FACTORY_CLASS = "net.sf.redi.wordnet.WNDictionaryFactory";
    String DEFAULT_CONFIGURATION_RESOURCE_NAME = "redi.xml";
    String PROVIDED_CONFIGURATION_RESOURCE_NAME = "net.sf.redi.redi.xml";
    String DEFAULT_BUNDLE_CONFIGURATION_RESOURCE_NAME = "resources.xml";
    String PROVIDED_BUNDLE_CONFIGURATION_RESOURCE_NAME = "net.sf.redi.resources.xml";
    int DEFAULT_DEPTH = 1;
    int DEFAULT_MAX_RESULTS = 20;

    // configuration file elements
    String ELT_ROOT = "redi";
    String ELT_DICTIONARY = "dictionary";

    // configuration file attributes
    String ATTR_DICTIONARY_CLASS = "class";
    String ATTR_DICTIONARY_FACTORY_CLASS = "factory";
    String ATTR_RULES_CLASS = "rules";

    // resource configuration file elements
    String RESOURCE_ELT_ROOT = "resources";
    String RESOURCE_ELT_RESOURCE = "resource";

    // resource configuration file attributes
    String RESOURCE_ATTR_LANGUAGE = "language";
    String RESOURCE_ATTR_COUNTRY = "country";
    String RESOURCE_ATTR_NAME = "name";
}