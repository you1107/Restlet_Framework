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

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.restlet.Restlet;
import org.restlet.Route;
import org.restlet.data.Request;
import org.restlet.data.Response;

/**
 * Modifiable list of routes with some helper methods. Note that this class
 * implements the java.util.List interface using the Route class as the generic
 * type. This allows you to use an instance of this class as any other
 * java.util.List, in particular all the helper methods in
 * java.util.Collections.<br/> <br/> Note that structural changes to this list
 * are thread-safe, using an underlying
 * {@link java.util.concurrent.CopyOnWriteArrayList}.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 * @see java.util.Collections
 * @see java.util.List
 */
public final class RouteList extends WrapperList<Route> {
    /** The index of the last route used in the round robin mode. */
    private int lastIndex;

    /**
     * Constructor.
     */
    public RouteList() {
        super(new CopyOnWriteArrayList<Route>());
        this.lastIndex = -1;
    }

    /**
     * Constructor.
     * 
     * @param delegate
     *                The delegate list.
     */
    public RouteList(List<Route> delegate) {
        super(new CopyOnWriteArrayList<Route>(delegate));
        this.lastIndex = -1;
    }

    /**
     * Returns the best route match for a given call.
     * 
     * @param request
     *                The request to score.
     * @param response
     *                The response to score.
     * @param requiredScore
     *                The minimum score required to have a match.
     * @return The best route match or null.
     */
    public Route getBest(Request request, Response response, float requiredScore) {
        Route result = null;
        float bestScore = 0F;
        float score;
        for (Route current : this) {
            score = current.score(request, response);

            if ((score > bestScore) && (score >= requiredScore)) {
                bestScore = score;
                result = current;
            }
        }

        return result;
    }

    /**
     * Returns the first route match for a given call.
     * 
     * @param request
     *                The request to score.
     * @param response
     *                The response to score.
     * @param requiredScore
     *                The minimum score required to have a match.
     * @return The first route match or null.
     */
    public Route getFirst(Request request, Response response,
            float requiredScore) {
        for (Route current : this) {
            if (current.score(request, response) >= requiredScore)
                return current;
        }

        // No match found
        return null;
    }

    /**
     * Returns the last route match for a given call.
     * 
     * @param request
     *                The request to score.
     * @param response
     *                The response to score.
     * @param requiredScore
     *                The minimum score required to have a match.
     * @return The last route match or null.
     */
    public Route getLast(Request request, Response response, float requiredScore) {
        for (int j = size() - 1; (j >= 0); j--) {
            Route route = get(j);
            if (route.score(request, response) >= requiredScore)
                return route;
        }

        // No match found
        return null;
    }

    /**
     * Returns a next route match in a round robin mode for a given call.
     * 
     * @param request
     *                The request to score.
     * @param response
     *                The response to score.
     * @param requiredScore
     *                The minimum score required to have a match.
     * @return A next route or null.
     */
    public Route getNext(Request request, Response response, float requiredScore) {
        if (!isEmpty()) {
            for (int initialIndex = lastIndex++; initialIndex != lastIndex; lastIndex++) {
                if (lastIndex >= size()) {
                    lastIndex = 0;
                }

                Route route = get(lastIndex);
                if (route.score(request, response) >= requiredScore)
                    return route;
            }
        }

        // No match found
        return null;
    }

    /**
     * Returns a random route match for a given call.
     * 
     * @param request
     *                The request to score.
     * @param response
     *                The response to score.
     * @param requiredScore
     *                The minimum score required to have a match.
     * @return A random route or null.
     */
    public Route getRandom(Request request, Response response,
            float requiredScore) {
        int length = size();
        if (length > 0) {
            int j = new Random().nextInt(length);
            Route route = get(j);
            if (route.score(request, response) >= requiredScore)
                return route;

            boolean loopedAround = false;
            do {
                if (j == length && loopedAround == false) {
                    j = 0;
                    loopedAround = true;
                }
                route = get(j++);
                if (route.score(request, response) >= requiredScore)
                    return route;
            } while (j < length || !loopedAround);
        }

        // No match found
        return null;
    }

    /**
     * Removes all routes routing to a given target.
     * 
     * @param target
     *                The target Restlet to detach.
     */
    public void removeAll(Restlet target) {
        for (int i = size() - 1; i >= 0; i--) {
            if (get(i).getNext() == target)
                remove(i);
        }
    }

    /**
     * Returns a view of the portion of this list between the specified
     * fromIndex, inclusive, and toIndex, exclusive.
     * 
     * @param fromIndex
     *                The start position.
     * @param toIndex
     *                The end position (exclusive).
     * @return The sub-list.
     */
	@Override
    public RouteList subList(int fromIndex, int toIndex) {
        return new RouteList(getDelegate().subList(fromIndex, toIndex));
    }
}
