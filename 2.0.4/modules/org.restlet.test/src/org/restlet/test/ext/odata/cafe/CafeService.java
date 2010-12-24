/**
 * Copyright 2005-2010 Noelios Technologies.
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL 1.0 (the
 * "Licenses"). You can select the license that you prefer but you may not use
 * this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0.html
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1.php
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1.php
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.noelios.com/products/restlet-engine
 * 
 * Restlet is a registered trademark of Noelios Technologies.
 */

package org.restlet.test.ext.odata.cafe;

import org.restlet.ext.odata.Query;
import org.restlet.ext.odata.Service;

/**
* Generated by the generator tool for the WCF Data Services extension for the Restlet framework.<br>
*
* @see <a href="http://localhost:8111/Cafe.svc/$metadata">Metadata of the target WCF Data Services</a>
*
*/
public class CafeService extends Service {

    /**
     * Constructor.
     * 
     */
    public CafeService() {
        super("http://localhost:8111/Cafe.svc");
    }

    /**
     * Adds a new entity to the service.
     * 
     * @param entity
     *            The entity to add to the service.
     * @throws Exception 
     */
    public void addEntity(Cafe entity) throws Exception {
        addEntity("/Cafes", entity);
    }

    /**
     * Creates a query for cafe entities hosted by this service.
     * 
     * @param subpath
     *            The path to this entity relatively to the service URI.
     * @return A query object.
     */
    public Query<Cafe> createCafeQuery(String subpath) {
        return createQuery(subpath, Cafe.class);
    }


    /**
     * Adds a new entity to the service.
     * 
     * @param entity
     *            The entity to add to the service.
     * @throws Exception 
     */
    public void addEntity(Item entity) throws Exception {
        addEntity("/Items", entity);
    }

    /**
     * Creates a query for item entities hosted by this service.
     * 
     * @param subpath
     *            The path to this entity relatively to the service URI.
     * @return A query object.
     */
    public Query<Item> createItemQuery(String subpath) {
        return createQuery(subpath, Item.class);
    }


    /**
     * Adds a new entity to the service.
     * 
     * @param entity
     *            The entity to add to the service.
     * @throws Exception 
     */
    public void addEntity(Contact entity) throws Exception {
        addEntity("/Contacts", entity);
    }

    /**
     * Creates a query for contact entities hosted by this service.
     * 
     * @param subpath
     *            The path to this entity relatively to the service URI.
     * @return A query object.
     */
    public Query<Contact> createContactQuery(String subpath) {
        return createQuery(subpath, Contact.class);
    }


}