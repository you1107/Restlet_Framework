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

package org.restlet.data;

import org.restlet.util.Engine;
import org.restlet.util.Series;

/**
 * Authentication challenge sent by an origin server to a client.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public final class ChallengeRequest {
    /** The challenge scheme. */
    private ChallengeScheme scheme;

    /** The authentication realm. */
    private String realm;

    /** The scheme parameters. */
    private Series<Parameter> parameters;

    /**
     * Constructor.
     * 
     * @param scheme
     *            The challenge scheme.
     * @param realm
     *            The authentication realm.
     */
    public ChallengeRequest(ChallengeScheme scheme, String realm) {
        this.scheme = scheme;
        this.realm = realm;
        this.parameters = null;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean equals(final Object obj) {
        boolean result = (obj == this);

        // if obj == this no need to go further
        if (!result) {
            // if obj isn't a challenge request or is null don't evaluate
            // further
            if ((obj instanceof ChallengeRequest) && obj != null) {
                ChallengeRequest that = (ChallengeRequest) obj;
                result = (this.getParameters().equals(that.getParameters()));

                if (result) {
                    if (getRealm() != null) {
                        result = getRealm().equals(that.getRealm());
                    } else {
                        result = (that.getRealm() == null);
                    }

                    if (result) {
                        if (getScheme() != null) {
                            result = getScheme().equals(that.getScheme());
                        } else {
                            result = (that.getScheme() == null);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Returns the scheme parameters.
     * 
     * @return The scheme parameters.
     */
    public Series<Parameter> getParameters() {
        if (this.parameters == null)
            this.parameters = new Form();
        return this.parameters;
    }

    /**
     * Returns the realm name.
     * 
     * @return The realm name.
     */
    public String getRealm() {
        return this.realm;
    }

    /**
     * Returns the scheme used.
     * 
     * @return The scheme used.
     */
    public ChallengeScheme getScheme() {
        return this.scheme;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Engine.hashCode(getScheme(), getRealm(), getParameters());
    }

    /**
     * Sets the realm name.
     * 
     * @param realm
     *            The realm name.
     */
    public void setRealm(String realm) {
        this.realm = realm;
    }

    /**
     * Sets the scheme used.
     * 
     * @param scheme
     *            The scheme used.
     */
    public void setScheme(ChallengeScheme scheme) {
        this.scheme = scheme;
    }

}
