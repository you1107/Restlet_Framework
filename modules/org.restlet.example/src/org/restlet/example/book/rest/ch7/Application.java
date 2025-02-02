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

package org.restlet.example.book.rest.ch7;

import java.io.File;

import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Route;
import org.restlet.Router;
import org.restlet.data.Protocol;
import org.restlet.util.Variable;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;

/**
 * The main Web application.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class Application extends org.restlet.Application {

    public static void main(String... args) throws Exception {
        // Create a component with an HTTP server connector
        Component comp = new Component();
        comp.getServers().add(Protocol.HTTP, 3000);

        // Attach the application to the default host and start it
        comp.getDefaultHost().attach("/v1", new Application());
        comp.start();
    }

    private ObjectContainer container;

    public Application() {
        /** Open and keep the db4o object container. */
        Configuration config = Db4o.configure();
        config.updateDepth(2);
        this.container = Db4o.openFile(System.getProperty("user.home")
                + File.separator + "restbook.dbo");
    }

    @Override
    public Restlet createRoot() {
        Router router = new Router(getContext());

        // Add a route for user resources
        router.attach("/users/{username}", UserResource.class);

        // Add a route for user's bookmarks resources
        router.attach("/users/{username}/bookmarks", BookmarksResource.class);

        // Add a route for bookmark resources
        Route uriRoute = router.attach("/users/{username}/bookmarks/{URI}",
                BookmarkResource.class);
        uriRoute.getTemplate().getVariables().put("URI",
                new Variable(Variable.TYPE_URI_ALL));

        return router;
    }

    /**
     * Returns the database container.
     * 
     * @return the database container.
     */
    public ObjectContainer getContainer() {
        return this.container;
    }

}
