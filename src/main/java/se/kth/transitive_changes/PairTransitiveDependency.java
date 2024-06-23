package se.kth.transitive_changes;

/*
    * Represents a pair of transitive dependencies
    * Contains the new version and the old version

 */
public record PairTransitiveDependency(
        Dependency newVersion,
        Dependency oldVersion
) {

    @Override
    public String toString() {
        return "PairTransitiveDependency{" +
                "newVersion=" + newVersion +
                ", oldVersion=" + oldVersion +
                '}';
    }

    @Override
    public int hashCode() {
        return newVersion.hashCode() + oldVersion.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PairTransitiveDependency other = (PairTransitiveDependency) obj;
        return newVersion.equals(other.newVersion)
                && oldVersion.equals(other.oldVersion);
    }
}
