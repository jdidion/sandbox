/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import junit.framework.TestCase;

import javax.activation.MimeType;

public class MimeTypeTestTest extends TestCase {
    public MimeTypeTestTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void test() throws Exception {
        MimeType mt = new MimeType("text/plain");
        System.out.println(mt.getBaseType());
        System.out.println(mt.getPrimaryType());
        System.out.println(mt.getSubType());
    }
}