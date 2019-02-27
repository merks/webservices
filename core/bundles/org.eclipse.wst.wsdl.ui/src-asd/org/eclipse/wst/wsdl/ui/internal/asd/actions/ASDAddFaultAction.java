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

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.wsdl.ui.internal.adapters.basic.W11MessageReference;
import org.eclipse.wst.wsdl.ui.internal.asd.ASDEditorPlugin;
import org.eclipse.wst.wsdl.ui.internal.asd.Messages;
import org.eclipse.wst.wsdl.ui.internal.asd.facade.IMessageReference;
import org.eclipse.wst.wsdl.ui.internal.asd.facade.IOperation;
import org.eclipse.wst.wsdl.ui.internal.asd.facade.IParameter;

public class ASDAddFaultAction extends BaseSelectionAction {
	public static String ID = "ASDAddFaultActionn";  //$NON-NLS-1$
	
	public ASDAddFaultAction(IWorkbenchPart part)	{
		super(part);
		setId(ID);
		setText(Messages._UI_ACTION_ADD_FAULT);   //$NON-NLS-1$
		setImageDescriptor(ASDEditorPlugin.getImageDescriptor("icons/fault_obj.gif")); //$NON-NLS-1$
	}
	
	public void run() {
		if (getSelectedObjects().size() > 0) {
			Object o = getSelectedObjects().get(0);
			IOperation iOperation = null;
			Object possibleFault = null;
			
			if (o instanceof IOperation) {
				iOperation = (IOperation) o;
			}
			else if (o instanceof IMessageReference) {
				iOperation = ((IMessageReference) o).getOwnerOperation();
				possibleFault = ((W11MessageReference) o).getTarget();
			}
			else if (o instanceof IParameter) {
				iOperation = ((IMessageReference) ((IParameter) o).getOwner()).getOwnerOperation();
				possibleFault = ((W11MessageReference) ((IParameter) o).getOwner()).getTarget();
			}
			
			if (iOperation != null) {
				Command command = iOperation.getAddFaultCommand(possibleFault);
			    CommandStack stack = (CommandStack) ASDEditorPlugin.getActiveEditor().getAdapter(CommandStack.class);
			    stack.execute(command);
			    
			    if (command instanceof IASDAddCommand) {
			    	Object object = ((IASDAddCommand) command).getNewlyAddedComponent();
			    	performSelection(object);
			    }
			}
		}  
	}
}