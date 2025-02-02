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

package org.restlet;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.restlet.data.Cookie;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.util.Series;
import org.restlet.util.Template;
import org.restlet.util.Variable;

/**
 * Filter scoring the affinity of calls with the attached Restlet. The score is
 * used by an associated Router in order to determine the most appropriate
 * Restlet for a given call. The routing is based on a reference template. It
 * also supports the extraction of some attributes from a call. Multiple
 * extractions can be defined, based on the query string of the resource
 * reference, on the request form (ex: posted from a browser) or on cookies.
 * 
 * @see org.restlet.util.Template
 * @author Jerome Louvel (contact@noelios.com)
 */
public class Route extends Filter {
    /** Internal class holding extraction information. */
    private static final class ExtractInfo {
        /** Target attribute name. */
        protected String attribute;

        /** Name of the parameter to look for. */
        protected String parameter;

        /** Indicates how to handle repeating values. */
        protected boolean first;

        /**
         * Constructor.
         * 
         * @param attribute
         *            Target attribute name.
         * @param parameter
         *            Name of the parameter to look for.
         * @param first
         *            Indicates how to handle repeating values.
         */
        public ExtractInfo(String attribute, String parameter, boolean first) {
            this.attribute = attribute;
            this.parameter = parameter;
            this.first = first;
        }
    }

    /** Internal class holding validation information. */
    private static final class ValidateInfo {
        /** Name of the attribute to look for. */
        protected String attribute;

        /** Indicates if the attribute presence is required. */
        protected boolean required;

        /** Format of the attribute value, using Regex pattern syntax. */
        protected String format;

        /**
         * Constructor.
         * 
         * @param attribute
         *            Name of the attribute to look for.
         * @param required
         *            Indicates if the attribute presence is required.
         * @param format
         *            Format of the attribute value, using Regex pattern syntax.
         */
        public ValidateInfo(String attribute, boolean required, String format) {
            this.attribute = attribute;
            this.required = required;
            this.format = format;
        }
    }

    /** The parent router. */
    private Router router;

    /** The list of attribute validations. */
    private List<ValidateInfo> validations;

    /** The list of cookies to extract. */
    private List<ExtractInfo> cookieExtracts;

    /** The list of query parameters to extract. */
    private List<ExtractInfo> queryExtracts;

    /** The list of request entity parameters to extract. */
    private List<ExtractInfo> entityExtracts;

    /** The reference template to match. */
    private Template template;

    /**
     * Constructor behaving as a simple extractor filter.
     * 
     * @param next
     *            The next Restlet.
     */
    public Route(Restlet next) {
        this(null, (Template) null, next);
    }

    /**
     * Constructor.
     * 
     * @param router
     *            The parent router.
     * @param uriTemplate
     *            The URI template.
     * @param next
     *            The next Restlet.
     */
    public Route(Router router, String uriTemplate, Restlet next) {
        this(router, new Template(router.getLogger(), uriTemplate,
                Template.MODE_STARTS_WITH, Variable.TYPE_URI_SEGMENT, "", true,
                false), next);
    }

    /**
     * Constructor.
     * 
     * @param router
     *            The parent router.
     * @param template
     *            The URI template.
     * @param next
     *            The next Restlet.
     */
    public Route(Router router, Template template, Restlet next) {
        super(router == null ? null : router.getContext(), next);
        this.router = router;
        this.cookieExtracts = null;
        this.queryExtracts = null;
        this.entityExtracts = null;
        this.template = template;
        this.validations = null;
    }

    /**
     * Allows filtering before its handling by the target Restlet. Does nothing
     * by default.
     * 
     * @param request
     *            The request to filter.
     * @param response
     *            The response to filter.
     */
	@Override
    protected void beforeHandle(Request request, Response response) {
        // 1 - Parse the template variables and adjust the base reference
        if (getTemplate() != null) {
            String remainingPart = request.getResourceRef().getRemainingPart();
            int matchedLength = getTemplate().parse(remainingPart, request);

            if (getLogger().isLoggable(Level.FINER)) {
                getLogger().finer(
                        "Attempting to match this pattern: "
                                + getTemplate().getPattern() + " >> "
                                + matchedLength);
            }

            if (matchedLength != -1) {
                // Updates the context
                String matchedPart = remainingPart.substring(0, matchedLength);
                Reference baseRef = request.getResourceRef().getBaseRef();

                if (baseRef == null) {
                    baseRef = new Reference(matchedPart);
                } else {
                    baseRef = new Reference(baseRef.toString(false, false)
                            + matchedPart);
                }

                request.getResourceRef().setBaseRef(baseRef);

                if (getLogger().isLoggable(Level.FINE)) {
                    getLogger().fine(
                            "New base URI: "
                                    + request.getResourceRef().getBaseRef());
                    getLogger().fine(
                            "New remaining part: "
                                    + request.getResourceRef()
                                            .getRemainingPart());
                }

                if (getLogger().isLoggable(Level.FINE)) {
                    getLogger().fine(
                            "Delegating the call to the target Restlet");
                }
            } else {
                response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            }
        }

        // 2 - Extract the attributes from form parameters (query, cookies,
        // entity).
        extractAttributes(request, response);

        // 3 - Validate the attributes extracted (or others)
        validateAttributes(request, response);
    }

    /**
     * Extracts the attributes value from the request.
     * 
     * @param request
     *            The request to process.
     * @param response
     *            The response to process.
     */
    private void extractAttributes(Request request, Response response) {
        // Extract the query parameters
        if (this.queryExtracts != null) {
            Form form = request.getResourceRef().getQueryAsForm();

            if (form != null) {
                for (ExtractInfo ei : getQueryExtracts()) {
                    if (ei.first) {
                        request.getAttributes().put(ei.attribute,
                                form.getFirstValue(ei.parameter));
                    } else {
                        request.getAttributes().put(ei.attribute,
                                form.subList(ei.parameter));
                    }
                }
            }
        }

        // Extract the request entity parameters
        if (this.entityExtracts != null) {
            Form form = request.getEntityAsForm();

            if (form != null) {
                for (ExtractInfo ei : getEntityExtracts()) {
                    if (ei.first) {
                        request.getAttributes().put(ei.attribute,
                                form.getFirstValue(ei.parameter));
                    } else {
                        request.getAttributes().put(ei.attribute,
                                form.subList(ei.parameter));
                    }
                }
            }
        }

        // Extract the cookie parameters
        if (this.cookieExtracts != null) {
            Series<Cookie> cookies = request.getCookies();

            if (cookies != null) {
                for (ExtractInfo ei : getCookieExtracts()) {
                    if (ei.first) {
                        request.getAttributes().put(ei.attribute,
                                cookies.getFirstValue(ei.parameter));
                    } else {
                        request.getAttributes().put(ei.attribute,
                                cookies.subList(ei.parameter));
                    }
                }
            }
        }
    }

    /**
     * Extracts an attribute from the request cookies.
     * 
     * @param attribute
     *            The name of the request attribute to set.
     * @param cookieName
     *            The name of the cookies to extract.
     * @param first
     *            Indicates if only the first cookie should be set. Otherwise as
     *            a List instance might be set in the attribute value.
     * @return The current Filter.
     */
    public Route extractCookie(String attribute, String cookieName,
            boolean first) {
        getCookieExtracts().add(new ExtractInfo(attribute, cookieName, first));
        return this;
    }

    /**
     * Extracts an attribute from the request entity form.
     * 
     * @param attribute
     *            The name of the request attribute to set.
     * @param parameter
     *            The name of the entity form parameter to extract.
     * @param first
     *            Indicates if only the first cookie should be set. Otherwise as
     *            a List instance might be set in the attribute value.
     * @return The current Filter.
     */
    public Route extractEntity(String attribute, String parameter, boolean first) {
        getEntityExtracts().add(new ExtractInfo(attribute, parameter, first));
        return this;
    }

    /**
     * Extracts an attribute from the query string of the resource reference.
     * 
     * @param attribute
     *            The name of the request attribute to set.
     * @param parameter
     *            The name of the query string parameter to extract.
     * @param first
     *            Indicates if only the first cookie should be set. Otherwise as
     *            a List instance might be set in the attribute value.
     * @return The current Filter.
     */
    public Route extractQuery(String attribute, String parameter, boolean first) {
        getQueryExtracts().add(new ExtractInfo(attribute, parameter, first));
        return this;
    }

    /**
     * Returns the list of query extracts.
     * 
     * @return The list of query extracts.
     */
    private List<ExtractInfo> getCookieExtracts() {
        if (this.cookieExtracts == null)
            this.cookieExtracts = new ArrayList<ExtractInfo>();
        return this.cookieExtracts;
    }

    /**
     * Returns the list of query extracts.
     * 
     * @return The list of query extracts.
     */
    private List<ExtractInfo> getEntityExtracts() {
        if (this.entityExtracts == null)
            this.entityExtracts = new ArrayList<ExtractInfo>();
        return this.entityExtracts;
    }

    /**
     * Returns the list of query extracts.
     * 
     * @return The list of query extracts.
     */
    private List<ExtractInfo> getQueryExtracts() {
        if (this.queryExtracts == null)
            this.queryExtracts = new ArrayList<ExtractInfo>();
        return this.queryExtracts;
    }

    /**
     * Returns the parent router.
     * 
     * @return The parent router.
     */
    public Router getRouter() {
        return this.router;
    }

    /**
     * Returns the reference template to match.
     * 
     * @return The reference template to match.
     */
    public Template getTemplate() {
        return this.template;
    }

    /**
     * Returns the list of attribute validations.
     * 
     * @return The list of attribute validations.
     */
    private List<ValidateInfo> getValidations() {
        if (this.validations == null)
            this.validations = new ArrayList<ValidateInfo>();
        return this.validations;
    }

    /**
     * Returns the score for a given call (between 0 and 1.0).
     * 
     * @param request
     *            The request to score.
     * @param response
     *            The response to score.
     * @return The score for a given call (between 0 and 1.0).
     */
    public float score(Request request, Response response) {
        float result = 0F;

        if ((getRouter() != null) && (request.getResourceRef() != null)
                && (getTemplate() != null)) {
            String remainingPart = request.getResourceRef().getRemainingPart();
            if (remainingPart != null) {
                int matchedLength = getTemplate().match(remainingPart);

                if (matchedLength != -1) {
                    float totalLength = remainingPart.length();

                    if (totalLength > 0.0F) {
                        result = getRouter().getRequiredScore()
                                + (1.0F - getRouter().getRequiredScore())
                                * (((float) matchedLength) / totalLength);
                    } else {
                        result = 1.0F;
                    }
                }
            }
            
            if (getLogger().isLoggable(Level.FINER)) {
                getLogger().finer(
                        "Call score for the \"" + getTemplate().getPattern()
                                + "\" URI pattern: " + result);
            }
        }

        return result;
    }

    /**
     * Sets the reference template to match.
     * 
     * @param template
     *            The reference template to match.
     */
    public void setTemplate(Template template) {
        this.template = template;
    }

    /**
     * Checks the request attributes for presence, format, etc. If the check
     * fails, then a response status CLIENT_ERROR_BAD_REQUEST is returned with
     * the proper status description.
     * 
     * @param attribute
     *            Name of the attribute to look for.
     * @param required
     *            Indicates if the attribute presence is required.
     * @param format
     *            Format of the attribute value, using Regex pattern syntax.
     */
    public void validate(String attribute, boolean required, String format) {
        getValidations().add(new ValidateInfo(attribute, required, format));
    }

    /**
     * Validates the attributes from the request.
     * 
     * @param request
     *            The request to process.
     * @param response
     *            The response to process.
     */
    private void validateAttributes(Request request, Response response) {
        if (this.validations != null) {
            for (ValidateInfo validate : getValidations()) {
                if (validate.required
                        && !request.getAttributes().containsKey(
                                validate.attribute)) {
                    response
                            .setStatus(
                                    Status.CLIENT_ERROR_BAD_REQUEST,
                                    "Unable to find the \""
                                            + validate.attribute
                                            + "\" attribute in the request. Please check your request.");
                } else if (validate.format != null) {
                    Object value = request.getAttributes().get(
                            validate.attribute);
                    if (value == null) {
                        response
                                .setStatus(
                                        Status.CLIENT_ERROR_BAD_REQUEST,
                                        "Unable to validate the \""
                                                + validate.attribute
                                                + "\" attribute with a null value. Please check your request.");
                    } else {
                        if (!Pattern.matches(validate.format, value.toString())) {
                            response
                                    .setStatus(
                                            Status.CLIENT_ERROR_BAD_REQUEST,
                                            "Unable to validate the value of the \""
                                                    + validate.attribute
                                                    + "\" attribute. The expected format is: "
                                                    + validate.format
                                                    + " (Java Regex). Please check your request.");
                        }
                    }
                }
            }
        }
    }
}
