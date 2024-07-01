package se.kth.werror;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.core.ChangesBetweenVersions;

import se.kth.data.BreakingUpdateMetadata;
import se.kth.data.DockerImages;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static se.kth.data.BuildHelp.getBreakingCommit;

public class Main {
    static List<BreakingUpdateMetadata> list = new ArrayList<>();


    /**
     * Main method to run the program
     * One line trigger werror detection
     * Many lines contains warning detection
     *
     * @param args
     */

//    public static void main(String[] args) {
//        String log = "/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/c5fd5187ce64d2b53602717f09cc18dd21d55e8d/nem/c5fd5187ce64d2b53602717f09cc18dd21d55e8d.log";
//        String client = "/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/c5fd5187ce64d2b53602717f09cc18dd21d55e8d/nem";
//
//        WError werror = new WError("werror.md");
//
//        Changes_V2 changes = new Changes_V2(
//                new ApiMetadata(Path.of(client)),
//                new ApiMetadata(Path.of(client))
//        );
//
//        try {
//            werror.analyzeWerror(log, client, changes);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//
//
//
//
//    }
    public static void main(String[] args) {
        String fileName = "ed7fbdd75abc666d9d5a2794e9392ed33e75de9b";

        list = getBreakingCommit(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/benchmark/data/benchmark"));
//        list = getBreakingCommit(Path.of("examples/Benchmark"));
//        list = getBreakingCommit(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/benchmark/data/benchmark/%s.json".formatted(fileName)));
//
        List<BreakingUpdateMetadata> compilationErrors = list.stream().filter(b -> b.failureCategory().equals("COMPILATION_FAILURE")).toList();


//        compilationErrors = read(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/explanationStatistics-data-old.json"), compilationErrors);
        generateTemplate(compilationErrors);
    }


    public static void generateTemplate(List<BreakingUpdateMetadata> breakingUpdateList) {

        String githubURL = "https://github.com/knaufk/flink-faker/blob/1ef97ea6c5b6e34151fe6167001b69e003449f95/src/main/java/com/github/knaufk/flink/faker/DateTime.java#L44";

        DockerImages dockerImages = new DockerImages();

        List<se.kth.data.Main.ExplanationStatistics> explanationStatistics = new ArrayList<>();

        Path info = Path.of("breaking_good_stats.json");

        for (BreakingUpdateMetadata breakingUpdate : breakingUpdateList) {

//            if (breakingUpdate.project().equals("google-cloud-java")) {
//                continue;
//            }

//            Path explaining = Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/Explanations/%s.md".formatted(breakingUpdate.breakingCommit()));
//            if (Files.exists(explaining)) {
//                continue;
//            }

            Path jarsFile = Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/");

            System.out.println("Processing breaking update " + breakingUpdate.breakingCommit());
            try {
                dockerImages.getProject(breakingUpdate);
            } catch (IOException | InterruptedException e) {
                System.out.println("Error downloading breaking update " + breakingUpdate.breakingCommit());
            }

            try {
                generate(breakingUpdate, jarsFile, explanationStatistics);

            } catch (Exception e) {
                System.out.println("Error processing breaking update " + breakingUpdate.breakingCommit());
                System.out.println(e.toString());
                continue;
            }
        }

    }

    private static void generate(BreakingUpdateMetadata breakingUpdate, Path jarsFile, List<se.kth.data.Main.ExplanationStatistics> explanationStatistics) {

        WError werror = new WError("Explanations/werror/%s.md".formatted(breakingUpdate.breakingCommit()));

        String client = "/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/%s/%s".formatted(breakingUpdate.breakingCommit(), breakingUpdate.project());
        String log = "projects/%s/%s/%s.log".formatted(breakingUpdate.breakingCommit(), breakingUpdate.project(), breakingUpdate.breakingCommit());

        Path oldDependency = jarsFile.resolve("%s/%s-%s.jar".formatted(breakingUpdate.breakingCommit(), breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().previousVersion()));
        Path newDependency = jarsFile.resolve("%s/%s-%s.jar".formatted(breakingUpdate.breakingCommit(), breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().newVersion()));


        ApiMetadata newApiVersion = new ApiMetadata(newDependency.toFile().getName(), newDependency);
        ApiMetadata oldApiVersion = new ApiMetadata(oldDependency.toFile().getName(), oldDependency);

        ChangesBetweenVersions changes = new ChangesBetweenVersions(oldApiVersion, newApiVersion);

        try {
            werror.analyzeWerror(log, client, changes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
