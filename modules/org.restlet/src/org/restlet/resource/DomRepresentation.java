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
import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.restlet.data.MediaType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * XML representation based on a DOM document. DOM is a standard XML object
 * model defined by the W3C.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class DomRepresentation extends XmlRepresentation {
    /** The wrapped DOM document. */
    private Document dom;

    /** The source XML representation. */
    private Representation xmlRepresentation;

    /**
     * Constructor for an empty document.
     * 
     * @param mediaType
     *                The representation's media type.
     */
    public DomRepresentation(MediaType mediaType) throws IOException {
        super(mediaType);
        this.dom = getDocumentBuilder().newDocument();
    }

    /**
     * Constructor from an existing DOM document.
     * 
     * @param mediaType
     *                The representation's media type.
     * @param xmlDocument
     *                The source DOM document.
     */
    public DomRepresentation(MediaType mediaType, Document xmlDocument) {
        super(mediaType);
        this.dom = xmlDocument;
    }

    /**
     * Constructor.
     * 
     * @param xmlRepresentation
     *                A source XML representation to parse.
     */
    public DomRepresentation(Representation xmlRepresentation) {
        super((xmlRepresentation == null) ? null : xmlRepresentation
                .getMediaType());
        this.xmlRepresentation = xmlRepresentation;
    }

    @Override
    public Object evaluate(String expression, QName returnType)
            throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(this);
        return xpath.evaluate(expression, getDocument(), returnType);
    }

    /**
     * Returns the wrapped DOM document.
     * 
     * @return The wrapped DOM document.
     */
    public Document getDocument() throws IOException {
        if ((this.dom == null) && (this.xmlRepresentation != null)) {
            try {
                this.dom = getDocumentBuilder().parse(
                        xmlRepresentation.getStream());
            } catch (SAXException se) {
                throw new IOException("Couldn't read the XML representation. "
                        + se.getMessage());
            }
        }

        return this.dom;
    }

    /**
     * Returns a document builder properly configured.
     * 
     * @return A document builder properly configured.
     */
    private DocumentBuilder getDocumentBuilder() throws IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(isNamespaceAware());
            dbf.setValidating(false);
            return dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            throw new IOException("Couldn't create the empty document: "
                    + pce.getMessage());
        }
    }

    /**
     * Sets the wrapped DOM document.
     * 
     * @param dom
     *                The wrapped DOM document.
     */
    public void setDocument(Document dom) {
        this.dom = dom;
    }

    /**
     * Writes the representation to a byte stream.
     * 
     * @param outputStream
     *                The output stream.
     */
	@Override
    public void write(OutputStream outputStream) throws IOException {
        try {
            if (getDocument() != null) {
                Transformer transformer = TransformerFactory.newInstance()
                        .newTransformer();
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");

                if (getDocument().getDoctype() != null) {
                    transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                            getDocument().getDoctype().getSystemId());
                    transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                            getDocument().getDoctype().getPublicId());
                }

                transformer.transform(new DOMSource(getDocument()),
                        new StreamResult(outputStream));
            }
        } catch (TransformerConfigurationException tce) {
            throw new IOException("Couldn't write the XML representation: "
                    + tce.getMessage());
        } catch (TransformerException te) {
            throw new IOException("Couldn't write the XML representation: "
                    + te.getMessage());
        } catch (TransformerFactoryConfigurationError tfce) {
            throw new IOException("Couldn't write the XML representation: "
                    + tfce.getMessage());
        }
    }
}
