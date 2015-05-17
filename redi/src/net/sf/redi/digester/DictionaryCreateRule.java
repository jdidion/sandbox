/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi.digester;

import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

import java.lang.reflect.Constructor;

import static net.sf.redi.Constants.*;
import net.sf.redi.Utils;
import net.sf.redi.DictionaryManager;

public class DictionaryCreateRule extends Rule {
    public void begin(String namespace, String name, Attributes attributes)
            throws Exception {
        String dictionaryClassName = attributes.
                getValue(ATTR_DICTIONARY_CLASS);
        String factoryClassName = attributes.
                getValue(ATTR_DICTIONARY_FACTORY_CLASS);
        digester.push(Utils.createDictionaryOrFactory(
                factoryClassName, dictionaryClassName));
    }

    public void end(String namespace, String name) throws Exception {
        Object obj = getDigester().peek();
        ((DictionaryManager) getDigester().peek(1)).
                registerDictionary(Utils.createDictionary(obj));
    }
}