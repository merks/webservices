/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.wsdl.ui.internal.asd.actions;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.wsdl.ui.internal.asd.ASDEditorPlugin;
import org.eclipse.wst.wsdl.ui.internal.asd.Messages;
import org.eclipse.wst.wsdl.ui.internal.asd.design.editparts.ServiceEditPart;
import org.eclipse.wst.wsdl.ui.internal.asd.facade.IDescription;
import org.eclipse.wst.wsdl.ui.internal.asd.outline.ICategoryAdapter;

public class ASDAddServiceAction extends BaseSelectionAction {	
	public static String ID = "ASDAddServiceAction";  //$NON-NLS-1$
	
	public ASDAddServiceAction(IWorkbenchPart part)	{
		super(part);
		setId(ID);
		setText(Messages._UI_ACTION_ADD_SERVICE); //$NON-NLS-1$
		setImageDescriptor(ASDEditorPlugin.getImageDescriptor("icons/service_obj.gif")); //$NON-NLS-1$
	}
	
	public void run() {
		if (getSelectedObjects().size() > 0) {
			Object o = getSelectedObjects().get(0);
			
			if (o instanceof ICategoryAdapter) {
				o = ((ICategoryAdapter) o).getOwnerDescription();
			}
			
			if (o instanceof IDescription) {
				Command command = ((IDescription) o).getAddServiceCommand();
			    CommandStack stack = (CommandStack) ASDEditorPlugin.getActiveEditor().getAdapter(CommandStack.class);
			    stack.execute(command);
			    
			    if (command instanceof IASDAddCommand) {
			    	Object element = ((IASDAddCommand) command).getNewlyAddedComponent();
			    	selectAndDirectEdit(element);
			    }
			}
		}  
	}
	
	protected void doDirectEdit(EditPart ep) {
		if (ep instanceof ServiceEditPart) {
			((ServiceEditPart) ep).performDirectEdit(null);
		}
	}
}