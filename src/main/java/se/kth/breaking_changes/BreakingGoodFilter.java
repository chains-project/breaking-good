package se.kth.breaking_changes;

import japicmp.model.*;
import japicmp.output.OutputFilter;

import java.util.Iterator;
import java.util.List;

/**
 * Extends the default {@link OutputFilter}'s behavior with additional filters:
 * - Remove BCs related to anonymous classes, as they're not exposed
 * - Remove BCs on NEW types, as they won't affect anyone
 * - Remove any BC type that is excluded in Breaking good filter options
 */
public class BreakingGoodFilter extends OutputFilter {

    private final BreakingGoodOptions options;

    public BreakingGoodFilter(BreakingGoodOptions options) {
        super(options.getDefaultOptions());
        this.options = options;
    }

    @Override
    public void filter(List<JApiClass> jApiClasses) {
        filter(jApiClasses, new FilterVisitor() {
            @Override
            public void visit(Iterator<JApiClass> iterator, JApiClass jApiClass) {
                jApiClass.getCompatibilityChanges().removeIf(c -> options.getExclude().contains(c.getType()));
//                jApiClass.getCompatibilityChanges().removeAll(options.getExclude());
            }

            @Override
            public void visit(Iterator<JApiMethod> iterator, JApiMethod jApiMethod) {
                jApiMethod.getCompatibilityChanges().removeIf(c -> options.getExclude().contains(c.getType()));
            }

            @Override
            public void visit(Iterator<JApiConstructor> iterator, JApiConstructor jApiConstructor) {
                jApiConstructor.getCompatibilityChanges().removeIf(c -> options.getExclude().contains(c.getType()));
            }

            @Override
            public void visit(Iterator<JApiImplementedInterface> iterator, JApiImplementedInterface jApiImplementedInterface) {
                jApiImplementedInterface.getCompatibilityChanges().removeIf(c -> options.getExclude().contains(c.getType()));
            }

            @Override
            public void visit(Iterator<JApiField> iterator, JApiField jApiField) {
                jApiField.getCompatibilityChanges().removeIf(c -> options.getExclude().contains(c.getType()));
            }

            @Override
            public void visit(Iterator<JApiAnnotation> iterator, JApiAnnotation jApiAnnotation) {
                jApiAnnotation.getCompatibilityChanges().removeIf(c -> options.getExclude().contains(c.getType()));
            }

            @Override
            public void visit(JApiSuperclass jApiSuperclass) {
                jApiSuperclass.getCompatibilityChanges().removeIf(c -> options.getExclude().contains(c.getType()));
            }
        });

        super.filter(jApiClasses);
    }

}
