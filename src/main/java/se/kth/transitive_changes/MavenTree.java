package se.kth.transitive_changes;

import lombok.Getter;
import lombok.Setter;
import se.kth.breaking_changes.ApiMetadata;
import util.MavenCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class MavenTree {

    ApiMetadata apiMetadata;


    public MavenTree() {

    }


    /**
     * Read the pom file and create a tree of dependencies
     *
     * @param apiMetadata the metadata of the api
     * @return the set of dependencies
     */
    public static Set<Dependency> read(ApiMetadata apiMetadata) {
        // read the pom file and create a tree
        try {
            //create a temporary file to store the pom file
            Path pom = Files.createTempFile("tmp-pom", ".xml");
            Path treeFile = Files.createFile(Path.of("trees%s.txt".formatted(new Date().getTime())));
            apiMetadata.extractPomFromJar(pom);

            //execute the maven command to get the dependency tree
            String command = "mvn dependency:tree -DoutputType=dot -f %s -DoutputFile=%s".formatted(pom.toAbsolutePath().toString(), treeFile.toAbsolutePath().toString());
            MavenCommand.execCommand(command, null);
            return parseTree(treeFile);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Parse the tree file and create a set of dependencies
     *
     * @param treeFile the file containing the tree
     * @return the set of dependencies
     */
    public static Set<Dependency> parseTree(Path treeFile) {
        Set<Dependency> dependencies = new HashSet<>();

        //parse the tree file and create a tree
        try {
            List<String> lines = Files.readAllLines(treeFile);
            for (String line : lines) {
                if (line.contains("->")) {
                    String[] parts = line.split("->");
                    String parent = parts[0].trim();
                    String child = parts[1].trim();
                    Dependency parentDependency = readDependency(parent);
                    Dependency childDependency = readDependency(child);
                    dependencies.add(parentDependency);
                    dependencies.add(childDependency);

                }
            }

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
        return new Dependency(parts[0].split("\"")[1], parts[1], parts[2], parts[3].split("\"")[0]);
    }


    /*
     * Compare two sets of dependencies and print the differences
     */
    public static Set<PairTransitiveDependency> diff(Set<Dependency> v1, Set<Dependency> v2) {

        Set<PairTransitiveDependency> transitiveDependencies = new HashSet<>();


        v2.forEach(d -> {
            for (Dependency oldVersion : v1) {
                if (oldVersion.getGroupId().equals(d.getGroupId()) && oldVersion.getArtifactId().equals(d.getArtifactId()) && !oldVersion.getVersion().equals(d.getVersion())) {
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
