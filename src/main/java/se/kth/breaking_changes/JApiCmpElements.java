package se.kth.breaking_changes;

import japicmp.model.*;
import lombok.Getter;
import se.kth.core.Instruction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Getter
public class JApiCmpElements implements JApiCompareScan {
    private final Set<ApiChange> changes = new HashSet<>();

    public Set<ApiChange> getAllChanges() {
        return changes;
    }

    public void visit(JApiClass cls) {

        Collection<JApiCompatibilityChange> bcs = cls.getCompatibilityChanges();
        changes.addAll(bcs.stream().map(c -> {
            ApiChange apiChange = new ApiChange();
            apiChange.setCategory(cls.getChangeStatus().toString());
            apiChange.setName(cls.getFullyQualifiedName());
            apiChange.setChangeType(cls.getChangeStatus());
            apiChange.setCompatibilityChange(c);
            apiChange.setInstruction(Instruction.Class.toString());
            return apiChange;
        }).toList());

        //handle interfaces
        cls.getInterfaces().forEach(i ->
                visit(cls, i)
        );
    }

    @Override
    public void visit(JApiMethod jApiMethod) {
        changes.addAll(jApiMethod.getCompatibilityChanges().stream().map(c -> {
            ApiChange apiChange = new ApiChange();
            apiChange.setCategory(jApiMethod.getChangeStatus().toString());
            apiChange.setName(jApiMethod.getName());
            apiChange.setChangeType(jApiMethod.getChangeStatus());
            apiChange.setNewLongName(jApiMethod.getOldMethod().isPresent() ? jApiMethod.getOldMethod().get().getLongName() : jApiMethod.getNewMethod().isPresent() ? jApiMethod.getNewMethod().get().getName() : "null");
            apiChange.setCompatibilityChange(c);
            apiChange.setInstruction(Instruction.Method.toString());
            apiChange.setReference(new MethodBreakingChange(jApiMethod));
            return apiChange;
        }).toList());
    }

    @Override
    public void visit(JApiField f) {
        Collection<JApiCompatibilityChange> bcs = f.getCompatibilityChanges();
        changes.addAll(bcs.stream().map(c -> {
            ApiChange apiChange = new ApiChange();
            apiChange.setCategory(f.getChangeStatus().toString());
            apiChange.setName(f.getName());
            apiChange.setChangeType(f.getChangeStatus());
            apiChange.setCompatibilityChange(c);
            apiChange.setNewLongName(f.getName());
            apiChange.setInstruction(Instruction.Field.toString());
            apiChange.setReference(new FieldBreakingChange(f));
            return apiChange;
        }).toList());

    }

    @Override
    public void visit(JApiConstructor cons) {
        Collection<JApiCompatibilityChange> bcs = cons.getCompatibilityChanges();
        changes.addAll(bcs.stream().map(c -> {
            ApiChange apiChange = new ApiChange();
            apiChange.setCategory(cons.getChangeStatus().toString());
            apiChange.setName(cons.getName());
            apiChange.setChangeType(cons.getChangeStatus());
            apiChange.setCompatibilityChange(c);
            apiChange.setInstruction(Instruction.Constructor.toString());
            apiChange.setReference(new MethodBreakingChange(cons));
            return apiChange;
        }).toList());
    }

    @Override
    public void visit(JApiImplementedInterface intf) {
        // Using visit(JApiClass jApiClass, JApiImplementedInterface jApiImplementedInterface)
//        System.out.println("JApiImplementedInterface " + intf.getFullyQualifiedName() + " : " + (!intf.getCompatibilityChanges().isEmpty() ? intf.getCompatibilityChanges().get(0).getType() : "null"));

    }

    @Override
    public void visit(JApiAnnotation ann) {
    }

    @Override
    public void visit(JApiSuperclass superCls) {
        Collection<JApiCompatibilityChange> bcs = superCls.getCompatibilityChanges();
        changes.addAll(bcs.stream().map(c -> {
            ApiChange apiChange = new ApiChange();
            apiChange.setCategory(superCls.getChangeStatus().toString());
            apiChange.setName(superCls.getJApiClassOwning().getFullyQualifiedName());
            apiChange.setChangeType(superCls.getChangeStatus());
            apiChange.setCompatibilityChange(c);
            apiChange.setInstruction(Instruction.Class.toString());
            apiChange.setReference(new TypeBreakingChange(superCls.getJApiClassOwning()));
            return apiChange;
        }).toList());


    }

    public void visit(JApiClass cls, JApiImplementedInterface intf) {
        Collection<JApiCompatibilityChange> bcs = intf.getCompatibilityChanges();
        changes.addAll(bcs.stream().map(c -> {
            ApiChange apiChange = new ApiChange();
            apiChange.setCategory(cls.getChangeStatus().toString());
            apiChange.setName(cls.getFullyQualifiedName());
            apiChange.setChangeType(cls.getChangeStatus());
            apiChange.setCompatibilityChange(c);
            apiChange.setInstruction(Instruction.Class.toString());
            apiChange.setReference(new TypeBreakingChange(cls));
            return apiChange;
        }).toList());

    }


}
