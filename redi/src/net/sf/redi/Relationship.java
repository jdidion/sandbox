/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

import java.util.List;
import java.util.LinkedList;

public class Relationship<I extends UsageId, T extends PointerType> extends Pointer<I, T> {
    private List<Pointer<I, T>> _pointers = new LinkedList<Pointer<I, T>>();

    protected Relationship(I source, I target, T pointerType) {
        super(source, target, pointerType);
    }

    public void addPointer(Pointer<I, T> pointer) {
        _pointers.add(pointer);
    }

    public List<Pointer<I, T>> getPointers() {
        return _pointers;
    }
}