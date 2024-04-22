package se.kth.data;

import com.fasterxml.jackson.databind.type.MapType;
import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.breaking_changes.JApiCmpAnalyze;
import se.kth.core.Changes;
import se.kth.core.CombineResults;
import se.kth.explaining.CompilationErrorTemplate;
import se.kth.explaining.ExplanationTemplate;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.log_Analyzer.MavenLogAnalyzer;
import se.kth.spoon_compare.Client;
import spoon.reflect.CtModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static List<BreakingUpdateMetadata> list = new ArrayList<>();
    static Set<BreakingGoodInfo> breakingGoodInfoList = new HashSet<>();

    public static void main(String[] args) {
        String fileName = "0abf7148300f40a1da0538ab060552bca4a2f1d8";

//        list = getBreakingCommit(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/benchmark/data/benchmark"));
        list = getBreakingCommit(Path.of("examples/Benchmark"));
//        list = getBreakingCommit(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/benchmark/data/benchmark/%s.json".formatted(fileName)));
//
        List<BreakingUpdateMetadata> compilationErrors = list.stream().filter(b -> b.failureCategory().equals("COMPILATION_FAILURE")).toList();


//        compilationErrors = read(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/explanationStatistics-data-old.json"), compilationErrors);
        generateTemplate(compilationErrors);
    }

    public void metadata() {
        final var path = Path.of("breaking_good_stats.json");
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static List<BreakingUpdateMetadata> getBreakingCommit(Path benchmarkDir) {
        File[] breakingUpdates = null;

        if (Files.isDirectory(benchmarkDir)) {
            breakingUpdates = benchmarkDir.toFile().listFiles();
        } else {
            breakingUpdates = new File[]{benchmarkDir.toFile()};
        }

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

        DockerImages dockerImages = new DockerImages();

        List<ExplanationStatistics> explanationStatistics = new ArrayList<>();

        Path info = Path.of("breaking_good_stats.json");

        for (BreakingUpdateMetadata breakingUpdate : breakingUpdateList) {

            if (breakingUpdate.project().equals("google-cloud-java")) {
                continue;
            }

            Path jarsFile = Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/");

            System.out.println("Processing breaking update " + breakingUpdate.breakingCommit());
            try {
                dockerImages.getProject(breakingUpdate);
            } catch (IOException | InterruptedException e) {
                System.out.println("Error downloading breaking update " + breakingUpdate.breakingCommit());
            }

            try {
                processingBreakingUpdate(breakingUpdate, jarsFile, explanationStatistics);

            } catch (Exception e) {
                System.out.println("Error processing breaking update " + breakingUpdate.breakingCommit());
                System.out.println(e.toString());
                continue;
            }
        }

    }

    private static void processingBreakingUpdate(BreakingUpdateMetadata breakingUpdate, Path
            jarsFile, List<ExplanationStatistics> explanationStatistics) {
        try {

            //Metadata
            BreakingGoodInfo bg = new BreakingGoodInfo();
            bg.setBreakingCommit(breakingUpdate.breakingCommit());
            bg.setFailureCategory(breakingUpdate.failureCategory());
            MavenErrorLog mavenLogAnalyzer = mavenLogParser(breakingUpdate, bg);


            Path oldDependency = jarsFile.resolve("%s/%s-%s.jar".formatted(breakingUpdate.breakingCommit(), breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().previousVersion()));
            Path newDependency = jarsFile.resolve("%s/%s-%s.jar".formatted(breakingUpdate.breakingCommit(), breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().newVersion()));

            ApiMetadata newApiVersion = new ApiMetadata(newDependency.toFile().getName(), newDependency);
            ApiMetadata oldApiVersion = new ApiMetadata(oldDependency.toFile().getName(), oldDependency);

            JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
                    oldApiVersion,
                    newApiVersion
            );

            Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();
            bg.setJApiCmpChanges(apiChanges.size());
            System.out.println("Number of changes: " + apiChanges.size());

            Client client = new Client(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/%s/%s".formatted(breakingUpdate.breakingCommit(), breakingUpdate.project())));
            client.setClasspath(List.of(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/%s/%s-%s.jar".formatted(breakingUpdate.breakingCommit(), breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().previousVersion()))));

            CtModel model = client.createModel();
            CombineResults combineResults = new CombineResults(apiChanges, oldApiVersion, newApiVersion, mavenLogAnalyzer, model);
            combineResults.setProject("projects/%s".formatted(breakingUpdate.breakingCommit()));

            try {
                Changes changes = combineResults.analyze();
                changesCount(changes, bg);
                System.out.println("Project: " + breakingUpdate.project());
                System.out.println("Breaking Commit: " + breakingUpdate.breakingCommit());
                System.out.println("Changes: " + changes.changes().size());

                String explanationFolder = list.size() > 1 ? "Explanations/" : "Explanations_tmp/";
                final var dir = Path.of(explanationFolder);
                if (Files.notExists(dir)) {
                    Files.createDirectory(dir);
                }

                explanationStatistics.add(new ExplanationStatistics(breakingUpdate.project(), breakingUpdate.breakingCommit(), changes.changes().size()));
                ExplanationTemplate explanationTemplate = new CompilationErrorTemplate(changes, explanationFolder + "/" + breakingUpdate.breakingCommit() + ".md");
                explanationTemplate.generateTemplate();
                System.out.println("**********************************************************");
                if (Files.exists(Path.of(explanationFolder + "/" + breakingUpdate.breakingCommit() + ".md"))) {
                    bg.setHasExplanation(true);
                } else {
                    System.out.println("Error generating explanation template for breaking update " + breakingUpdate.breakingCommit());
                }
                breakingGoodInfoList.add(bg);
            } catch (IOException e) {
                System.out.println("Error analyzing breaking update " + breakingUpdate.breakingCommit());
                System.out.println(e);
                throw new RuntimeException(e);
            }
            try {
                Path file = (list.size() > 1) ? Path.of("explanationStatistics-last.json") : Path.of("tmpStatistics.json");
                Files.deleteIfExists(file);
                Files.createFile(file);
                JsonUtils.writeToFile(file, explanationStatistics);
                JsonUtils.writeToFile(Path.of("breaking_good_stats.json"), breakingGoodInfoList);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            System.out.println("Error processing breaking update " + breakingUpdate.breakingCommit());
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }
    }

    private static void changesCount(Changes changes, BreakingGoodInfo bg) {
        int breakingChanges = 0;
        for (var change : changes.changes()) {
            breakingChanges += change.getErrorInfo().size();
        }
        bg.setTotalErrorsInExplanation(breakingChanges);
    }

    private static MavenErrorLog mavenLogParser(BreakingUpdateMetadata breakingUpdate, BreakingGoodInfo bg) throws IOException {
        MavenLogAnalyzer mavenLogAnalyzer = new MavenLogAnalyzer(
                new File("projects/%s/%s/%s.log".formatted(breakingUpdate.breakingCommit(), breakingUpdate.project(), breakingUpdate.breakingCommit())));

        MavenErrorLog errorLog = mavenLogAnalyzer.analyzeCompilationErrors();
        AtomicInteger errorsCount = new AtomicInteger();
        errorLog.getErrorInfo().forEach((k, v) -> {
            System.out.println("Path: " + k);
            errorsCount.addAndGet(v.size());
            v.forEach(errorInfo -> {
                System.out.println("Line: " + errorInfo.getClientLinePosition());
                System.out.println("Error: " + errorInfo.getErrorMessage());
            });
        });

        bg.setErrorsFromLog(errorsCount.get());
        return errorLog;
    }

    public record ExplanationStatistics(String project, String commit, int changes) {
    }
}
