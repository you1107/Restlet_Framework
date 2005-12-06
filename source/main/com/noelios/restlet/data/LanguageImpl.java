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

import org.restlet.data.Language;

/**
 * Default language implementation.
 */
public class LanguageImpl extends MetadataImpl implements Language
{
   /**
    * Constructor.
    * @param name The language name;
    */
   public LanguageImpl(String name)
   {
      super(name.toLowerCase());
   }

   /**
    * Returns the main tag.
    * @return The main tag.
    */
   public String getMainTag()
   {
      int separator = getName().indexOf('-');

      if(separator == -1)
      {
         return getName();
      }
      else
      {
         return getName().substring(0, separator);
      }
   }

   /**
    * Returns the sub tag.
    * @return The sub tag.
    */
   public String getSubTag()
   {
      int separator = getName().indexOf('-');

      if(separator == -1)
      {
         return null;
      }
      else
      {
         return getName().substring(separator + 1);
      }
   }

   /**
    * Returns the description of this REST element.
    * @return The description of this REST element.
    */
   public String getDescription()
   {
      return "Language or range of languages";
   }

   /**
    * Indicates if the language is equal to a given one.
    * @param language The language to compare to.
    * @return True if the language is equal to a given one.
    */
   public boolean equals(Language language)
   {
      return getName().equalsIgnoreCase(language.getName());
   }

}
