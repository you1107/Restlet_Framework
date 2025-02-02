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

package com.noelios.restlet.ext.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.Reference;

import com.noelios.restlet.http.HttpRequest;
import com.noelios.restlet.http.HttpResponse;
import com.noelios.restlet.http.HttpServerConverter;

/**
 * HTTP converter from Servlet calls to Restlet calls. This class can be used in
 * any Servlet, just create a new instance and override the service() method in
 * your Servlet to delegate all those calls to this class's service() method.
 * Remember to set the target Restlet, for example using a Restlet Router
 * instance. You can get the Restlet context directly on instances of this
 * class, it will be based on the parent Servlet's context for logging purpose.<br/>
 * <br/>
 * 
 * This class is especially useful when directly integrating Restlets with
 * Spring managed Web applications. Here is a simple usage example:
 * 
 * <pre>
 * public class TestServlet extends HttpServlet {
 *     private ServletConverter converter;
 * 
 *     public void init() throws ServletException {
 *         super.init();
 *         this.converter = new ServletConverter(getServletContext());
 * 
 *         Restlet trace = new Restlet(this.converter.getContext()) {
 *             public void handle(Request req, Response res) {
 *                 getLogger().info(&quot;Hello World&quot;);
 *                 res.setEntity(&quot;Hello World!&quot;, MediaType.TEXT_PLAIN);
 *             }
 *         };
 * 
 *         this.converter.setTarget(trace);
 *     }
 * 
 *     protected void service(HttpServletRequest req, HttpServletResponse res)
 *             throws ServletException, IOException {
 *         this.converter.service(req, res);
 *     }
 * }
 * </pre>
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class ServletConverter extends HttpServerConverter {
    /** The target Restlet. */
    private Restlet target;

    /**
     * Constructor. Remember to manually set the "target" property before
     * invoking the service() method.
     * 
     * @param context
     *            The Servlet context.
     */
    public ServletConverter(ServletContext context) {
        this(context, null);
    }

    /**
     * Constructor.
     * 
     * @param context
     *            The Servlet context.
     * @param target
     *            The target Restlet.
     */
    public ServletConverter(ServletContext context, Restlet target) {
        super(new Context(new ServletLogger(context)));
        this.target = target;
    }

    /**
     * Services a HTTP Servlet request as a Restlet request handled by the
     * "target" Restlet.
     * 
     * @param request
     *            The HTTP Servlet request.
     * @param response
     *            The HTTP Servlet response.
     */
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (getTarget() != null) {
            // Convert the Servlet call to a Restlet call
            ServletCall servletCall = new ServletCall(getLogger(), request
                    .getLocalAddr(), request.getLocalPort(), request, response);
            HttpRequest httpRequest = toRequest(servletCall);
            HttpResponse httpResponse = new HttpResponse(servletCall,
                    httpRequest);

            // Adjust the relative reference
            httpRequest.getResourceRef().setBaseRef(getBaseRef(request));

            // Adjust the root reference
            httpRequest.setRootRef(getRootRef(request));

            // Handle the request and commit the response
            getTarget().handle(httpRequest, httpResponse);
            commit(httpResponse);
        } else {
            getLogger().warning("Unable to find the Restlet target");
        }
    }

    /**
     * Returns the base reference of new Restlet requests.
     * 
     * @param request
     *            The Servlet request.
     * @return The base reference of new Restlet requests.
     */
    public Reference getBaseRef(HttpServletRequest request) {
        Reference result = null;
        String basePath = request.getContextPath() + request.getServletPath();
        String baseUri = request.getRequestURL().toString();
        int baseIndex = baseUri.indexOf(basePath);
        if (baseIndex != -1) {
            result = new Reference(baseUri.substring(0, baseIndex
                    + basePath.length()));
        }

        return result;
    }

    /**
     * Returns the root reference of new Restlet requests. By default it returns
     * the result of getBaseRef().
     * 
     * @param request
     *            The Servlet request.
     * @return The root reference of new Restlet requests.
     */
    public Reference getRootRef(HttpServletRequest request) {
        return getBaseRef(request);
    }

    /**
     * Returns the target Restlet.
     * 
     * @return The target Restlet.
     */
    public Restlet getTarget() {
        return this.target;
    }

    /**
     * Sets the target Restlet.
     * 
     * @param target
     *            The target Restlet.
     */
    public void setTarget(Restlet target) {
        this.target = target;
    }

}
