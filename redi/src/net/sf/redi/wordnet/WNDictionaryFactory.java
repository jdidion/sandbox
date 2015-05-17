/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi.wordnet;

import net.sf.redi.DictionaryFactory;
import net.sf.redi.Dictionary;
import net.sf.redi.UsageIdImpl;
import net.sf.redi.locale.LatinPartOfSpeech;

public class WNDictionaryFactory<D extends WNDictionary> implements DictionaryFactory<D> {

    public D create() {
        WNDictionary dictionary = new WNDictionary();
        
        return dictionary;
    }
}