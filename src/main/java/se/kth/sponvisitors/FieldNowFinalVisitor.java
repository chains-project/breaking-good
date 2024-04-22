package se.kth.sponvisitors;


import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.reference.CtFieldReference;

/**
 * Broken uses of FIELD_NOW_FINAL are:
 * - Attempting to write-access a now-final field
 */
public class FieldNowFinalVisitor extends BreakingChangeVisitor {
	private final CtFieldReference<?> fRef;

	public FieldNowFinalVisitor(CtFieldReference<?> fRef) {
		super(JApiCompatibilityChangeType.FIELD_NOW_FINAL);
		this.fRef = fRef;
	}

	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		if (fRef.equals(fieldWrite.getVariable()))
			brokenUse(fieldWrite, fieldWrite.getVariable(), fRef, APIUse.FIELD_ACCESS);
	}
}
