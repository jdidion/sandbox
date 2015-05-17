package net.sf.redi.wordnet.princeton.en;

import net.sf.redi.PointerType;
import net.sf.redi.PartOfSpeech;
import net.sf.redi.locale.LatinPartOfSpeech;
import static net.sf.redi.wordnet.princeton.en.Flag.*;
import static net.sf.redi.locale.LatinPartOfSpeech.*;

import java.util.EnumSet;
import java.util.Map;
import java.util.EnumMap;
import java.util.HashMap;

/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
public enum WNPrincetonEnPointerType implements PointerType<WNPrincetonEnPointerType, LatinPartOfSpeech> {
    // All categories
	ANTONYM(N,V,ADJ,ADV,LEXICAL),
    CATEGORY(N,V,ADJ,ADV,LEXICAL),
    REGION(N,V,ADJ,ADV,LEXICAL),
    USAGE(N,V,ADJ,ADV,LEXICAL),

	// Nouns and Verbs
	HYPERNYM(N,V),
	HYPONYM(N,V),
    NOMINALIZATION(N,V),

	// Nouns and Adjectives
	ATTRIBUTE(N,ADJ),
	SEE_ALSO(N,V,ADJ,LEXICAL),

    // Nouns
    MEMBER_HOLONYM(N),
    SUBSTANCE_HOLONYM(N),
    PART_HOLONYM(N),
    MEMBER_MERONYM(N),
    SUBSTANCE_MERONYM(N),
    PART_MERONYM(N),
    CATEGORY_MEMBER(N),
    REGION_MEMBER(N),
    USAGE_MEMBER(N),

	// Verbs
    ENTAILMENT(V),
	ENTAILED_BY(V),
	CAUSE(V),
	VERB_GROUP(V),

	// Adjectives
	SIMILAR_TO(ADJ),
	PARTICIPLE_OF(ADJ,LEXICAL),
	PERTAINYM(ADJ,LEXICAL),

	// Adverbs
	DERIVED(ADV);

    static {
        setSymmetric(ANTONYM, ANTONYM);
		setSymmetric(HYPERNYM, HYPONYM);
		setSymmetric(MEMBER_MERONYM, MEMBER_HOLONYM);
		setSymmetric(SUBSTANCE_MERONYM, SUBSTANCE_HOLONYM);
		setSymmetric(PART_MERONYM, PART_HOLONYM);
		setSymmetric(SIMILAR_TO, SIMILAR_TO);
        setSymmetric(ATTRIBUTE, ATTRIBUTE);
        setSymmetric(VERB_GROUP, VERB_GROUP);
		setSymmetric(ENTAILMENT, ENTAILED_BY);
        setSymmetric(CATEGORY, CATEGORY_MEMBER);
        setSymmetric(REGION, REGION_MEMBER);
        setSymmetric(USAGE,  USAGE_MEMBER);
        setSymmetric(NOMINALIZATION, NOMINALIZATION);
    }

    private static void setSymmetric(WNPrincetonEnPointerType type1, WNPrincetonEnPointerType type2) {
        type1.setSymmetricType(type2);
        type2.setSymmetricType(type1);
    }

    private EnumSet<Flag> _flags;
    private WNPrincetonEnPointerType _symmetricType;

    private WNPrincetonEnPointerType(Flag first, Flag... rest) {
        _flags = EnumSet.of(first, rest);
    }

    private void setSymmetricType(WNPrincetonEnPointerType type) {
        _symmetricType = type;
    }

    public WNPrincetonEnPointerType getSymmetricType() {
        return _symmetricType;
    }

    public boolean isAppliesTo(LatinPartOfSpeech pos) {
        return _flags.contains(getByPartOfSpeech(pos));
    }
}

// Flags for tagging a pointer type with the POS types it apples to.
enum Flag { 
    N, V, ADJ, ADV, LEXICAL;
    
    private static EnumMap<LatinPartOfSpeech, Flag> _posToFlagMap =
            new EnumMap<LatinPartOfSpeech, Flag>(LatinPartOfSpeech.class);
    
    static {
        _posToFlagMap.put(NOUN, N);
        _posToFlagMap.put(VERB, V);
        _posToFlagMap.put(ADJECTIVE, ADJ);
        _posToFlagMap.put(ADVERB, ADV);
    }
    
    public static Object getByPartOfSpeech(LatinPartOfSpeech pos) {
        return _posToFlagMap.get(pos);
    }
}