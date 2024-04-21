package se.kth.breaking_changes;

import japicmp.model.JApiClass;

public class TypeBreakingChange extends AbstractApiChange{

    private final JApiClass jApiClass;

    public TypeBreakingChange(JApiClass jApiClass) {
        this.jApiClass = jApiClass;
    }

    @Override
    public String variantName() {
        return null;
    }

    @Override
    public String getFullQualifiedName() {
        return jApiClass.getFullyQualifiedName();
    }
}
