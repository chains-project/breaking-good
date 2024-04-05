package se.kth.breaking_changes;

import japicmp.model.*;
import lombok.Getter;
import se.kth.core.Instruction;

import java.util.*;


@Getter
public class JApiCmpElements implements JApiCompareScan {
    private final Set<ApiChange> changes = new HashSet<>();

    public Set<ApiChange> getAllChanges() {
        return changes;
    }

    public void visit(JApiClass cls) {


        Collection<JApiCompatibilityChange> bcs = cls.getCompatibilityChanges();
        ApiChange apiChange = new ApiChange();
        apiChange.setOldElement(cls.getOldClass().isPresent() ? cls.getOldClass().get().getName() : "");
        apiChange.setNewElement(cls.getNewClass().isPresent() ? cls.getNewClass().get().getName() : "");
        apiChange.setCategory(cls.getChangeStatus().toString());
        apiChange.setName(cls.getFullyQualifiedName());
        apiChange.setChangeType(cls.getChangeStatus());
        apiChange.setOldElement(cls.getOldClass().isPresent() ? cls.getOldClass().get().getName() : "");
        apiChange.setNewElement(cls.getNewClass().isPresent() ? cls.getNewClass().get().getName() : "");
        apiChange.setBehavior(null);
        apiChange.setInstruction(Instruction.Class.toString());
        changes.add(apiChange);


    }

    @Override
    public void visit(JApiMethod jApiMethod) {
        changes.add(new ApiChange(
                jApiMethod.getOldMethod().isPresent() ? jApiMethod.getOldMethod().get().getName() : "null",
                jApiMethod.getNewMethod().isPresent() ? jApiMethod.getNewMethod().get().getName() : "null",
                jApiMethod.getCompatibilityChanges().toString(),
                jApiMethod.getName(),
                jApiMethod.getOldMethod().isPresent() ? jApiMethod.getOldMethod().get().getLongName() : jApiMethod.getNewMethod().isPresent() ? jApiMethod.getNewMethod().get().getName() : "null",
                jApiMethod.getChangeStatus(),
                null,
                null,
                jApiMethod,
                Instruction.Method.toString()

        ));
    }

    @Override
    public void visit(JApiField f) {
        Collection<JApiCompatibilityChange> bcs = f.getCompatibilityChanges();


    }

    @Override
    public void visit(JApiConstructor cons) {
        Collection<JApiCompatibilityChange> bcs = cons.getCompatibilityChanges();


    }

    @Override
    public void visit(JApiImplementedInterface intf) {
        // Using visit(JApiClass jApiClass, JApiImplementedInterface jApiImplementedInterface)
    }

    @Override
    public void visit(JApiAnnotation ann) {
    }

    @Override
    public void visit(JApiSuperclass superCls) {
        superCls.getCompatibilityChanges().forEach(jApiCompatibilityChange -> {
            System.out.println(superCls.getJApiClassOwning().getFullyQualifiedName());
            System.out.println("jApiCompatibilityChange = " + jApiCompatibilityChange.getType()
            );
        });

        System.out.println("superCls = " + Arrays.stream(superCls.getCompatibilityChanges().toArray()).map(Object::toString));
        Collection<JApiCompatibilityChange> bcs = superCls.getCompatibilityChanges();


    }

    public void visit(JApiClass cls, JApiImplementedInterface intf) {
        Collection<JApiCompatibilityChange> bcs = intf.getCompatibilityChanges();
    }


}
