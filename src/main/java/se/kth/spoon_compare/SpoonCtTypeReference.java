package se.kth.spoon_compare;

import japicmp.model.JApiCompatibilityChangeType;
import se.kth.breaking_changes.ApiChange;
import se.kth.core.Util;
import spoon.reflect.code.CtInvocation;

import java.util.Objects;

public class SpoonCtTypeReference {

    private JApiCompatibilityChangeType compatibilityChangeType;
    private CtInvocation<?> invocation;
    private ApiChange apiChange;

    public SpoonCtTypeReference(CtInvocation<?> invocation, ApiChange apiChange) {
        Objects.requireNonNull(invocation);
        Objects.requireNonNull(apiChange);
        this.compatibilityChangeType = apiChange.getCompatibilityChange();
        this.invocation = invocation;
        this.apiChange = apiChange;
    }

    public boolean compare() {
        return switch (compatibilityChangeType) {
            case CLASS_LESS_ACCESSIBLE -> classLessAccessible();
            case ANNOTATION_DEPRECATED_ADDED -> annotationDeprecatedAdded();
            case CLASS_REMOVED -> classRemoved();
            default -> false;
        };
    }

    private boolean classRemoved() {
        String invocationName = Util.fullyQualifiedName(invocation.getExecutable());
        String apiReference = apiChange.getReference().getFullQualifiedName();
        return invocationName.equals(apiReference);
    }

    private boolean annotationDeprecatedAdded() {
        return false;
    }

    private boolean classLessAccessible() {
        return false;
    }
}
