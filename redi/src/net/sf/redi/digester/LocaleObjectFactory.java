/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi.digester;

import org.apache.commons.digester.ObjectCreationFactory;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;
import static net.sf.redi.Constants.*;

import java.util.Locale;

public class LocaleObjectFactory extends AbstractObjectCreationFactory {
    public Object createObject(Attributes attributes) throws Exception {
        String language = attributes.getValue(RESOURCE_ATTR_LANGUAGE);
        String country = attributes.getValue(RESOURCE_ATTR_COUNTRY);

        if (language == null) {
            return Locale.getDefault();
        } else if (country == null) {
            return new Locale(language);
        } else {
            return new Locale(language, country);
        }
    }
}