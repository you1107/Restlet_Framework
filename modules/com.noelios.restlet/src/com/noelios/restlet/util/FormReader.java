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

package com.noelios.restlet.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.resource.Representation;
import org.restlet.util.Series;

/**
 * Form reader.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class FormReader {
    /** The form stream. */
    private InputStream stream;

    /** The logger to use. */
    private Logger logger;

    /** The encoding to use. */
    private CharacterSet characterSet;

    /**
     * Constructor.<br/>In case the representation does not define a character
     * set, the UTF-8 character set is used.
     * 
     * @param logger
     *                The logger.
     * @param representation
     *                The web form content.
     */
    public FormReader(Logger logger, Representation representation)
            throws IOException {
        this.logger = logger;
        this.stream = representation.getStream();
        if (representation.getCharacterSet() != null) {
            this.characterSet = representation.getCharacterSet();
        } else {
            this.characterSet = CharacterSet.UTF_8;
        }
    }

    /**
     * Constructor.
     * 
     * @param logger
     *                The logger.
     * @param query
     *                The query string.
     * @param characterSet
     *                The supported character encoding.
     */
    public FormReader(Logger logger, String query, CharacterSet characterSet)
            throws IOException {
        this.logger = logger;
        this.stream = new ByteArrayInputStream(query.getBytes());
        this.characterSet = characterSet;
    }

    /**
     * Reads the parameters with the given name. If multiple values are found, a
     * list is returned created.
     * 
     * @param name
     *                The parameter name to match.
     * @return The parameter value or list of values.
     */
    @SuppressWarnings("unchecked")
    public Object readParameter(String name) throws IOException {
        Parameter param = readNextParameter();
        Object result = null;

        while (param != null) {
            if (param.getName().equals(name)) {
                if (result != null) {
                    List<Object> values = null;

                    if (result instanceof List) {
                        // Multiple values already found for this parameter
                        values = (List) result;
                    } else {
                        // Second value found for this parameter
                        // Create a list of values
                        values = new ArrayList<Object>();
                        values.add(result);
                        result = values;
                    }

                    if (param.getValue() == null) {
                        values.add(Series.EMPTY_VALUE);
                    } else {
                        values.add(param.getValue());
                    }
                } else {
                    if (param.getValue() == null) {
                        result = Series.EMPTY_VALUE;
                    } else {
                        result = param.getValue();
                    }
                }
            }

            param = readNextParameter();
        }

        this.stream.close();
        return result;
    }

    /**
     * Reads the first parameter with the given name.
     * 
     * @param name
     *                The parameter name to match.
     * @return The parameter value.
     * @throws IOException
     */
    public Parameter readFirstParameter(String name) throws IOException {
        Parameter param = readNextParameter();
        Parameter result = null;

        while ((param != null) && (result == null)) {
            if (param.getName().equals(name)) {
                result = param;
            }

            param = readNextParameter();
        }

        this.stream.close();
        return result;
    }

    /**
     * Reads the parameters whose name is a key in the given map. If a matching
     * parameter is found, its value is put in the map. If multiple values are
     * found, a list is created and set in the map.
     * 
     * @param parameters
     *                The parameters map controlling the reading.
     */
    @SuppressWarnings("unchecked")
    public void readParameters(Map<String, Object> parameters)
            throws IOException {
        Parameter param = readNextParameter();
        Object currentValue = null;

        while (param != null) {
            if (parameters.containsKey(param.getName())) {
                currentValue = parameters.get(param.getName());

                if (currentValue != null) {
                    List<Object> values = null;

                    if (currentValue instanceof List) {
                        // Multiple values already found for this parameter
                        values = (List) currentValue;
                    } else {
                        // Second value found for this parameter
                        // Create a list of values
                        values = new ArrayList<Object>();
                        values.add(currentValue);
                        parameters.put(param.getName(), values);
                    }

                    if (param.getValue() == null) {
                        values.add(Series.EMPTY_VALUE);
                    } else {
                        values.add(param.getValue());
                    }
                } else {
                    if (param.getValue() == null) {
                        parameters.put(param.getName(), Series.EMPTY_VALUE);
                    } else {
                        parameters.put(param.getName(), param.getValue());
                    }
                }
            }

            param = readNextParameter();
        }

        this.stream.close();
    }

    /**
     * Reads the next parameter available or null.
     * 
     * @return The next parameter available or null.
     */
    public Parameter readNextParameter() throws IOException {
        Parameter result = null;

        try {
            boolean readingName = true;
            boolean readingValue = false;
            StringBuilder nameBuffer = new StringBuilder();
            StringBuilder valueBuffer = new StringBuilder();

            int nextChar = 0;
            while ((result == null) && (nextChar != -1)) {
                nextChar = this.stream.read();

                if (readingName) {
                    if (nextChar == '=') {
                        if (nameBuffer.length() > 0) {
                            readingName = false;
                            readingValue = true;
                        } else {
                            throw new IOException(
                                    "Empty parameter name detected. Please check your form data");
                        }
                    } else if ((nextChar == '&') || (nextChar == -1)) {
                        if (nameBuffer.length() > 0) {
                            result = FormUtils.create(nameBuffer, null,
                                    characterSet);
                        } else if (nextChar == -1) {
                            // Do nothing return null preference
                        } else {
                            throw new IOException(
                                    "Empty parameter name detected. Please check your form data");
                        }
                    } else {
                        nameBuffer.append((char) nextChar);
                    }
                } else if (readingValue) {
                    if ((nextChar == '&') || (nextChar == -1)) {
                        if (valueBuffer.length() > 0) {
                            result = FormUtils.create(nameBuffer, valueBuffer,
                                    characterSet);
                        } else {
                            result = FormUtils.create(nameBuffer, null,
                                    characterSet);
                        }
                    } else {
                        valueBuffer.append((char) nextChar);
                    }
                }
            }
        } catch (UnsupportedEncodingException uee) {
            throw new IOException(
                    "Unsupported encoding. Please contact the administrator");
        }

        return result;
    }

    /**
     * Reads all the parameters.
     * 
     * @return The form read.
     */
    public Form read() throws IOException {
        Form result = new Form();
        Parameter param = readNextParameter();

        while (param != null) {
            result.add(param);
            param = readNextParameter();
        }

        this.stream.close();
        return result;
    }

    /**
     * Adds the parameters into a given form.
     * 
     * @param form
     *                The target form.
     */
    public void addParameters(Form form) {
        boolean readNext = true;
        Parameter param = null;

        // Let's read all form parameters
        try {
            while (readNext) {
                param = readNextParameter();

                if (param != null) {
                    // Add parsed parameter to the form
                    form.add(param);
                } else {
                    // Last parameter parsed
                    readNext = false;
                }
            }
        } catch (IOException ioe) {
            getLogger()
                    .log(
                            Level.WARNING,
                            "Unable to parse a form parameter. Skipping the remaining parameters.",
                            ioe);
        }

        try {
            this.stream.close();
        } catch (IOException ioe) {
            getLogger().log(Level.WARNING,
                    "Unable to close the form input stream", ioe);
        }
    }

    /**
     * Returns the logger.
     * 
     * @return The logger.
     */
    private Logger getLogger() {
        if (this.logger == null)
            this.logger = Logger.getLogger(FormReader.class.getCanonicalName());
        return this.logger;
    }

}
