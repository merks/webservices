/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
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
import org.eclipse.wst.wsdl.ui.internal.asd.ASDEditorPlugin;
import org.eclipse.wst.wsdl.ui.internal.asd.Messages;
import org.eclipse.wst.wsdl.ui.internal.asd.facade.IBinding;

public class ASDGenerateBindingAction extends BaseSelectionAction {
	public static String ID = "ASDGenerateBindingActionn";  //$NON-NLS-1$
	
	public ASDGenerateBindingAction(IWorkbenchPart part)	{
		super(part);
		setId(ID);
		setText(Messages._UI_GENERATE_BINDING_CONTENT);   //$NON-NLS-1$
//		setImageDescriptor(WSDLEditorPlugin.getImageDescriptor("icons/input_obj.gif"));
	}
	
	public void run() {
 		if (getSelectedObjects().size() > 0) {
			Object o = getSelectedObjects().get(0);
			
			if (o instanceof IBinding) {
				Command command = ((IBinding) o).getGenerateBindingCommand();
			    CommandStack stack = (CommandStack) ASDEditorPlugin.getActiveEditor().getAdapter(CommandStack.class);
			    stack.execute(command);
			}
			
			performSelection(o);
		}  
	}
}
