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

package org.restlet.test;

import junit.framework.TestCase;

import org.restlet.Server;
import org.restlet.data.Protocol;

/**
 * Test the ability of a connector to be restarted.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class RestartTestCase extends TestCase {

    public void testRestart() throws Exception {
        int waitTime = 100;

        Server connector = new Server(Protocol.HTTP, 8182, null);

        System.out.print("Starting connector... ");
        connector.start();
        System.out.println("done");
        Thread.sleep(waitTime);

        System.out.print("Stopping connector... ");
        connector.stop();
        System.out.println("done");
        Thread.sleep(waitTime);

        System.out.print("Restarting connector... ");
        connector.start();
        System.out.println("done");
        Thread.sleep(waitTime);
    }

}
