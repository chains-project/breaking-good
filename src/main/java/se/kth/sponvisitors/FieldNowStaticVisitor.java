package se.kth.sponvisitors;

import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.reference.CtFieldReference;

/**
 * Broken uses of FIELD_NOW_STATIC are:
 * - Currently none
 * <p>
 * AFAIK, FIELD_NOW_STATIC is source-compatible (though japicmp says otherwise?)
 * We still need to implement broken uses for binary incompatibilities
 */
public class FieldNowStaticVisitor extends BreakingChangeVisitor {
	public FieldNowStaticVisitor(CtFieldReference<?> fRef) {
		super(JApiCompatibilityChangeType.FIELD_NOW_STATIC);
	}
}
