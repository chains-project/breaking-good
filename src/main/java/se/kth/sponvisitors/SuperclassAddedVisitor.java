package se.kth.sponvisitors;

import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.reference.CtTypeReference;

import java.util.Set;

/**
 * Visitor in charge of gathering all superclass added issues in client code.
 */
public class SuperclassAddedVisitor extends SupertypeAddedVisitor {
	public SuperclassAddedVisitor(CtTypeReference<?> clsRef, CtTypeReference<?> newClass) {
		super(clsRef, Set.of(newClass), JApiCompatibilityChangeType.SUPERCLASS_ADDED);
	}
}
