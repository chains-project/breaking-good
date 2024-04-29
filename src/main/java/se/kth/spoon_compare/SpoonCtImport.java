package se.kth.spoon_compare;

import japicmp.model.JApiCompatibilityChangeType;
import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.TypeBreakingChange;
import spoon.reflect.declaration.CtImport;

import java.util.Objects;

public class SpoonCtImport {
    private JApiCompatibilityChangeType compatibilityChangeType;
    private CtImport invocation;
    private ApiChange apiChange;


    public SpoonCtImport(CtImport invocation, ApiChange apiChange) {
        Objects.requireNonNull(invocation);
        Objects.requireNonNull(apiChange);
        this.compatibilityChangeType = apiChange.getCompatibilityChange();
        this.invocation = invocation;
        this.apiChange = apiChange;
    }

    public boolean compare() {
        return switch (compatibilityChangeType) {
            case CLASS_REMOVED -> classRemoved();
            default -> false;
        };
    }

    private boolean classRemoved() {
        if (apiChange.getReference() == null) {
            return false;
        }
        if (invocation.getReference() == null) {
            return false;
        }
        String invocationSignature = invocation.getReference().toString();
        String apiReference = ((TypeBreakingChange) apiChange.getReference()).getFullQualifiedName();
        return invocationSignature.equals(apiReference);
    }
}
