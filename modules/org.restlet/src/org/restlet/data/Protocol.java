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

/**
 * Protocol used by client and server connectors. Connectors enable the
 * communication between components by implementing standard protocols.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public final class Protocol extends Metadata {

    /** Indicates that the port number is undefined. */
    public static final int UNKNOWN_PORT = -1;

    /** All protocols wildcard. */
    public static final Protocol ALL = new Protocol("all", "ALL",
            "Wildcard for all protocols", UNKNOWN_PORT);

    /**
     * AJP 1.3 protocol to communicate with Apache HTTP server or Microsoft IIS.
     */
    public static final Protocol AJP = new Protocol("ajp", "AJP",
            "Apache Jakarta Protocol", 8009);

    /**
     * CLAP (ClassLoader Access Protocol) is a custom scheme to access to
     * representations via classloaders. Example URI:
     * "clap://thread/org/restlet/Restlet.class".<br>
     * <br>
     * In order to work, CLAP requires a client connector provided by the core
     * Restlet engine.
     * 
     * @see org.restlet.data.LocalReference
     */
    public static final Protocol CLAP = new Protocol("clap", "CLAP",
            "Class Loader Access Protocol", UNKNOWN_PORT);

    /** Local Web Archive access protocol. */
    public static final Protocol WAR = new Protocol("war", "WAR",
            "Web Archive Access Protocol", UNKNOWN_PORT);

    /** Local file system access protocol. */
    public static final Protocol FILE = new Protocol("file", "FILE",
            "Local File System Protocol", UNKNOWN_PORT);

    /**
     * FILE is a standard scheme to access to representations stored in the file
     * system (locally most of the time). Example URI:
     * "file:///D/root/index.html".<br>
     * <br>
     * In order to work, FILE requires a client connector provided by the core
     * Restlet engine.
     * 
     * @see org.restlet.data.LocalReference
     */
    public static final Protocol FTP = new Protocol("ftp", "FTP",
            "File Transfer Protocol", 21);

    /** HTTP protocol. */
    public static final Protocol HTTP = new Protocol("http", "HTTP",
            "HyperText Transport Protocol", 80);

    /** HTTPS protocol (via SSL socket). */
    public static final Protocol HTTPS = new Protocol("https", "HTTPS",
            "HyperText Transport Protocol (Secure)", 443);

    /**
     * JAR (Java ARchive) is a common scheme to access to representations inside
     * archive files. Example URI:
     * "jar:http://www.foo.com/bar/baz.jar!/COM/foo/Quux.class".
     * 
     * @see org.restlet.data.LocalReference
     */
    public static final Protocol JAR = new Protocol("jar", "JAR",
            "Java ARchive", UNKNOWN_PORT);

    /** JDBC protocol. */
    public static final Protocol JDBC = new Protocol("jdbc", "JDBC",
            "Java DataBase Connectivity", UNKNOWN_PORT);

    /** SMTP protocol. */
    public static final Protocol SMTP = new Protocol("smtp", "SMTP",
            "Simple Mail Transfer Protocol", 25);

    /** SMTP with STARTTLS protocol (started with a plain socket). */
    public static final Protocol SMTP_STARTTLS = new Protocol("smtp",
            "SMTP_STARTTLS",
            "Simple Mail Transfer Protocol (starting a TLS encryption)", 25);

    /** SMTPS protocol (via SSL/TLS socket). */
    public static final Protocol SMTPS = new Protocol("smtps", "SMTPS",
            "Simple Mail Transfer Protocol (Secure)", 465);

    /**
     * Creates the protocol associated to a URI scheme name. If an existing
     * constant exists then it is returned, otherwise a new instance is created.
     * 
     * @param schemeName
     *                The scheme name.
     * @return The associated protocol.
     */
    public static Protocol valueOf(final String schemeName) {
        Protocol result = null;

        if (schemeName != null) {
            if (schemeName.equalsIgnoreCase(AJP.getSchemeName()))
                result = AJP;
            else if (schemeName.equalsIgnoreCase(WAR.getSchemeName()))
                result = WAR;
            else if (schemeName.equalsIgnoreCase(FILE.getSchemeName()))
                result = FILE;
            else if (schemeName.equalsIgnoreCase(HTTP.getSchemeName()))
                result = HTTP;
            else if (schemeName.equalsIgnoreCase(HTTPS.getSchemeName()))
                result = HTTPS;
            else if (schemeName.equalsIgnoreCase(JDBC.getSchemeName()))
                result = JDBC;
            else if (schemeName.equalsIgnoreCase(SMTP.getSchemeName()))
                result = SMTP;
            else if (schemeName.equalsIgnoreCase(SMTPS.getSchemeName()))
                result = SMTPS;
            else
                result = new Protocol(schemeName);
        }

        return result;
    }

    /** The scheme name. */
    private String schemeName;

    /** The default port if known or -1. */
    private int defaultPort;

    /**
     * Constructor.
     * 
     * @param schemeName
     *                The scheme name.
     */
    public Protocol(final String schemeName) {
        this(schemeName, schemeName.toUpperCase(), schemeName.toUpperCase()
                + " Protocol", UNKNOWN_PORT);
    }

    /**
     * Constructor.
     * 
     * @param schemeName
     *                The scheme name.
     * @param name
     *                The unique name.
     * @param description
     *                The description.
     * @param defaultPort
     *                The default port.
     */
    public Protocol(final String schemeName, final String name,
            final String description, int defaultPort) {
        super(name, description);
        this.schemeName = schemeName;
        this.defaultPort = defaultPort;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object object) {
        return (object instanceof Protocol)
                && getName().equalsIgnoreCase(((Protocol) object).getName());
    }

    /**
     * Returns the default port number.
     * 
     * @return The default port number.
     */
    public int getDefaultPort() {
        return this.defaultPort;
    }

    /**
     * Returns the URI scheme name.
     * 
     * @return The URI scheme name.
     */
    public String getSchemeName() {
        return this.schemeName;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return (getName() == null) ? 0 : getName().toLowerCase().hashCode();
    }
}
