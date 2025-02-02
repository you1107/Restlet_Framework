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

package org.restlet.example.book.rest.ch3;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.DomRepresentation;
import org.w3c.dom.Node;

/**
 * Amazon S3 bucket.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class S3Bucket extends S3Authorized {

    private String name;

    private boolean truncated;

    public S3Bucket(String name) {
        this.name = name;
    }

    /**
     * Stores this bucket on S3. Analagous to ActiveRecord::Base#save, which
     * stores an object in the database.
     */
    public Status save() {
        return authorizedPut(getUri(), null).getStatus();
    }

    /**
     * Deletes this bucket.
     */
    public Status delete() {
        return authorizedDelete(getUri()).getStatus();
    }

    /**
     * Get the objects in this bucket: all of them, or some subset.
     * 
     * If S3 decides not to return the whole bucket/subset, the second return
     * value will be set to true. To get the rest of the objects, you'll need to
     * manipulate the subset options.
     * 
     * Subset options are :Prefix, :Marker, :Delimiter, :MaxKeys. For details,
     * see the S3 docs on "Listing Keys".
     * 
     * @return The objects in this nucket.
     */
    public List<S3Object> getObjects(String prefix, String marker,
            String delimiter, Integer maxKeys) {
        List<S3Object> result = new ArrayList<S3Object>();

        // Construct the request URI by appending optional listing keys
        StringBuilder uri = new StringBuilder().append(getUri());
        String suffix = "?";
        if (prefix != null) {
            uri.append(suffix).append("prefix=").append(prefix);
            suffix = "&";
        }
        if (marker != null) {
            uri.append(suffix).append("marker=").append(marker);
            suffix = "&";
        }
        if (delimiter != null) {
            uri.append(suffix).append("delimiter=").append(delimiter);
            suffix = "&";
        }
        if (maxKeys != null) {
            uri.append(suffix).append("maxKeys=").append(maxKeys);
            suffix = "&";
        }

        // Make the request and parse the document.
        Response response = authorizedGet(uri.toString());
        DomRepresentation document = response.getEntityAsDom();

        // Update the truncated flag
        this.truncated = document.getNodes("//IsTruncated").get(0)
                .getTextContent().equals("true");

        // Browse the list of object keys
        for (Node node : document.getNodes("//Contents/Key")) {
            result.add(new S3Object(this, node.getTextContent()));
        }

        return result;
    }

    public boolean isTruncated() {
        return this.truncated;
    }

    public String getUri() {
        return S3App.HOST + getName();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
