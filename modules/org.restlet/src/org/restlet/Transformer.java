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

import org.restlet.data.CharacterSet;
import org.restlet.data.Encoding;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.TransformRepresentation;

/**
 * Filter that can transform XML representations by applying an XSLT transform
 * sheet. It uses the {@link org.restlet.resource.TransformRepresentation} to
 * actually transform the XML entities.
 * 
 * @author Jerome Louvel (contact@noelios.com) <a
 *         href="http://www.noelios.com/">Noelios Technologies</a>
 */
public class Transformer extends Filter {
    /**
     * Mode that transforms request entities before their handling by the
     * attached Restlet.
     */
    public static final int MODE_REQUEST = 1;

    /**
     * Mode that transforms response entities after their handling by the
     * attached Restlet.
     */
    public static final int MODE_RESPONSE = 2;

    /** The transformation mode. */
    private int mode;

    /** The XSLT transform sheet to apply to message entities. */
    private Representation transformSheet;

    /**
     * The character set of the result representation. The default value is
     * null.
     */
    private CharacterSet resultCharacterSet;

    /**
     * The encodings of the result representation.
     */
    private List<Encoding> resultEncodings;

    /** The languages of the result representation. */
    private List<Language> resultLanguages;

    /**
     * The media type of the result representation. MediaType.APPLICATION_XML by
     * default.
     */
    private MediaType resultMediaType;

    /**
     * Constructor.
     * 
     * @param mode
     *                The transformation mode.
     * @param transformSheet
     *                The XSLT transform sheet to apply to message entities.
     */
    public Transformer(int mode, Representation transformSheet) {
        this.mode = mode;
        this.transformSheet = transformSheet;
        this.resultMediaType = MediaType.APPLICATION_XML;
        this.resultLanguages = null;
        this.resultCharacterSet = null;
    }

    @Override
    protected void afterHandle(Request request, Response response) {
        if (getMode() == MODE_RESPONSE) {
            response.setEntity(transform(response.getEntity()));
        }
    }

    @Override
    protected void beforeHandle(Request request, Response response) {
        if (getMode() == MODE_REQUEST) {
            request.setEntity(transform(request.getEntity()));
        }
    }

    /**
     * Returns the transformation mode. See MODE_* constants.
     * 
     * @return The transformation mode.
     */
    public int getMode() {
        return this.mode;
    }

    /**
     * Returns the character set of the result representation. The default value
     * is null.
     * 
     * @return The character set of the result representation.
     */
    public CharacterSet getResultCharacterSet() {
        return this.resultCharacterSet;
    }

    /**
     * Returns the encoding of the result representation. The default value is
     * null.
     * 
     * @return The encoding of the result representation.
     */
    public List<Encoding> getResultEncodings() {
        if (this.resultEncodings == null)
            this.resultEncodings = new ArrayList<Encoding>();
        return this.resultEncodings;
    }

    /**
     * Returns the languages of the result representation.
     * 
     * @return The language of the result representation.
     */
    public List<Language> getResultLanguages() {
        if (this.resultLanguages == null)
            this.resultLanguages = new ArrayList<Language>();
        return this.resultLanguages;
    }

    /**
     * Returns the media type of the result representation. The default value is
     * MediaType.APPLICATION_XML.
     * 
     * @return The media type of the result representation.
     */
    public MediaType getResultMediaType() {
        return this.resultMediaType;
    }

    /**
     * Returns the XSLT transform sheet to apply to message entities.
     * 
     * @return The XSLT transform sheet to apply to message entities.
     */
    public Representation getTransformSheet() {
        return this.transformSheet;
    }

    /**
     * Sets the transformation mode. See MODE_* constants.
     * 
     * @param mode
     *                The transformation mode.
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * Sets the character set of the result representation.
     * 
     * @param resultCharacterSet
     *                The character set of the result representation.
     */
    public void setResultCharacterSet(CharacterSet resultCharacterSet) {
        this.resultCharacterSet = resultCharacterSet;
    }

    /**
     * Sets the media type of the result representation.
     * 
     * @param resultMediaType
     *                The media type of the result representation.
     */
    public void setResultMediaType(MediaType resultMediaType) {
        this.resultMediaType = resultMediaType;
    }

    /**
     * Sets the XSLT transform sheet to apply to message entities.
     * 
     * @param transformSheet
     *                The XSLT transform sheet to apply to message entities.
     */
    public void setTransformSheet(Representation transformSheet) {
        this.transformSheet = transformSheet;
    }

    /**
     * Transforms a source XML representation by applying an XSLT transform
     * sheet to it.
     * 
     * @param source
     *                The source XML representation.
     * @return The generated result representation.
     */
    public Representation transform(Representation source) {
        Representation result = new TransformRepresentation(getContext(),
                source, getTransformSheet());

        if (this.resultLanguages != null) {
            result.getLanguages().addAll(getResultLanguages());
        }

        result.setCharacterSet(getResultCharacterSet());
        if (this.resultEncodings != null) {
            result.getEncodings().addAll(getResultEncodings());
        }

        result.setMediaType(getResultMediaType());
        return result;
    }

}
