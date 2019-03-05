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

package org.eclipse.wst.ws.internal.explorer.platform.actions;

import javax.servlet.http.HttpServletRequest;

public class WriteWSDLToWorkbenchAction extends Action
{
  public static final String getActionLink()
  {
    return "actions/WriteWSDLToWorkbenchActionJSP.jsp";
  }
    
  public boolean populatePropertyTable(HttpServletRequest request)
  {
    return true;
  }
  
  public boolean run()
  {
    return true;
  }
}