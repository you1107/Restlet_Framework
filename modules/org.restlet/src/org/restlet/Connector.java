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

import org.restlet.data.Protocol;

/**
 * Restlet enabling communication between Components. "A connector is an
 * abstract mechanism that mediates communication, coordination, or cooperation
 * among components. Connectors enable communication between components by
 * transferring data elements from one interface to another without changing the
 * data." Roy T. Fielding</br> <br/> "Encapsulate the activities of accessing
 * resources and transferring resource representations. The connectors present
 * an abstract interface for component communication, enhancing simplicity by
 * providing a clean separation of concerns and hiding the underlying
 * implementation of resources and communication mechanisms" Roy T. Fielding
 * 
 * @see <a
 *      href="http://roy.gbiv.com/pubs/dissertation/software_arch.htm#sec_1_2_2">Source
 *      dissertation</a>
 * @see <a
 *      href="http://roy.gbiv.com/pubs/dissertation/rest_arch_style.htm#sec_5_2_2">Source
 *      dissertation</a>
 * @author Jerome Louvel (contact@noelios.com)
 */
public abstract class Connector extends Restlet {
    /** The list of protocols simultaneously supported. */
    private List<Protocol> protocols;

    /**
     * Constructor.
     * 
     * @param context
     *            The context.
     */
    public Connector(Context context) {
        this(context, null);
    }

    /**
     * Constructor.
     * 
     * @param context
     *            The context.
     * @param protocols
     *            The supported protocols.
     */
    public Connector(Context context, List<Protocol> protocols) {
        super(context);
        this.protocols = protocols;
    }

    /**
     * Returns the protocols simultaneously supported.
     * 
     * @return The protocols simultaneously supported.
     */
    public List<Protocol> getProtocols() {
        if (this.protocols == null)
            this.protocols = new ArrayList<Protocol>();
        return this.protocols;
    }

}
