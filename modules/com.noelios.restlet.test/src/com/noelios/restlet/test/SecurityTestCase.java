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

import java.io.IOException;

import junit.framework.TestCase;

import com.noelios.restlet.util.SecurityUtils;

/**
 * Unit tests for the SecurityData related classes.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class SecurityTestCase extends TestCase {
    /**
     * Tests the cookies parsing.
     */
    public void testParsing() throws IOException {
        String authenticate1 = "Basic realm=\"Restlet tutorial\"";
        String authorization1 = "Basic c2NvdHQ6dGlnZXI=";

        assertEquals(authorization1, SecurityUtils.format(SecurityUtils
                .parseResponse(null, null, authorization1), null, null));
        assertEquals(authenticate1, SecurityUtils.format(SecurityUtils
                .parseRequest(authenticate1)));
    }

}
