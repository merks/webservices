/*******************************************************************************
 * Copyright (c) 2002-2005 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.wsi.internal.analyzer;

import java.util.List;

import org.eclipse.wst.wsi.internal.core.ToolInfo;
import org.eclipse.wst.wsi.internal.core.WSIException;
import org.eclipse.wst.wsi.internal.core.analyzer.BasicProfileAnalyzer;
import org.eclipse.wst.wsi.internal.core.profile.validator.ProfileValidatorFactory;
import org.eclipse.wst.wsi.internal.core.report.Report;
import org.eclipse.wst.wsi.internal.core.util.MessageList;
import org.eclipse.wst.wsi.internal.document.DocumentFactoryImpl;

/**
 * WSIBasicProfileAnalyzer
 * 
 * An extension of the BasicProfileAnalyzer from the WS-I Test Tools that
 * allows calling code to get the report generated from WS-I validation.
 */
public class WSIBasicProfileAnalyzer extends BasicProfileAnalyzer
{
  /**
   * Default document factory class name.
   */
  public static final String DEF_DOCUMENT_FACTORY = "org.eclipse.wsi.test.tools.util.document.DocumentFactoryImpl";

  /**
   * Constructor for WSIBasicProfileAnalyzer.
   * @param analyzerConfig configuration information.
   * @param wsdlURI the location of the WSDL document.
   * @throws WSIException @throws WSIException if unable to create a Basic profile analyzer.
   */
  public WSIBasicProfileAnalyzer(List analyzerConfig, String wsdlURI) throws WSIException
  {
    super(analyzerConfig, wsdlURI);
  }

  /**
   * Constructor for WSIBasicProfileAnalyzer.
   * @param analyzerConfig configuration information.
   * @throws WSIException @throws WSIException if unable to create a Basic profile analyzer.
   */
  public WSIBasicProfileAnalyzer(List analyzerConfig) throws WSIException
  {
    super(analyzerConfig);
  }

  /**
   * Common initialization.
   * @param toolInfo a ToolInfo object.
   * @throws WSIException if problems during initialization.
   */
  protected void init(ToolInfo toolInfo) throws WSIException
  {
    this.toolInfo = toolInfo;
    
    // Create message list
    messageList = new MessageList(RESOURCE_BUNDLE_NAME);

    // Create profile validator factory
    factory = ProfileValidatorFactory.newInstance();

    // Create document factory
    documentFactory = new DocumentFactoryImpl();
  }

  /**
   * Method getReport.
   * 
   * Return the Report generated by the validation.
   * @return the Report generated by the validation.
   */
  public Report getReport()
  {
    return reporter.getReport();
  }

}