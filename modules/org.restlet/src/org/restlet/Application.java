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

import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.service.ConnectorService;
import org.restlet.service.ConverterService;
import org.restlet.service.DecoderService;
import org.restlet.service.MetadataService;
import org.restlet.service.StatusService;
import org.restlet.service.TunnelService;
import org.restlet.util.Engine;
import org.restlet.util.Helper;

/**
 * Restlet that can be attached to one or more VirtualHosts. Applications are
 * guaranteed to receive calls with their base reference set relatively to the
 * VirtualHost that served them. This class is both a descriptor able to create
 * the root Restlet and the actual Restlet that can be attached to one or more
 * VirtualHost instances.<br>
 * <br>
 * Applications also have many useful Services associated. They are available as
 * properties that can be eventually overriden:
 * <ul>
 * <li>"connectorService" to manage client and server connectors.</li>
 * <li>"converterService" to convert message entities into higher-level
 * objects.</li>
 * <li>"decoderService" to automatically decode or decompress request entities.</li>
 * <li>"metadataService" to provide access to metadata and their associated
 * extension names.</li>
 * <li>"statusService" to provide common representations for exception status.</li>
 * <li>"tunnelService" to tunnel method names or client preferences via query
 * parameters.</li>
 * </ul>
 * <br>
 * If you need to retrieve the reference to an Application from one of its
 * contained Restlets, you can use the {@link #KEY} constant to lookup the
 * Context.attributes property.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public abstract class Application extends Restlet {
	/**
	 * Name of the attribute key containing a reference to the current
	 * application.
	 */
	public static final String KEY = "org.restlet.application";

	/** The display name. */
	private String name;

	/** The description. */
	private String description;

	/** The author(s). */
	private String author;

	/** The owner(s). */
	private String owner;

	/** The root Restlet. */
	private Restlet root;

	/** The connector service. */
	private ConnectorService connectorService;

	/** The converter service. */
	private ConverterService converterService;

	/** The decoder service. */
	private DecoderService decoderService;

	/** The local service. */
	private MetadataService metadataService;

	/** The status service. */
	private StatusService statusService;

	/** The tunnel service. */
	private TunnelService tunnelService;

	/** The helper provided by the implementation. */
	private Helper helper;

	/**
	 * Constructor. Note that usage of this constructor is not recommended as
	 * your application won't have access to the parent component context. For
	 * example, no dispatching will be possible as it requires access to the
	 * component's client connectors.
	 */
	public Application() {
		this((Context) null);
	}

	/**
	 * Constructor.
	 * 
	 * @param parentContext
	 *            The parent context. Typically the component's context.
	 */
	public Application(Context parentContext) {
		super(null);

		if (Engine.getInstance() != null) {
			this.helper = Engine.getInstance()
					.createHelper(this, parentContext);

			// Compose the logger name
			String applicationName = (getName() == null) ? Integer
					.toString(hashCode()) : getName();
			String loggerName = Application.class.getCanonicalName() + "."
					+ applicationName;

			// Create the application context
			setContext(this.helper.createContext(loggerName));
		}

		this.name = null;
		this.description = null;
		this.author = null;
		this.owner = null;
		this.root = null;
		this.connectorService = null;
		this.decoderService = null;
		this.metadataService = null;
		this.statusService = null;
		this.tunnelService = null;
	}

	/**
	 * Creates a root Restlet that will receive all incoming calls. In general,
	 * instances of Router, Filter or Handler classes will be used as initial
	 * application Restlet. The default implementation returns null by default.
	 * This method is intended to be overriden by subclasses.
	 * 
	 * @return The root Restlet.
	 */
	public abstract Restlet createRoot();

	/**
	 * Returns the author(s).
	 * 
	 * @return The author(s).
	 */
	public String getAuthor() {
		return this.author;
	}

	/**
	 * Returns the connector service.
	 * 
	 * @return The connector service.
	 */
	public ConnectorService getConnectorService() {
		if (this.connectorService == null)
			this.connectorService = new ConnectorService();
		return this.connectorService;
	}

	/**
	 * Returns the converter service.
	 * 
	 * @return The converter service.
	 */
	public ConverterService getConverterService() {
		if (this.converterService == null)
			this.converterService = new ConverterService();
		return this.converterService;
	}

	/**
	 * Returns the decoder service. This service is enabled by default.
	 * 
	 * @return The decoderservice.
	 */
	public DecoderService getDecoderService() {
		if (this.decoderService == null)
			this.decoderService = new DecoderService(true);
		return this.decoderService;
	}

	/**
	 * Returns the description.
	 * 
	 * @return The description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Returns the helper provided by the implementation.
	 * 
	 * @return The helper provided by the implementation.
	 */
	private Helper getHelper() {
		return this.helper;
	}

	/**
	 * Returns the metadata service.
	 * 
	 * @return The metadata service.
	 */
	public MetadataService getMetadataService() {
		if (this.metadataService == null)
			this.metadataService = new MetadataService();
		return this.metadataService;
	}

	/**
	 * Returns the display name.
	 * 
	 * @return The display name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the owner(s).
	 * 
	 * @return The owner(s).
	 */
	public String getOwner() {
		return this.owner;
	}

	/**
	 * Returns the root Restlet. Invokes the createRoot() method if no Restlet
	 * exists.
	 * 
	 * @return The root Restlet.
	 */
	public Restlet getRoot() {
		if (this.root == null) {
			this.root = createRoot();
		}

		return this.root;
	}

	/**
	 * Returns the status service. This service is enabled by default.
	 * 
	 * @return The status service.
	 */
	public StatusService getStatusService() {
		if (this.statusService == null)
			this.statusService = new StatusService(true);
		return this.statusService;
	}

	/**
	 * Returns the tunnel service. This service is enabled by default.
	 * 
	 * @return The tunnel service.
	 */
	public TunnelService getTunnelService() {
		if (this.tunnelService == null)
			this.tunnelService = new TunnelService(true, true, true);
		return this.tunnelService;
	}

	/**
	 * Handles a call.
	 * 
	 * @param request
	 *            The request to handle.
	 * @param response
	 *            The response to update.
	 */
	@Override
	public void handle(Request request, Response response) {
		init(request, response);
		if (getHelper() != null)
			getHelper().handle(request, response);
	}

	/**
	 * Sets the author(s).
	 * 
	 * @param author
	 *            The author(s).
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the display name.
	 * 
	 * @param name
	 *            The display name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the owner(s).
	 * 
	 * @param owner
	 *            The owner(s).
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public void start() throws Exception {
		if (isStopped()) {
			super.start();
			if (getHelper() != null)
				getHelper().start();
		}
	}

	@Override
	public void stop() throws Exception {
		if (isStarted()) {
			if (getHelper() != null)
				getHelper().stop();
			super.stop();
		}
	}

	/**
	 * Sets the connector service.
	 * 
	 * @param connectorService
	 *            The connector service.
	 */
	public void setConnectorService(ConnectorService connectorService) {
		this.connectorService = connectorService;
	}

	/**
	 * Sets the converter service.
	 * 
	 * @param converterService
	 *            The converter service.
	 */
	public void setConverterService(ConverterService converterService) {
		this.converterService = converterService;
	}

	/**
	 * Sets the decoder service.
	 * 
	 * @param decoderService
	 *            The decoder service.
	 */
	public void setDecoderService(DecoderService decoderService) {
		this.decoderService = decoderService;
	}

	/**
	 * Sets the metadata service.
	 * 
	 * @param metadataService
	 *            The metadata service.
	 */
	public void setMetadataService(MetadataService metadataService) {
		this.metadataService = metadataService;
	}

	/**
	 * Sets the status service.
	 * 
	 * @param statusService
	 *            The status service.
	 */
	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}

	/**
	 * Sets the tunnel service.
	 * 
	 * @param tunnelService
	 *            The tunnel service.
	 */
	public void setTunnelService(TunnelService tunnelService) {
		this.tunnelService = tunnelService;
	}

}
