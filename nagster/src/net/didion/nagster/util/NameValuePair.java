/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.util;

import java.io.Serializable;

public class NameValuePair implements Serializable {
    private String _name;
    private String _value;

    public NameValuePair() {
    }

    public NameValuePair(String name, String value) {
        _name = name;
        _value = value;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String value) {
        _value = value;
    }
}