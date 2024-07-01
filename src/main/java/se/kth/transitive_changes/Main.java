package se.kth.transitive_changes;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.data.JsonUtils;
import se.kth.japianalysis.BreakingChange;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static se.kth.transitive_changes.Dependency.findUniqueDependencies;

public class Main {


    public static void main(String[] args) {

        ApiMetadata apiMetadata = new ApiMetadata(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/db02c6bcb989a5b0f08861c3344b532769530467/asto-core-v1.13.0.jar"));
        ApiMetadata apiMetadata2 = new ApiMetadata(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/db02c6bcb989a5b0f08861c3344b532769530467/asto-core-v1.15.3.jar"));

        Dependency oldVersion = new Dependency("com.artipie", "asto-core", "v1.13.0", "jar", "compile");
        Dependency newVersion = new Dependency("com.artipie", "asto-core", "v1.15.3", "jar", "compile");


        Set<Dependency> v1 = MavenTree.read(apiMetadata, oldVersion);
        Set<Dependency> v2 = MavenTree.read(apiMetadata2, newVersion);

        Set<PairTransitiveDependency> transitiveDependencies = MavenTree.diff(v1, v2);


        Set<Dependency> newDependencies = findUniqueDependencies(v1, v2);



        Set<DependencyRecord> changedDependencies = Dependency.findChangedDependenciesBySemVer(v1, v2);
        System.out.println("Dependencias que han cambiado según SemVer:");
        for (DependencyRecord record : changedDependencies) {
            System.out.println(record.oldDependency().getArtifactId() + " - " + record.changeType() + ": " +
                    record.oldDependency().getVersion() + " -> " + record.newDependency().getVersion());
        }

        System.out.println();

        // Encontrar dependencias que han modificado solo versión y tipo
        Set<DependencyRecord> modifiedDependencies = Dependency.findModifiedDependenciesByVersionAndScope(v1, v2);
        System.out.println("Dependencias que han modificado solo versión y tipo:");
        for (DependencyRecord record : modifiedDependencies) {
            System.out.println(record.oldDependency().getArtifactId() + ": " +
                    record.oldDependency().getVersion() + " -> " + record.newDependency().getVersion());
        }


        Set<Dependency> removedDependencies = findUniqueDependencies(v2, v1);

        for (PairTransitiveDependency pair : transitiveDependencies) {
            try {
                System.out.println("Comparing " + pair.newVersion() + " and " + pair.oldVersion());
                CompareTransitiveDependency compareTransitiveDependency = new CompareTransitiveDependency(pair.newVersion(), pair.oldVersion());
                List<BreakingChange> changes = compareTransitiveDependency.getChangesBetweenDependencies();
                System.out.println("Breaking changes for " + pair.newVersion() + " and " + pair.oldVersion());
                System.out.println("Breaking Changes amount: " + changes.size());

                JsonUtils.writeToFile(Path.of("breaking-changes-%s.json".formatted(pair.oldVersion().getArtifactId())), compareTransitiveDependency);




            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
