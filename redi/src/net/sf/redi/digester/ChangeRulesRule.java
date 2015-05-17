/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi.digester;

import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Rules;
import org.xml.sax.Attributes;
import static net.sf.redi.Constants.*;

public class ChangeRulesRule extends Rule {
    private Rules _oldRules;

    public void begin(String namespace, String name, Attributes attributes)
            throws Exception {
        String className = attributes.getValue(ATTR_RULES_CLASS);
        if (className != null) {
            Rules newRules = ((Class<Rules>) Class.forName(className)).newInstance();
            _oldRules = getDigester().getRules();
            getDigester().setRules(newRules);
        }
    }

    public void end(String namespace, String name) throws Exception {
        if (_oldRules != null) {
            getDigester().setRules(_oldRules);
        }
    }
}