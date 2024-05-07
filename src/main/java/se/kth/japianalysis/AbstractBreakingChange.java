package se.kth.japianalysis;

import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;

import java.util.Objects;

public abstract class AbstractBreakingChange implements BreakingChange {
    protected final JApiCompatibilityChangeType change;
    protected CtElement sourceElement;

    protected AbstractBreakingChange(JApiCompatibilityChangeType change) {
        this.change = Objects.requireNonNull(change);
    }

    @Override
    public JApiCompatibilityChangeType getChange() {
        return change;
    }

    @Override
    public CtElement getSourceElement() {
        return this.sourceElement;
    }

    @Override
    public void setSourceElement(CtElement element) {
        this.sourceElement = Objects.requireNonNull(element);
    }


    @Override
    public String toString() {
        return """
                [%s]
                Reference: %s
                Source: %s %s
                """.formatted(
                getChange(),
                getReference(),
                getSourceElement() instanceof CtNamedElement ne ? ne.getSimpleName() : getSourceElement(),
                getSourceElement() != null ? getSourceElement().getPosition() : "<no source>");
    }
}
