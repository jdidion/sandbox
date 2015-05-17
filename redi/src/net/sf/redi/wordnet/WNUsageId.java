/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi.wordnet;

import net.sf.redi.locale.LatinPartOfSpeech;
import net.sf.redi.UsageIdImpl;
import net.sf.redi.PartOfSpeech;

public class WNUsageId<P extends PartOfSpeech> extends UsageIdImpl<P, Long> {
    public WNUsageId(P pos, Long id) {
        super(pos, id);
    }
}