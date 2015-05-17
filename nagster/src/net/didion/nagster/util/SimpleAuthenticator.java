/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SimpleAuthenticator extends Authenticator {
    private PasswordAuthentication _auth;

    public SimpleAuthenticator(PasswordAuthentication auth) {
        _auth = auth;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return _auth;
    }
}