/*******************************************************************************
 * Copyright (c) 2002-2005 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *******************************************************************************/
 package org.eclipse.wst.wsi.internal.report;

/**
 * AssertionError
 * 
 * Object to hold an assertion error generated by the WS-I Test Tools.
 * Provides access to all the information about the error including the
 * id, message and location in the file.
 */
public class AssertionError
{

  protected String message;
  protected String assertionid;
  protected int column;
  protected int line;
  /**
   * Constructor for AssertionError.
   */
  public AssertionError()
  {
    message = assertionid = "";
    line = column = 0;
  }

  /**
   * Constructor for AssertionError.
   * @param id assertion id.
   * @param message a error message.
   * @param line a line number.
   * @param column a column number.
   */
  public AssertionError(String id, String message, int line, int column)
  {
    this.assertionid = id;
    this.message = message;
    this.line = line;
    this.column = column;
  }

  /**
   * Method setAssertionID.
   * @param id assertion id.
   * @see #getAssertionID
   */
  public void setAssertionID(String id)
  {
    assertionid = id;
  }

  /**
   * Method getAssertionID.
   * @return asserion id.
   * @see #setAssertionID
   */
  public String getAssertionID()
  {
    return assertionid;
  }

  /**
   * Method setErrorMessage.
   * @param message a error message.
   * @see #getErrorMessage
   */
  public void setErrorMessage(String message)
  {
    this.message = message;
  }

  /**
   * Method getErrorMessage.
   * @return error message.
   * @see #setErrorMessage
   */
  public String getErrorMessage()
  {
    return message;
  }

  /**
   * Method setLine.
   * @param line line number.
   * @see #getLine
   */
  public void setLine(int line)
  {
    this.line = line;
  }

  /**
   * Method getLine.
   * @return line number.
   * @see #setLine
   */
  public int getLine()
  {
    return line;
  }

  /**
   * Method setColumn.
   * @param column column number.
   * @see #getColumn
   */
  public void setColumn(int column)
  {
    this.column = column;
  }

  /**
   * Method getColumn.
   * @return column number.
   * @see #setColumn
   */
  public int getColumn()
  {
    return column;
  }

}
