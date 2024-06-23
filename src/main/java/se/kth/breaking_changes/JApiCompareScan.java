package se.kth.breaking_changes;

import japicmp.model.*;
import japicmp.output.Filter;

import java.util.Iterator;
import java.util.List;

/**
 * Based on the JApiCmpVisitor from japicmp.
 * Also is based on Maracas' JApiCmpDeltaVisitor.
 *
 */
public interface JApiCompareScan {

    void visit(JApiClass jApiClass);

    void visit(JApiMethod jApiMethod);

    void visit(JApiConstructor jApiCons);

    void visit(JApiImplementedInterface jApiImpl);

    void visit(JApiField jApiField);

    void visit(JApiAnnotation jApiAnnotation);

    void visit(JApiSuperclass jApiSuper);

    static void visit(List<JApiClass> classes, JApiCompareScan visitor) {
        Filter.filter(classes, new Filter.FilterVisitor() {
            @Override
            public void visit(Iterator<JApiClass> iterator, JApiClass jApiClass) {
                visitor.visit(jApiClass);
            }

            @Override
            public void visit(Iterator<JApiMethod> iterator, JApiMethod jApiMethod) {
                visitor.visit(jApiMethod);
            }

            @Override
            public void visit(Iterator<JApiConstructor> iterator, JApiConstructor jApiConstructor) {
                visitor.visit(jApiConstructor);
            }

            @Override
            public void visit(Iterator<JApiImplementedInterface> iterator,
                              JApiImplementedInterface jApiImplementedInterface) {
                visitor.visit(jApiImplementedInterface);
            }

            @Override
            public void visit(Iterator<JApiField> iterator, JApiField jApiField) {
                visitor.visit(jApiField);
            }

            @Override
            public void visit(Iterator<JApiAnnotation> iterator, JApiAnnotation jApiAnnotation) {
                visitor.visit(jApiAnnotation);
            }

            @Override
            public void visit(JApiSuperclass jApiSuperclass) {
                visitor.visit(jApiSuperclass);
            }
        });
    }
}
