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

import org.restlet.Client;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.util.Series;

/**
 * Test the Amazon Web Service authentication.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class AwsTest {
	public static void main(String[] args) throws Exception {
		// Prepare the request
		Request request = new Request(Method.GET,
				"http://s3.amazonaws.com/quotes/nelson");
		request.setChallengeResponse(new ChallengeResponse(
				ChallengeScheme.HTTP_AWS, "44CF9590006BF252F707",
				"OtxrzxIsfpFjA7SwPzILwy8Bw21TLhquhboDYROV"));

		// Add some extra headers
		Series<Parameter> extraHeaders = new Form();
		extraHeaders.add("X-Amz-Meta-Author", "foo@bar.com");
		extraHeaders.add("X-Amz-Magic", "abracadabra");

		// For the test we hard coded a special date header. Normally you don't
		// need this as the
		// HTTP client connector will automatically provide an accurate Date
		// header and use it
		// for authentication.
		// extraHeaders.add("X-Amz-Date", "Thu, 17 Nov 2005 18:49:58 GMT");

		request.getAttributes().put("org.restlet.http.headers", extraHeaders);

		// Handle it using an HTTP client connector
		Client client = new Client(Protocol.HTTP);
		Response response = client.handle(request);

		// Write the response entity on the console
		Representation output = response.getEntity();
		output.write(System.out);
	}

}
