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

package org.restlet.util;

/**
 * Variable descriptor for reference templates.
 * 
 * @see Template
 * @author Jerome Louvel (contact@noelios.com)
 */
public final class Variable {
    /** Matches all characters. */
    public static final int TYPE_ALL = 1;

    /** Matches all alphabetical characters. */
    public static final int TYPE_ALPHA = 2;

    /** Matches all alphabetical and digital characters. */
    public static final int TYPE_ALPHA_DIGIT = 3;

    /** Matches all digital characters. */
    public static final int TYPE_DIGIT = 4;

    /** Matches all URI characters. */
    public static final int TYPE_URI_ALL = 5;

    /** Matches URI fragment characters. */
    public static final int TYPE_URI_FRAGMENT = 6;

    /** Matches URI query characters. */
    public static final int TYPE_URI_QUERY = 7;

    /** Matches URI scheme characters. */
    public static final int TYPE_URI_SCHEME = 8;

    /** Matches URI segment characters. */
    public static final int TYPE_URI_SEGMENT = 9;

    /** Matches unreserved URI characters. */
    public static final int TYPE_URI_UNRESERVED = 10;

    /** Matches all alphabetical and digital characters plus the underscore. */
    public static final int TYPE_WORD = 11;

    /** The type of variable. See TYPE_* constants. */
    private int type;

    /** The default value to use if the key couldn't be found in the model. */
    private String defaultValue;

    /** Indicates if the variable is required or optional. */
    private boolean required;

    /**
     * Indicates if the value is fixed, in which case the "defaultValue"
     * property is always used.
     */
    private boolean fixed;

    /**
     * Default constructor. Type is TYPE_ALL, default value is "", required is
     * true and fixed is false.
     */
    public Variable() {
        this(Variable.TYPE_ALL, "", true, false);
    }

    /**
     * Constructor. Default value is "", required is true and fixed is false.
     * 
     * @param type
     *            The type of variable. See TYPE_* constants.
     */
    public Variable(int type) {
        this(type, "", true, false);
    }

    /**
     * Constructor.
     * 
     * @param type
     *            The type of variable. See TYPE_* constants.
     * @param defaultValue
     *            The default value to use if the key couldn't be found in the
     *            model.
     * @param required
     *            Indicates if the variable is required or optional.
     * @param fixed
     *            Indicates if the value is fixed, in which case the
     *            "defaultValue" property is always used.
     */
    public Variable(int type, String defaultValue, boolean required,
            boolean fixed) {
        this.type = type;
        this.defaultValue = defaultValue;
        this.required = required;
        this.fixed = fixed;
    }

    /**
     * Returns the type of variable. See TYPE_* constants.
     * 
     * @return The type of variable. See TYPE_* constants.
     */
    public int getType() {
        return this.type;
    }

    /**
     * Returns the default value to use if the key couldn't be found in the
     * model.
     * 
     * @return The default value to use if the key couldn't be found in the
     *         model.
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Returns true if the variable is required or optional.
     * 
     * @return True if the variable is required or optional.
     */
    public boolean isRequired() {
        return this.required;
    }

    /**
     * Returns true if the value is fixed, in which case the "defaultValue"
     * property is always used.
     * 
     * @return True if the value is fixed, in which case the "defaultValue"
     *         property is always used.
     */
    public boolean isFixed() {
        return this.fixed;
    }

}
