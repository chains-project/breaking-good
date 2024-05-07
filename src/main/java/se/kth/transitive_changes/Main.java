package se.kth.transitive_changes;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.core.Util;
import se.kth.data.JsonUtils;

import java.nio.file.Path;
import java.util.Set;

public class Main {


    public static void main(String[] args) {

        ApiMetadata apiMetadata = new ApiMetadata(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/0a11c04038eae517540051dbf51f7f26b7221f20/snakeyaml-1.24.jar"));
        ApiMetadata apiMetadata2 = new ApiMetadata(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/0a11c04038eae517540051dbf51f7f26b7221f20/snakeyaml-2.0.jar"));

        Set<Dependency> v1 = MavenTree.read(apiMetadata);
        Set<Dependency> v2 = MavenTree.read(apiMetadata2);

        Set<PairTransitiveDependency> transitiveDependencies = MavenTree.diff(v1, v2);

        for (PairTransitiveDependency pair : transitiveDependencies) {
            try {
                System.out.println("Comparing " + pair.newVersion() + " and " + pair.oldVersion());
                CompareTransitiveDependency compareTransitiveDependency = new CompareTransitiveDependency(pair.newVersion(), pair.oldVersion());
                compareTransitiveDependency.compareDependency();
                System.out.println("Breaking changes for " + pair.newVersion() + " and " + pair.oldVersion());
                System.out.println("Breaking Changes amount: " + compareTransitiveDependency.getBreakingChanges().size());

                JsonUtils.writeToFile(Path.of("breaking-changes-%s.json".formatted(pair.oldVersion().getArtifactId())),compareTransitiveDependency);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
