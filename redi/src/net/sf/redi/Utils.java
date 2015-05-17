/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.commons.digester.Digester;

import java.lang.reflect.Constructor;
import java.util.Formatter;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Locale;
import java.io.InputStream;

import static net.sf.redi.Constants.*;
import net.sf.redi.util.I18NLogger;
import net.sf.redi.util.CompositeResourceBundle;
import net.sf.redi.digester.LocaleObjectFactory;

public final class Utils {
    private static ResourceBundle _resourceBundle;

    public static Dictionary createDictionary() throws Exception {
        return createDictionary(null, null);
    }

    public static Dictionary createDictionary(
            String factoryClassName, String dictionaryClassName)
            throws Exception {
        Object obj = createDictionaryOrFactory(
                factoryClassName, dictionaryClassName);
        return createDictionary(obj);
    }

    public static Dictionary createDictionary(Object obj) throws Exception {
        if (obj instanceof Dictionary) {
            return (Dictionary) obj;
        } else if (obj instanceof DictionaryFactory) {
            return ((DictionaryFactory) obj).create();
        } else {
            throw new Exception("unexpected object: " + obj);
        }
    }

    public static Object createDictionaryOrFactory() throws Exception {
        return createDictionaryOrFactory(null, null);
    }

    public static Object createDictionaryOrFactory(
            String factoryClassName, String dictionaryClassName)
            throws Exception {
        Object obj = null;
        if (factoryClassName != null) {
            obj = createDictionaryFactory(factoryClassName, dictionaryClassName);
        } else if (dictionaryClassName != null) {
            obj = createDictionary(dictionaryClassName);
        } else {
            String dictionaryClass = System.getProperty(PROP_DEFAULT_DICTIONARY_CLASS);
            String factoryClass = System.getProperty(PROP_DEFAULT_DICTIONARY_FACTORY_CLASS);

            if (dictionaryClass != null) {
                if (factoryClass == null) {
                    obj = createDictionary(dictionaryClass);
                } else {
                    obj = createDictionaryFactory(factoryClass, dictionaryClass);
                }
            } else {
                dictionaryClass = DEFAULT_DICTIONARY_CLASS;
                if (factoryClass == null) {
                    factoryClass = DEFAULT_DICTIONARY_FACTORY_CLASS;
                }
                obj = createDictionaryFactory(factoryClass, dictionaryClass);
            }
        }
        return obj;
    }

    public static DictionaryFactory createDictionaryFactory(
            String factoryClassName, String dictionaryClassName)
            throws Exception {
        Class<? extends DictionaryFactory> factoryClass =
                (Class<? extends DictionaryFactory>) Class.forName(factoryClassName);
        if (dictionaryClassName != null) {
            Class<? extends Dictionary> dictionaryClass =
                    (Class<? extends Dictionary>) Class.forName(dictionaryClassName);
            try {
                Constructor<? extends DictionaryFactory> con =
                        factoryClass.getConstructor(new Class[] {Class.class});
                return con.newInstance(new Object[] {dictionaryClass});
            } catch (Exception ex) {
            }
        }
        return (DictionaryFactory) factoryClass.newInstance();
    }

    public static Dictionary createDictionary(String className) throws Exception {
        Class<? extends Dictionary> clazz =
                (Class<? extends Dictionary>) Class.forName(className);
        return clazz.newInstance();
    }

    public static String getPattern(String root, String... elements) {
        StringBuffer buf = new StringBuffer(root);
        for (String element : elements) {
            buf.append("/").append(element);
        }
        return buf.toString();
    }

    public static String format(String s, Object... args) {
        return format(getResourceBundle(), s, args);
    }

    public static String format(
            ResourceBundle resource, String s, Object... args) {
        if (resource != null) {
            try {
                s = resource.getString(s);
            } catch (MissingResourceException ex) {
                // ignore, just treat s as the the resource
            }
        }
        StringBuilder sb = new StringBuilder();
        new Formatter(sb).format(s, args);
        return sb.toString();
    }

    public static ResourceBundle getResourceBundle() {
        if (_resourceBundle == null) {
            String rootPattern = getPattern(RESOURCE_ELT_ROOT);
            String resourcePattern = getPattern(RESOURCE_ELT_ROOT, RESOURCE_ELT_RESOURCE);

            CompositeResourceBundle bundle = new CompositeResourceBundle();
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.push(bundle);
            // These rules set the locale
            digester.addFactoryCreate(rootPattern, new LocaleObjectFactory());
            digester.addCallMethod(rootPattern, "setLocale", 1, new Class[] {Locale.class});
            digester.addCallParam(rootPattern, 0, true);
            // These rules adds resources to the bundle
            digester.addCallMethod(resourcePattern, "addResource", 1, new Class[] {String.class});
            digester.addCallParam(resourcePattern, 0, RESOURCE_ATTR_NAME);

            try {
                _resourceBundle = (ResourceBundle)
                        digester.parse(getBundleConfigStream());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return _resourceBundle;
    }

    private static InputStream getBundleConfigStream() {
        String resource = System.getProperty(PROP_BUNDLE_CONFIGURATION_RESOURCE_NAME);
        if (resource == null) {
            resource = DEFAULT_BUNDLE_CONFIGURATION_RESOURCE_NAME;
        }
        InputStream in = Utils.class.getResourceAsStream(resource);
        if (in == null) {
            in = Utils.class.getResourceAsStream(PROVIDED_BUNDLE_CONFIGURATION_RESOURCE_NAME);
        }
        return in;
    }

    private Utils() {
    }
}