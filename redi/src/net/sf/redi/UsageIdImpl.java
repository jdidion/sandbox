/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

public class UsageIdImpl<P extends PartOfSpeech, X> implements UsageId {
    private P _pos;
    private X _key;

    public UsageIdImpl(P pos, X id) {
        _pos = pos;
        _key = id;
    }

    public P getPos() {
        return _pos;
    }

    public X getKey() {
        return _key;
    }

    public String toString() {
        return Utils.format("UsageId<%s, %s>", _pos, _key);
    }
}