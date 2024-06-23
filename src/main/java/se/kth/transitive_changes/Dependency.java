package se.kth.transitive_changes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dependency {

    private String groupId;
    private String artifactId;
    private String version;


    public Dependency(String groupId, String artifactId, String scope, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
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
}
