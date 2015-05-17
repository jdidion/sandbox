/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

import org.apache.commons.digester.Digester;
import org.apache.commons.beanutils.PropertyUtils;
import org.xml.sax.SAXException;

import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;

import static net.sf.redi.Constants.*;
import net.sf.redi.digester.ChangeRulesRule;
import net.sf.redi.digester.DictionaryCreateRule;

public class DictionaryManager {
    private static DictionaryManager _instance;

    public static synchronized DictionaryManager getInstance() {
        if (_instance == null) {
            InputStream configStream = getConfigStream();
            DictionaryManager manager = new DictionaryManager();
            manager.init(configStream);
            _instance = manager;
        }
        return _instance;
    }

    private static InputStream getConfigStream() {
        String resourceName = System.getProperty(PROP_CONFIGURATION_RESOURCE_NAME);
        if (resourceName == null) {
            resourceName = DEFAULT_CONFIGURATION_RESOURCE_NAME;
        }
        InputStream in = DictionaryManager.class.getResourceAsStream(resourceName);
        if (in == null) {
            in = DictionaryManager.class.getResourceAsStream(PROVIDED_CONFIGURATION_RESOURCE_NAME);
        }
        return in;
    }

    private Map<String, Dictionary> _dictionaries = new HashMap<String, Dictionary>();
    private String _default;

    public DictionaryManager() {
    }

    public void init() {
        init(null);
    }

    public void init(InputStream configStream) {
        if (configStream != null) {
            String rootPattern = Utils.getPattern(ELT_ROOT);
            String dictionaryPattern = Utils.getPattern(ELT_ROOT, ELT_DICTIONARY);

            Digester digester = new Digester();
            digester.setValidating(false);
            digester.push(this);
            // This rule will set properties on this dictionary manager from
            // attributes on the root <redi> element.
            digester.addSetProperties(rootPattern);
            // This rule will create a dictionary for each <dictionary> element.
            // If the class attribute is set, that class will be created. Else,
            // if the factory attribute is set, that factory will be created, and
            // will be used to create the dictionary in the end() method. Else,
            // it will behave as if the factory attribute is set to the default
            // dictionary factory.
            digester.addRule(dictionaryPattern, new DictionaryCreateRule());
            // This rule will set properties on the created dictionary/factory
            // from attributes on the <dictionary> element.
            digester.addSetProperties(dictionaryPattern);
            // This rule will change to a custom Rules implementation
            // if the rulesClass attribute is set.
            digester.addRule(dictionaryPattern, new ChangeRulesRule());

            try {
                digester.parse(configStream);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            try {
                Object obj = Utils.createDictionaryOrFactory();
                try {
                    // Try to set the dictionary's name to our default.
                    PropertyUtils.setProperty(obj, "name", getDefault());
                    registerDictionary(Utils.createDictionary(obj));
                } catch (Exception ex) {
                    // If there's no setName method, use the dictionary's
                    // name as the default.
                    Dictionary dictionary = Utils.createDictionary(obj);
                    if (dictionary.getName() == null) {
                        throw new Exception();
                    }
                    registerDictionary(dictionary);
                    setDefault(dictionary.getName());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public Dictionary getDictionary() {
        return getDictionary(getDefault());
    }

    public Dictionary getDictionary(String name) {
        return (Dictionary) _dictionaries.get(name);
    }

    public void registerDictionary(Dictionary dictionary) {
        _dictionaries.put(dictionary.getName(), dictionary);
    }

    public void setDefault(String name) {
        _default = name;
    }

    public String getDefault() {
        if (_default == null) {
            String defaultName = System.getProperty(PROP_DEFAULT_NAME);
            if (defaultName == null) {
                defaultName = DEFAULT_DICTIONARY_NAME;
            }
            _default = defaultName;
        }
        return _default;
    }
}