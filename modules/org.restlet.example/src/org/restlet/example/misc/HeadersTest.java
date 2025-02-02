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

package org.restlet.example.misc;

import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.util.Series;

/**
 * Display the HTTP accept header sent by the Web browsers.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class HeadersTest {
	public static void main(String[] args) throws Exception {
		Restlet restlet = new Restlet() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(Request request, Response response) {
				// ------------------------------
				// Getting an HTTP request header
				// ------------------------------
				Series<Parameter> headers = (Series<Parameter>) request
						.getAttributes().get("org.restlet.http.headers");

				// The headers list contains all received HTTP headers, in raw
				// format.
				// Below, we simply display the standard "Accept" HTTP header.
				response.setEntity("Accept header: "
						+ headers.getFirstValue("accept", true),
						MediaType.TEXT_PLAIN);

				// -----------------------
				// Adding response headers
				// -----------------------
				headers = new Form();

				// Non-standard headers are allowed
				headers.add("X-Test", "Test value");

				// Standard HTTP headers are forbidden. If you happen to add one
				// like the "Location"
				// header below, it will be ignored and a warning message will
				// be displayed in the logs.
				headers.add("Location", "http://www.restlet.org");

				// Setting the additional headers into the shared call's
				// attribute
				response.getAttributes().put("org.restlet.http.headers",
						headers);
			}
		};

		// Create the HTTP server and listen on port 8182
		Server server = new Server(Protocol.HTTP, 8182, restlet);
		server.start();
	}

}
