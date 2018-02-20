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
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Created by Pavan.VijayaNar on 9/8/2017.
 */
@SupportedAnnotationTypes(
        {"com.example.KpiLog"}
)
public class KpiAnnotationProcessor extends AbstractProcessor {

    private Messager _messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        _messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        for (TypeElement te : annotations) {

            for (Element e : roundEnvironment.getElementsAnnotatedWith(te)) {
                if (e.getKind() == ElementKind.METHOD) {
                    debug(e, "method is annotated with " + te);

                }
                //fail(e, "Don't do this");
            }
        }
        return true;
    }

    private void debug(Element e, String message) {
        String printMsg = String.format("\n%s :%S",e.getSimpleName().toString(), message);
        _messager.printMessage(Diagnostic.Kind.OTHER, printMsg, e);
    }

    private void warn(Element e, String message) {
        String printMsg = String.format("\n%s :%S",e.getSimpleName().toString(), message);
        _messager.printMessage(Diagnostic.Kind.WARNING, printMsg, e);
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
