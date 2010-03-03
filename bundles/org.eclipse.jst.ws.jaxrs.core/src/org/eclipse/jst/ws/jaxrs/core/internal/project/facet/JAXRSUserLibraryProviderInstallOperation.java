/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 * yyyymmdd bug      Email and other contact information
 * -------- -------- -----------------------------------------------------------
 * 20100303   291954 kchong@ca.ibm.com - Keith Chong, JAX-RS: Implement JAX-RS Facet
 *******************************************************************************/
package org.eclipse.jst.ws.jaxrs.core.internal.project.facet;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperationConfig;
import org.eclipse.jst.j2ee.internal.common.classpath.WtpUserLibraryProviderInstallOperation;
import org.eclipse.jst.j2ee.project.EarUtilities;
import org.eclipse.jst.ws.jaxrs.core.internal.jaxrssharedlibraryconfig.SharedLibraryConfigurator;
import org.eclipse.jst.ws.jaxrs.core.internal.jaxrssharedlibraryconfig.SharedLibraryConfiguratorUtil;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.jst.server.core.FacetUtil;

public class JAXRSUserLibraryProviderInstallOperation extends WtpUserLibraryProviderInstallOperation
{

  public JAXRSUserLibraryProviderInstallOperation()
  {
    // TODO Auto-generated constructor stub
  }

  public void execute(final LibraryProviderOperationConfig libConfig, final IProgressMonitor monitor)

  throws CoreException

  {
    super.execute(libConfig, monitor);

    JAXRSUserLibraryProviderInstallOperationConfig cfg = (JAXRSUserLibraryProviderInstallOperationConfig) libConfig;
    IDataModel config = cfg.getModel();

    // If config is null, we aren't in project creation mode.

    IRuntime runtime = cfg.getFacetedProject().getPrimaryRuntime();
    IProject project = cfg.getFacetedProject().getProject();

    String targetRuntimeID = runtime.getName();
    if (config != null)
      targetRuntimeID = config.getStringProperty(IJAXRSFacetInstallDataModelProperties.TARGETRUNTIME);
    else
    {
      org.eclipse.wst.server.core.IRuntime iruntime = FacetUtil.getRuntime(runtime);
      if (iruntime != null)
      {
        IRuntimeType rtType = iruntime.getRuntimeType();
        if (rtType != null)
        {
          targetRuntimeID = rtType.getId();
        }
      }
    }

    IProject[] ears = EarUtilities.getReferencingEARProjects(project);
    SharedLibraryConfiguratorUtil.getInstance();
    java.util.List<SharedLibraryConfigurator> configurators = SharedLibraryConfiguratorUtil.getConfigurators();
    Iterator<SharedLibraryConfigurator> sharedLibConfiguratorIterator = configurators.iterator();

    if (cfg.isDeploy())
      return;

    while (sharedLibConfiguratorIterator.hasNext())
    {
      SharedLibraryConfigurator thisConfigurator = sharedLibConfiguratorIterator.next();
      if (targetRuntimeID.equals(thisConfigurator.getRuntimeID()))
      {
        IProject earProject = null;
        Boolean addToEar = null;
        if (config != null)
        {
          earProject = getEARProject(config);
          addToEar = getAddToEar(config);
        }
        else
        {
          if (ears.length > 0)
            earProject = ears[0];
          else 
            return;
          addToEar = new Boolean(true);
        }
        if (thisConfigurator.getIsSharedLibSupported(project, earProject, addToEar, null)) // libref.getID()
        {
          thisConfigurator.installSharedLibs(project, earProject, monitor, cfg.getLibraryNames()); // libref.getID()
          break;
        }
      }
    }
  }

  private IProject getEARProject(IDataModel config)
  {
    String projName = config.getStringProperty(IJAXRSFacetInstallDataModelProperties.EARPROJECT_NAME);
    if (projName == null || "".equals(projName))
      return null;

    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
    return project;
  }

  private boolean getAddToEar(IDataModel config)
  {
    return config.getBooleanProperty(IJAXRSFacetInstallDataModelProperties.ADD_TO_EAR);
  }

}