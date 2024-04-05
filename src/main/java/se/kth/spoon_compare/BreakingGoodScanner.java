package se.kth.spoon_compare;

import se.kth.breaking_changes.ApiChange;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

import java.util.Set;

public class BreakingGoodScanner extends CtScanner {

    private Set<ApiChange> apiChanges;
    private int clientLine;

//    private final Collection<CtScanner> visitors;
//    private final Options options;
//    private final org.slf4j.Logger logger;

    public BreakingGoodScanner() {
    }

    @Override
    public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
        //// visitors.forEach(v -> v.visitCtTypeReference(reference));
        System.out.print("TypeReference Simple Name: " + reference.getSimpleName());
        System.out.println("  Position:  " + (reference.getPosition().isValidPosition() ? reference.getPosition().getLine() : "Invalid"));
        System.out.println("TypeReference: " + reference.getQualifiedName());
        System.out.println("TypeReference: " + reference.isClass());
        System.out.println("TypeReference: " + reference.isInterface());
        System.out.println("TypeReference: " + reference.getReferencedTypes().toString());
        System.out.println("Parent: " + reference.getParent().toString());
        super.visitCtTypeReference(reference);
    }
}