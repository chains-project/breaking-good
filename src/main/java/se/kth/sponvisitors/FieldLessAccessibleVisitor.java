package se.kth.sponvisitors;



import japicmp.model.AccessModifier;
import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtFieldReference;
import util.SpoonHelpers;

/**
 * Broken brokenChanges of FIELD_LESS_ACCESSIBLE are:
 * - Any access to a now-private field
 * - Any access to a now-package-private field outside the package
 * - Any access to a now-protected field outside its subtype hierarchy or package
 * <p>
 * 	TODO: what about inner classes (e.g. using private fields of the outer?)
 */
public class FieldLessAccessibleVisitor extends BreakingChangeVisitor {
	private final CtFieldReference<?> fRef;
	private final AccessModifier newAccessModifier;

	public FieldLessAccessibleVisitor(CtFieldReference<?> fRef, AccessModifier newAccessModifier) {
		super(JApiCompatibilityChangeType.FIELD_LESS_ACCESSIBLE);
		this.fRef = fRef;
		this.newAccessModifier = newAccessModifier;
	}

	@Override
	public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
		visitCtFieldAccess(fieldRead);
	}

	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		visitCtFieldAccess(fieldWrite);
	}

	private <T> void visitCtFieldAccess(CtFieldAccess<T> fieldAccess) {
		if (fRef.equals(fieldAccess.getVariable())) {
			String enclosingPkg = SpoonHelpers.getEnclosingPkgName(fieldAccess);
			String expectedPkg = SpoonHelpers.getEnclosingPkgName(fRef.getFieldDeclaration());

			switch (newAccessModifier) {
				// Private always breaks
				case PRIVATE -> brokenUse(fieldAccess, fieldAccess.getVariable(), fRef, APIUse.FIELD_ACCESS);

				// Package-private breaks if packages do not match
				case PACKAGE_PROTECTED -> {
					if (!enclosingPkg.equals(expectedPkg))
						brokenUse(fieldAccess, fieldAccess.getVariable(), fRef, APIUse.FIELD_ACCESS);
				}
				// Protected fails if not a subtype and packages do not match
				case PROTECTED -> {
					if (!fieldAccess.getParent(CtType.class).isSubtypeOf(fRef.getDeclaringType()) &&
						!enclosingPkg.equals(expectedPkg))
						brokenUse(fieldAccess, fieldAccess.getVariable(), fRef, APIUse.FIELD_ACCESS);
				}
				default -> {
				}
				// Can't happen
			}
		}
	}
}
