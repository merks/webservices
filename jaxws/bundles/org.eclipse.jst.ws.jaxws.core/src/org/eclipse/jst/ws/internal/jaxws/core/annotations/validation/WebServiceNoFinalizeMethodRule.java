/*******************************************************************************
 * Copyright (c) 2009 Shane Clarke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shane Clarke - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.ws.internal.jaxws.core.annotations.validation;

import java.util.Collection;

import javax.jws.WebService;

import org.eclipse.jst.ws.internal.jaxws.core.JAXWSCoreMessages;

import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;

/**
 * 
 * @author sclarke
 *
 */
public class WebServiceNoFinalizeMethodRule extends AbstractJAXWSAnnotationProcessor {

    @Override
    public void process() {
        AnnotationTypeDeclaration annotationDeclaration = (AnnotationTypeDeclaration) environment
                .getTypeDeclaration(WebService.class.getName());

        Collection<Declaration> annotatedTypes = environment
                .getDeclarationsAnnotatedWith(annotationDeclaration);

        for (Declaration declaration : annotatedTypes) {
            Collection<AnnotationMirror> annotationMirrors = declaration.getAnnotationMirrors();
            for (AnnotationMirror mirror : annotationMirrors) {
                if (isFinalizeDefined(declaration)) {
                    printError(mirror.getPosition(), 
                                JAXWSCoreMessages.WEBSERVICE_OVERRIDE_FINALIZE_MESSAGE);
                }
            }
        }
    }
    
    private boolean isFinalizeDefined(Declaration declaration) {
        if (declaration instanceof TypeDeclaration) {
            TypeDeclaration typeDeclaration = (TypeDeclaration)declaration;
            Collection<? extends MethodDeclaration> methodDeclarations = typeDeclaration.getMethods();
            for (MethodDeclaration methodDeclaration : methodDeclarations) {
                if (methodDeclaration.getSimpleName().equals(FINALIZE) 
                        && methodDeclaration.getParameters().size() == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
