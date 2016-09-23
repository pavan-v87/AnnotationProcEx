package com.example;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Created by Pavan.VijayaNar on 8/8/2016.
 */
@SupportedAnnotationTypes(
        {"com.example.PrintMe"}
)
public class MyProcessor extends AbstractProcessor {
    private Messager _messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        _messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement te : annotations) {
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
                if (e.getKind() == ElementKind.CLASS) {

                }
                //fail(e, "Don't do this");
            }
        }
        return true;
    }

    private void fail(Element e, String message) {
        String printMsg = String.format("\n%s :%S",e.getSimpleName().toString(), message);
        _messager.printMessage(Diagnostic.Kind.ERROR, printMsg, e);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }
}
