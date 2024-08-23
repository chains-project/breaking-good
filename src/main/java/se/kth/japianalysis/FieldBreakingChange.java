package se.kth.japianalysis;


import japicmp.model.JApiCompatibilityChangeType;
import japicmp.model.JApiField;
import javassist.NotFoundException;
import se.kth.sponvisitors.*;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.Objects;

/**
 * Represents a field-level breaking change
 */
public class FieldBreakingChange extends AbstractBreakingChange {
	private final JApiField jApiField;
	private final CtFieldReference<?> fRef;

	public FieldBreakingChange(JApiField field, CtFieldReference<?> fRef, JApiCompatibilityChangeType change) {
		super(change);
		this.jApiField = Objects.requireNonNull(field);
		this.fRef = Objects.requireNonNull(fRef);
	}

	@Override
	public CtReference getReference() {
		return fRef;
	}

	@Override
	public BreakingChangeVisitor getVisitor() {
		return
			switch (change) {
				case FIELD_REMOVED               -> new FieldRemovedVisitor(fRef);
				case FIELD_NOW_FINAL             -> new FieldNowFinalVisitor(fRef);
				case FIELD_NO_LONGER_STATIC      -> new FieldNoLongerStaticVisitor(fRef);
				case FIELD_NOW_STATIC            -> new FieldNowStaticVisitor(fRef);
				case FIELD_LESS_ACCESSIBLE       -> new FieldLessAccessibleVisitor(fRef, jApiField.getAccessModifier().getNewModifier().get());
				case ANNOTATION_DEPRECATED_ADDED -> new AnnotationDeprecatedAddedToFieldVisitor(fRef);
				case FIELD_TYPE_CHANGED -> {
					try {
						// Thanks for the checked exception japi <3
						String newTypeName = jApiField.getNewFieldOptional().get().getType().getName();
						CtTypeReference<?> newType = fRef.getFactory().Type().createReference(newTypeName);
						yield new FieldTypeChangedVisitor(fRef, newType);
					} catch (NotFoundException e) {
						System.out.println("japicmp gave us a FIELD_TYPE_CHANGED without the new type of the field");
						yield null;
//						throw new IllegalStateException("japicmp gave us a FIELD_TYPE_CHANGED without the new type of the field");
					}

				}
				// TODO: To be implemented
				case FIELD_STATIC_AND_OVERRIDES_STATIC, FIELD_GENERICS_CHANGED -> null;
				default ->
					null;
			};
	}
}
