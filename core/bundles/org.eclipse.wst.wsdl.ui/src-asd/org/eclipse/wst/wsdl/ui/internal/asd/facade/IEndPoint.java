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
package org.eclipse.wst.wsdl.ui.internal.asd.facade;

import org.eclipse.gef.commands.Command;

public interface IEndPoint extends INamedObject
{
	public IService getOwnerService();
	public String getAddress();
	public IBinding getBinding();
	public String getProtocol();
  
	public Command getSetBindingCommand(IBinding binding);
	public Command getSetAddressCommand(String newAddress);
}
