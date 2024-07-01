package se.kth.transitive_changes;

import lombok.Getter;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.breaking_changes.Download;
import se.kth.breaking_changes.JApiCmpAnalyze;
import se.kth.japianalysis.BreakingChange;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Getter
public class CompareTransitiveDependency {

    Dependency newVersion;

    Dependency oldVersion;

    ApiMetadata newApiMetadata;

    ApiMetadata oldApiMetadata;

    List<BreakingChange> breakingChanges;

    public CompareTransitiveDependency(Dependency newVersion, Dependency oldVersion) {
        this.newVersion = newVersion;
        this.oldVersion = oldVersion;
    }


    public static ApiMetadata convertToApiMetadata(Dependency dependency, Path folder) throws IOException, InterruptedException {

        File file = Download.getJarFile(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), folder, "jar");

        if (file == null) {
            System.out.println("Could not download the jar file for " + dependency);
            return null;
        }
        return new ApiMetadata(file.toPath());
    }

    public List<BreakingChange> getChangesBetweenDependencies() throws IOException, InterruptedException {

        Path tmp = Files.createTempDirectory("tmp");

        newApiMetadata = convertToApiMetadata(newVersion, tmp);

        oldApiMetadata = convertToApiMetadata(oldVersion, tmp);
        // compare the dependencies
        JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
                oldApiMetadata,
                newApiMetadata
        );
        return jApiCmpAnalyze.useJApiCmp_v2();
    }

    @Override
    public String toString() {
        return "CompareTransitiveDependency{" +
                "newVersion=" + newVersion +
                ", oldVersion=" + oldVersion +

                '}';
    }




}
