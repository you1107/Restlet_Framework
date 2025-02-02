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

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.restlet.resource.Variant;
import org.restlet.util.DateUtils;

/**
 * Set of conditions applying to a request. This is equivalent to the HTTP
 * conditional headers.
 * 
 * @see <a
 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.24">If-Match</a>
 * @see <a
 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.25">If-Modified-Since</a>
 * @see <a
 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.26">If-None-Match</a>
 * @see <a
 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.28">If-Unmodified-Since</a>
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public final class Conditions {
    /** The "if-modified-since" condition */
    private Date modifiedSince;

    /** The "if-unmodified-since" condition */
    private Date unmodifiedSince;

    /** The "if-match" condition */
    private List<Tag> match;

    /** The "if-none-match" condition */
    private List<Tag> noneMatch;

    /**
     * Constructor.
     */
    public Conditions() {
    }

    /**
     * Returns the "if-match" condition.
     * 
     * @return The "if-match" condition.
     */
    public List<Tag> getMatch() {
        return this.match;
    }

    /**
     * Returns the "if-modified-since" condition.
     * 
     * @return The "if-modified-since" condition.
     */
    public Date getModifiedSince() {
        return this.modifiedSince;
    }

    /**
     * Returns the "if-none-match" condition.
     * 
     * @return The "if-none-match" condition.
     */
    public List<Tag> getNoneMatch() {
        return this.noneMatch;
    }

    /**
     * Returns the conditional status of a variant using a given method.
     * 
     * @param method
     *                The request method.
     * @param variant
     *                The representation whose entity tag or date of
     *                modification will be tested
     * @return Null if the requested method can be performed, the status of the
     *         response otherwise.
     */
    public Status getStatus(Method method, Variant variant) {
        Status result = null;

        // Is the "if-Match" rule followed or not?
        if (getMatch() != null && getMatch().size() != 0) {
            boolean matched = false;
            boolean failed = false;
            boolean all = getMatch().get(0).equals(Tag.ALL);

            if (variant != null) {
                // If a tag exists
                if (!all && variant.getTag() != null) {
                    // Check if it matches one of the representations already
                    // cached by the client
                    Tag tag;
                    
                    for (Iterator<Tag> iter = getMatch().iterator(); !matched
                            && iter.hasNext();) {
                        tag = iter.next();
                        matched = tag.equals(variant.getTag(), false);
                    }
                } else {
                    matched = all;
                }
            } else {
                // see
                // http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.24
                // If none of the entity tags match, or if "*" is given and no
                // current entity exists, the server MUST NOT perform the
                // requested method
                failed = all;
            }
            
            failed = failed || !matched;
            
            if (failed) {
                result = Status.CLIENT_ERROR_PRECONDITION_FAILED;
            }
        }

        // Is the "if-None-Match" rule followed or not?
        if (result == null && getNoneMatch() != null
                && getNoneMatch().size() != 0) {
            boolean matched = false;
            
            if (variant != null) {
                // If a tag exists
                if (variant.getTag() != null) {
                    // Check if it matches one of the representations
                    // already cached by the client
                    Tag tag;
                    
                    for (Iterator<Tag> iter = getNoneMatch().iterator(); !matched
                            && iter.hasNext();) {
                        tag = iter.next();
                        matched = tag.equals(variant.getTag(), (Method.GET
                                .equals(method) || Method.HEAD.equals(method)));
                    }
                    
                    // The current representation matches one of those already
                    // cached by the client
                    if (matched) {
                        // Check if the current representation has been updated
                        // since the "if-modified-since" date. In this case, the
                        // rule is followed.
                        Date modifiedSince = getModifiedSince();
                        boolean isModifiedSince = (modifiedSince != null)
                                && (DateUtils.after(new Date(), modifiedSince)
                                        || (variant.getModificationDate() == null) || DateUtils
                                        .after(modifiedSince, variant
                                                .getModificationDate()));
                        matched = !isModifiedSince;
                    }
                }
            } else {
                matched = getNoneMatch().get(0).equals(Tag.ALL);
            }
            
            if (matched) {
                if (Method.GET.equals(method) || Method.HEAD.equals(method)) {
                    result = Status.REDIRECTION_NOT_MODIFIED;
                } else {
                    result = Status.CLIENT_ERROR_PRECONDITION_FAILED;
                }
            }
        }

        // Is the "if-Modified-Since" rule followed or not?
        if (result == null && getModifiedSince() != null) {
            if (variant != null) {
                Date modifiedSince = getModifiedSince();
                boolean isModifiedSince = (DateUtils.after(new Date(),
                        modifiedSince)
                        || (variant.getModificationDate() == null) || DateUtils
                        .after(modifiedSince, variant.getModificationDate()));
                
                if (!isModifiedSince) {
                    result = Status.REDIRECTION_NOT_MODIFIED;
                }
            }
        }

        // Is the "if-Unmodified-Since" rule followed or not?
        if (result == null && getUnmodifiedSince() != null) {
            if (variant != null) {
                Date unModifiedSince = getUnmodifiedSince();
                
                boolean isUnModifiedSince = ((unModifiedSince == null)
                        || (variant.getModificationDate() == null) || DateUtils
                        .after(variant.getModificationDate(), unModifiedSince));
                
                if (!isUnModifiedSince) {
                    result = Status.CLIENT_ERROR_PRECONDITION_FAILED;
                }
            }
        }

        return result;
    }

    /**
     * Returns the "if-unmodified-since" condition.
     * 
     * @return The "if-unmodified-since" condition.
     */
    public Date getUnmodifiedSince() {
        return this.unmodifiedSince;
    }

    /**
     * Indicates if there are some conditions set.
     * 
     * @return True if there are some conditions set.
     */
    public boolean hasSome() {
        return ((getMatch() != null && !getMatch().isEmpty())
                || (getNoneMatch() != null && !getNoneMatch().isEmpty())
                || (getModifiedSince() != null) || (getUnmodifiedSince() != null));
    }

    /**
     * Sets the "if-match" condition.
     * 
     * @param tags
     *                The "if-match" condition.
     */
    public void setMatch(List<Tag> tags) {
        this.match = tags;
    }

    /**
     * Sets the "if-modified-since" condition.
     * 
     * @param date
     *                The "if-modified-since" condition.
     */
    public void setModifiedSince(Date date) {
        this.modifiedSince = DateUtils.unmodifiable(date);
    }

    /**
     * Sets the "if-none-match" condition.
     * 
     * @param tags
     *                The "if-none-match" condition.
     */
    public void setNoneMatch(List<Tag> tags) {
        this.noneMatch = tags;
    }

    /**
     * Sets the "if-unmodified-since" condition.
     * 
     * @param date
     *                The "if-unmodified-since" condition.
     */
    public void setUnmodifiedSince(Date date) {
        this.unmodifiedSince = DateUtils.unmodifiable(date);
    }

}
