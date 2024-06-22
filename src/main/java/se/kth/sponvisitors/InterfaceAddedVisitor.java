package se.kth.sponvisitors;

import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.reference.CtTypeReference;

import java.util.Set;

/**
 * Visitor in charge of gathering all interface added issues in client code.
 */
public class InterfaceAddedVisitor extends SupertypeAddedVisitor {
	/**
	 * Creates a InterfaceAddedVisitor instance.
	 *
	 * @param clsRef        reference to the client impacted class
	 * @param newInterfaces added interfaces
	 */
	public InterfaceAddedVisitor(CtTypeReference<?> clsRef, Set<CtTypeReference<?>> newInterfaces) {
		super(clsRef, newInterfaces, JApiCompatibilityChangeType.INTERFACE_ADDED);
	}
}
