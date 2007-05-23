/*******************************************************************************
 * Copyright (c) 2007 WSO2 Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * WSO2 Inc. - initial API and implementation
 * yyyymmdd bug      Email and other contact information
 * -------- -------- -----------------------------------------------------------
 * 20070518        187311 sandakith@wso2.com - Lahiru Sandakith, Fixing test resource addition
 *******************************************************************************/
package org.eclipse.jst.ws.axis2.consumption.core.command;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jst.ws.axis2.consumption.core.data.DataModel;
import org.eclipse.jst.ws.axis2.consumption.core.messages.Axis2ConsumptionUIMessages;
import org.eclipse.jst.ws.axis2.core.plugin.messages.Axis2CoreUIMessages;
import org.eclipse.jst.ws.axis2.core.utils.FileUtils;
import org.eclipse.jst.ws.internal.common.J2EEUtils;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.ws.internal.common.BundleUtils;


/**
 * This Class will first check whether Axis2 Client will generate the TestCase
 * and if so then it will integrate that test to the initiated dynamic web project
 *
 */
public class Axis2ClientTestCaseIntegrateCommand extends AbstractDataModelOperation {

    private DataModel model;
    private IProject project;

    public Axis2ClientTestCaseIntegrateCommand(IProject project_,DataModel model_){
        this.model = model_;
        this.project = project_;
    }
    
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        IStatus status = Status.OK_STATUS;
        String workspaceDirectory = ResourcesPlugin.getWorkspace().getRoot().
                                                    getLocation().toOSString();
        // Check whether the service generate test case is enabled
        if(model.isTestCaseCheck()){
        	try{
        	// Then add the latest junit.jar from to the project class path
            IPath junitPath = new Path(CopyJUnitJarToProject(workspaceDirectory));

            // make the test folder as source folder.
            // Get the Project Handler
            IJavaProject javaProj = new JavaProject(project,null);
            // to get existing class path entries
            IClasspathEntry[] classpathEntries = javaProj.getRawClasspath(); 
            // increase the class path entries array by 1 to make room for the new source directory
            // where path is the new source directory
            IClasspathEntry newClasspathEntry = JavaCore
                                .newSourceEntry(getPathToTestFolder(project,workspaceDirectory)); 
            IClasspathEntry junitClasspathEntry = JavaCore.newLibraryEntry (junitPath,null,null); 
           
            int classPathLength = classpathEntries.length;
            IClasspathEntry[] newClasspathEntryArray = new IClasspathEntry[classPathLength+2];
            for (int i = 0; i < classpathEntries.length; i++) {
                newClasspathEntryArray[i]= classpathEntries[i];
            }
           
            // add new Class Path Entries of junit.jar and test directory
            newClasspathEntryArray[classPathLength] = newClasspathEntry;
            newClasspathEntryArray[classPathLength+1] = junitClasspathEntry;
           
            javaProj.setRawClasspath(newClasspathEntryArray,monitor);
           
            } catch (JavaModelException e) {
                throw new ExecutionException(e.getMessage());
            } catch (IOException e) {
            	 throw new ExecutionException(e.getMessage());
			}
        }

        return status;
       
    }
   
    /**
     * Copy the Junit jar from the framework location to the project classpath.
     * @param workspace
     * @return absolute path location of the copying file
     * @throws ExecutionException
     * @throws IOException
     */
    private String CopyJUnitJarToProject(String workspace) throws ExecutionException, IOException {
    	File relativeWebInfJunitFile = new File(FileUtils.addAnotherNodeToPath(
    												J2EEUtils.getWebInfPath(project).toOSString(),
    												Axis2CoreUIMessages.DIR_LIB+File.separator+
    												Axis2ConsumptionUIMessages.JUNIT_JAR));
    	
    	File obsaluteWebInfJunitFile = new File(FileUtils.addAnotherNodeToPath(
    														workspace, 
    														relativeWebInfJunitFile.toString()));
		FileUtils.copy(getFrameworkJunitFile(), obsaluteWebInfJunitFile);
		return obsaluteWebInfJunitFile.getAbsolutePath();
	}

    /**
     * returns the Framework JUnit File location.
     * @return
     * @throws ExecutionException
     */
	private File getFrameworkJunitFile() throws ExecutionException {
    	IPath junitJarPath=BundleUtils.getJarredPluginPath(Axis2ConsumptionUIMessages.JUNIT_BUNDLE);
    	if (junitJarPath != null){
    		return new File(FileUtils.addAnotherNodeToPath(junitJarPath.toOSString(),
    						Axis2ConsumptionUIMessages.JUNIT_JAR));
    	}else{
    		throw new ExecutionException(Axis2ConsumptionUIMessages.ERROR_JUNIT_JAR_NOT_FOUND);
    	}
    }

	/**
	 * returns the path to the newly generated test folder 
	 * @param project
	 * @param workspace
	 * @return IPath to the test folder
	 */
    private IPath getPathToTestFolder(IProject project, String workspace) {
        String pathToTest = project.getFullPath().toOSString() + File.separator
                            + Axis2CoreUIMessages.DIR_TEST;
        IPath pathToTestFolder = new Path(pathToTest);
        return pathToTestFolder;
    }
   
}
