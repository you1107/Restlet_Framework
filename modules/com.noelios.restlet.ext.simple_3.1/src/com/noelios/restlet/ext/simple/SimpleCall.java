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

package com.noelios.restlet.ext.simple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.util.Series;

import simple.http.Request;
import simple.http.Response;

import com.noelios.restlet.http.HttpServerCall;

/**
 * Call that is used by the Simple HTTP server.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a
 *         href="http://semagia.com/">Semagia</a>
 * @author Jerome Louvel (contact@noelios.com)
 */
public class SimpleCall extends HttpServerCall {
	/**
	 * Simple Request.
	 */
	private Request request;

	/**
	 * Simple Response.
	 */
	private Response response;

	/** Indicates if the request headers were parsed and added. */
	private boolean requestHeadersAdded;

	/**
	 * Constructs this class with the specified {@link simple.http.Request} and
	 * {@link simple.http.Response}.
	 * 
	 * @param server
	 *            The parent server.
	 * @param request
	 *            Request to wrap.
	 * @param response
	 *            Response to wrap.
	 * @param confidential
	 *            Indicates if this call is acting in HTTP or HTTPS mode.
	 */
	SimpleCall(Server server, Request request, Response response,
			boolean confidential) {
		super(server);
		this.request = request;
		this.response = response;
		setConfidential(confidential);
		this.requestHeadersAdded = false;
	}

	@Override
	public String getClientAddress() {
		return request.getInetAddress().getHostAddress();
	}

	@Override
	public int getClientPort() {
		Socket sock = (Socket) request
				.getAttribute(SimplePipelineFactory.PROPERTY_SOCKET);
		return (sock != null) ? sock.getPort() : -1;
	}

	/**
	 * Returns the request method.
	 * 
	 * @return The request method.
	 */
	@Override
	public String getMethod() {
		return request.getMethod();
	}

	/**
	 * Returns the request entity channel if it exists.
	 * 
	 * @return The request entity channel if it exists.
	 */
	@Override
	public ReadableByteChannel getRequestChannel() {
		// Unsupported.
		return null;
	}

	/**
	 * Returns the list of request headers.
	 * 
	 * @return The list of request headers.
	 */
	@Override
	public Series<Parameter> getRequestHeaders() {
		Series<Parameter> result = super.getRequestHeaders();

		if (!this.requestHeadersAdded) {
			int headerCount = request.headerCount();
			for (int i = 0; i < headerCount; i++) {
				result.add(new Parameter(request.getName(i), request
						.getValue(i)));
			}

			this.requestHeadersAdded = true;
		}

		return result;
	}

	/**
	 * Returns the request entity stream if it exists.
	 * 
	 * @return The request entity stream if it exists.
	 */
	@Override
	public InputStream getRequestStream() {
		try {
			return request.getInputStream();
		} catch (IOException ex) {
			return null;
		}
	}

	/**
	 * Returns the full request URI.
	 * 
	 * @return The full request URI.
	 */
	@Override
	public String getRequestUri() {
		return request.getURI();
	}

	/**
	 * Returns the response channel if it exists.
	 * 
	 * @return The response channel if it exists.
	 */
	@Override
	public WritableByteChannel getResponseChannel() {
		// Unsupported.
		return null;
	}

	/**
	 * Returns the response stream if it exists.
	 * 
	 * @return The response stream if it exists.
	 */
	@Override
	public OutputStream getResponseStream() {
		try {
			return response.getOutputStream();
		} catch (IOException ex) {
			return null;
		}
	}

	@Override
	public String getVersion() {
		return request.getMajor() + "." + request.getMinor();
	}

	@Override
	public void writeResponseHead(org.restlet.data.Response restletResponse)
			throws IOException {
		response.clear();
		for (Parameter header : getResponseHeaders()) {
			response.add(header.getName(), header.getValue());
		}

		// Set the status
		response.setCode(getStatusCode());
		response.setText(getReasonPhrase());

		// To ensure that Simple doesn't switch to chunked encoding
		if (restletResponse.getEntity() == null) {
			response.setContentLength(0);
		}
	}
}
