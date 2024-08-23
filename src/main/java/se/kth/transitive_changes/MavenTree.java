package se.kth.transitive_changes;

import lombok.Getter;
import lombok.Setter;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.breaking_changes.Download;

import util.MavenCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class MavenTree {

    ApiMetadata apiMetadata;
    Dependency dependency;

    public MavenTree() {

    }

    /**
     * Download the pom file and create a tree of dependencies
     *
     * @param apiMetadata the metadata of the api
     * @return the set of dependencies
     */
    public static Set<Dependency> read(ApiMetadata apiMetadata, Dependency dependency) {
        // read the pom file and create a tree
        System.out.printf("Reading the tree for %s%n", dependency.toString());
        try {
            Path treeFile = Files.createFile(Path.of("trees-%s-%s-%s.txt".formatted(dependency.getArtifactId(), dependency.getVersion(), new java.util.Date().getTime())));
            File pom = Download.getJarFile(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), Path.of(""), "pom");

            //execute the maven command to get the dependency tree
            assert pom != null;
            String command = "mvn dependency:tree -DoutputType=dot -f %s -DoutputFile=%s".formatted(pom.getAbsolutePath(), treeFile.toAbsolutePath().toString());
            MavenCommand.execCommand(command, null);
            return parseTree(treeFile, dependency);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Parse the tree file and create a set of dependencies
     *
     * @param treeFile the file containing the tree
     * @return the set of dependencies
     */
    public static Set<Dependency> parseTree(Path treeFile, Dependency dependency
    ) {
        Set<Dependency> dependencies = new HashSet<>();
        String direct = dependency.getGroupId() + ":" + dependency.getArtifactId() + ":jar:" + dependency.getVersion();
        //parse the tree file and create a tree
        try {
            List<String> lines = Files.readAllLines(treeFile);
            for (String line : lines) {
                if (line.contains("->")) {
                    String[] parts = line.split("->");
                    String parent = parts[0].trim();
                    String child = parts[1].trim();
//                    if (parent.contains(direct) || child.contains(direct)) {
                    Dependency parentDependency = readDependency(parent);
                    Dependency childDependency = readDependency(child);
                    dependencies.add(parentDependency);
                    dependencies.add(childDependency);
//                    }
                }
            }

            File file = new File("example.json");
            JsonUtils.writeToFile(file.toPath(), dependencies);
            return dependencies;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Read a dependency from a line
     *
     * @param line the line
     * @return the dependency
     */
    private static Dependency readDependency(String line) {
        String[] parts = line.split(":");
        String scope = parts.length < 5 ? "" : parts[4];
        return new Dependency(
                parts[0].split("\"")[1],
                parts[1],
                parts[3].split("\"")[0],
                parts[2],
                scope);
    }


    /*
     * Compare two sets of dependencies and print the differences
     */
    public static Set<PairTransitiveDependency> diff(Set<Dependency> v1, Set<Dependency> v2) {

        Set<PairTransitiveDependency> transitiveDependencies = new HashSet<>();
        v2.forEach(d -> {
            for (Dependency oldVersion : v1) {
                if (
                        oldVersion.getGroupId().equals(d.getGroupId())
                                && oldVersion.getArtifactId().equals(d.getArtifactId())
                                && !oldVersion.getVersion().equals(d.getVersion())

                ) {
                    System.out.println("New version: %s".formatted(d));
                    System.out.println("Old version: %s".formatted(oldVersion));

                    PairTransitiveDependency pairTransitiveDependency = new PairTransitiveDependency(d, oldVersion);
                    transitiveDependencies.add(pairTransitiveDependency);
                }
            }
        });

        return transitiveDependencies;
    }


    public void getChanges() {

    }


}
