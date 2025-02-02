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

import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Router;

/**
 * Router that collects calls from all applications and dispatches them to the
 * appropriate client connectors.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class ClientRouter extends Router {
    /** The parent component. */
    private Component component;

    /**
     * Constructor.
     * 
     * @param component
     *            The parent component.
     */
    public ClientRouter(Component component) {
        super(component.getContext());
        this.component = component;
    }

    /** Starts the Restlet. */
	@Override
    public void start() throws Exception {
        for (Client client : getComponent().getClients()) {
            getRoutes().add(new ClientRoute(this, client));
        }

        super.start();
    }

    /**
     * Returns the parent component.
     * 
     * @return The parent component.
     */
    private Component getComponent() {
        return this.component;
    }
}
