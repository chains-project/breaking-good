package se.kth.transitive_changes;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
public class Dependency {

    private String groupId;
    private String artifactId;
    private String version;
    private String scope;
    private String type;


    public Dependency(String groupId, String artifactId, String version, String type, String scope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.scope = scope;
        this.type = type;

    }

    public Dependency(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;

    }

    @Override
    public String toString() {
        return "Dependency{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return groupId.hashCode() + artifactId.hashCode() + version.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Dependency other = (Dependency) obj;
        return groupId.equals(other.groupId)
                && artifactId.equals(other.artifactId)
                && version.equals(other.version);
    }

    public static Set<Dependency> findUniqueDependencies(Set<Dependency> v1, Set<Dependency> v2) {
        Set<String> v1Identifiers = new HashSet<>();

        // Create a set of "groupId:artifactId" for all dependencies in v1
        for (Dependency dep : v1) {
            v1Identifiers.add(dep.getGroupId() + ":" + dep.getArtifactId() + ":" + dep.getScope());
        }

        Set<Dependency> uniqueDependencies = new HashSet<>();

        // Find dependencies in v2 that don't have the same "groupId:artifactId" in v1
        for (Dependency dep : v2) {
            String identifier = dep.getGroupId() + ":" + dep.getArtifactId() + ":" + dep.getScope();
            if (!v1Identifiers.contains(identifier)) {
                uniqueDependencies.add(dep);
            }
        }

        return uniqueDependencies;
    }

    public static Set<DependencyRecord> findChangedDependenciesBySemVer(Set<Dependency> oldDependencies, Set<Dependency> newDependencies) {
        Set<DependencyRecord> changedDependencies = new HashSet<>();

        for (Dependency oldDep : oldDependencies) {
            for (Dependency newDep : newDependencies) {
                if (oldDep.getGroupId().equals(newDep.getGroupId())
                        && oldDep.getArtifactId().equals(newDep.getArtifactId())) {
                    if (!oldDep.getVersion().equals(newDep.getVersion())) {
                        // Determine SemVer change type (Major, Minor, Patch)
                        if (isMajorChange(oldDep.getVersion(), newDep.getVersion())) {
                            changedDependencies.add(new DependencyRecord(oldDep, newDep, "MAJOR"));
                        } else if (isMinorChange(oldDep.getVersion(), newDep.getVersion())) {
                            changedDependencies.add(new DependencyRecord(oldDep, newDep, "MINOR"));
                        } else if (isPatchChange(oldDep.getVersion(), newDep.getVersion())) {
                            changedDependencies.add(new DependencyRecord(oldDep, newDep, "PATCH"));
                        }
                    }
                    break; // No need to check further for this old dependency
                }
            }
        }

        return changedDependencies;
    }

    public static Set<DependencyRecord> findModifiedDependenciesByVersionAndScope(Set<Dependency> oldDependencies, Set<Dependency> newDependencies) {
        Set<DependencyRecord> modifiedDependencies = new HashSet<>();

        for (Dependency oldDep : oldDependencies) {
            for (Dependency newDep : newDependencies) {
                if (oldDep.getGroupId().equals(newDep.getGroupId())
                        && oldDep.getArtifactId().equals(newDep.getArtifactId())
                        && oldDep.getType().equals(newDep.getType())) {
                    if (!oldDep.getScope().equals(newDep.getScope())) {
                        modifiedDependencies.add(new DependencyRecord(oldDep, newDep, "SCOPE_ONLY"));
                    } else if (!oldDep.getVersion().equals(newDep.getVersion())) {
                        modifiedDependencies.add(new DependencyRecord(oldDep, newDep, "VERSION_ONLY"));
                    }
                    break; // No need to check further for this old dependency
                }
            }
        }
        return modifiedDependencies;
    }

    private static boolean isMajorChange(String oldVersion, String newVersion) {
        // Implement logic to determine if it's a major version change
        // For simplicity, assuming version format is like "X.Y.Z"
        return !getVersionPart(oldVersion, 0).equals(getVersionPart(newVersion, 0));
    }

    private static boolean isMinorChange(String oldVersion, String newVersion) {
        // Implement logic to determine if it's a minor version change
        return getVersionPart(oldVersion, 0).equals(getVersionPart(newVersion, 0))
                && !getVersionPart(oldVersion, 1).equals(getVersionPart(newVersion, 1));
    }

    private static boolean isPatchChange(String oldVersion, String newVersion) {
        // Implement logic to determine if it's a patch version change
        return getVersionPart(oldVersion, 0).equals(getVersionPart(newVersion, 0))
                && getVersionPart(oldVersion, 1).equals(getVersionPart(newVersion, 1))
                && !getVersionPart(oldVersion, 2).equals(getVersionPart(newVersion, 2));
    }

    private static String getVersionPart(String version, int index) {
        String[] parts = version.split("\\.");
        if (index < parts.length) {
            return parts[index];
        }
        return "";
    }

    // Record to store change details

}
