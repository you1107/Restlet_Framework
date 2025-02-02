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

package org.restlet.service;

import org.restlet.data.ClientInfo;

/**
 * Service tunnelling method names or client preferences via query parameters.
 * Clients applications such as browsers can easily override the default values
 * of their client connector by specifying additional query parameters. Here is
 * the list of the default parameter names supported: <table>
 * <tr>
 * <th>Property</th>
 * <th>Default name</th>
 * <th>Value type</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>methodParameter</td>
 * <td>method</td>
 * <td>See values in org.restlet.data.Method</td>
 * <td>For POST requests, specify the actual method to use (DELETE, PUT, etc.).</td>
 * </tr>
 * <tr>
 * <td>characterSetParameter</td>
 * <td>charset</td>
 * <td>Use extension names defined in org.restlet.service.MetadataService</td>
 * <td>For GET requests, replaces the accepted character set by the given
 * value.</td>
 * </tr>
 * <tr>
 * <td>encodingParameter</td>
 * <td>encoding</td>
 * <td>Use extension names defined in org.restlet.service.MetadataService</td>
 * <td>For GET requests, replaces the accepted encoding by the given value.</td>
 * </tr>
 * <tr>
 * <td>languageParameter</td>
 * <td>language</td>
 * <td>Use extension names defined in org.restlet.service.MetadataService</td>
 * <td>For GET requests, replaces the accepted language by the given value.</td>
 * </tr>
 * <tr>
 * <td>mediaTypeParameter</td>
 * <td>media</td>
 * <td>Use extension names defined in org.restlet.service.MetadataService</td>
 * <td>For GET requests, replaces the accepted media type set by the given
 * value.</td>
 * </tr>
 * </table>
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class TunnelService {
    /** Indicates if the service has been enabled. */
    private boolean enabled;

    /** Indicates if the method name can be tunneled. */
    private boolean methodTunnel;

    /** The name of the parameter containing the method name. */
    private String methodParameter;

    /** Indicates if the client preferences can be tunneled. */
    private boolean preferencesTunnel;

    /** The name of the parameter containing the accepted character set. */
    private String characterSetParameter;

    /** The name of the parameter containing the accepted encoding. */
    private String encodingParameter;

    /** The name of the parameter containing the accepted language. */
    private String languageParameter;

    /** The name of the parameter containing the accepted media type. */
    private String mediaTypeParameter;

    /**
     * Constructor.
     * 
     * @param enabled
     *            True if the service has been enabled.
     * @param methodTunnel
     *            Indicates if the method name can be tunneled.
     * @param preferencesTunnel
     *            Indicates if the client preferences can be tunneled.
     */
    public TunnelService(boolean enabled, boolean methodTunnel,
            boolean preferencesTunnel) {
        this.enabled = enabled;
        this.methodTunnel = methodTunnel;
        this.methodParameter = "method";
        this.preferencesTunnel = preferencesTunnel;
        this.characterSetParameter = "charset";
        this.encodingParameter = "encoding";
        this.languageParameter = "language";
        this.mediaTypeParameter = "media";
    }

    /**
     * Indicates if the request from a given client can be tunnelled. The
     * default implementation always return true. This could be customize to
     * restrict the usage of the tunnel service.
     * 
     * @param client
     *            The client to test.
     * @return True if the request from a given client can be tunnelled.
     */
    public boolean allowClient(ClientInfo client) {
        return true;
    }

    /**
     * Returns the character set parameter name.
     * 
     * @return The character set parameter name.
     */
    public String getCharacterSetAttribute() {
        return this.characterSetParameter;
    }

    /**
     * Returns the name of the parameter containing the accepted encoding.
     * 
     * @return The name of the parameter containing the accepted encoding.
     */
    public String getEncodingAttribute() {
        return this.encodingParameter;
    }

    /**
     * Returns the name of the parameter containing the accepted language.
     * 
     * @return The name of the parameter containing the accepted language.
     */
    public String getLanguageAttribute() {
        return this.languageParameter;
    }

    /**
     * Returns the name of the parameter containing the accepted media type.
     * 
     * @return The name of the parameter containing the accepted media type.
     */
    public String getMediaTypeAttribute() {
        return this.mediaTypeParameter;
    }

    /**
     * Returns the method parameter name.
     * 
     * @return The method parameter name.
     */
    public String getMethodParameter() {
        return this.methodParameter;
    }

    /**
     * Indicates if the service should be enabled.
     * 
     * @return True if the service should be enabled.
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Indicates if the method name can be tunneled.
     * 
     * @return True if the method name can be tunneled.
     */
    public boolean isMethodTunnel() {
        return this.methodTunnel;
    }

    /**
     * Indicates if the client preferences can be tunneled.
     * 
     * @return True if the client preferences can be tunneled.
     */
    public boolean isPreferencesTunnel() {
        return this.preferencesTunnel;
    }

    /**
     * Sets the character set parameter name.
     * 
     * @param parameterName
     *            The character set parameter name.
     */
    public void setCharacterSetAttribute(String parameterName) {
        this.characterSetParameter = parameterName;
    }

    /**
     * Indicates if the service should be enabled.
     * 
     * @param enabled
     *            True if the service should be enabled.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets the name of the parameter containing the accepted encoding.
     * 
     * @param parameterName
     *            The name of the parameter containing the accepted encoding.
     */
    public void setEncodingAttribute(String parameterName) {
        this.encodingParameter = parameterName;
    }

    /**
     * Sets the name of the parameter containing the accepted language.
     * 
     * @param parameterName
     *            The name of the parameter containing the accepted language.
     */
    public void setLanguageAttribute(String parameterName) {
        this.languageParameter = parameterName;
    }

    /**
     * Sets the name of the parameter containing the accepted media type.
     * 
     * @param parameterName
     *            The name of the parameter containing the accepted media type.
     */
    public void setMediaTypeAttribute(String parameterName) {
        this.mediaTypeParameter = parameterName;
    }

    /**
     * Sets the method parameter name.
     * 
     * @param parameterName
     *            The method parameter name.
     */
    public void setMethodParameter(String parameterName) {
        this.methodParameter = parameterName;
    }

    /**
     * Indicates if the method name can be tunneled.
     * 
     * @param methodTunnel
     *            True if the method name can be tunneled.
     */
    public void setMethodTunnel(boolean methodTunnel) {
        this.methodTunnel = methodTunnel;
    }

    /**
     * Indicates if the client preferences can be tunneled.
     * 
     * @param preferencesTunnel
     *            True if the client preferences can be tunneled.
     */
    public void setPreferencesTunnel(boolean preferencesTunnel) {
        this.preferencesTunnel = preferencesTunnel;
    }

}
