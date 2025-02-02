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

package org.restlet.ext.spring;

import java.util.ArrayList;
import java.util.List;

import org.restlet.Context;
import org.restlet.resource.Representation;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Spring application context based on a Restlet context. Here is an example
 * illustrating the various ways to use this class:
 * 
 * <pre>
 * SpringContext springContext = new SpringContext(getContext());
 * springContext.getPropertyConfigRefs().add(&quot;war://config/database.properties&quot;);
 * springContext.getXmlConfigRefs().add(&quot;war://config/applicationContext.xml&quot;);
 * springContext.getXmlConfigRefs().add(
 *         &quot;file:///C/myApp/config/applicationContext.xml&quot;);
 * springContext.getXmlConfigRefs().add(
 *         &quot;clap://thread/config/applicationContext.xml&quot;);
 * </pre>
 * 
 * @author Jerome Louvel (contact@noelios.com)</a>
 */
public class SpringContext extends GenericApplicationContext {
    /** The parent Restlet context. */
    private Context restletContext;

    /**
     * The modifiable list of configuration URIs for beans definitions via
     * property representations.
     */
    private List<String> propertyConfigRefs;

    /**
     * The modifiable list of configuration URIs for beans definitions via XML
     * representations.
     */
    private List<String> xmlConfigRefs;

    /** Indicates if the context has been already loaded. */
    private boolean loaded;

    /**
     * Constructor.
     * 
     * @param restletContext
     *                The parent Restlet context.
     */
    public SpringContext(Context restletContext) {
        this.restletContext = restletContext;
        this.propertyConfigRefs = null;
        this.xmlConfigRefs = null;
        this.loaded = false;
    }

    /**
     * Returns the parent Restlet context.
     * 
     * @return The parent Restlet context.
     */
    public Context getRestletContext() {
        return this.restletContext;
    }

    /**
     * Returns the modifiable list of configuration URIs for beans definitions
     * via property representations.
     * 
     * @return The modifiable list of configuration URIs.
     */
    public List<String> getPropertyConfigRefs() {
        if (this.propertyConfigRefs == null)
            this.propertyConfigRefs = new ArrayList<String>();
        return this.propertyConfigRefs;
    }

    /**
     * Returns the modifiable list of configuration URIs for beans definitions
     * via XML representations.
     * 
     * @return The modifiable list of configuration URIs.
     */
    public List<String> getXmlConfigRefs() {
        if (this.xmlConfigRefs == null)
            this.xmlConfigRefs = new ArrayList<String>();
        return this.xmlConfigRefs;
    }

    @Override
    public void refresh() {
        // If this context hasn't been loaded yet, read all the configurations
        // registered
        if (!this.loaded) {
            Representation config = null;

            // First, read the bean definitions from properties representations
            PropertiesBeanDefinitionReader propReader = null;
            for (String ref : getPropertyConfigRefs()) {
                config = getRestletContext().getDispatcher().get(ref)
                        .getEntity();

                if (config != null) {
                    propReader = new PropertiesBeanDefinitionReader(this);
                    propReader.loadBeanDefinitions(new SpringResource(config));
                }
            }

            // Then, read the bean definitions from XML representations
            XmlBeanDefinitionReader xmlReader = null;
            for (String ref : getXmlConfigRefs()) {
                config = getRestletContext().getDispatcher().get(ref)
                        .getEntity();

                if (config != null) {
                    xmlReader = new XmlBeanDefinitionReader(this);
                    xmlReader
                            .setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
                    xmlReader.loadBeanDefinitions(new SpringResource(config));
                }
            }
        }

        // Now load or refresh
        super.refresh();
    }

}
