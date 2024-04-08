package se.kth.breaking_changes;

import japicmp.model.JApiClass;

public class TypeBreakingChange extends AbstractApiChange{

    private final JApiClass jApiClass;

    public TypeBreakingChange(JApiClass jApiClass) {
        this.jApiClass = jApiClass;
    }

}
