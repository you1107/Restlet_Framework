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

package com.noelios.restlet.application;

import org.restlet.Application;
import org.restlet.Filter;
import org.restlet.data.CharacterSet;
import org.restlet.data.Encoding;
import org.restlet.data.Form;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Metadata;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Request;
import org.restlet.data.Response;

/**
 * Filter tunnelling browser calls into full REST calls. The request method can
 * be changed (via POST requests only) as well as the accepted media types,
 * languages, encodings and character sets.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class TunnelFilter extends Filter {
    /** The application. */
    private Application application;

    /**
     * Constructor.
     * 
     * @param application
     *            The parent application.
     */
    public TunnelFilter(Application application) {
        super(application.getContext());
        this.application = application;
    }

    /**
     * Returns the application.
     * 
     * @return The application.
     */
    public Application getApplication() {
        return this.application;
    }

    /**
     * Allows filtering before its handling by the target Restlet. Does nothing
     * by default.
     * 
     * @param request
     *            The request to handle.
     * @param response
     *            The response to update.
     */
	@Override
    public void beforeHandle(Request request, Response response) {
        super.beforeHandle(request, response);
        Form query = request.getResourceRef().getQueryAsForm(null);
        boolean queryModified = false;

        // Tunnels the extracted attributes into the proper call objects.
        if (getApplication().getTunnelService().isMethodTunnel()
                && request.getMethod().equals(Method.POST)) {
            String methodName = query.getFirstValue(getApplication()
                    .getTunnelService().getMethodParameter());

            if (methodName != null) {
                request.setMethod(Method.valueOf(methodName));

                // The parameter is removed from the query
                query.removeFirst(getApplication().getTunnelService()
                        .getMethodParameter());
                queryModified = true;
            }
        }

        if (request.getMethod().equals(Method.GET)
                && getApplication().getTunnelService().isPreferencesTunnel()) {
            // Extract the header values
            String acceptCharset = query.getFirstValue(getApplication()
                    .getTunnelService().getCharacterSetAttribute());
            String acceptEncoding = query.getFirstValue(getApplication()
                    .getTunnelService().getEncodingAttribute());
            String acceptLanguage = query.getFirstValue(getApplication()
                    .getTunnelService().getLanguageAttribute());
            String acceptMediaType = query.getFirstValue(getApplication()
                    .getTunnelService().getMediaTypeAttribute());

            // Parse the headers and update the call preferences
            Metadata metadata = null;
            if (acceptCharset != null) {
                metadata = getApplication().getMetadataService().getMetadata(
                        acceptCharset);

                if (metadata instanceof CharacterSet) {
                    request.getClientInfo().getAcceptedCharacterSets().clear();
                    request.getClientInfo().getAcceptedCharacterSets().add(
                            new Preference<CharacterSet>(
                                    (CharacterSet) metadata));

                    // The parameter is removed from the query
                    query.removeFirst(getApplication().getTunnelService()
                            .getCharacterSetAttribute());
                    queryModified = true;
                }
            }

            if (acceptEncoding != null) {
                metadata = getApplication().getMetadataService().getMetadata(
                        acceptEncoding);

                if (metadata instanceof Encoding) {
                    request.getClientInfo().getAcceptedEncodings().clear();
                    request.getClientInfo().getAcceptedEncodings().add(
                            new Preference<Encoding>((Encoding) metadata));

                    // The parameter is removed from the query
                    query.removeFirst(getApplication().getTunnelService()
                            .getEncodingAttribute());
                    queryModified = true;
                }
            }

            if (acceptLanguage != null) {
                metadata = getApplication().getMetadataService().getMetadata(
                        acceptLanguage);

                if (metadata instanceof Language) {
                    request.getClientInfo().getAcceptedLanguages().clear();
                    request.getClientInfo().getAcceptedLanguages().add(
                            new Preference<Language>((Language) metadata));

                    // The parameter is removed from the query
                    query.removeFirst(getApplication().getTunnelService()
                            .getLanguageAttribute());
                    queryModified = true;
                }
            }

            if (acceptMediaType != null) {
                metadata = getApplication().getMetadataService().getMetadata(
                        acceptMediaType);

                if (metadata instanceof MediaType) {
                    request.getClientInfo().getAcceptedMediaTypes().clear();
                    request.getClientInfo().getAcceptedMediaTypes().add(
                            new Preference<MediaType>((MediaType) metadata));

                    // The parameter is removed from the query
                    query.removeFirst(getApplication().getTunnelService()
                            .getMediaTypeAttribute());
                    queryModified = true;
                }
            }
        }

        // Update the query if it has been modified
        if (queryModified) {
            request.getResourceRef().setQuery(query.getQueryString(null));
        }
    }

}
