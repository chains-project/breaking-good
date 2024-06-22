package se.kth.sponvisitors;


import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.reference.CtFieldReference;

public class FieldReferenceVisitor extends BreakingChangeVisitor {
	/**
	 * Spoon reference to the removed field.
	 */
	private final CtFieldReference<?> fRef;

	/**
	 * Creates a {@link FieldReferenceVisitor} instance.
	 *
	 * @param fRef   modified field reference
	 * @param change kind of breaking change
	 */
	protected FieldReferenceVisitor(CtFieldReference<?> fRef, JApiCompatibilityChangeType change) {
		super(change);
		this.fRef = fRef;
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		if (fRef.equals(reference))
			brokenUse(reference, reference, fRef, APIUse.FIELD_ACCESS);
	}
}
