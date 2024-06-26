package se.kth.sponvisitors;

import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.reference.CtFieldReference;

/**
 * Visitor in charge of gathering field deprecated issues in client code.
 * <p>
 * The visitor detects the following cases:
 * <ul>
 * <li> Any reference to the now-deprecated field. Example:
 *      <pre>
 *      var a = field;
 *      self.field = 10;
 *      </pre>
 * </ul>
 */
public class AnnotationDeprecatedAddedToFieldVisitor extends FieldReferenceVisitor {
	/**
	 * Creates a {@link AnnotationDeprecatedAddedToFieldVisitor} instance.
	 *
	 * @param fRef the now-deprecated field
	 */
	public AnnotationDeprecatedAddedToFieldVisitor(CtFieldReference<?> fRef) {
		super(fRef, JApiCompatibilityChangeType.ANNOTATION_DEPRECATED_ADDED);
	}
}
