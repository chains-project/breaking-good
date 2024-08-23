package se.kth.transitive_changes;

public record DependencyRecord(Dependency oldDependency, Dependency newDependency, String changeType) {
}


