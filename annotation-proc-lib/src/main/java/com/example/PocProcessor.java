/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.example;

import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.*;
import javax.tools.*;
import static javax.tools.Diagnostic.Kind.*;
import static javax.tools.StandardLocation.*;
import java.io.*;
import java.util.*;

/**
 * Quick and dirty annotation processor to provide a proof of concept
 * demonstration on how Properties support could be implemented using
 * standard annotation processing facilities.
 *
 * @author Joseph D. Darcy 
 */
@SupportedAnnotationTypes("foo.ProofOfConceptProperty")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class PocProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;
    private Elements eltUtils;

    public PocProcessor() {
	super();
    }

    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer    = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        eltUtils = processingEnv.getElementUtils();
    }

    public boolean process(Set<? extends TypeElement> annotations,
		   RoundEnvironment roundEnv) {
	if (!roundEnv.processingOver()) {
	    // Find the types with fields annotated with
	    // @ProofOfConceptProperty
	    Set<Element> temp = new HashSet<Element>();
	
	    for(Element e :
		    roundEnv.
		    getElementsAnnotatedWith(ProofOfConceptProperty.class)) {
		temp.add(e.getEnclosingElement());
	    }

	    // For each annotated type that is a Property
	    // 1) Generate its superclass to define the methods
	    // 2) Generate the child class to provide the implementation
	    for(TypeElement propertyClass : ElementFilter.typesIn(temp)) {
		/*
		 * A more than proof of concept implementation would
		 * implement various additional structural checks of
		 * the types found to contain "@Property" fields, such
		 * as, say, all the instance fields of a class were
		 * annotated with @Property.
		 */
		if (propertyClass.getKind() != ElementKind.CLASS) {
		    messager.printMessage(ERROR,
					  "Only a class can be treated as a property.",
					  propertyClass);
		    return true;
		}
					   
		List<VariableElement> instanceFields = new ArrayList<VariableElement>();
		for(VariableElement field :
			ElementFilter.fieldsIn(propertyClass.getEnclosedElements())) {
		    if (! (field.getModifiers().contains(Modifier.STATIC))) {
			// Should also check that the modifiers are
			// neither too permissive (public) or too
			// restrictive (private).
			instanceFields.add(field);
		    }
		}

		generateSuperclass(propertyClass, instanceFields);
		geneateChildClass(propertyClass, instanceFields);
	    }
	}
	return true;
    }

    private void generateSuperclass(TypeElement propertyClass,
				    List<VariableElement> instanceFields) {
	TypeMirror parentClass = propertyClass.getSuperclass();
	String parentClassName = parentClass.toString(); // Assume fully-qualified name

	if (parentClassName.equals("java.lang.Object")) {
	    messager.printMessage(ERROR,
				  "A Property class must have a to-be-generated superclass",
				  propertyClass);
	    return;
	} else {
	    try {
		JavaFileObject parentClassSourceFile = 
		    filer.createSourceFile(parentClassName, propertyClass);
		Writer w = parentClassSourceFile.openWriter();
		try {
		    // Generate package-private superclass with proper set of
		    // methods in same package as the Property class.

		    generatePackageStatement(propertyClass, w);
	    
		    w.write("\nclass ");
		    w.write(parentClassName);
		    w.write(" {\n");

		    // for each instance field of property Foo
		    // generate "public FooType getFoo()"
		    // and if readOnly == false, generate
		    // "public void setFoo(FooType field)"

		    for(VariableElement field : instanceFields ) {
			ProofOfConceptProperty pocProperty = 
			    field.getAnnotation(ProofOfConceptProperty.class);
			if (pocProperty == null) {
			    messager.printMessage(WARNING,
						  "Missing expected ProofOfConceptProperty annotation.",
						  field);
			} else {
			    // Generate getter
			    String fieldName  = field.getSimpleName().toString();
			    String fieldType  = field.asType().toString();

			    w.write("\npublic ");
			    w.write(fieldType);
			    w.write(" ");
			    w.write("get" + fieldName  + "() {\n");
			    w.write("    throw new java.lang.AssertionError(\"Cannot be called.\");\n"); 
			    w.write("}\n");
			
			    // Generate setter if appropriate
			    if (pocProperty.readOnly() == false) {
				w.write("\npublic void ");
				w.write("set" + fieldName);
				w.write("(" + fieldType + " " + fieldName + ") { \n");
				w.write("    throw new java.lang.AssertionError(\"Cannot be called.\");\n"); 
				w.write("}\n");
			    }
			}
		    }

		    w.write("}\n");
		} finally {
		    w.close();
		}
	    } catch (IOException ioe) {
		// Should print stacktrace or handle in a more informative manner.
		messager.printMessage(ERROR,
				      "IOException encountered.",
				      propertyClass);
	    }
	
	}
    }

    private void geneateChildClass(TypeElement propertyClass,
				   List<VariableElement> instanceFields) {
	String parentClassName = propertyClass.getQualifiedName().toString(); 
	String childClassName =  parentClassName + "Child";

	try {
	    JavaFileObject childClassSourceFile = filer.createSourceFile(childClassName, propertyClass);

	    Writer w = childClassSourceFile.openWriter();
	    try {
		generatePackageStatement(propertyClass, w);

		w.write("\nclass ");
		w.write(childClassName);
		w.write(" extends " + parentClassName);
		w.write(" {\n");

		// Generate package-private constructor
		w.write(childClassName);
		w.write("(");

		// Generate constructor parameter list
		{
		    boolean prefixComma = false;
		    for(VariableElement parameter : instanceFields) {
			String parameterName  = parameter.getSimpleName().toString();
			String parameterType  = parameter.asType().toString();

			if (prefixComma) {
			    w.write(", ");
			}
			w.write(parameterType);
			w.write(" ");
			w.write(parameterName);
			prefixComma = true;
		    }
		}
		
		w.write(") {\n");
		for(VariableElement parameter : instanceFields) {
		    String parameterName  = parameter.getSimpleName().toString();
		    String parameterType  = parameter.asType().toString();

		    w.write("    this.");
		    w.write(parameterName);
		    w.write(" = ");
		    w.write(parameterName);
		    w.write(";\n");

		}
		
		w.write("}\n");

		// Generate getters and setters

		for(VariableElement field : instanceFields) {
		    // For every instance field

		    ProofOfConceptProperty pocProperty = 
			field.getAnnotation(ProofOfConceptProperty.class);
		    if (pocProperty == null) {
			messager.printMessage(WARNING,
					      "Missing expected ProofOfConceptProperty annotation.",
					      field);
		    } else {
			// Generate getter
			// @java.lang.Override
			// public FooType getFoo() {
			//    return this.foo();
			// }

			String fieldName  = field.getSimpleName().toString();
			String fieldType  = field.asType().toString();

			w.write("\n@java.lang.Override\n");
			w.write("public ");
			w.write(fieldType);
			w.write(" ");
			w.write("get" + fieldName  + "() {\n");
			w.write("    return this." + fieldName + ";\n"); 
			w.write("}\n");
			
			// @java.lang.Override
			// public void setFoo(FooType x) {
			//    this.foo = x;
			// }

			if (pocProperty.readOnly() == false) { // Generate setter
			    w.write("\n@java.lang.Override\n");
			    w.write("public ");
			    w.write("void ");
			    w.write("set" + fieldName);
			    w.write("(" + fieldType + " " + fieldName + ") { \n");
			    w.write("    this." +  fieldName + " = " + fieldName + ";\n"); 
			    w.write("}\n");
			}
		    }

		}

		// TODO: Generate equals and hashCode methods.

		w.write("}\n");
	    } finally {
		w.close();
	    }
	} catch (IOException ioe) {
	    // Should print stacktrace or handle in a more informative manner.
	    messager.printMessage(ERROR,
				  "IOException encountered.",
				  propertyClass);
	}
    }

    /**
     * Generate a package statement corresponding to the package of
     * the argument element and written to the Writer.
     */
    private void generatePackageStatement(Element e, Writer w) throws IOException {
	PackageElement p = eltUtils.getPackageOf(e);
	if (!p.isUnnamed()) {
	    w.write("package ");
	    w.write(p.getQualifiedName().toString());
	    w.write(";\n");
	}
    }
}
