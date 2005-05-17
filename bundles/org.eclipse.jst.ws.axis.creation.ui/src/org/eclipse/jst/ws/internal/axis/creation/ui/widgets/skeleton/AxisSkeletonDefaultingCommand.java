/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.ws.internal.axis.creation.ui.widgets.skeleton;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jst.ws.internal.axis.consumption.core.common.JavaWSDLParameter;
import org.eclipse.jst.ws.internal.consumption.common.WSDLParserFactory;
import org.eclipse.wst.command.internal.provisional.env.core.SimpleCommand;
import org.eclipse.wst.command.internal.provisional.env.core.common.Environment;
import org.eclipse.wst.command.internal.provisional.env.core.common.SimpleStatus;
import org.eclipse.wst.command.internal.provisional.env.core.common.Status;
import org.eclipse.wst.ws.internal.parser.wsil.WebServicesParser;


public class AxisSkeletonDefaultingCommand extends SimpleCommand
{
  private IStructuredSelection initialSelection;
  private IStructuredSelection objectSelection;
  private WebServicesParser webServicesParser;
  private String wsdlURI_;
  private JavaWSDLParameter javaWSDLParam;
  
  public Status execute(Environment env){
    
    javaWSDLParam = new JavaWSDLParameter();
    javaWSDLParam.setServerSide(JavaWSDLParameter.SERVER_SIDE_BEAN);
    javaWSDLParam.setSkeletonDeploy(true);
    javaWSDLParam.setMetaInfOnly(false);
    return new SimpleStatus("");
    
  }
  
  public void setInitialSelection(IStructuredSelection initialSelection)
  {
    this.initialSelection = initialSelection;
  }

  public void setObjectSelection(IStructuredSelection objectSelection)
  {
    this.objectSelection = objectSelection;
  }
  
  public void setWebServicesParser(WebServicesParser webServicesParser)
  {
    this.webServicesParser = webServicesParser;
  }
  
  public WebServicesParser getWebServicesParser()
  {
    if (webServicesParser == null)
		webServicesParser = WSDLParserFactory.getWSDLParser();
    return webServicesParser;
  }

  public String getWebServiceURI()
  {
    if (initialSelection != null && !initialSelection.isEmpty())
    {
      Object firstElement = initialSelection.getFirstElement();
      if (firstElement instanceof IFile)
      {
        IFile ifile = (IFile)firstElement;
        String fileExtension = ifile.getFileExtension();
        if (fileExtension.equals("wsdl") ||
            fileExtension.equals("wsil") ||
            fileExtension.equals("html"))
        {
          return ifile.getFullPath().toString();  
        }
      }
    }
    return "";
  }
  
  public String getWsdlURI()
  {
//    if (objectSelection != null && !objectSelection.isEmpty())
//    {
//      Object object = objectSelection.getFirstElement();
//      if (object instanceof IResource)
//        return ((IResource)object).getLocation().toString();
//      else
//        return object.toString();
//    }
//    return "";
	  return wsdlURI_;
  }
  
  public void setWsdlURI(String wsdlURI)
  {
	  wsdlURI_ = wsdlURI;
	  
  }
  
  public boolean getGenWSIL()
  {
    return false;
  }
  
  public String getWsilURI()
  {
    String wsURI = getWsdlURI();
    int index = wsURI.lastIndexOf('.');
    if (index != -1)
    {
      StringBuffer sb = new StringBuffer(wsURI.substring(0, index));
      sb.append(".wsil");
      return sb.toString();
    }
    return "";
  }
  
  public JavaWSDLParameter getJavaWSDLParam()
  {
    return javaWSDLParam;
  }

  public String getHttpBasicAuthPassword()
  {
    return getWebServicesParser().getHTTPBasicAuthPassword();
  }

  public String getHttpBasicAuthUsername()
  {
    return getWebServicesParser().getHTTPBasicAuthUsername();
  }
  
}