package se.kth.sponvisitors;

import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.reference.CtTypeReference;

/**
 * FIXME: the current implementation only targets @Deprecated types
 */
public class AnnotationDeprecatedAddedToClassVisitor extends TypeReferenceVisitor {
	public AnnotationDeprecatedAddedToClassVisitor(CtTypeReference<?> clsRef) {
		super(clsRef, JApiCompatibilityChangeType.ANNOTATION_DEPRECATED_ADDED);
	}
}
