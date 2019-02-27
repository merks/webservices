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
package org.eclipse.wst.wsi.internal.core.profile.validator.impl.wsdl;

import java.util.Iterator;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.xml.namespace.QName;

import org.eclipse.wst.wsi.internal.core.WSIException;
import org.eclipse.wst.wsi.internal.core.WSITag;
import org.eclipse.wst.wsi.internal.core.profile.TestAssertion;
import org.eclipse.wst.wsi.internal.core.profile.validator.EntryContext;
import org.eclipse.wst.wsi.internal.core.profile.validator.impl.AssertionProcess;
import org.eclipse.wst.wsi.internal.core.report.AssertionResult;
import org.eclipse.wst.wsi.internal.core.util.ErrorList;
import org.eclipse.wst.wsi.internal.core.util.TypesRegistry;


/**
 * BP2110.
   * <context>For a candidate wsdl:types</context>
   * <assertionDescription>Array declaration wrapper elements does not use the naming convention ArrayOfXXX.</assertionDescription>
 */
public class BP2110 extends AssertionProcess implements WSITag
{
  private final WSDLValidatorImpl validator;

  /**
   * @param WSDLValidatorImpl
   */
  public BP2110(WSDLValidatorImpl impl)
  {
    super(impl);
    this.validator = impl;
  }

  private ErrorList errors = new ErrorList();

  /* Validates the test assertion.
   * @see org.wsi.test.profile.validator.impl.BaseValidatorImpl.AssertionProcess#validate(org.wsi.test.profile.TestAssertion, org.wsi.test.profile.validator.EntryContext)
   */
  public AssertionResult validate(
    TestAssertion testAssertion,
    EntryContext entryContext)
    throws WSIException
  {
    result = AssertionResult.RESULT_WARNING;

    Types t = (Types) entryContext.getEntry().getEntryDetail();

    // Search the definitions in CandidateInfo to locate the definition element that contains the specified types element
    Definition definition = null;
    if ((definition = validator.analyzerContext.getCandidateInfo().getDefinition(t))
      == null)
    {
      // This should never happen, but if it does then throw an execption
      throw new WSIException("Could not locate types element definition.");
    }

    else
    {
      TypesRegistry registry =
        new TypesRegistry(
          t,
          definition.getDocumentBaseURI(),
          validator);

      Iterator it = registry.getElementList().iterator();
      while (it.hasNext())
      {
        QName type = (QName) it.next();
        if (type.getLocalPart().startsWith("ArrayOf"))
          errors.add(type);
      }

      if (!errors.isEmpty())
      {
        result = AssertionResult.RESULT_WARNING;
        failureDetail = this.validator.createFailureDetail(errors.toString(), entryContext);
      }

      else
        result = AssertionResult.RESULT_PASSED;
    }

    return validator.createAssertionResult(testAssertion, result, failureDetail);
  }
}