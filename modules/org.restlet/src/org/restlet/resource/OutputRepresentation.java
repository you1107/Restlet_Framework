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

package org.restlet.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.restlet.data.MediaType;
import org.restlet.util.ByteUtils;

/**
 * Representation based on a BIO output stream. This class is a good basis to write 
 * your own representations, especially for the dynamic and large ones. For this you 
 * just need to create a subclass and override the abstract 
 * Representation.write(OutputStream) method. This method will later be called back
 * by the connectors when the actual representation's content is needed.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public abstract class OutputRepresentation extends StreamRepresentation {
    /**
     * Constructor.
     * 
     * @param mediaType
     *            The representation's mediaType.
     */
    public OutputRepresentation(MediaType mediaType) {
        super(mediaType);
    }

    /**
     * Constructor.
     * 
     * @param mediaType
     *            The representation's mediaType.
     * @param expectedSize
     *            The expected input stream size.
     */
    public OutputRepresentation(MediaType mediaType, long expectedSize) {
        super(mediaType);
        setSize(expectedSize);
    }

    /**
     * Returns a stream with the representation's content. Internally, it uses a
     * writer thread and a pipe stream.
     * 
     * @return A stream with the representation's content.
     */
	@Override
    public InputStream getStream() throws IOException {
        return ByteUtils.getStream(this);
    }

    /**
     * Writes the representation to a byte stream.
     * 
     * @param outputStream
     *            The output stream.
     */
	@Override
    public abstract void write(OutputStream outputStream) throws IOException;

}
