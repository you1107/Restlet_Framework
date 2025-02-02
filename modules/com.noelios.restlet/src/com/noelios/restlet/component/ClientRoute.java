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

package com.noelios.restlet.component;

import java.util.logging.Level;

import org.restlet.Client;
import org.restlet.Route;
import org.restlet.Router;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;

/**
 * Router scorer based on a target client connector.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class ClientRoute extends Route {
    /**
     * Constructor.
     * 
     * @param router
     *            The parent router.
     * @param target
     *            The target client.
     */
    public ClientRoute(Router router, Client target) {
        super(router, "", target);
    }

    /**
     * Returns the target client.
     * 
     * @return The target client.
     */
    public Client getClient() {
        return (Client) getNext();
    }

    /**
     * Sets the next client.
     * 
     * @param next
     *            The next client.
     */
    public void setNext(Client next) {
        super.setNext(next);
    }

    /**
     * Returns the score for a given call (between 0 and 1.0).
     * 
     * @param request
     *            The request to score.
     * @param response
     *            The response to score.
     * @return The score for a given call (between 0 and 1.0).
     */
	@Override
    public float score(Request request, Response response) {
        float result = 0F;

        // Add the protocol score
        Protocol protocol = request.getProtocol();

        if (protocol == null) {
            getLogger().warning(
                    "Unable to determine the protocol to use for this call.");
        } else if (getClient().getProtocols().contains(protocol)) {
            result = 1.0F;
        }

        if (getLogger().isLoggable(Level.FINER)) {
            getLogger().finer(
                    "Call score for the \""
                            + getClient().getProtocols().toString()
                            + "\" client: " + result);
        }

        return result;
    }
}
