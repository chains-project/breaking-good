package se.kth.sponvisitors;

import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.reference.CtTypeReference;

/**
 * Visitor in charge of gathering class removed issues in client code.
 */
public class ClassRemovedVisitor extends TypeReferenceVisitor {
	/**
	 * Creates a ClassRemovedVisitor instance.
	 *
	 * @param clsRef the now-removed class
	 */
	public ClassRemovedVisitor(CtTypeReference<?> clsRef) {
		super(clsRef, JApiCompatibilityChangeType.CLASS_REMOVED);
	}
}
