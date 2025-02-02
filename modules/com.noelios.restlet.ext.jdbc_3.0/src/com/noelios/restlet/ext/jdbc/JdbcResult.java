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

package com.noelios.restlet.ext.jdbc;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC result wrapper. Used by the JDBC client connector as a response entity
 * of JDBC calls.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class JdbcResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The JDBC statement. */
    private transient Statement statement;

    /**
     * Constructor.
     * 
     * @param statement
     *            The JDBC statement.
     */
    public JdbcResult(Statement statement) {
        this.statement = statement;
    }

    /**
     * Release the statement connection. To call when result navigation is done.
     * 
     * @throws SQLException
     */
    public void release() throws SQLException {
        // One connection per jdbcResult
        // releasing the instance means releasing the connection too
        // and not only the statement.
        statement.getConnection().close();
    }

    /**
     * Returns the result set.
     * 
     * @return The result set.
     * @throws SQLException
     */
    public ResultSet getResultSet() throws SQLException {
        return statement.getResultSet();
    }

    /**
     * Returns the generated keys.
     * 
     * @return The generated keys.
     * @throws SQLException
     */
    public ResultSet getGeneratedKeys() throws SQLException {
        return statement.getGeneratedKeys();
    }

    /**
     * Returns the update count.
     * 
     * @return The update count.
     * @throws SQLException
     */
    public int getUpdateCount() throws SQLException {
        return statement.getUpdateCount();
    }

}
