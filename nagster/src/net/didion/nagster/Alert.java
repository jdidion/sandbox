/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import java.io.Serializable;

public interface Alert extends Serializable {
    Integer getId();
    boolean execute(ActionManager executor) throws Exception;
}