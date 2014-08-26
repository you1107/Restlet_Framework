/**
 * Copyright 2005-2014 Restlet
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://restlet.com/products/restlet-framework
 * 
 * Restlet is a registered trademark of Restlet S.A.S.
 */

package org.restlet.ext.apispark.internal.firewall.handler.policy;

import java.util.HashMap;
import java.util.Map;

import org.restlet.Request;

/**
 * Defines a limit to a sets of counted values.
 * 
 * @author Guillaume Blondeau
 */
public class GroupLimitPolicy extends LimitPolicy {

    /**
     * The default limit applied when the counted value has not been found in
     * any group.
     */
    public int defaultLimit;

    /** Maps a counted value to a group. */
    private Map<String, String> groups;

    /** Maps a group name to a limit. */
    private Map<String, Integer> limitsPerGroup;

    /**
     * Constructor.<br>
     * Defines only the {@link GroupLimitPolicy#defaultLimit} to 0.
     */
    public GroupLimitPolicy() {
        this(0);
    }

    /**
     * Constructor.<br>
     * Defines only the {@link GroupLimitPolicy#defaultLimit}.
     * 
     * @param defaultLimit
     *            The default limit.
     */
    public GroupLimitPolicy(int defaultLimit) {
        this.defaultLimit = defaultLimit;
        this.limitsPerGroup = new HashMap<String, Integer>();
        this.groups = new HashMap<String, String>();
    }

    /**
     * Associates a counted value with a group.
     * 
     * @param countedValue
     *            The counted value.
     * @param group
     *            The name of the group.
     */
    public void addCountedValue(String countedValue, String group) {
        groups.put(countedValue, group);
    }

    /**
     * Specifies a limit for a group.
     * 
     * @param group
     *            The name of the group.
     * @param limit
     *            The associated limit.
     */
    public void addGroup(String group, int limit) {
        limitsPerGroup.put(group, limit);
    }

    @Override
    public int getLimit(Request request, String countedValue) {
        int result = defaultLimit;
        String group = groups.get(countedValue);
        if (group != null) {
            if (limitsPerGroup.containsKey(group)) {
                result = limitsPerGroup.get(group);
            }
        }

        return result;
    }

}
