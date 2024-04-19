package se.kth.spoon_compare;

import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.code.CtInvocation;

@lombok.Getter
@lombok.Setter
public class SpoonCtInvocation {

    private JApiCompatibilityChangeType compatibilityChangeType;
    private CtInvocation<?> invocation;

    public SpoonCtInvocation(JApiCompatibilityChangeType compatibilityChangeType, CtInvocation<?> invocation) {
        this.compatibilityChangeType = compatibilityChangeType;
        this.invocation = invocation;
    }


    public void compare() {
        switch (compatibilityChangeType) {
            case METHOD_REMOVED:
                methodRemove();
                break;

        }
        System.out.println("Comparing SpoonCtInvocation");
    }


    public void methodRemove() {

    }


}
