/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jst.ws.internal.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jst.j2ee.applicationclient.componentcore.util.AppClientArtifactEdit;
import org.eclipse.jst.j2ee.componentcore.util.EARArtifactEdit;
import org.eclipse.jst.j2ee.ejb.EJBJar;
import org.eclipse.jst.j2ee.ejb.EnterpriseBean;
import org.eclipse.jst.j2ee.ejb.Session;
import org.eclipse.jst.j2ee.ejb.SessionType;
import org.eclipse.jst.j2ee.ejb.componentcore.util.EJBArtifactEdit;
import org.eclipse.jst.j2ee.internal.J2EEVersionConstants;
import org.eclipse.jst.j2ee.internal.earcreation.AddModuleToEARProjectCommand;
import org.eclipse.jst.j2ee.internal.earcreation.EARNatureRuntime;
import org.eclipse.jst.j2ee.internal.earcreation.IEARNatureConstants;
import org.eclipse.jst.j2ee.internal.ejb.project.EJBNatureRuntime;
import org.eclipse.jst.j2ee.internal.project.IEJBNatureConstants;
import org.eclipse.jst.j2ee.internal.project.J2EENature;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.jst.j2ee.internal.web.operations.J2EEWebNatureRuntime;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.wst.command.env.common.FileResourceUtils;
import org.eclipse.wst.command.env.core.common.Log;
import org.eclipse.wst.command.env.eclipse.EclipseLog;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
/**
 * This class contains some useful J2EE utilities.
 * 
 * @author Peter Moogk
 * @date August 23, 2001
 */
public final class J2EEUtils {
	private static final String webProjectNature = IModuleConstants.JST_WEB_MODULE; // IWebNatureConstants.J2EE_NATURE_ID;
	private static final String ejbProjectNature = IModuleConstants.JST_EJB_MODULE; // IEJBNatureConstants.NATURE_ID;
	private static final String earProjectNature = IModuleConstants.JST_EAR_MODULE; // IEARNatureConstants.NATURE_ID;

	
	/**
	 * Returns an IVirtualComponent
	 * @param project
	 * @param componentName
	 * @return
	 */
	public static IVirtualComponent getIVirtualComponent(IProject project, String componentName){
		return ComponentCore.createComponent(project, componentName);
	}
	
	/**
	 * Returns the J2EE version id (defined in J2EEVersionConstants) of the
	 * project. If the project does not have a J2EENature, -1 is returned.
	 * 
	 * @param p
	 * @return the J2EE version id (defined in J2EEVersionConstants), -1 if p
	 *         does not have a J2EENature.
	 * 
	 * @deprecated this method only returns the first module's j2ee version
	 * 				use getJ2EEVersion(IProject, String)
	 */
	public static int getJ2EEVersion(IProject p) {
		int j2eeVer = -1;
		StructureEdit mc = null;
		try {
			mc = StructureEdit.getStructureEditForRead(p);
			WorkbenchComponent[] wbcs = mc.getWorkbenchModules();
			if (wbcs.length!=0) {
//				j2eeVer = getWebModuleJ2EEVersion(wbcs[0]);
			}
		}
		catch (Exception e){
			//handle exception
		}
		finally{
			if (mc!=null)
				mc.dispose();
		}
		return j2eeVer;
	}

	/**
	 * TODO: Implement this method
	 * @param p
	 * @param module
	 * @return
	 */
	public static int getJ2EEVersion(IProject p, String componentName){
		int j2eeVer = -1;
		StructureEdit mc = null;
		try {
			mc = StructureEdit.getStructureEditForRead(p);
			WorkbenchComponent[] wbcs = mc.getWorkbenchModules();
			
			for (int i=0;i<wbcs.length;i++){
				if (wbcs[i].getName().equals(componentName)){
					j2eeVer = getComponentJ2EEVersion(p, wbcs[i]);
				}
			}
			
		}
		catch (Exception e){
			//handle exception
		}
		finally{
			if (mc!=null)
				mc.dispose();
		}
		return j2eeVer;	
	}
	
	/**
	 * Returns the J2EEVersion of the component identified by project and component name
	 * @param project
	 * @param wbc
	 * @return int version if applicable, otherwise returns -1
	 */
	private static int getComponentJ2EEVersion(IProject project, WorkbenchComponent wbc){
		int j2eeVer = -1;
		//check type
		if (wbc!=null) {
			if (isWebComponent(project, wbc.getName()))
				j2eeVer = getWebComponentJ2EEVersion(wbc);
			if (isAppClientComponent(project, wbc.getName()))
				j2eeVer = getAppClientComponentJ2EEVersion(wbc);
			if (isEJBComponent(project, wbc.getName()))
				j2eeVer = getEJBComponentJ2EEVersion(wbc);
			if (isEARComponent(project, wbc.getName()))
				j2eeVer = getEARComponentJ2EEVersion(wbc);
					
			// TODO: isJavaComponent
			
		}
		return j2eeVer;
	}
	
	/**
	 * Return's the EAR module's J2EEVersion
	 * @param wbc
	 * @return
	 */
	private static int getEARComponentJ2EEVersion(WorkbenchComponent wbc){
		EARArtifactEdit edit = null;
		int nVersion = 12;
		try {
			edit = EARArtifactEdit.getEARArtifactEditForRead(wbc);
			if (edit != null) {
				nVersion = edit.getJ2EEVersion();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (edit != null)
				edit.dispose();
		}
		return nVersion;		
	}
	
	
	/**
	 * 
	 * @param p
	 * @return
	 * @deprecated
	 */
	public static String getJ2EEVersionAsString(IProject p){
		int j2eeVer = getJ2EEVersion(p);
		if (j2eeVer!=-1){
			return String.valueOf(j2eeVer);
		}
		else 
			return null;
	}
	
	/**
	 * TODO
	 * @param p
	 * @param compName
	 * @return
	 */
	public static String getJ2EEVersionAsString(IProject p, String compName){
		int j2eeVer = getJ2EEVersion(p);
		if (j2eeVer!=-1){
			return String.valueOf(j2eeVer);
		}
		else 
			return null;
	}
	
	/**
	 * Returns the Web Module's J2EE version
	 * @param wbModule
	 * @return the J2EE version id
	 */
	private static int getWebComponentJ2EEVersion(WorkbenchComponent wbModule) {
		WebArtifactEdit webEdit = null;
		int nVersion = 12;
		try {
			webEdit = WebArtifactEdit.getWebArtifactEditForRead(wbModule);
			if (webEdit != null) {
				nVersion = webEdit.getJ2EEVersion();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (webEdit != null)
				webEdit.dispose();
		}
		return nVersion;
	}

	/**
	 * Returns Application client's J2EE version
	 * @param wbc
	 * @return
	 */
	private static int getAppClientComponentJ2EEVersion(WorkbenchComponent wbc){
		AppClientArtifactEdit edit = null;
		int nVersion = 12;
		try {
			edit = AppClientArtifactEdit.getAppClientArtifactEditForRead(wbc);
			if (edit != null) {
				nVersion = edit.getJ2EEVersion();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (edit != null)
				edit.dispose();
		}
		return nVersion;		
	}
	
	/**
	 * Returns EJB component's J2EE version
	 * @param wbc
	 * @return
	 */
	private static int getEJBComponentJ2EEVersion(WorkbenchComponent wbc){
		EJBArtifactEdit edit = null;
		int nVersion = 12;
		try {
			edit = EJBArtifactEdit.getEJBArtifactEditForRead(wbc);
			if (edit != null) {
				nVersion = edit.getJ2EEVersion();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (edit != null)
				edit.dispose();
		}
		return nVersion;			
	}
	
	/**
	 * This method returns all of the EAR projects that reference the specified
	 * project.
	 * 
	 * @deprecated
	 */
	public static EARNatureRuntime[] getEARProjects(IProject project) {
		
		EARNatureRuntime[] ears = J2EEProjectUtilities.getReferencingEARProjects(project);
		return ears;
	}
	
	/**
	 * Returns the EAR components in a Project
	 * @param project
	 * @return empty if no EAR components, null if no components at all
	 */
	public static String[] getEARComponents(IProject project){
		
		//get all components in the project
		StructureEdit mc = null;
		String[] earComponents = new String[0];
		try {
			mc = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent[] wbcs = mc.getWorkbenchModules();
			earComponents = new String[wbcs.length];
			for (int i=0;i<wbcs.length;i++){
				if (isEARComponent(project, wbcs[i].getName()))
					earComponents[i] = wbcs[i].getName();
			}
		}
		catch (Exception e){
			//handle exception
		}
		finally{
			if (mc!=null)
				mc.dispose();
		}		
		return earComponents;
	}
	
	/**
	 * Returns Web components names in a project
	 * @param project
	 * @return empty array if no web components
	 */
	public static String[] getWebComponents(IProject project){
		
		//get all components in the project
		StructureEdit mc = null;
		String[] components = new String[0];
		try {
			mc = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent[] wbcs = mc.getWorkbenchModules();
			components = new String[wbcs.length];
			for (int i=0;i<wbcs.length;i++){
				if (isWebComponent(project, wbcs[i].getName()))
					components[i] = wbcs[i].getName();
			}
			
		}
		catch (Exception e){
			//handle exception
		}
		finally{
			if (mc!=null)
				mc.dispose();
		}		
		return components;
	} 
	
	/**
	 * True if there exists a underlying resource backing up the component and project 
	 * @param projectName
	 * @param componentName
	 * @return
	 */
	public static boolean exists(String projectName, String componentName){
		IProject project = null;
		if (projectName!=null && projectName.length() > 0 )
		  project = FileResourceUtils.getWorkspaceRoot().getProject(projectName);
		else 
			return false;
		
		return exists(project, componentName);
	}
	
	/**
	 * True if there exists a underlying resource backing up the component and project
	 * @param project
	 * @param componentName
	 * @return
	 */
	private static boolean exists(IProject project, String componentName){
		if (project!=null && 
				componentName!=null && 
				componentName.length() > 0) {
			IVirtualComponent vc = ComponentCore.createComponent(project, componentName);
			return vc.exists();
		}
		else 
			return false;
		
	}
	
	/**
	 * 
	 * @param project
	 * @return
	 * 
	 * @deprecated  --  use getEARProjects(IProject)
	 */
//	public static String[] getEARProjectNamesForWebProject(IProject project) {
//		Vector EARNames = new Vector();
//		if (project != null) {
//			EARNatureRuntime[] ears = getEARProjects(project);
//			for (int i = 0; i < ears.length; i++) {
//				EARNames.add(ears[i].getProject().getName());
//			}
//		}
//		return EARNames.isEmpty() ? null : (String[]) EARNames
//				.toArray(new String[0]);
//	}

	/**
	 * This method returns all of the EAR projects that reference the specified
	 * ejb project.
	 * 
	 * @deprecated  - use getEARProjects(IProject)
	 */
	public static EARNatureRuntime[] getEJBEARProjects(IProject project) {
		EARNatureRuntime[] ears = J2EEProjectUtilities.getReferencingEARProjects(project);
		return ears;
	}

	/**
	 * 
	 * @param project
	 * @return
	 * @deprecated use getEARProjectNames (not used; to be deleted)
	 */
	public static String[] getEARProjectNamesForEJBProject(IProject project) {
		Vector EARNames = new Vector();
		if (project != null) {
			EARNatureRuntime[] ears = getEJBEARProjects(project);
			for (int i = 0; i < ears.length; i++) {
				EARNames.add(ears[i].getProject().getName());
			}
		}
		return EARNames.isEmpty() ? null : (String[]) EARNames
				.toArray(new String[0]);
	}

	/**
	 * 
	 * @param project
	 * @return
	 * @deprecated not used; to be deleted
	 */
	public static EARNatureRuntime[] getAppClientEARProjects(IProject project) {
		EARNatureRuntime[] ears = J2EEProjectUtilities.getReferencingEARProjects(project);
		return ears;
	}

	/**
	 * Returns the EAR nature runtime from an EAR project
	 * 
	 * @param IProject
	 *            the EAR project
	 * @return EARNatureRuntime of the project
	 * 
	 * @deprecated not used; to be deleted
	 */
	public static EARNatureRuntime getEARNatureRuntimeFromProject(
			IProject project) {
		return EARNatureRuntime.getRuntime(project);
	}

	/**
	 * This method returns a list of EAR names that are referenced by the
	 * specified web project.
	 * 
	 * @deprecated not used; to be deleted
	 */
	public static String[] getEARNames(IProject project) {
		EARNatureRuntime[] ears = getEARProjects(project);
		String[] earNames = new String[ears == null ? 0 : ears.length];

		for (int index = 0; index < earNames.length; index++) {
			earNames[index] = ears[index].getProject().getName();
		}

		return earNames;
	}

	/**
	 * Find all EJB projects for a particular EAR Nature.
	 * 
	 * @return a vector of EJBNatureRuntimes.
	 * @deprecated use getEJBProjectsFromEAR(IProject, String)
	 */
	public static Vector getEJBProjects(EARNatureRuntime ear) {
		Vector ejbs = new Vector();
		Iterator earProjects = ear.getModuleProjects().values().iterator();

		while (earProjects.hasNext()) {
			Object object = earProjects.next();

			if (object != null) {
				J2EENature j2eeNature = (J2EENature) object;

				if (j2eeNature instanceof EJBNatureRuntime) {
					ejbs.add(j2eeNature);
				}
			}
		}

		return ejbs;
	}
	
	/**
	 * TODO: implement
	 * @param project
	 * @param componentName
	 * @return
	 */
	public static IProject[] getEJBProjectsFromEAR(IProject project, String componentName){
		
		return null;
	}

	/**
	 * Find all Web projects for a particular EAR Nature.
	 * 
	 * @return a vector of J2EEWebNatureRuntimes.
	 * 
	 * @deprecated use getWebProjectsFromEAR(IProject, String)
	 * 			!! not used; to be deleted
	 */
	public static Vector getWebProjects(EARNatureRuntime ear) {
		Vector webProjects = new Vector();
		Iterator earProjects = ear.getModuleProjects().values().iterator();

		while (earProjects.hasNext()) {
			Object object = earProjects.next();

			if (object != null) {
				J2EENature j2eeNature = (J2EENature) object;

				if (j2eeNature instanceof J2EEWebNatureRuntime) {
					webProjects.add(j2eeNature);
				}
			}
		}

		return webProjects;
	}


	/**
	 * @return returns a list of projects names for a given ear.
	 * @deprecated use getEJBProjectFromEAR(IProject, String)
	 * 	!! not used; to be deleted
	 */
	public static String[] getEJBProjectNames(EARNatureRuntime ear) {
		Vector ejbNatures = getEJBProjects(ear);
		String[] ejbProjectNames = new String[ejbNatures.size()];

		for (int index = 0; index < ejbProjectNames.length; index++) {
			ejbProjectNames[index] = ((EJBNatureRuntime) (ejbNatures
					.elementAt(index))).getProject().getName();
		}

		return ejbProjectNames;
	}


	
	/**
	 * 
	 * @param jar
	 * @return  Vector of bean String names.
	 */
	public static Vector getBeanNames(EJBJar jar) {
		// We currently only support Stateless session beans.
		// List cmpBeans = jar.getBeanManagedBeans();
		// List bmpBeans = jar.getContainerManagedBeans();
		List sessionBeans = jar.getSessionBeans();

		Vector names = new Vector();

		// getBeanNames( names, cmpBeans );
		// getBeanNames( names, bmpBeans );
		getBeanNames(names, sessionBeans);

		return names;
	}

	/**
	 * @param names
	 *            specifies that vector of strings that will be used to add bean
	 *            names to.
	 * @param beans
	 *            specifies a list of beans.
	 */
	private static void getBeanNames(Vector names, List beans) {
		Iterator iterator = beans.iterator();

		while (iterator.hasNext()) {
			EnterpriseBean bean = (EnterpriseBean) (iterator.next());

			if (bean.isSession()) {
				Session sessionBean = (Session) bean;

				if (sessionBean.getSessionType().getValue() == SessionType.STATELESS) {
					names.add(bean.getName());
				}
			}
		}
	}

	/**
	 * Uses emg ProjectUtilities to get the project
	 * @param ejb eObject
	 * @return IProject
	 */
	public static IProject getProjectFromEJB(EnterpriseBean ejb) {
		return ProjectUtilities.getProject(ejb);
	}

	/**
	 * Get an array of IProject, given a Vector of J2EENature's
	 * 
	 * @deprecated pls request a new method if necessary
	 */
	public static IProject[] getIProjectsFromJ2EENatures(Vector j2eenatureVector) {
		IProject[] projects = new IProject[j2eenatureVector == null
				? 0
				: j2eenatureVector.size()];
		Enumeration e = j2eenatureVector.elements();
		int i = 0;
		while (e.hasMoreElements()) {
			J2EENature nature = (J2EENature) e.nextElement();
			IProject project = nature.getProject();
			projects[i] = project;
			i++;
		}

		return projects;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String[] getAllEARComponents() {
		Vector v = new Vector();
		IProject[] projects = ResourceUtils.getWorkspaceRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			try {
				if (ResourceUtils.isEARProject(projects[i])) {
					v.add(projects[i]);
				}
			} catch (Exception e) {
				e.printStackTrace();
				//handle exception
			}

		}
		String[] earProjects = new String[v.size()];
		v.copyInto(earProjects);
		return earProjects;
	}


	/**
	 * Get a J2EE 1.2 EAR Project. Returns null if no J2EE 1.2 EAR Projects
	 * exist
	 * 
	 * @deprecated  use getDefault12EARProject()
	 */
	public static EARNatureRuntime get12EAR() {
		try {
			IProject[] allEARs = getEARProjects();
			for (int i = 0; i < allEARs.length; i++) {
				// return the first 1.2 EAR encountered
				EARNatureRuntime thisEAR = (EARNatureRuntime) (allEARs[i]
						.getNature(IEARNatureConstants.NATURE_ID));
				if (thisEAR.getJ2EEVersion() == J2EEVersionConstants.J2EE_1_2_ID) {
					return thisEAR;
				}
			}
		} catch (CoreException ce) {
			// handle exception
		}
		return null;

	}
	
	/**
	 *  Returns the first j2ee 1.2 EAR project in the workspace
	 * @return null if no 1.2 EAR projects
	 * 
	 * @deprecated -  if not used; to be deleted
	 */
	public static IProject getDefault12EARProject(){
		try{
			IProject[] allEARs = ResourceUtils.getWorkspaceRoot().getProjects(); // getEARProjects();
			for (int i=0;i<allEARs.length;i++){
				// return the first 1.2 EAR project
				IProject ear = allEARs[i];
				if (getJ2EEVersionAsString(ear).equals(IModuleConstants.J2EE_VERSION_1_2)){
					return ear;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			//handle exception
		}
		return null;
	}
	
		
	/**
	 * Get a J2EE 1.3 EAR Project. Returns null if no J2EE 1.3 EAR Projects
	 * exist
	 * 
	 * @deprecated use getDefault13EARComponent()
	 * 
	 */
	public static EARNatureRuntime get13EAR() {
		try {
			IProject[] allEARs = getEARProjects();
			for (int i = 0; i < allEARs.length; i++) {
				// return the first 1.3 EAR encountered
				EARNatureRuntime thisEAR = (EARNatureRuntime) (allEARs[i]
						.getNature(IEARNatureConstants.NATURE_ID));
				if (thisEAR.getJ2EEVersion() == J2EEVersionConstants.J2EE_1_3_ID) {
					return thisEAR;
				}
			}
		} catch (CoreException ce) {
			return null;
		}
		return null;

	}

	/**
	 * Returns a default J2EE 1.3 EAR component in the workspace
	 * @return EAR component name
	 */
	public static String getDefault13EARComponent(){
		String earComponentName = null;
		boolean found = false;
		IProject[] projects = ResourceUtils.getWorkspaceRoot().getProjects();
		for (int i=0;i<projects.length;i++){
			 String[] earComponents = getEARComponents(projects[i]);
			 for (int j=0;j<earComponents.length;j++){
				 int j2eeVersion = getJ2EEVersion(projects[i], earComponents[j]);
				 if (j2eeVersion == J2EEVersionConstants.J2EE_1_3_ID) {
					 return earComponents[j];
				 }
			 }
		}
		return earComponentName;
	}
	
	/**
	 * Returns a default J2EE 1.4 EAR component in the workspace
	 * @return EAR component name
	 */
	public static String getDefault14EARComponent(){
		String earComponentName = null;
		boolean found = false;
		IProject[] projects = ResourceUtils.getWorkspaceRoot().getProjects();
		for (int i=0;i<projects.length;i++){
			 String[] earComponents = getEARComponents(projects[i]);
			 for (int j=0;j<earComponents.length;j++){
				 int j2eeVersion = getJ2EEVersion(projects[i], earComponents[j]);
				 if (j2eeVersion == J2EEVersionConstants.J2EE_1_4_ID) {
					 return earComponents[j];
				 }
			 }
		}
		return earComponentName;
	}
		
	/**
	 * 
	 * @param versionId
	 * @return
	 * 
	 * @deprecated use getEARProjectOfVersion(int)
	 */
	public static EARNatureRuntime getEAR(int versionId) {
		try {
			IProject[] allEARs = getEARProjects();
			for (int i = 0; i < allEARs.length; i++) {
				EARNatureRuntime thisEAR = (EARNatureRuntime) (allEARs[i]
						.getNature(IEARNatureConstants.NATURE_ID));
				if (thisEAR.getJ2EEVersion() == versionId) {
					return thisEAR;
				}
			}
		} catch (CoreException ce) {
			// handle exception
		}
		return null;
	}
	
	public static String getEARComponentofJ2EEVersion(int versionId){
		
		return null;
	}
	
	/**
	 * Returns the first EAR project of a given version id
	 * @param versionId
	 * @return
	 * @deprecated // use getEARComponentofJ2EEVersion
	 */
	public static IProject getEARProjectOfVersion(int versionId){
		try {
			IProject[] allEARs = getEARProjects();
			for (int i = 0; i < allEARs.length; i++) {
				IProject ear = allEARs[i];
				if (getJ2EEVersion(ear) == versionId) {
					return ear;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}


	/**
	 * 
	 * @param j2eeVersionInt
	 * @return
	 */
	public static String getLabelFromJ2EEVersion(String j2eeVersionInt) {
		if (j2eeVersionInt == null || j2eeVersionInt.length() == 0)
			return "";

		int j2eeVersion = Integer.parseInt(j2eeVersionInt);
		switch (j2eeVersion) {
			case J2EEVersionConstants.J2EE_1_2_ID :
				return J2EEVersionConstants.VERSION_1_2_TEXT;
			case J2EEVersionConstants.J2EE_1_3_ID :
				return J2EEVersionConstants.VERSION_1_3_TEXT;
			case J2EEVersionConstants.J2EE_1_4_ID :
				return J2EEVersionConstants.VERSION_1_4_TEXT;
			default :
				System.out.println("This is not a J2EE version!!");
				return "";
		}
	}

	public static String getJ2EEVersionFromLabel(String j2eeLabel) {
		String j2ee12String = String.valueOf(J2EEVersionConstants.J2EE_1_2_ID);
		String j2ee13String = String.valueOf(J2EEVersionConstants.J2EE_1_3_ID);
		String j2ee14String = String.valueOf(J2EEVersionConstants.J2EE_1_4_ID);
		if (j2eeLabel.equals(J2EEVersionConstants.VERSION_1_2_TEXT))
			return j2ee12String;

		if (j2eeLabel.equals(J2EEVersionConstants.VERSION_1_3_TEXT))
			return j2ee13String;

		if (j2eeLabel.equals(J2EEVersionConstants.VERSION_1_4_TEXT))
			return j2ee14String;

		return "";

	}

	// ----------------------------------------------------------------------

	/**
	 * Return all the ear projects in which this project is a nested module;
	 * 
	 * @param project
	 *            The project
	 * @return EARs EAR projects, possibly null
	 * 
	 * @deprecated
	 *   This method has too much complexity; to be simplified
	 */
	public static EARNatureRuntime[] getEARProjects(IProject serviceProject,
			IServer server) {

	
		EARNatureRuntime[] earProjects = null;
		EARNatureRuntime ear = null;
		IProject earProject = null;

		if (serviceProject != null && serviceProject.exists()) {
			try {

				EARNatureRuntime[] ears = null;
				boolean isWebEJBOrAppClient = ResourceUtils.isWebProject(serviceProject) //serviceProject.hasNature(IWebNatureConstants.J2EE_NATURE_ID)
						||  ResourceUtils.isEJBProject(serviceProject) //serviceProject.hasNature(IEJBNatureConstants.NATURE_ID)
						||  ResourceUtils.isAppClientProject(serviceProject);//serviceProject.hasNature(IApplicationClientNatureConstants.NATURE_ID);
				if (!isWebEJBOrAppClient) {
					return null;
				}

				ears = J2EEProjectUtilities.getReferencingEARProjects(serviceProject);

				// separate EARs which are already deployed to the existing
				// server
				if (ears != null && ears.length >= 1) {
					ArrayList preferredEARList = new ArrayList();
					ArrayList secondaryEARList = new ArrayList();
					for (int i = 0; i < ears.length; i++) {
						ear = ears[i];
						earProject = ear.getProject();
						IModule module = ResourceUtils.getModule(earProject);
						if (module != null) {
							if (server != null
									|| ServerUtil.containsModule(server,
											module, new NullProgressMonitor())) {
								preferredEARList.add(ear);
							} else {
								secondaryEARList.add(ear);
							}
						}
					}
					// add secondaryEARList items to end of primary list
					for (int j = 0; j < secondaryEARList.size(); j++) {
						preferredEARList.add(secondaryEARList.get(j));
					}
					// toArray
					if (preferredEARList != null) {
						earProjects = (EARNatureRuntime[]) preferredEARList
								.toArray(new EARNatureRuntime[0]);
					}
				}
			} catch (Exception ce) {
				Log log = new EclipseLog();
				log.log(Log.ERROR, 5039, J2EEUtils.class, "getEARProjects", ce);

			}
		}
		return earProjects;
	}

	/**
	 * 
	 * @return
	 * 
	 * @deprecated  // use getALLEARComponents
	 */
	public static IProject[] getEARProjects() {
		Vector v = new Vector();
		IProject[] projects = ResourceUtils.getWorkspaceRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			try {
//				if (projects[i].hasNature(IEARNatureConstants.NATURE_ID)) {
				if (ResourceUtils.isEARProject(projects[i])) {
					v.add(projects[i]);
				}
			} catch (Exception e) {
				e.printStackTrace();
				//handle exception
			}

		}
		IProject[] earProjects = new IProject[v.size()];
		v.copyInto(earProjects);
		return earProjects;
	}
	
	
	/**
	 * Returns the first EAR project associated with the project and server
	 * @param serviceProject
	 * @param server
	 * @return
	 * 
	 * @deprecated  // to be simplified
	 */
	public static IProject getDefaultEARProject(IProject serviceProject, IServer server) {

		IProject[] earProjects = null;
		IProject ear = null;

		if (serviceProject != null && serviceProject.exists()) {
			try {

				boolean isWebEJBOrAppClient = ResourceUtils.isWebProject(serviceProject) //serviceProject.hasNature(IWebNatureConstants.J2EE_NATURE_ID)
						||  ResourceUtils.isEJBProject(serviceProject) //serviceProject.hasNature(IEJBNatureConstants.NATURE_ID)
						||  ResourceUtils.isAppClientProject(serviceProject);//serviceProject.hasNature(IApplicationClientNatureConstants.NATURE_ID);
				if (!isWebEJBOrAppClient) {
					return null;
				}

				IProject[] ears = getEARProjects();

				// separate EARs which are already deployed to the existing
				// server
				if (ears != null && ears.length >= 1) {
					ArrayList preferredEARList = new ArrayList();
					ArrayList secondaryEARList = new ArrayList();
					for (int i = 0; i < ears.length; i++) {
						ear = ears[i];
						IModule module = ResourceUtils.getModule(ear);
						if (module != null) {
							if (server != null
									|| ServerUtil.containsModule(server,
											module, new NullProgressMonitor())) {
								preferredEARList.add(ear);
							} else {
								secondaryEARList.add(ear);
							}
						}
					}
					// add secondaryEARList items to end of primary list
					for (int j = 0; j < secondaryEARList.size(); j++) {
						preferredEARList.add(secondaryEARList.get(j));
					}
					// toArray
					if (preferredEARList != null) {
						earProjects = (IProject[]) preferredEARList.toArray(new IProject[0]);
					}
				}
			} catch (Exception ce) {
				Log log = new EclipseLog();
				log.log(Log.ERROR, 5039, J2EEUtils.class, "getEARProjects", ce);

			}
		}
		return earProjects[0];
	}
	
	/**
	 * Returns EJB projects in the ears
	 * 
	 * @param earProjects
	 * @return projects EJB projects
	 * 
	 * @deprecated use getEJB20ComponentsFromEars
	 */
	public static IProject[] getEJB2_0ProjectsFromEARS(EARNatureRuntime[] earProjects) {
		if (earProjects == null)
			return null;

		ArrayList ejbProjects = new ArrayList();
		for (int i = 0; i < earProjects.length; i++) {
			if (earProjects[i] instanceof EARNatureRuntime) {
				EARNatureRuntime ear = (EARNatureRuntime) earProjects[i];
				Map projectsInEAR = ear.getModuleProjects();
				if (projectsInEAR != null && !projectsInEAR.isEmpty()) {
					Iterator iter = projectsInEAR.values().iterator();
					while (iter.hasNext()) {
						Object MOFObject = iter.next();
						if (MOFObject instanceof EJBNatureRuntime) {
							if (((EJBNatureRuntime) MOFObject)
									.getModuleVersion() >= J2EEVersionConstants.EJB_2_0_ID) {
								IProject project = ((EJBNatureRuntime) MOFObject)
										.getProject();
								if (project != null) {
									ejbProjects.add(project);
								}
							}
						}
					}
				}
			}
		} // end for earProjects loop

		return (IProject[]) ejbProjects.toArray(new IProject[0]);
	}
	
	public static String[] getEJB20ComponentsFromEars(String[] earComponentNames){
		
		return null;
	}

	/**
	 * Returns EJB projects in the ears
	 * 
	 * @param earProjects
	 * @return projects EJB projects
	 * 
	 * @deprecated
	 */
	public static IProject[] getEJBProjectsFromEARS(
			EARNatureRuntime[] earProjects) {
		if (earProjects == null)
			return null;

		ArrayList ejbProjects = new ArrayList();
		for (int i = 0; i < earProjects.length; i++) {
			if (earProjects[i] instanceof EARNatureRuntime) {
				EARNatureRuntime ear = (EARNatureRuntime) earProjects[i];
				Map projectsInEAR = ear.getModuleProjects();
				if (projectsInEAR != null && !projectsInEAR.isEmpty()) {
					Iterator iter = projectsInEAR.values().iterator();
					while (iter.hasNext()) {
						Object MOFObject = iter.next();
						if (MOFObject instanceof EJBNatureRuntime) {

							IProject project = ((EJBNatureRuntime) MOFObject)
									.getProject();
							if (project != null) {
								ejbProjects.add(project);
							}

						}
					}
				}
			}
		} // end for earProjects loop

		return (IProject[]) ejbProjects.toArray(new IProject[0]);
	}

	/**
	 * 
	 * @param earComponents
	 * @return
	 */
	public static IVirtualComponent[] getEJBComponentsFromEars(IVirtualComponent[] earComponents){
		
		return null;
	}
	
	
	/**
	 * Utility method to combine two IProject[]
	 * @param projectArray1
	 * @param projectArray2
	 * @return
	 */
	public static IProject[] combineProjectArrays(IProject[] projectArray1,
			IProject[] projectArray2) {

		// check if either or both arrays are null.
		if (projectArray1 == null && projectArray2 == null)
			return null;
		else if (projectArray1 != null && projectArray2 == null)
			return projectArray1;
		else if (projectArray1 == null && projectArray2 != null)
			return projectArray2;

		IProject[] combinedProjects = new IProject[projectArray1.length
				+ projectArray2.length];

		System.arraycopy(projectArray1, 0, combinedProjects, 0,
				projectArray1.length);
		if (projectArray2.length > 0) {
			System.arraycopy(projectArray2, 0, combinedProjects,
					projectArray1.length, projectArray2.length);
		}

		return combinedProjects;
	}

	/**
	 * Returns all Web projects in the ear(s)
	 * 
	 * @param earProjects
	 * @return projects Web projects
	 * @deprecated  use getWebComponentsForEars
	 */
	public static IProject[] getWebProjectsFromEARS(
			EARNatureRuntime[] earProjects) {
		if (earProjects == null)
			return null;

		ArrayList webProjects = new ArrayList();
		for (int i = 0; i < earProjects.length; i++) {
			if (earProjects[i] instanceof EARNatureRuntime) {
				EARNatureRuntime ear = (EARNatureRuntime) earProjects[i];
				Map projectsInEAR = ear.getModuleProjects();
				if (projectsInEAR != null && !projectsInEAR.isEmpty()) {
					Iterator iter = projectsInEAR.values().iterator();
					while (iter.hasNext()) {
						// IProjectNature nature =
						// iter.next().getNature(IWebNatureConstants.J2EE_NATURE_ID);
						Object MOFObject = iter.next();
						if (MOFObject instanceof J2EEWebNatureRuntime) {
							IProject project = ((J2EEWebNatureRuntime) MOFObject)
									.getProject();
							if (project != null) {
								webProjects.add(project);
							}

						}
					}
				}
			}
		} // end for earProjects loop

		return (IProject[]) webProjects.toArray(new IProject[0]);
	}

	/**
	 * 
	 * @param earComponents
	 * @return
	 */
	public static IVirtualComponent[] getWebComponentsForEars(IVirtualComponent earComponents){
		
		return null;
	}
	
	/**
	 * 
	 * @param project
	 * @return
	 * 
	 * @deprecated // use isEJB20Component
	 */
	public static boolean isEJB2_0Project(IProject project) {

		if (ResourceUtils.isEJBProject(project)) {
			try {

				if (project.hasNature(IEJBNatureConstants.NATURE_ID)
						&& EJBNatureRuntime.getRuntime(project)
								.getModuleVersion() >= J2EEVersionConstants.EJB_2_0_ID) {
					return true;
				}
			} catch (CoreException e) {
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param ejbComponent
	 * @return
	 */
	public static boolean isEJB20Component(IVirtualComponent ejbComponent){
		
		return false;
	}

	/**
	 * Returns true if the given <code>project</code> is an EAR 1.2 or EAR 1.3
	 * Project.
	 * 
	 * @param project
	 *            The project.
	 * @return True if the project is an EAR 1.2 or an EAR 1.3 Project.
	 * 
	 * @deprecated // use isEARComponent
	 */
	public static boolean isEARProject(IProject project) {
		try {
			if (project.hasNature(IEARNatureConstants.NATURE_ID))
				return true;
		} catch (CoreException e) {
		}
		return false;
	}

	/**
	 * 
	 * @param module
	 * @param EAR
	 * @return
	 * 
	 * @deprecated   use isComponentAssociated
	 */
	public static boolean isEARAssociated(IProject module, IProject EAR) {

		EARNatureRuntime[] ears = getEARProjects(module);
		if (ears != null && ears.length != 0) {
			Vector EARNames = new Vector();
			for (int i = 0; i < ears.length; i++) {
				EARNames.add(ears[i].getProject().getName());
			}
			String[] earNames = (String[]) EARNames.toArray(new String[0]);
			if (Arrays.binarySearch(earNames, EAR.getName()) >= 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param ear
	 * @param component
	 * @return
	 */
	public static boolean isComponentAssociated(IVirtualComponent ear, IVirtualComponent component){
		
		return false;
	}

	/**
	 * 
	 * @param module
	 * @param EARProject
	 * 
	 * @deprecated new method to be determined
	 */
	public static void associateWebProject(IProject module, IProject EARProject) {
		try {

			String uri = module.getName() + ".war";
			String contextRoot = module.getName();
			AddModuleToEARProjectCommand amiec = new AddModuleToEARProjectCommand(
					module, EARProject, uri, contextRoot, null);
			if (amiec.canExecute())
				amiec.execute();
			/*
			 * EARNatureRuntime EARNature =
			 * (EARNatureRuntime)getEARNatureRuntimeFromProject(EARProject);
			 * 
			 * Application application = EARNature.getApplication(); Module mod =
			 * (Module)application.getFirstModule(module.getName());
			 * EAREditModel edm = EARNature.getEarEditModelForRead();
			 * edm.addModuleMapping(mod, EARProject); edm.releaseAccess();
			 * 
			 */
		} catch (Exception e) {

		}
	}

	/**
	 * 
	 * @param ejbProject
	 * @param EARProject
	 * 
	 * @deprecated to be determined
	 */
	public static void associateEJBProject(IProject ejbProject,
			IProject EARProject) {
		try {
			String uri = ejbProject.getName() + ".jar";
			String contextRoot = ejbProject.getName();
			AddModuleToEARProjectCommand amiec = new AddModuleToEARProjectCommand(
					ejbProject, EARProject, uri, contextRoot, null);
			if (amiec.canExecute())
				amiec.execute();

		} catch (Exception e) {

		}

	}
	
	/**
	 * Returns the first Module's WEB-INF directory
	 * @param project
	 * @return
	 * 
	 * @deprecated  use getWebInfPath(project, compName) instead
	 */
	public static IPath getFirstWebInfPath(IProject project){
		IPath modulePath = null;
		StructureEdit mc = null;
		try {
		  mc = StructureEdit.getStructureEditForRead(project);
		  WorkbenchComponent[] wbcs = mc.getWorkbenchModules();
		  
		  if (wbcs.length!=0) {
//			WebArtifactEdit webEdit = null;
//			try {
//			  webEdit = WebArtifactEdit.getWebArtifactEditForRead(wbcs[0]);
//			  if (webEdit!=null){
//				  IPath webXMLPath = webEdit.getDeploymentDescriptorPath();
//				  modulePath = webXMLPath.removeLastSegments(1);
//				  System.out.println("WebModulePath/DDPath = "+modulePath);
//			  }
//			}
//			finally{
//				if (webEdit!=null)
//					webEdit.dispose();
//			}
			IVirtualComponent component = ComponentCore.createComponent(project, wbcs[0].getName());
			IVirtualFolder webInfDir = component.getFolder(new Path("/WEB-INF"));
			modulePath = webInfDir.getWorkspaceRelativePath();
			System.out.println("FirstWebInfPath = " +modulePath);
		  }
		}
		catch(Exception ex){}
		finally{
			if (mc!=null)
				mc.dispose();
		}

		return modulePath;		
	}
	
	/**
	 * TODO: to be implemented
	 * @param project
	 * @param componentName
	 * @return
	 */
	public static IPath getWebInfPath(IProject project, String componentName){
		
		IVirtualComponent component = ComponentCore.createComponent(project, componentName);
		IVirtualFolder webInfDir = component.getFolder(new Path("/WEB-INF"));
		IPath modulePath = webInfDir.getWorkspaceRelativePath();
		System.out.println("FirstWebInfPath = " +modulePath);
		
		return modulePath;
	}
	
	
	/**
	 * 
	 * @param project
	 * @return
	 * 
	 * @deprecated use getWebContentPath(IProject, String)
	 */
	public static IPath getFirstWebContentPath(IProject project){
		
		IPath modulePath = null;
		StructureEdit mc = null;
		try {
		  mc = StructureEdit.getStructureEditForRead(project);
		  WorkbenchComponent[] wbcs = mc.getWorkbenchModules();
		  if (wbcs.length!=0) {
//			WebArtifactEdit webEdit = null;
//			try {
//			  webEdit = WebArtifactEdit.getWebArtifactEditForRead(wbcs[0]);
//			  if (webEdit!=null){
//				  IPath webXMLPath = webEdit.getDeploymentDescriptorPath();
//				  modulePath = webXMLPath.removeLastSegments(2);
//				  System.out.println("WebContent Path = "+modulePath);
//			  }
//			}
//			finally{
//				if (webEdit!=null)
//					webEdit.dispose();
//			}
			IVirtualComponent component = ComponentCore.createComponent(project, wbcs[0].getName());
			modulePath = component.getWorkspaceRelativePath();
			System.out.println("FirstWebContentPath = " +modulePath);
		  }
		}
		catch(Exception ex){}
		finally{
			if (mc!=null)
				mc.dispose();
		}

		return modulePath;			
	}
	
	/**
	 * TODO: implement
	 * @param project
	 * @param componentName
	 * @return
	 */
	public static IPath getWebContentPath(IProject project, String componentName){
		
		IVirtualComponent component = ComponentCore.createComponent(project, componentName);
		IPath modulePath = component.getWorkspaceRelativePath();
		System.out.println("FirstWebContentPath = " +modulePath);
		
		return modulePath;
	}
	
	/**
	 * 
	 * @param project
	 * @return
	 * 
	 * @deprecated use getWebContentContainer(IProject, String)
	 */
	public static IContainer getFirstWebContentContainer(IProject project){
		IContainer container = null;
		IPath modulePath = getFirstWebContentPath(project);
		IResource res = ResourceUtils.findResource(modulePath);
		if (res!=null){
		  container = res.getParent();
		}		
		  
		return container;
	}
	
	/**
	 * TODO: implement
	 * @param project
	 * @param componentName
	 * @return
	 */
	public static IContainer getWebContentContainer(IProject project, String componentName){
		IContainer container = null;
		IPath modulePath = getWebContentPath(project, componentName);
		IResource res = ResourceUtils.findResource(modulePath);
		if (res!=null){
		  container = res.getParent();
		}		
		  
		return container;
	}
	
	
	/**
	 * Returns the first Module name 
	 * @param project
	 * @return
	 * 
	 * @deprecated  not necessary; to be deleted
	 */
	public static String getFirstWebModuleName(IProject project){
		String moduleName = null;
		StructureEdit mc = null;
		try {
		  mc = StructureEdit.getStructureEditForRead(project);
		  WorkbenchComponent[] wbcs = mc.getWorkbenchModules();
		  if (wbcs.length!=0) {
			  moduleName = wbcs[0].getName();
			  System.out.println("First Module name = "+moduleName);
		  }
		}
		catch(Exception ex){}
		finally{
			if (mc!=null)
				mc.dispose();
		}

		return moduleName;				
	}
	

	/**
	 * True if the component is a valid Web component
	 * @param project
	 * @param componentName
	 * @return
	 */
	public static boolean isWebComponent(IProject project, String componentName) {
		boolean isWeb = false;
		StructureEdit mc = null;
		try {
		  mc = StructureEdit.getStructureEditForRead(project);
		  WorkbenchComponent[] wbcs = mc.getWorkbenchModules();
		  for(int i=0;i<wbcs.length;i++){
			  if (wbcs[i].getName().equals(componentName)){
				  isWeb = WebArtifactEdit.isValidWebModule(wbcs[i]);
				  break;
			  }
		  }
		}
		catch(Exception e){
			e.printStackTrace();
			// handle Unresolveable URI exception
		}
		finally{
			if (mc!=null)
				mc.dispose();
		}

		return isWeb;
	}
	
	public static boolean isWebComponent(IVirtualComponent comp){
		return isWebComponent(comp.getProject(), comp.getName());
	}

	/**
	 * True is the component is a valid EAR component
	 * @param project
	 * @param componentName
	 * @return
	 */
	public static boolean isEARComponent(IProject project, String componentName){
		boolean isEAR = false;
		StructureEdit mc = null;
		try {
		  mc = StructureEdit.getStructureEditForRead(project);
		  WorkbenchComponent[] wbcs = mc.getWorkbenchModules();
		  for(int i=0;i<wbcs.length;i++){
			  if (wbcs[i].getName().equals(componentName)){
				  isEAR = EARArtifactEdit.isValidEARModule(wbcs[i]);
				  break;
			  }
		  }
		}
		catch(Exception ex){}
		finally{
			if (mc!=null)
				mc.dispose();
		}
		
		return isEAR;
	}
	
	public static boolean isEARComponent(IVirtualComponent comp){
		return isEARComponent(comp.getProject(), comp.getName());
	}

	/**
	 * True if the component is a valid EJB component
	 * @param project
	 * @param componentName
	 * @return
	 */
	public static boolean isEJBComponent(IProject project, String componentName) {
		boolean isEJB = false;
		StructureEdit mc = null;
		try {
		  mc = StructureEdit.getStructureEditForRead(project);
		  WorkbenchComponent[] wbcs = mc.getWorkbenchModules();
		  for(int i=0;i<wbcs.length;i++){
			  if (wbcs[i].getName().equals(componentName)){
				  isEJB = EJBArtifactEdit.isValidEJBModule(wbcs[i]);
				  break;
			  }
		  }
		}
		catch(Exception ex){}
		finally{
			if (mc!=null)
				mc.dispose();
		}
		
		return isEJB;	
	}

	public static boolean isEJBComponent(IVirtualComponent comp){
		return isEJBComponent(comp.getProject(), comp.getName());
	}
	
	/**
	 * True if the component is a true Application client component
	 * @param project
	 * @param componentName
	 * @return
	 */
	public static boolean isAppClientComponent(IProject project, String componentName) {
		boolean isAppClient = false;
		StructureEdit mc = null;
		try {
		  mc = StructureEdit.getStructureEditForRead(project);
		  WorkbenchComponent[] wbcs = mc.getWorkbenchModules();
		  for(int i=0;i<wbcs.length;i++){
			  if (wbcs[i].getName().equals(componentName)){
				  isAppClient = AppClientArtifactEdit.isValidApplicationClientModule(wbcs[i]);
				  break;
			  }
		  }
		}
		catch(Exception ex){}
		finally{
			if (mc!=null)
				mc.dispose();
		}
		
		return isAppClient;	
	}	

	public static boolean isAppClientComponent(IVirtualComponent comp){
		return isAppClientComponent(comp.getProject(), comp.getName());
	}
	
	/**
	 * True if the component is a valid Java component
	 * @param project
	 * @param componentName
	 * @return
	 */
	public static boolean isJavaComponent(IProject project, String componentName) {
		boolean isJava = false;
		StructureEdit mc = null;
		try {
		  mc = StructureEdit.getStructureEditForRead(project);
		  WorkbenchComponent[] wbcs = mc.getWorkbenchModules();
		  for(int i=0;i<wbcs.length;i++){
			  if (wbcs[i].getName().equals(componentName)){
				  isJava = ArtifactEdit.isValidEditableModule(wbcs[i]);
				  break;
			  }
		  }
		}
		catch(Exception e){
			e.printStackTrace();
			// handle Unresolveable URI exception
		}
		finally{
			if (mc!=null)
				mc.dispose();
		}

		return isJava;
	}
	
	public static boolean isJavaComponent(IVirtualComponent comp){
		return isJavaComponent(comp.getProject(), comp.getName());
	}	
	
	
}
