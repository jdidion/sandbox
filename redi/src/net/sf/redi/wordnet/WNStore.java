/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi.wordnet;

import net.sf.redi.PartOfSpeech;

public interface WNStore<P extends PartOfSpeech> {
    List<IndexEntry<P>> selectIndexEntries(P pos, int maxResults, int offset);
    List<IndexEntry<P>> selectIndexEntries(P pos, String lemma, int maxResults, int offset);
    List<Synset<P>> selectSynsets(P pos, int maxResults, int offset);
    Synset<P> selectSynset(P pos, long index);
    List<Exc<P>> selectExceptions(P pos, int maxResults, int offset);
    Exc<P> selectException(P pos, String derivation);
}