package se.kth.spoon_compare;

import japicmp.model.JApiCompatibilityChangeType;
import se.kth.breaking_changes.ApiChange;
import se.kth.core.Util;
import spoon.reflect.code.CtInvocation;

@lombok.Getter
@lombok.Setter
public class SpoonCtInvocation {

    private JApiCompatibilityChangeType compatibilityChangeType;
    private CtInvocation<?> invocation;
    private ApiChange apiChange;

    public SpoonCtInvocation(CtInvocation<?> invocation, ApiChange apiChange) {
        this.compatibilityChangeType = apiChange.getCompatibilityChange().getType();
        this.invocation = invocation;
        this.apiChange = apiChange;
    }


    public boolean compare() {
        switch (compatibilityChangeType) {
            case METHOD_REMOVED:
                return methodRemove();

            case INTERFACE_REMOVED:
                interfaceRemove();
                break;

            case SUPERCLASS_REMOVED:
                superClassRemove();
                break;
            case CONSTRUCTOR_REMOVED:
                return constructorRemove();

            case METHOD_NOW_ABSTRACT:
                return methodNowAbstract();

            case METHOD_RETURN_TYPE_CHANGED:
                return methodReturnTypeChanged();

            default:
                return false;

        }
        return false;
    }

    private boolean methodReturnTypeChanged() {
        String invocationName = Util.fullyQualifiedName(invocation.getExecutable());
        String apiReference = apiChange.getReference().getFullQualifiedName();
        return invocationName.equals(apiReference);
    }

    private boolean methodNowAbstract() {
        String invocationName = Util.fullyQualifiedName(invocation.getExecutable());
        String apiReference = apiChange.getReference().getFullQualifiedName();
        return invocationName.equals(apiReference);
    }

    private void superClassRemove() {
    }

    private boolean constructorRemove() {
        String invocationName = Util.fullyQualifiedName(invocation.getExecutable());
        String apiReference = apiChange.getReference().getFullQualifiedName();
        return invocationName.equals(apiReference);
    }

    private void interfaceRemove() {
        String invocationName = Util.fullyQualifiedName(invocation.getExecutable());

    }


    public boolean methodRemove() {

        String invocationName = Util.fullyQualifiedName(invocation.getExecutable());
        String apiReference = apiChange.getReference().getFullQualifiedName();
       if(apiReference.contains("setPort")){
           apiReference = apiReference.split("::")[1];}
        return invocationName.equals(apiReference);
    }


}
