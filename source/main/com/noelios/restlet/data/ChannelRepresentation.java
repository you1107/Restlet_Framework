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

package com.noelios.restlet.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import org.restlet.data.AbstractRepresentation;
import org.restlet.data.MediaType;

import com.noelios.restlet.util.ByteUtils;

/**
 * Representation based on a byte channel.
 */
public abstract class ChannelRepresentation extends AbstractRepresentation
{
   /**
    * Constructor.
    * @param mediaType The representation's media type.
    */
   public ChannelRepresentation(MediaType mediaType)
   {
      super(mediaType);
   }

   /**
    * Writes the representation to a byte stream.
    * @param outputStream The output stream.
    */
   public void write(OutputStream outputStream) throws IOException
   {
      write(Channels.newChannel(outputStream));
   }

   /**
    * Writes the representation to a byte channel.
    * @param writableChannel A writable byte channel.
    */
   public void write(WritableByteChannel writableChannel) throws IOException
   {
      ByteUtils.write(getChannel(), writableChannel);
   }

   /**
    * Returns a stream with the representation's content.
    * @return A stream with the representation's content.
    */
   public InputStream getStream() throws IOException
   {
      return Channels.newInputStream(getChannel());
   }

   /**
    * Converts the representation to a string.
    * @return The representation as a string.
    */
   public String toString()
   {
      String result = null;

      try
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         write(baos);
         result = baos.toString();
      }
      catch(Exception ioe)
      {
         // Return an empty string
      }

      return result;
   }

}
