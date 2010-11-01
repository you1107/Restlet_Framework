/**
 * Copyright 2005-2010 Noelios Technologies.
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL 1.0 (the
 * "Licenses"). You can select the license that you prefer but you may not use
 * this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0.html
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1.php
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1.php
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.noelios.com/products/restlet-engine
 * 
 * Restlet is a registered trademark of Noelios Technologies.
 */

package org.restlet.engine.connector;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.logging.Level;

import org.restlet.Message;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.engine.http.header.HeaderConstants;
import org.restlet.engine.http.header.HeaderUtils;
import org.restlet.engine.io.BlockableChannel;
import org.restlet.engine.io.BufferState;
import org.restlet.engine.io.IoState;
import org.restlet.engine.io.ReadableChunkingChannel;
import org.restlet.engine.io.ReadableSizedChannel;
import org.restlet.engine.util.StringUtils;
import org.restlet.representation.Representation;
import org.restlet.util.SelectionRegistration;
import org.restlet.util.Series;

/**
 * A network connection way though which messages are sent. Messages can be
 * either requests or responses.
 * 
 * @author Jerome Louvel
 */
public abstract class OutboundWay extends Way {

    /**
     * Returns the protocol version.
     * 
     * @param request
     *            The request.
     * @return The protocol version.
     */
    protected static String getVersion(Request request) {
        Protocol protocol = request.getProtocol();
        String protocolVersion = protocol.getVersion();
        return protocol.getTechnicalName() + '/'
                + ((protocolVersion == null) ? "1.1" : protocolVersion);
    }

    /** The entity as a NIO readable byte channel. */
    private volatile ReadableByteChannel entityChannel;

    /** The type of the entity channel. */
    private volatile EntityType entityChannelType;

    /**
     * The entity's NIO selection key holding the link between the entity to be
     * written and the way.
     */
    private volatile SelectionKey entitySelectionKey;

    /** The header index. */
    private volatile int headerIndex;

    /**
     * Constructor.
     * 
     * @param connection
     */
    public OutboundWay(Connection<?> connection) {
        super(connection, connection.getHelper().getOutboundBufferSize());
        this.entityChannel = null;
        this.entitySelectionKey = null;
        this.headerIndex = 0;
    }

    /**
     * Adds the entity headers for the given response.
     * 
     * @param entity
     *            The entity to inspect.
     */
    protected void addEntityHeaders(Representation entity,
            Series<Parameter> headers) {
        HeaderUtils.addEntityHeaders(entity, headers);
    }

    /**
     * Adds the general headers from the {@link Message} to the {@link Series}.
     * 
     * @param headers
     *            The target headers {@link Series}.
     */
    protected void addGeneralHeaders(Series<Parameter> headers) {
        if (!getConnection().isPersistent()) {
            headers.set(HeaderConstants.HEADER_CONNECTION, "close", true);
        }

        if (shouldBeChunked(getActualMessage().getEntity())) {
            headers.add(HeaderConstants.HEADER_TRANSFER_ENCODING, "chunked");
        }

        HeaderUtils.addGeneralHeaders(getActualMessage(), headers);
    }

    /**
     * Add all the headers, including the general, the message specific and the
     * entity headers.
     * 
     * @param headers
     *            The headers to update.
     */
    protected abstract void addHeaders(Series<Parameter> headers);

    @Override
    public void clear() {
        super.clear();
        this.entityChannel = null;
        this.entitySelectionKey = null;
        this.headerIndex = 0;
    }

    /**
     * Drains the byte buffer by writing available bytes to the socket channel.
     * 
     * @throws IOException
     */
    protected void drainByteBuffer() throws IOException {
        if ((getByteBuffer().hasRemaining())
                && (getIoState() == IoState.PROCESSING)) {
            int bytesWritten = getConnection().getWritableSelectionChannel()
                    .write(getByteBuffer());

            if (getLogger().isLoggable(Level.INFO)) {
                getLogger().info("Bytes written: " + bytesWritten);
            }

            if (bytesWritten == 0) {
                // The byte buffer hasn't been written, the socket
                // channel can't write more. We needs to put the
                // byte buffer in the filling state again and
                // wait for a new NIO selection.
                setIoState(IoState.INTEREST);
            } else if (getByteBuffer().hasRemaining()) {
                // All the buffer couldn't be written. Compact the
                // remaining bytes so that filling can happen again.
                getByteBuffer().compact();
            } else if (getMessageState() == MessageState.END) {
                // Message fully written, ready for a new one
                onCompleted();
            } else {
                // The byte buffer has been fully written, but
                // the socket channel wants more, reset it.
                getByteBuffer().clear();
                setByteBufferState(BufferState.FILLING);
            }
        }
    }

    /**
     * Fills the byte buffer by writing the current message.
     * 
     * @throws IOException
     */
    protected void fillByteBuffer() throws IOException {
        while (isProcessing() && getByteBuffer().hasRemaining()
                && (getMessageState() != MessageState.END)) {
            if (getMessageState() == MessageState.BODY) {
                int result = getEntityChannel().read(getByteBuffer());

                // Detect end of entity reached
                if (result == -1) {
                    setMessageState(MessageState.END);
                }
            } else {
                // Write the start line or the headers,
                // relying on the line builder
                if (getLineBuilder().length() == 0) {
                    // A new line can be written in the builder
                    writeLine();
                }

                if (getLineBuilder().length() > 0) {
                    // We can fill the byte buffer with the
                    // remaining line builder
                    int remaining = getByteBuffer().remaining();

                    if (remaining >= getLineBuilder().length()) {
                        // Put the whole builder line in the buffer
                        getByteBuffer().put(
                                StringUtils.getLatin1Bytes(getLineBuilder()
                                        .toString()));
                        clearLineBuilder();
                    } else {
                        // Put the maximum number of characters
                        // into the byte buffer
                        getByteBuffer().put(
                                StringUtils.getLatin1Bytes(getLineBuilder()
                                        .substring(0, remaining)));
                        getLineBuilder().delete(0, remaining);
                    }
                }
            }
        }

        if (getByteBuffer().position() > 0) {
            // After filling the byte buffer, we can now flip it
            // and start draining it.
            getByteBuffer().flip();
            setByteBufferState(BufferState.DRAINING);
        }
    }

    /**
     * Returns the entity as a NIO readable byte channel.
     * 
     * @return The entity as a NIO readable byte channel.
     */
    public ReadableByteChannel getEntityChannel() {
        return entityChannel;
    }

    /**
     * Returns the type of the entity channel.
     * 
     * @return The type of the entity channel.
     */
    protected EntityType getEntityChannelType() {
        return entityChannelType;
    }

    /**
     * Returns the entity as a NIO file channel.
     * 
     * @return The entity as a NIO file channel.
     */
    public FileChannel getEntityFileChannel() {
        return (FileChannel) getEntityChannel();
    }

    /**
     * Registers interest of this way for socket NIO operations.
     * 
     * @return The operations of interest.
     */
    public int getEntityInterestOps() {
        int result = 0;

        if (getIoState() == IoState.INTEREST) {
            result = SelectionKey.OP_READ;
        }

        return result;
    }

    /**
     * Returns the entity as a NIO non-blocking selectable channel.
     * 
     * @return The entity as a NIO non-blocking selectable channel.
     */
    public SelectableChannel getEntitySelectableChannel() {
        return (SelectableChannel) getEntityChannel();
    }

    /**
     * Returns the entity's NIO selection key holding the link between the
     * entity to be written and the way.
     * 
     * @return The entity's NIO selection key.
     */
    public SelectionKey getEntitySelectionKey() {
        return entitySelectionKey;
    }

    /**
     * Returns the header index.
     * 
     * @return The header index.
     */
    protected int getHeaderIndex() {
        return headerIndex;
    }

    @Override
    public int getSocketInterestOps() {
        int result = 0;

        if (getIoState() == IoState.INTEREST) {
            result = SelectionKey.OP_WRITE;
        }

        return result;
    }

    @Override
    public void onCompleted() {
        setHeaders(null);
        setHeaderIndex(0);

        if (getLogger().isLoggable(Level.FINE)) {
            getLogger().fine("Outbound message sent");
        }

        super.onCompleted();
    }

    @Override
    public void onSelected(SelectionRegistration registration) {
        try {
            Response message = getMessage();

            if (message != null) {
                super.onSelected(registration);

                while (isProcessing()) {
                    if (getByteBufferState() == BufferState.FILLING) {
                        // Write the message or part of it in the byte buffer
                        fillByteBuffer();
                    } else if (getByteBufferState() == BufferState.DRAINING) {
                        // Write the byte buffer or part of it to the socket
                        drainByteBuffer();

                        if (getMessageState() == MessageState.IDLE) {
                            // Message fully sent, check if another is ready
                            updateState();
                        }
                    }
                }
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING,
                    "Error while writing an HTTP message", e);
            getLogger().log(Level.INFO, "Error while writing an HTTP message",
                    e);
        }
    }

    /**
     * Sets the entity as a NIO readable byte channel.
     * 
     * @param entityChannel
     *            The entity as a NIO readable byte channel.
     */
    public void setEntityChannel(ReadableByteChannel entityChannel) {
        this.entityChannel = entityChannel;
    }

    /**
     * Sets the type of the entity channel.
     * 
     * @param entityChannelType
     *            The type of the entity channel.
     */
    protected void setEntityChannelType(EntityType entityChannelType) {
        this.entityChannelType = entityChannelType;
    }

    /**
     * Sets the entity's NIO selection key holding the link between the entity
     * to be written and the way.
     * 
     * @param entityKey
     *            The entity's NIO selection key.
     */
    public void setEntitySelectionKey(SelectionKey entityKey) {
        this.entitySelectionKey = entityKey;
    }

    /**
     * Sets the header index.
     * 
     * @param headerIndex
     *            The header index.
     */
    protected void setHeaderIndex(int headerIndex) {
        this.headerIndex = headerIndex;
    }

    /**
     * Indicates if the entity should be chunked because its length is unknown.
     * 
     * @param entity
     *            The entity to analyze.
     * @return True if the entity should be chunked.
     */
    protected boolean shouldBeChunked(Representation entity) {
        return (entity != null)
                && (entity.getSize() == Representation.UNKNOWN_SIZE);
    }

    @Override
    public void updateState() {
        super.updateState();

        // Update the IO state if necessary
        if ((getIoState() == IoState.IDLE) && !getMessages().isEmpty()) {
            if (getMessage() == null) {
                setIoState(IoState.INTEREST);
                setMessage(getMessages().peek());
            }
        }
    }

    /**
     * Write a new line into the line builder.
     * 
     * @throws IOException
     */
    protected void writeLine() throws IOException {
        switch (getMessageState()) {
        case START:
            writeStartLine();
            setMessageState(MessageState.HEADERS);
            break;

        case HEADERS:
            if (getHeaders() == null) {
                setHeaders(new Form());
                setHeaderIndex(0);
                addHeaders(getHeaders());
            }

            if (getHeaderIndex() < getHeaders().size()) {
                // Write header
                Parameter header = getHeaders().get(getHeaderIndex());
                getLineBuilder().append(header.getName());
                getLineBuilder().append(": ");
                getLineBuilder().append(header.getValue());
                getLineBuilder().append('\r'); // CR
                getLineBuilder().append('\n'); // LF

                // Move to the next header
                setHeaderIndex(getHeaderIndex() + 1);
            } else {
                // Write the end of the headers section
                getLineBuilder().append('\r'); // CR
                getLineBuilder().append('\n'); // LF

                // Prepare entity writing if available
                if (getActualMessage().isEntityAvailable()) {
                    setMessageState(MessageState.BODY);
                    ReadableByteChannel rbc = getActualMessage().getEntity()
                            .getChannel();

                    if (rbc instanceof FileChannel) {
                        setEntityChannelType(EntityType.TRANSFERABLE);
                    } else if (rbc instanceof BlockableChannel) {
                        BlockableChannel bc = (BlockableChannel) rbc;

                        if (bc.isBlocking()) {
                            setEntityChannelType(EntityType.BLOCKING);
                        } else {
                            setEntityChannelType(EntityType.NON_BLOCKING);
                        }
                    } else if (rbc instanceof SelectableChannel) {
                        SelectableChannel sc = (SelectableChannel) rbc;

                        if (sc.isBlocking()) {
                            setEntityChannelType(EntityType.BLOCKING);
                        } else {
                            setEntityChannelType(EntityType.NON_BLOCKING);
                        }
                    } else {
                        setEntityChannelType(EntityType.BLOCKING);
                    }

                    if (getActualMessage().getEntity().getSize() == Representation.UNKNOWN_SIZE) {
                        setEntityChannel(new ReadableChunkingChannel(rbc,
                                getByteBuffer().capacity()));
                    } else {
                        setEntityChannel(new ReadableSizedChannel(rbc,
                                getActualMessage().getEntity().getSize()));
                    }

                } else {
                    setMessageState(MessageState.END);
                }
            }

            break;
        }
    }

    /**
     * Writes the start line of the current outbound message.
     * 
     * @throws IOException
     */
    protected abstract void writeStartLine() throws IOException;

}
