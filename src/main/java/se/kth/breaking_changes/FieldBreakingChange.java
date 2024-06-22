package se.kth.breaking_changes;

import japicmp.model.JApiField;

public class FieldBreakingChange extends AbstractApiChange {

    private final JApiField jApiField;

    public FieldBreakingChange(JApiField jApiField) {
        this.jApiField = jApiField;
    }

    public JApiField getJApiField() {
        return jApiField;
    }

    @Override
    public String toString() {
        return "FieldBreakingChange{" +
                "jApiField=" + jApiField +
                '}';
    }


    @Override
    public String variantName() {
        return null;
    }

    @Override
    public String getFullQualifiedName() {
        return jApiField.getjApiClass().getFullyQualifiedName() + "." + jApiField.getName();

    }
}
