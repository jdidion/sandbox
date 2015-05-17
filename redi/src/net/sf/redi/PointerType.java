/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

public interface PointerType<T extends PointerType<T, P>, P extends PartOfSpeech> {
    String toString();
    T getSymmetricType();
    boolean isAppliesTo(P pos);
}