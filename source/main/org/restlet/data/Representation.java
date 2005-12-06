/*
 * Copyright 2005 J�r�me LOUVEL
 * 
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the "License").  You may not use this file except 
 * in compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * http://www.opensource.org/licenses/cddl1.txt 
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * HEADER in each file and include the License file at 
 * http://www.opensource.org/licenses/cddl1.txt
 * If applicable, add the following below this CDDL 
 * HEADER, with the fields enclosed by brackets "[]"
 * replaced with your own identifying information: 
 * Portions Copyright [yyyy] [name of copyright owner]
 */

package org.restlet.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.restlet.Resource;

/**
 * Current or intended state of a resource. For performance purpose, it is essential that a minimal overhead
 * occurs upon initialization. Most overhead should occurs during invocation of content processing methods
 * (write, getStream, getChannel and toString)<br/><br/> "REST components perform actions on a resource by
 * using a representation to capture the current or intended state of that resource and transferring that
 * representation between components. A representation is a sequence of bytes, plus representation metadata to
 * describe those bytes. Other commonly used but less precise names for a representation include: document,
 * file, and HTTP message entity, instance, or variant." Roy T. Fielding
 * @see <a href="http://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm#sec_5_2_1_2">Source
 * dissertation</a>
 */
public interface Representation extends Data
{
   /**
    * Returns the represented resource if available.
    * @return The represented resource if available.
    */
   public Resource getResource();

   /**
    * Sets the represented resource.
    * @param resource The represented resource.
    */
   public void setResource(Resource resource);
   
   /**
    * Returns the metadata.
    * @return The metadata.
    */
   public RepresentationMetadata getMetadata();

   /**
    * Returns a readable byte channel. If it is supported by a file a read-only instance of FileChannel is
    * returned.
    * @return A readable byte channel.
    * @throws IOException
    */
   public ReadableByteChannel getChannel() throws IOException;

   /**
    * Returns a stream with the representation's content.
    * @return A stream with the representation's content.
    * @throws IOException
    */
   public InputStream getStream() throws IOException;

   /**
    * Writes the representation to a byte channel.
    * @param writableChannel A writable byte channel.
    * @throws IOException
    */
   public void write(WritableByteChannel writableChannel) throws IOException;

   /**
    * Writes the representation to a byte stream.
    * @param outputStream The output stream.
    * @throws IOException
    */
   public void write(OutputStream outputStream) throws IOException;

   /**
    * Converts the representation to a string.
    * @return The representation as a string.
    */
   public String toString();
}
