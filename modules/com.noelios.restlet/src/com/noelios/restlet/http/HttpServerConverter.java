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

package com.noelios.restlet.http;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.data.CookieSetting;
import org.restlet.data.Dimension;
import org.restlet.data.Encoding;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.util.DateUtils;
import org.restlet.util.Series;

import com.noelios.restlet.util.CookieUtils;
import com.noelios.restlet.util.SecurityUtils;

/**
 * Converter of low-level HTTP server calls into high-level uniform calls.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class HttpServerConverter extends HttpConverter {
    /**
     * Constructor.
     * 
     * @param context
     *            The client context.
     */
    public HttpServerConverter(Context context) {
        super(context);
    }

    /**
     * Converts a low-level HTTP call into a high-level uniform request.
     * 
     * @param httpCall
     *            The low-level HTTP call.
     * @return A new high-level uniform request.
     */
    public HttpRequest toRequest(HttpServerCall httpCall) {
        HttpRequest result = new HttpRequest(getContext(), httpCall);
        result.getAttributes().put(HttpConstants.ATTRIBUTE_HEADERS,
                httpCall.getRequestHeaders());

        if (httpCall.getVersion() != null) {
            result.getAttributes().put(HttpConstants.ATTRIBUTE_VERSION,
                    httpCall.getVersion());
        }

        return result;
    }

    /**
     * Commits the changes to a handled uniform call back into the original HTTP
     * call. The default implementation first invokes the "addResponseHeaders"
     * then asks the "htppCall" to send the response back to the client.
     * 
     * @param response
     *            The high-level response.
     */
    public void commit(HttpResponse response) {
        try {
            // Add the response headers
            addResponseHeaders(response);

            // Send the response to the client
            response.getHttpCall().sendResponse(response);
        } catch (Exception e) {
            getLogger().log(Level.INFO, "Exception intercepted", e);
            response.getHttpCall().setStatusCode(
                    Status.SERVER_ERROR_INTERNAL.getCode());
            response.getHttpCall().setReasonPhrase(
                    "An unexpected exception occured");
        }
    }

    /**
     * Adds the response headers for the handled uniform call.
     * 
     * @param response
     *            The response returned.
     */
    @SuppressWarnings("unchecked")
    protected void addResponseHeaders(HttpResponse response) {
        try {
            // Add all the necessary response headers
            Series<Parameter> responseHeaders = response.getHttpCall()
                    .getResponseHeaders();

            if (response.getStatus().equals(
                    Status.CLIENT_ERROR_METHOD_NOT_ALLOWED)
                    || Method.OPTIONS.equals(response.getRequest().getMethod())) {
                // Format the "Allow" header
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (Method method : response.getAllowedMethods()) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", ");
                    }

                    sb.append(method.getName());
                }

                responseHeaders.add(HttpConstants.HEADER_ALLOW, sb.toString());
            }

            // Add the date
            responseHeaders.add(HttpConstants.HEADER_DATE, DateUtils.format(
                    new Date(), DateUtils.FORMAT_RFC_1123.get(0)));

            // Add the cookie settings
            List<CookieSetting> cookies = response.getCookieSettings();
            for (int i = 0; i < cookies.size(); i++) {
                responseHeaders.add(HttpConstants.HEADER_SET_COOKIE,
                        CookieUtils.format(cookies.get(i)));
            }

            // Set the redirection URI
            if (response.getRedirectRef() != null) {
                responseHeaders.add(HttpConstants.HEADER_LOCATION, response
                        .getRedirectRef().toString());
            }

            // Set the security data
            if (response.getChallengeRequest() != null) {
                responseHeaders.add(HttpConstants.HEADER_WWW_AUTHENTICATE,
                        SecurityUtils.format(response.getChallengeRequest()));
            }

            // Set the server name again
            response.getHttpCall().getResponseHeaders().add(
                    HttpConstants.HEADER_SERVER,
                    response.getServerInfo().getAgent());

            // Set the status code in the response
            if (response.getStatus() != null) {
                response.getHttpCall().setStatusCode(
                        response.getStatus().getCode());
                response.getHttpCall().setReasonPhrase(
                        response.getStatus().getDescription());
            }

            // If an entity was set during the call, copy it to the output
            // stream;
            if (response.getEntity() != null) {
                Representation entity = response.getEntity();

                if (entity.getExpirationDate() != null) {
                    responseHeaders.add(HttpConstants.HEADER_EXPIRES, response
                            .getHttpCall().formatDate(
                                    entity.getExpirationDate(), false));
                }

                if (!entity.getEncodings().isEmpty()) {
                    StringBuilder value = new StringBuilder();
                    for (Encoding encoding : entity.getEncodings()) {
                        if (!encoding.equals(Encoding.IDENTITY)) {
                            if (value.length() > 0)
                                value.append(", ");
                            value.append(encoding.getName());
                        }
                        responseHeaders.add(
                                HttpConstants.HEADER_CONTENT_ENCODING, value
                                        .toString());
                    }
                }

                if (!entity.getLanguages().isEmpty()) {
                    StringBuilder value = new StringBuilder();
                    for (int i = 0; i < entity.getLanguages().size(); i++) {
                        if (i > 0)
                            value.append(", ");
                        value.append(entity.getLanguages().get(i).getName());
                    }
                    responseHeaders.add(HttpConstants.HEADER_CONTENT_LANGUAGE,
                            value.toString());
                }

                if (entity.getMediaType() != null) {
                    StringBuilder contentType = new StringBuilder(entity
                            .getMediaType().getName());

                    if (entity.getCharacterSet() != null) {
                        // Specify the character set parameter
                        contentType.append("; charset=").append(
                                entity.getCharacterSet().getName());
                    }

                    for (Parameter parameter : entity.getMediaType()
                            .getParameters()) {
                        contentType.append("; ").append(parameter.getName())
                                .append("=").append(parameter.getValue());
                    }

                    responseHeaders.add(HttpConstants.HEADER_CONTENT_TYPE,
                            contentType.toString());
                }

                if (entity.getModificationDate() != null) {
                    responseHeaders.add(HttpConstants.HEADER_LAST_MODIFIED,
                            response.getHttpCall().formatDate(
                                    entity.getModificationDate(), false));
                }

                if (entity.getTag() != null) {
                    responseHeaders.add(HttpConstants.HEADER_ETAG, entity
                            .getTag().format());
                }

                if (response.getEntity().getSize() != Representation.UNKNOWN_SIZE) {
                    responseHeaders.add(HttpConstants.HEADER_CONTENT_LENGTH,
                            Long.toString(response.getEntity().getSize()));
                }

                if (response.getEntity().getIdentifier() != null) {
                    responseHeaders.add(HttpConstants.HEADER_CONTENT_LOCATION,
                            response.getEntity().getIdentifier().toString());
                }
            }

            // Send the Vary header only to none-MSIE user agents as MSIE seems
            // to support partially and badly this header (cf issue 261).
            if (!(response.getRequest().getClientInfo().getAgent() != null && response
                    .getRequest().getClientInfo().getAgent().contains("MSIE"))) {
                // Add the Vary header if content negotiation was used
                Set<Dimension> dimensions = response.getDimensions();
                if (!dimensions.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    boolean first = true;

                    if (dimensions.contains(Dimension.CLIENT_ADDRESS)
                            || dimensions.contains(Dimension.TIME)
                            || dimensions.contains(Dimension.UNSPECIFIED)) {
                        // From an HTTP point of view the representations can
                        // vary in unspecified ways
                        responseHeaders.add(HttpConstants.HEADER_VARY, "*");
                    } else {
                        for (Dimension dim : response.getDimensions()) {
                            if (first) {
                                first = false;
                            } else {
                                sb.append(", ");
                            }

                            if (dim == Dimension.CHARACTER_SET) {
                                sb.append(HttpConstants.HEADER_ACCEPT_CHARSET);
                            } else if (dim == Dimension.CLIENT_AGENT) {
                                sb.append(HttpConstants.HEADER_USER_AGENT);
                            } else if (dim == Dimension.ENCODING) {
                                sb.append(HttpConstants.HEADER_ACCEPT_ENCODING);
                            } else if (dim == Dimension.LANGUAGE) {
                                sb.append(HttpConstants.HEADER_ACCEPT_LANGUAGE);
                            } else if (dim == Dimension.MEDIA_TYPE) {
                                sb.append(HttpConstants.HEADER_ACCEPT);
                            }
                        }

                        responseHeaders.add(HttpConstants.HEADER_VARY, sb
                                .toString());
                    }
                }
            }

            // Add user-defined extension headers
            Series<Parameter> additionalHeaders = (Series<Parameter>) response
                    .getAttributes().get(HttpConstants.ATTRIBUTE_HEADERS);
            addAdditionalHeaders(responseHeaders, additionalHeaders);
        } catch (Exception e) {
            getLogger().log(Level.INFO,
                    "Exception intercepted while adding the response headers",
                    e);
            response.getHttpCall().setStatusCode(
                    Status.SERVER_ERROR_INTERNAL.getCode());
            response.getHttpCall().setReasonPhrase(
                    Status.SERVER_ERROR_INTERNAL.getDescription());
        }
    }
}
