package se.kth.spoon_compare;

import japicmp.model.JApiCompatibilityChangeType;
import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.MethodBreakingChange;
import spoon.reflect.code.CtConstructorCall;

import java.util.Objects;

public class SpoonCtConstructorCall {
    private JApiCompatibilityChangeType compatibilityChangeType;
    private CtConstructorCall<?> invocation;
    private ApiChange apiChange;

    public SpoonCtConstructorCall(CtConstructorCall<?> invocation, ApiChange apiChange) {
        Objects.requireNonNull(invocation);
        Objects.requireNonNull(apiChange);
        this.compatibilityChangeType = apiChange.getCompatibilityChange();
        this.invocation = invocation;
        this.apiChange = apiChange;
    }

    public boolean compare() {
        return switch (compatibilityChangeType) {
            case CONSTRUCTOR_REMOVED -> constructorRemoved();
            default -> false;
        };
    }

    private boolean constructorRemoved() {
        String invocationSignature = invocation.getExecutable().getDeclaringType().getQualifiedName();
        String apiReference = ((MethodBreakingChange) apiChange.getReference()).getJApiMethod().getjApiClass().getFullyQualifiedName();
        return invocationSignature.equals(apiReference);

    }
}
