package se.kth.data;

import com.fasterxml.jackson.databind.type.MapType;
import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.JApiCmpAnalyze;
import se.kth.core.Changes;
import se.kth.core.CombineResults;
import se.kth.explaining.CompilationErrorTemplate;
import se.kth.explaining.ExplanationTemplate;
import se.kth.log_Analyzer.MavenLogAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {


    public static void main(String[] args) {
//        List<BreakingUpdateMetadata> list = getBreakingCommit(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/benchmark/data/benchmark"));
        List<BreakingUpdateMetadata> list = getBreakingCommit(Path.of("examples/Benchmark"));
//
        List<BreakingUpdateMetadata> compilationErrors = list.stream().filter(b -> b.failureCategory().equals("COMPILATION_FAILURE")).toList();

        generateTemplate(compilationErrors);
    }

    public static List<BreakingUpdateMetadata> getBreakingCommit(Path benchmarkDir) {

        File[] breakingUpdates = benchmarkDir.toFile().listFiles();
        MapType buJsonType = JsonUtils.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
        List<BreakingUpdateMetadata> breakingUpdateList = new ArrayList<>();

        if (breakingUpdates != null) {
            for (File breakingUpdate : breakingUpdates) {
                // read each breaking update json file
                BreakingUpdateMetadata bu = JsonUtils.readFromFile(breakingUpdate.toPath(), BreakingUpdateMetadata.class);
                // convert to BreakingUpdate object
                breakingUpdateList.add(bu);
            }
        } else {
            System.out.println("No breaking update found in " + benchmarkDir);
        }
        return breakingUpdateList;
    }


    public static void generateTemplate(List<BreakingUpdateMetadata> breakingUpdateList) {

        String githubURL = "https://github.com/knaufk/flink-faker/blob/1ef97ea6c5b6e34151fe6167001b69e003449f95/src/main/java/com/github/knaufk/flink/faker/DateTime.java#L44";

        Path jars = Path.of("/Users/frank/Documents/Work/PHD/Tools/bump_experiments/jars");

        DockerImages dockerImages = new DockerImages();

        List<ExplanationStatistics> explanationStatistics = new ArrayList<>();

        for (BreakingUpdateMetadata breakingUpdate : breakingUpdateList) {

            Path jarsFile = Path.of("projects/");

            System.out.println("Processing breaking update " + breakingUpdate.breakingCommit());
            try {
                dockerImages.getProject(breakingUpdate);
            } catch (IOException | InterruptedException e) {
                System.out.println("Error downloading breaking update " + breakingUpdate.breakingCommit());
            }

            processingBreakingUpdate(breakingUpdate, jarsFile, explanationStatistics);
        }

    }

    private static void processingBreakingUpdate(BreakingUpdateMetadata breakingUpdate, Path jarsFile, List<ExplanationStatistics> explanationStatistics) {
        try {
            JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
                    jarsFile.resolve("%s/%s-%s.jar".formatted(breakingUpdate.breakingCommit(), breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().previousVersion())),
                    jarsFile.resolve("%s/%s-%s.jar".formatted(breakingUpdate.breakingCommit(), breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().newVersion()))
            );


            Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();

            CombineResults combineResults = new CombineResults(apiChanges);

            combineResults.setDependencyGroupID(breakingUpdate.updatedDependency().dependencyGroupID());

            combineResults.setProject("projects/%s".formatted(breakingUpdate.breakingCommit()));

            combineResults.setMavenLog(new MavenLogAnalyzer(
                    new File("projects/%s/%s/%s.log".formatted(breakingUpdate.breakingCommit(), breakingUpdate.project(), breakingUpdate.breakingCommit()))));

            try {
                Changes changes = combineResults.analyze();

                System.out.println("Project: " + breakingUpdate.project());
                System.out.println("Breaking Commit: " + breakingUpdate.breakingCommit());
                System.out.println("Changes: " + changes.changes().size());
                explanationStatistics.add(new ExplanationStatistics(breakingUpdate.project(), breakingUpdate.breakingCommit(), changes.changes().size()));
                ExplanationTemplate explanationTemplate = new CompilationErrorTemplate(changes, "Explanations/" + breakingUpdate.breakingCommit() + ".md");
                explanationTemplate.generateTemplate();
                System.out.println("**********************************************************");
                System.out.println();
            } catch (IOException e) {
                System.out.println("Error analyzing breaking update " + breakingUpdate.breakingCommit());
                System.out.println(e);
                throw new RuntimeException(e);
            }
            try {
                Path file = Path.of("explanationStatistics.json");
                Files.deleteIfExists(file);
                Files.createFile(file);
                JsonUtils.writeToFile(file, explanationStatistics);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            System.out.println("Error processing breaking update " + breakingUpdate.breakingCommit());
            System.out.println(e);
        }
    }

    public record ExplanationStatistics(String project, String commit, int changes) {
    }
}
