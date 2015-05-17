/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

import net.sf.redi.locale.LatinPartOfSpeech;

import java.util.Map;
import java.util.HashMap;
import java.util.EnumSet;

public class LocaleManager {
    private static Map<String, EnumSet> _languageToLocale = new HashMap<String, EnumSet>();

    static {
        _languageToLocale.put("en", EnumSet.allOf(LatinPartOfSpeech.class));
    }

    public static EnumSet getPartsOfSpeechByLanguage(String language) {
        return _languageToLocale.get(language); 
    }

    private LocaleManager() {
    }
}