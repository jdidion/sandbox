/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

import java.util.*;

public class Usage<
        P extends PartOfSpeech,
        T extends PointerType<T, P>,
        I extends UsageId> {

    private I _id;
    private String _lemma;
    private String _definition;
    private P _pos;
    private int _senseIndex;
    private Map<T, List<Pointer<I, T>>> _pointers;

    public I getId() {
        return _id;
    }

    public String getLemma() {
        return _lemma;
    }

    public String getDefinition() {
        return _definition;
    }

    public P getPartOfSpeech() {
        return _pos;
    }

    public int getSenseIndex() {
        return _senseIndex;
    }

    public Set<T> getPointerTypes() {
        return _pointers.keySet();
    }

    public List<Pointer<I, T>> getPointers() {
        return getPointers(null);
    }

    public List<Pointer<I, T>> getPointers(T type) {
        Collection<List<Pointer<I, T>>> pointerLists = _pointers.values();
        List<Pointer<I, T>> allPointers = new ArrayList<Pointer<I, T>>();
        for (List<Pointer<I, T>> pointers : pointerLists) {
            for (Pointer<I, T> pointer : pointers) {
                if (type == null || type == pointer.getPointerType()) {
                    allPointers.add(pointer);
                }
            }
        }
        return allPointers;
    }
}