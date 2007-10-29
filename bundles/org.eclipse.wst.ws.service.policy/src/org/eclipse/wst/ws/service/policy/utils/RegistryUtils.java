/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 * yyyymmdd bug      Email and other contact information
 * -------- -------- -----------------------------------------------------------
 * 20071024   196997 pmoogk@ca.ibm.com - Peter Moogk
 * 20071024   196997 pmoogk@ca.ibm.com - Peter Moogk
 *******************************************************************************/
package org.eclipse.wst.ws.service.policy.utils;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.ws.service.policy.Descriptor;
import org.eclipse.wst.ws.service.policy.ServicePolicyActivator;

public class RegistryUtils
{
  public static Descriptor loadDescriptor( IConfigurationElement element )
  {
    Descriptor descriptor = new Descriptor();
    
    String shortName   = getAttribute( element, "shortname" ); //$NON-NLS-1$
    String longName    = getAttribute( element, "longname" ); //$NON-NLS-1$
    String description = getAttribute( element, "description" ); //$NON-NLS-1$
    String iconPath    = getAttribute( element, "iconpath" ); //$NON-NLS-1$
    String contextHelp = getAttribute( element, "contexthelpid" ); //$NON-NLS-1$
    
    if( shortName == null && longName == null )
    {
      ServicePolicyActivator.logError( "Short name or Long name missing from service policy descriptor.", null ); //$NON-NLS-1$
    }
    else if( shortName == null )
    {
      shortName = longName;
    }
    else if( longName == null )
    {
      longName = shortName;
    }
    
    descriptor.setDescription( description );
    descriptor.setShortName( shortName );
    descriptor.setLongName( longName );
    descriptor.setContextHelpId( contextHelp );
    
    if( iconPath != null )
    {
      descriptor.setIconPath( iconPath );
      descriptor.setIconBundleId( element.getContributor().getName() );
    }
    
    return descriptor;
  }
  
  /**
   * This method takes in a lower case attribute name.  It will then return
   * the same attribute name that includes potentially uppercase characters.
   * This method allows clients to specify attribute names in a case insensitive
   * fashion.
   *  
   * @param element
   * @param attribute
   * @return
   */
  public static String getAttributeName( IConfigurationElement element, String attribute )
  {
    String[] names  = element.getAttributeNames();
    String   result = null;
    
    for( String name : names )
    {
      if( name.toLowerCase().equals( attribute ) )
      {
        result = name;
        break;
      }
    }
        
    return result;
  }
  
  public static String getAttribute( IConfigurationElement element, String attribute )
  {
    String[] names = element.getAttributeNames();
    String   value = null;
    
    for( String name : names )
    {
      if( name.toLowerCase().equals( attribute ) )
      {
        value = element.getAttribute( name );
        break;
      }
    }
        
    return value;
  }
}