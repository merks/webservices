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
package org.eclipse.jst.ws.internal.creation.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jst.ws.internal.context.ScenarioContext;
import org.eclipse.jst.ws.internal.creation.ui.widgets.ServerWizardWidget;
import org.eclipse.jst.ws.internal.data.TypeRuntimeServer;
import org.eclipse.jst.ws.internal.plugin.WebServicePlugin;
import org.eclipse.jst.ws.internal.ui.common.UIUtils;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;



/**
 *
 */
public class ScenarioDefaultsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage 
{
	private ServerWizardWidget serverWidget_ = null;
	
	public void init(IWorkbench workbench)   { }	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	
	protected Control createContents(Composite parent) 
	{
	  UIUtils utils = new UIUtils(null);
	  
	  Composite page = utils.createComposite( parent, 1 );
	  
      serverWidget_ = new ServerWizardWidget( false );
      serverWidget_.addControls( page, new Listener()
                                       {
                                         public void handleEvent( Event evt ){}
                                       } );
	  initializeValues();
	  return page;
	}

	private void initializeValues()
	{
	  ScenarioContext context = WebServicePlugin.getInstance().getScenarioContext();

      // Set values from context.
	  TypeRuntimeServer serverIds = new TypeRuntimeServer();
	  TypeRuntimeServer clientIds = new TypeRuntimeServer();
		
	  serverIds.setTypeId( context.getWebServiceType() );
	  clientIds.setTypeId( context.getClientWebServiceType() );
		
      serverWidget_.setServiceTypeRuntimeServer( serverIds );
      serverWidget_.setClientTypeRuntimeServer( clientIds );
      serverWidget_.setGenerateProxy( new Boolean(context.getGenerateProxy()) );
      serverWidget_.setPublishService( new Boolean(context.getLaunchWebServiceExplorer()) );
      serverWidget_.setStartService( new Boolean(context.getStartWebService()) );
      serverWidget_.setTestService( new Boolean(context.getTestWebService()) );
      serverWidget_.setMonitorService(new Boolean(context.getMonitorWebService()));
      serverWidget_.internalize();
	}
	
    /**
	 * Does anything necessary because the default button has been pressed.
	*/
	protected void performDefaults()
	{
	  super.performDefaults();
	  setToDefaults();
	}
	
	private void setToDefaults()
	{
	  ScenarioContext context = WebServicePlugin.getInstance().getScenarioContext();

      // Set values from context.
	  TypeRuntimeServer serverIds = new TypeRuntimeServer();
	  TypeRuntimeServer clientIds = new TypeRuntimeServer();
		
	  serverIds.setTypeId( context.getWebServiceTypeDefault() );
	  clientIds.setTypeId( context.getClientWebServiceTypeDefault() );
		
      serverWidget_.setServiceTypeRuntimeServer( serverIds );
      serverWidget_.setClientTypeRuntimeServer( clientIds );
      serverWidget_.setGenerateProxy( new Boolean(context.getGenerateProxyDefault()) );
      serverWidget_.setPublishService( new Boolean(context.getLaunchWebServiceExplorerDefault()) );
      serverWidget_.setStartService( new Boolean(context.getStartWebServiceDefault()) );
      serverWidget_.setTestService( new Boolean(context.getTestWebServiceDefault()) );
      serverWidget_.setMonitorService(new Boolean(context.getMonitorWebServiceDefault()));
      serverWidget_.internalize();
	}

	/**
	 * Do anything necessary because the OK button has been pressed.
	 *  @return whether it is okay to close the preference page
	 */
	public boolean performOk()
	{
	  storeValues();
	  return true;
	}

	private void storeValues()
	{
	  ScenarioContext context = WebServicePlugin.getInstance().getScenarioContext();
	  
	  TypeRuntimeServer serverIds = serverWidget_.getServiceTypeRuntimeServer();
	  TypeRuntimeServer clientIds = serverWidget_.getClientTypeRuntimeServer();
	  
      context.setWebServiceType( serverIds.getTypeId() );
      context.setClientWebServiceType( clientIds.getTypeId() );
      context.setGenerateProxy( serverWidget_.getGenerateProxy().booleanValue() );
      context.setLaunchWebServiceExplorer( serverWidget_.getPublishService().booleanValue() );
      context.setStartWebService( serverWidget_.getStartService().booleanValue() );
      context.setTestWebService( serverWidget_.getTestService().booleanValue() );
      context.setMonitorWebService(serverWidget_.getMonitorService().booleanValue());
	}
	
	protected void performApply()
	{
	  performOk();
	}
}
