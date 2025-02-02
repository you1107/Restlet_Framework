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

package org.restlet.service;

import java.util.Map;
import java.util.TreeMap;

import org.restlet.data.Encoding;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Metadata;

/**
 * Service providing access to metadata and their associated extension names.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class MetadataService {
    /** The default encoding for local representations. */
    private Encoding defaultEncoding;

    /** The default language for local representations. */
    private Language defaultLanguage;

    /** The default media type for local representations. */
    private MediaType defaultMediaType;

    /** The mappings from extension names to metadata. */
    private Map<String, Metadata> metadataMappings;

    /**
     * Constructor.
     */
    public MetadataService() {
        this.defaultEncoding = Encoding.IDENTITY;
        this.defaultLanguage = Language.ENGLISH_US;
        this.defaultMediaType = MediaType.APPLICATION_OCTET_STREAM;
        this.metadataMappings = new TreeMap<String, Metadata>();
        addCommonExtensions();
    }

    /**
     * Adds a common list of associations from extensions to metadata. The list
     * of languages extensions:<br/>
     * <ul>
     * <li>en: English</li>
     * <li>es: Spanish</li>
     * <li>fr: French</li>
     * </ul>
     * <br/> The list of media type extensions:<br/>
     * <ul>
     * <li>css: CSS stylesheet</li>
     * <li>doc: Microsoft Word document</li>
     * <li>gif: GIF image</li>
     * <li>html: HTML document</li>
     * <li>ico: Windows icon (Favicon)</li>
     * <li>jpeg, jpg: JPEG image</li>
     * <li>js: JavaScript document</li>
     * <li>json: JavaScript Object Notation document</li>
     * <li>pdf: Adobe PDF document</li>
     * <li>png: PNG image</li>
     * <li>ppt: Microsoft Powerpoint document</li>
     * <li>rdf: Description Framework document</li>
     * <li>txt: Plain text</li>
     * <li>swf: Shockwave Flash object</li>
     * <li>xhtml: XHTML document</li>
     * <li>xml: XML document</li>
     * <li>zip: Zip archive</li>
     * </ul>
     */
    public void addCommonExtensions() {
        addExtension("en", Language.ENGLISH);
        addExtension("es", Language.SPANISH);
        addExtension("fr", Language.FRENCH);

        addExtension("css", MediaType.TEXT_CSS);
        addExtension("doc", MediaType.APPLICATION_WORD);
        addExtension("gif", MediaType.IMAGE_GIF);
        addExtension("html", MediaType.TEXT_HTML);
        addExtension("ico", MediaType.IMAGE_ICON);
        addExtension("jpeg", MediaType.IMAGE_JPEG);
        addExtension("jpg", MediaType.IMAGE_JPEG);
        addExtension("js", MediaType.APPLICATION_JAVASCRIPT);
        addExtension("json", MediaType.APPLICATION_JSON);
        addExtension("pdf", MediaType.APPLICATION_PDF);
        addExtension("png", MediaType.IMAGE_PNG);
        addExtension("ppt", MediaType.APPLICATION_POWERPOINT);
        addExtension("rdf", MediaType.APPLICATION_RDF_XML);
        addExtension("txt", MediaType.TEXT_PLAIN);
        addExtension("svg", MediaType.IMAGE_SVG);
        addExtension("swf", MediaType.APPLICATION_FLASH);
        addExtension("xhtml", MediaType.APPLICATION_XHTML_XML);
        addExtension("xml", MediaType.TEXT_XML);
        addExtension("zip", MediaType.APPLICATION_ZIP);
    }

    /**
     * Maps an extension to some metadata (media type, language or character
     * set) to an extension.
     * 
     * @param extension
     *                The extension name.
     * @param metadata
     *                The metadata to map.
     */
    public void addExtension(String extension, Metadata metadata) {
        this.metadataMappings.put(extension, metadata);
    }

    /**
     * Returns the default encoding for local representations.
     * 
     * @return The default encoding for local representations.
     */
    public Encoding getDefaultEncoding() {
        return this.defaultEncoding;
    }

    /**
     * Returns the default language for local representations.
     * 
     * @return The default language for local representations.
     */
    public Language getDefaultLanguage() {
        return this.defaultLanguage;
    }

    /**
     * Returns the default media type for local representations.
     * 
     * @return The default media type for local representations.
     */
    public MediaType getDefaultMediaType() {
        return this.defaultMediaType;
    }

    /**
     * Returns the first extension mapping to this metadata.
     * 
     * @param metadata
     *                The metadata to find.
     * @return The first extension mapping to this metadata.
     */
    public String getExtension(Metadata metadata) {
        for (String extension : getMappings().keySet()) {
            if (getMetadata(extension).equals(metadata)) {
                return extension;
            }
        }

        return null;
    }

    /**
     * Returns the mappings from extension names to metadata.
     * 
     * @return The mappings from extension names to metadata.
     */
    public Map<String, Metadata> getMappings() {
        return this.metadataMappings;
    }

    /**
     * Returns the metadata associated to this extension. It returns null if the
     * extension was not declared.
     * 
     * @param extension
     *                The extension name without any delimiter.
     * @return The metadata associated to this extension.
     */
    public Metadata getMetadata(String extension) {
        return getMappings().get(extension);
    }

    /**
     * Sets the default encoding for local representations.
     * 
     * @param defaultEncoding
     *                The default encoding for local representations.
     */
    public void setDefaultEncoding(Encoding defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    /**
     * Sets the default language for local representations.
     * 
     * @param defaultLanguage
     *                The default language for local representations.
     */
    public void setDefaultLanguage(Language defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    /**
     * Sets the default media type for local representations.
     * 
     * @param defaultMediaType
     *                The default media type for local representations.
     */
    public void setDefaultMediaType(MediaType defaultMediaType) {
        this.defaultMediaType = defaultMediaType;
    }

}
