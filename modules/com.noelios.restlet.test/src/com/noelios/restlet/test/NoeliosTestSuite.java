/**
 * Copyright 2005-2008 Noelios Technologies.
 * 
 * The contents of this file are subject to the terms of the following open
 * source licenses: LGPL 3.0 or LGPL 2.1 or CDDL 1.0 (the "Licenses"). You can
 * select the license that you prefer but you may not use this file except in
 * compliance with one of these Licenses.
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.sun.com/cddl/cddl.html
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royaltee free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.noelios.com/products/restlet-engine/.
 * 
 * Restlet is a registered trademark of Noelios Technologies.
 */

package com.noelios.restlet.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite of unit tests for the Noelios Restlet Engine.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class NoeliosTestSuite extends TestSuite {
    /** Constructor. */
    public NoeliosTestSuite() {
        addTestSuite(CookiesTestCase.class);
        addTestSuite(FormTestCase.class);
        addTestSuite(HeaderTestCase.class);
        addTestSuite(PreferencesTestCase.class);
        addTestSuite(SecurityTestCase.class);
    }

    /**
     * JUnit constructor.
     * 
     * @return The unit test.
     */
    public static Test suite() {
        return new NoeliosTestSuite();
    }
}
