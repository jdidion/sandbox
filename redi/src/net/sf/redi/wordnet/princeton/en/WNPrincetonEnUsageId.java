/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi.wordnet.princeton.en;

import net.sf.redi.locale.LatinPartOfSpeech;
import net.sf.redi.wordnet.WNUsageId;

public class WNPrincetonEnUsageId extends WNUsageId<LatinPartOfSpeech> {
    public WNPrincetonEnUsageId(LatinPartOfSpeech pos, Long id) {
        super(pos, id);
    }
}