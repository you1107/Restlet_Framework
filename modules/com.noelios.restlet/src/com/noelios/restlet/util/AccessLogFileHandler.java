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

package com.noelios.restlet.util;

import java.io.IOException;

/**
 * Log file handler that uses the {@link AccessLogFormatter} by default. Also
 * useful in configuration files to differentiate from the
 * java.util.logging.FileHandler.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class AccessLogFileHandler extends java.util.logging.FileHandler {
    /**
     * Constructor.
     * 
     * @throws IOException
     * @throws SecurityException
     */
    public AccessLogFileHandler() throws IOException, SecurityException {
        super();
        init();
    }

    /**
     * Constructor.
     * 
     * @param pattern
     *            The name of the output file.
     * @throws IOException
     * @throws SecurityException
     */
    public AccessLogFileHandler(String pattern) throws IOException,
            SecurityException {
        super(pattern);
        init();
    }

    /**
     * Constructor.
     * 
     * @param pattern
     *            The name of the output file.
     * @param append
     *            Specifies append mode.
     * @throws IOException
     * @throws SecurityException
     */
    public AccessLogFileHandler(String pattern, boolean append)
            throws IOException, SecurityException {
        super(pattern, append);
        init();
    }

    /**
     * Constructor.
     * 
     * @param pattern
     *            The name of the output file.
     * @param limit
     *            The maximum number of bytes to write to any one file.
     * @param count
     *            The number of files to use.
     * @throws IOException
     * @throws SecurityException
     */
    public AccessLogFileHandler(String pattern, int limit, int count)
            throws IOException, SecurityException {
        super(pattern, limit, count);
        init();
    }

    /**
     * Constructor.
     * 
     * @param pattern
     *            The name of the output file.
     * @param limit
     *            The maximum number of bytes to write to any one file.
     * @param count
     *            The number of files to use.
     * @param append
     *            Specifies append mode.
     * @throws IOException
     * @throws SecurityException
     */
    public AccessLogFileHandler(String pattern, int limit, int count,
            boolean append) throws IOException, SecurityException {
        super(pattern, limit, count, append);
        init();
    }

    /**
     * Initialization code common to all constructors.
     */
    protected void init() {
        setFormatter(new AccessLogFormatter());
    }

}
