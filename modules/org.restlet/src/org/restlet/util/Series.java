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

package org.restlet.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.restlet.data.Parameter;

/**
 * Modifiable list of entries with many helper methods. Note that this class
 * uses the Parameter class as the template type. This allows you to use an
 * instance of this class as any other java.util.List, in particular all the
 * helper methods in java.util.Collections.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 * @see org.restlet.data.Parameter
 * @see java.util.Collections
 * @see java.util.List
 */
public abstract class Series<E extends Parameter> extends WrapperList<E> {
    /**
     * A marker for empty values to differentiate from non existing values
     * (null).
     */
    public static final Object EMPTY_VALUE = new Object();

    /**
     * Constructor.
     */
    public Series() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param initialCapacity
     *            The initial list capacity.
     */
    public Series(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructor.
     * 
     * @param delegate
     *            The delegate list.
     */
    public Series(List<E> delegate) {
        super(delegate);
    }

    /**
     * Creates a new entry.
     * 
     * @param name
     *            The name of the entry.
     * @param value
     *            The value of the entry.
     * @return A new entry.
     */
    public abstract E createEntry(String name, String value);

    /**
     * Creates a new series.
     * 
     * @param delegate
     *            Optional delegate series.
     * @return A new series.
     */
    public abstract Series<E> createSeries(List<E> delegate);

    /**
     * Creates then adds a parameter at the end of the list.
     * 
     * @param name
     *            The parameter name.
     * @param value
     *            The parameter value.
     * @return True (as per the general contract of the Collection.add method).
     */
    public boolean add(String name, String value) {
        return add(createEntry(name, value));
    }

    /**
     * Copies the parameters whose name is a key in the given map.<br/> If a
     * matching parameter is found, its value is put in the map.<br/> If
     * multiple values are found, a list is created and set in the map.
     * 
     * @param params
     *            The map controlling the copy.
     */
    @SuppressWarnings("unchecked")
    public void copyTo(Map<String, Object> params) {
        Parameter param;
        Object currentValue = null;
        for (Iterator<E> iter = iterator(); iter.hasNext();) {
            param = iter.next();

            if (params.containsKey(param.getName())) {
                currentValue = params.get(param.getName());

                if (currentValue != null) {
                    List<Object> values = null;

                    if (currentValue instanceof List) {
                        // Multiple values already found for this entry
                        values = (List<Object>) currentValue;
                    } else {
                        // Second value found for this entry
                        // Create a list of values
                        values = new ArrayList<Object>();
                        values.add(currentValue);
                        params.put(param.getName(), values);
                    }

                    if (param.getValue() == null) {
                        values.add(Series.EMPTY_VALUE);
                    } else {
                        values.add(param.getValue());
                    }
                } else {
                    if (param.getValue() == null) {
                        params.put(param.getName(), Series.EMPTY_VALUE);
                    } else {
                        params.put(param.getName(), param.getValue());
                    }
                }
            }
        }
    }

    /**
     * Tests the equality of two string, potentially null, which a case
     * sensitivity flag.
     * 
     * @param value1
     *            The first value.
     * @param value2
     *            The second value.
     * @param ignoreCase
     *            Indicates if the test should be case insensitive.
     * @return True if both values are equal.
     */
    private boolean equals(String value1, String value2, boolean ignoreCase) {
        boolean result = (value1 == value2);

        if (!result) {
            if ((value1 != null) && (value2 != null)) {
                if (ignoreCase) {
                    result = value1.equalsIgnoreCase(value2);
                } else {
                    result = value1.equals(value2);
                }
            }
        }

        return result;
    }

    /**
     * Returns the first parameter found with the given name.
     * 
     * @param name
     *            The parameter name (case sensitive).
     * @return The first parameter found with the given name.
     */
    public E getFirst(String name) {
        return getFirst(name, false);
    }

    /**
     * Returns the first parameter found with the given name.
     * 
     * @param name
     *            The parameter name.
     * @param ignoreCase
     *            Indicates if the name comparison is case sensitive.
     * @return The first parameter found with the given name.
     */
    public E getFirst(String name, boolean ignoreCase) {
        for (E param : this) {
            if (equals(param.getName(), name, ignoreCase)) {
                return param;
            }
        }

        return null;
    }

    /**
     * Returns the value of the first parameter found with the given name.
     * 
     * @param name
     *            The parameter name (case sensitive).
     * @return The value of the first parameter found with the given name.
     */
    public String getFirstValue(String name) {
        return getFirstValue(name, false);
    }

    /**
     * Returns the value of the first parameter found with the given name.
     * 
     * @param name
     *            The parameter name.
     * @param ignoreCase
     *            Indicates if the name comparison is case sensitive.
     * @return The value of the first parameter found with the given name.
     */
    public String getFirstValue(String name, boolean ignoreCase) {
        return getFirstValue(name, ignoreCase, null);
    }

    /**
     * Returns the value of the first parameter found with the given name.
     * 
     * @param name
     *            The parameter name.
     * @param ignoreCase
     *            Indicates if the name comparison is case sensitive.
     * @param defaultValue
     *            The default value to return if no matching parameter found.
     * @return The value of the first parameter found with the given name or the
     *         default value.
     */
    public String getFirstValue(String name, boolean ignoreCase,
            String defaultValue) {
        String result = defaultValue;
        Parameter param = getFirst(name, ignoreCase);

        if (param != null) {
            result = param.getValue();
        }

        return result;
    }

    /**
     * Returns the value of the first parameter found with the given name.
     * 
     * @param name
     *            The parameter name (case sensitive).
     * @param defaultValue
     *            The default value to return if no matching parameter found.
     * @return The value of the first parameter found with the given name or the
     *         default value.
     */
    public String getFirstValue(String name, String defaultValue) {
        return getFirstValue(name, false, defaultValue);
    }

    /**
     * Returns the set of parameter names (case sensitive).
     * 
     * @return The set of parameter names.
     */
    public Set<String> getNames() {
        Set<String> result = new HashSet<String>();

        for (Parameter param : this) {
            result.add(param.getName());
        }

        return result;
    }

    /**
     * Returns the values of the parameters with a given name. If multiple
     * parameters with the same name are found, all values are concatenated and
     * separated by a comma (like for HTTP message headers).
     * 
     * @param name
     *            The parameter name (case insensitive).
     * @return The values of the parameters with a given name.
     */
    public String getValues(String name) {
        return getValues(name, ",", true);
    }

    /**
     * Returns the parameter values with a given name. If multiple parameters
     * with the same name are found, all values are concatenated and separated
     * by the given separator.
     * 
     * @param name
     *            The parameter name.
     * @param separator
     *            The separator character.
     * @param ignoreCase
     *            Indicates if the name comparison is case sensitive.
     * @return The sequence of values.
     */
    public String getValues(String name, String separator, boolean ignoreCase) {
        String result = null;
        StringBuilder sb = null;

        for (E param : this) {
            if (param.getName().equalsIgnoreCase(name)) {
                if (sb == null) {
                    if (result == null) {
                        result = param.getValue();
                    } else {
                        sb = new StringBuilder();
                        sb.append(result).append(separator).append(
                                param.getValue());
                    }
                } else {
                    sb.append(separator).append(param.getValue());
                }
            }
        }

        if (sb != null) {
            result = sb.toString();
        }

        return result;
    }

    /**
     * Removes all the parameters with a given name.
     * 
     * @param name
     *            The parameter name (case sensitive).
     * @return True if the list changed.
     */
    public boolean removeAll(String name) {
        return removeAll(name, false);
    }

    /**
     * Removes all the parameters with a given name.
     * 
     * @param name
     *            The parameter name.
     * @param ignoreCase
     *            Indicates if the name comparison is case sensitive.
     * @return True if the list changed.
     */
    public boolean removeAll(String name, boolean ignoreCase) {
        boolean changed = false;
        Parameter param = null;

        for (Iterator<E> iter = iterator(); iter.hasNext();) {
            param = iter.next();
            if (equals(param.getName(), name, ignoreCase)) {
                iter.remove();
                changed = true;
            }
        }

        return changed;
    }

    /**
     * Removes from this list the first entry whose name equals the specified
     * name ignoring the case.
     * 
     * @param name
     *            The name of the entries to be removed (case sensitive).
     * @return false if no entry has been removed, true otherwise.
     */
    public boolean removeFirst(String name) {
        return removeFirst(name, false);
    }

    /**
     * Removes from this list the first entry whose name equals the specified
     * name ignoring the case or not.
     * 
     * @param name
     *            The name of the entries to be removed.
     * @param ignoreCase
     *            true if the comparison ignores the case, false otherwise.
     * @return false if no entry has been removed, true otherwise.
     */
    public boolean removeFirst(String name, boolean ignoreCase) {
        boolean changed = false;
        Parameter param = null;

        for (Iterator<E> iter = iterator(); iter.hasNext() && !changed;) {
            param = iter.next();
            if (equals(param.getName(), name, ignoreCase)) {
                iter.remove();
                changed = true;
            }
        }

        return changed;
    }

    /**
     * Replaces the value of the first parameter with the given name and removes
     * all other parameters with the same name.
     * 
     * @param name
     *            The parameter name.
     * @param value
     *            The value to set.
     * @param ignoreCase
     *            Indicates if the name comparison is case sensitive.
     * @return The parameter set or added.
     */
    public E set(String name, String value, boolean ignoreCase) {
        E result = null;
        E param = null;
        boolean found = false;

        for (Iterator<E> iter = iterator(); iter.hasNext();) {
            param = iter.next();

            if (equals(param.getName(), name, ignoreCase)) {
                if (found) {
                    // Remove other entries with the same name
                    iter.remove();
                } else {
                    // Change the value of the first matching entry
                    found = true;
                    param.setValue(value);
                    result = param;
                }
            }
        }

        if (!found) {
            add(name, value);
        }

        return result;
    }

    /**
     * Returns a view of the portion of this list between the specified
     * fromIndex, inclusive, and toIndex, exclusive.
     * 
     * @param fromIndex
     *            The start position.
     * @param toIndex
     *            The end position (exclusive).
     * @return The sub-list.
     */
    @Override
    public Series<E> subList(int fromIndex, int toIndex) {
        return createSeries(getDelegate().subList(fromIndex, toIndex));
    }

    /**
     * Returns a list of all the values associated to the parameter name.
     * 
     * @param name
     *            The parameter name (case sensitive).
     * @return The list of values.
     */
    public Series<E> subList(String name) {
        return subList(name, false);
    }

    /**
     * Returns a list of all the values associated to the parameter name.
     * 
     * @param name
     *            The parameter name.
     * @param ignoreCase
     *            Indicates if the name comparison is case sensitive.
     * @return The list of values.
     */
    public Series<E> subList(String name, boolean ignoreCase) {
        Series<E> result = createSeries(null);

        for (E param : this) {
            if (equals(param.getName(), name, ignoreCase)) {
                result.add(param);
            }
        }

        return result;
    }

}
