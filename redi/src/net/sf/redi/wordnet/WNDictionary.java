/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi.wordnet;

import net.sf.redi.*;
import net.sf.redi.wordnet.princeton.en.WNPrincetonEnPointerType;
import net.sf.redi.locale.LatinPartOfSpeech;

import java.util.List;
import java.util.Set;
import java.util.Iterator;

public interface WNDictionary<
        P extends PartOfSpeech,
        T extends PointerType,
        I extends WNUsageId,
        U extends Usage>
        extends Dictionary<P, T, I, U> {

    /**
	 * Return an Iterator over all the IndexWords of part-of-speech
	 * <var>pos</var> in the database.
	 * @param	pos	The part-of-speech
	 * @return An iterator over <code>IndexWord</code>s
	 */
	Iterator<IndexEntry<P>> getIndexEntryIterator(P pos);

	/**
	 * Return an Iterator over all the IndexEntrys of part-of-speech <var>pos</var>
	 * whose lemmas contain <var>substring</var> as a substring.
	 * @param pos The part-of-speech.
	 * @return An iterator over <code>IndexEntry</code>s.
	 */
	Iterator<IndexEntry<P>> getIndexEntryIterator(P pos, String substring);

	/**
	 * Look up a word in the database. The search is case-independent,
	 * and phrases are separated by spaces ("look up", not "look_up").
	 * @param pos The part-of-speech.
	 * @param lemma The orthographic representation of the word.
	 * @return An IndexEntry representing the word, or <code>null</code> if
	 * 			no such entry exists.
	 */
	IndexEntry<P> getIndexEntry(P pos, String lemma);

    /**
     * Return an Iterator over all the Synsets of part-of-speech <var>pos</var>
     * in the database.
     * @param pos The part-of-speech.
     * @return An iterator over <code>Synset</code>s.
     */
    Iterator<Synset<P, T>> getSynsetIterator(P pos);

    /**
     * Return the <code>Synset</code> at offset <code>offset</code> from the database.
     * @param pos The part-of-speech file to look in
     * @param offset The offset of the synset in the file
     * @return A synset containing the parsed line from the database
     */
    Synset<P, T> getSynsetAt(P pos, Long offset);

    /**
     * Return an Iterator over all the Exceptions in the database.
     * @param	pos	the part-of-speech
     * @return	Iterator An iterator over <code>String</code>s
     */
    Iterator<Exc<P>> getExceptionIterator(P pos);

    /**
     * Lookup <code>derivation</code> in the exceptions file of part-of-speech <code>
     * pos</code> and return an Exc object containing the results.
     * @param pos the exception file to look in
     * @param derivation the word to look up
     * @return Exc the Exc object
     */
    Exc<P> getException(P pos, String derivation);
}