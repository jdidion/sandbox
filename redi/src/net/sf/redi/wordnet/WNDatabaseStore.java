/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi.wordnet;

import net.sf.redi.PartOfSpeech;

public abstract class WNDatabaseStore<P extends PartOfSpeech> implements WNStore<P> {
    public List<IndexEntry<P>> selectIndexEntries(
            PartOfSpeech partOfSpeech, int maxResults, int offset) {
        return null;
    }

    public List<IndexEntry<P>> selectIndexEntries(
            PartOfSpeech partOfSpeech, String lemma, int maxResults, int offset) {
        return null;
    }

    public List<Synset<P>> selectSynsets(
            PartOfSpeech partOfSpeech, int maxResults, int offset) {
        return null;
    }

    public Synset<P> selectSynset(PartOfSpeech partOfSpeech, long index) {
        return null;
    }

    public List<Exc<P>> selectExceptions(
            PartOfSpeech partOfSpeech, int maxResults, int offset) {
        return null;
    }

    public Exc<P> selectException(
            PartOfSpeech partOfSpeech, String derivation) {
        return null;
    }
}