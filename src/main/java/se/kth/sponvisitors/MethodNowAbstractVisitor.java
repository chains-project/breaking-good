package se.kth.sponvisitors;


import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * Broken brokenChanges of METHOD_NOW_ABSTRACT are:
 * - Non-abstract types extending/implementing the enclosing type of the now-abstract method unless:
 * - The now-abstract method is already implemented somewhere in the hierarchy
 * - Invocations in subtypes of the now-abstract method
 */
public class MethodNowAbstractVisitor extends BreakingChangeVisitor {
	private final CtExecutableReference<?> mRef;

	public MethodNowAbstractVisitor(CtExecutableReference<?> mRef) {
		super(JApiCompatibilityChangeType.METHOD_NOW_ABSTRACT);
		this.mRef = mRef;
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		CtTypeReference<?> enclosingType = mRef.getDeclaringType();
		if (
			!ctClass.isAbstract() &&
			ctClass.isSubtypeOf(enclosingType) &&
			mRef.getOverridingExecutable(ctClass.getReference()) == null
		)
			brokenUse(ctClass, enclosingType, mRef,
				enclosingType.isInterface() ? APIUse.IMPLEMENTS : APIUse.EXTENDS);
	}

	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		if (mRef.equals(invocation.getExecutable())) {
			brokenUse(invocation, invocation.getExecutable(), mRef, APIUse.METHOD_INVOCATION);
		}
	}
}
