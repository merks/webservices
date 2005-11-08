/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.command.internal.env;

import org.eclipse.osgi.util.NLS;

public final class EnvironmentMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.wst.command.internal.env.Environment";//$NON-NLS-1$

	private EnvironmentMessages() {
		// Do not instantiate
	}

	public static String MSG_ERROR_PATH_NOT_ABSOLUTE;
	public static String MSG_ERROR_PATH_EMPTY;
	public static String MSG_ERROR_PATH_NOT_FOLDER;
	public static String MSG_ERROR_FOLDER_CREATION_DISABLED;
	public static String MSG_ERROR_RESOURCE_NOT_FOLDER;
	public static String MSG_WARN_FILE_OVERWRITE_DISABLED;
	public static String MSG_ERROR_FILE_CHECKOUT_DISABLED;
	public static String MSG_ERROR_RESOURCE_NOT_FILE;
	public static String MSG_ERROR_FOLDER_HAS_CHILDREN;
	public static String MSG_ERROR_IO;
	public static String MSG_ERROR_FILE_OVERWRITE_DISABLED;
	public static String LABEL_YES;
	public static String LABEL_YES_TO_ALL;
	public static String LABEL_CANCEL;
	public static String TITLE_WARNING;
	public static String TITLE_ERROR;
	public static String TITLE_INFO;

	static {
		NLS.initializeMessages(BUNDLE_NAME, EnvironmentMessages.class);
	}
}