package se.kth.breaking_changes;

public abstract class AbstractApiChange implements IAbstractApiChange {

    public abstract String variantName() ;

    public abstract String getFullQualifiedName();
}
