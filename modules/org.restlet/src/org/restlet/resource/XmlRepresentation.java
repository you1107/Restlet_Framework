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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

import org.restlet.data.MediaType;
import org.restlet.util.NodeSet;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Representation based on an XML document. It knows how to evaluate XPath
 * expressions and how to manage a namespace context.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public abstract class XmlRepresentation extends OutputRepresentation implements
		NamespaceContext {
	/** Internal map of namespaces. */
	private Map<String, String> namespaces;

	/** Indicates if processing is namespace aware. */
	private boolean namespaceAware;

	/**
	 * Constructor.
	 * 
	 * @param mediaType
	 *            The representation's mediaType.
	 */
	public XmlRepresentation(MediaType mediaType) {
		super(mediaType);
		this.namespaces = null;
		this.namespaceAware = false;
	}

	/**
	 * Constructor.
	 * 
	 * @param mediaType
	 *            The representation's mediaType.
	 * @param expectedSize
	 *            The expected input stream size.
	 */
	public XmlRepresentation(MediaType mediaType, long expectedSize) {
		super(mediaType, expectedSize);
		this.namespaces = null;
		this.namespaceAware = false;
	}

	/**
	 * Evaluates an XPath expression and returns the result as in the given
	 * return type.
	 * 
	 * @param returnType
	 *            The qualified name of the return type.
	 * @return The evaluation result.
	 * @see javax.xml.xpath.XPathException
	 * @see javax.xml.xpath.XPathConstants
	 */
	public abstract Object evaluate(String expression, QName returnType)
			throws Exception;

	/**
	 * Evaluates an XPath expression as a boolean. If the evaluation fails, null
	 * will be returned.
	 * 
	 * @return The evaluation result.
	 */
	public Boolean getBoolean(String expression) {
		return (Boolean) internalEval(expression, XPathConstants.BOOLEAN);
	}

	/**
	 * Returns the map of namespaces.
	 * 
	 * @return The map of namespaces.
	 */
	private Map<String, String> getNamespaces() {
		if (this.namespaces == null)
			this.namespaces = new HashMap<String, String>();
		return this.namespaces;
	}

	/**
	 * {@inheritDoc javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String}
	 */
	public String getNamespaceURI(String prefix) {
		return this.namespaces.get(prefix);
	}

	/**
	 * Evaluates an XPath expression as a DOM Node. If the evaluation fails,
	 * null will be returned.
	 * 
	 * @return The evaluation result.
	 */
	public Node getNode(String expression) {
		return (Node) internalEval(expression, XPathConstants.NODE);
	}

	/**
	 * Evaluates an XPath expression as a DOM NodeList. If the evaluation fails,
	 * null will be returned.
	 * 
	 * @return The evaluation result.
	 */
	public NodeSet getNodes(String expression) {
		NodeList nodes = (NodeList) internalEval(expression,
				XPathConstants.NODESET);
		return (nodes == null) ? null : new NodeSet(nodes);
	}

	/**
	 * Evaluates an XPath expression as a number. If the evaluation fails, null
	 * will be returned.
	 * 
	 * @return The evaluation result.
	 */
	public Double getNumber(String expression) {
		return (Double) internalEval(expression, XPathConstants.NUMBER);
	}

	/**
	 * {@inheritDoc javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String}
	 */
	public String getPrefix(String namespaceURI) {
		String result = null;

		for (Entry<String, String> entry : getNamespaces().entrySet()) {
			if ((result == null) && entry.getValue().equals(namespaceURI))
				result = entry.getKey();
		}

		return result;
	}

	/**
	 * {@inheritDoc javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String}
	 */
	public Iterator<String> getPrefixes(String namespaceURI) {
		List<String> result = new ArrayList<String>();

		for (Entry<String, String> entry : getNamespaces().entrySet()) {
			if (entry.getValue().equals(namespaceURI))
				result.add(entry.getKey());
		}

		return Collections.unmodifiableList(result).iterator();
	}

	/**
	 * Evaluates an XPath expression as a string.
	 * 
	 * @return The evaluation result.
	 */
	public String getText(String expression) {
		return (String) internalEval(expression, XPathConstants.STRING);
	}

	/**
	 * Evaluates an XPath expression and returns the result as in the given
	 * return type.
	 * 
	 * @param returnType
	 *            The qualified name of the return type.
	 * @return The evaluation result.
	 */
	private Object internalEval(String expression, QName returnType) {
		try {
			return evaluate(expression, returnType);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Indicates if processing is namespace aware.
	 * 
	 * @return True if processing is namespace aware.
	 */
	public boolean isNamespaceAware() {
		return this.namespaceAware;
	}

	/**
	 * Puts a new mapping between a prefix and a namespace URI.
	 * 
	 * @param prefix
	 *            The namespace prefix.
	 * @param namespaceURI
	 *            The namespace URI.
	 */
	public void putNamespace(String prefix, String namespaceURI) {
		getNamespaces().put(prefix, namespaceURI);
	}

	/**
	 * Indicates if processing is namespace aware.
	 * 
	 * @param namespaceAware
	 *            Indicates if processing is namespace aware.
	 */
	public void setNamespaceAware(boolean namespaceAware) {
		this.namespaceAware = namespaceAware;
	}

}
