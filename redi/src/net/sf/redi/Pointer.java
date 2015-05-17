/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

public class Pointer<I extends UsageId, T extends PointerType> {
    private I _source;
    private I _target;
    private T _pointerType;

    public Pointer(I source, I target, T pointerType) {
        _source = source;
        _target = target;
        _pointerType = pointerType;
    }

    public I getSource() {
        return _source;
    }

    public I getTarget() {
        return _target;
    }

    public T getPointerType() {
        return _pointerType;
    }
}