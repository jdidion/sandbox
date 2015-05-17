/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

import java.util.List;
import java.util.EnumSet;
import java.util.Set;

public interface Dictionary<
        P extends PartOfSpeech,
        T extends PointerType<T, P>,
        I extends UsageId,
        U extends Usage<P, T, I>> {
    String getName();
    String getProvider();
    String getLanguage();
    String getVersion();
    Set<P> getPartsOfSpeach();
    Set<T> getPointerTypes();
    Set<T> getPointerTypes(P partOfSpeech);
    U getUsage(I id);
    List<U> search(String lemma);
    List<U> search(String lemma, P partOfSpeech);
}