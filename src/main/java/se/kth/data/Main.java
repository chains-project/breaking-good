package se.kth.data;

import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.breaking_changes.BreakingGoodOptions;
import se.kth.breaking_changes.JApiCmpAnalyze;
import se.kth.core.ChangesBetweenVersions;
import se.kth.core.CombineResults;
import se.kth.explaining.CompilationErrorTemplate;
import se.kth.explaining.ExplanationTemplate;
import se.kth.japianalysis.BreakingChange;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.sponvisitors.BreakingChangeVisitor;
import se.kth.spoon_compare.Client;
import se.kth.transitive_changes.Dependency;
import se.kth.transitive_changes.TransitiveDependencyAnalysis;
import spoon.reflect.CtModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static se.kth.data.BuildHelp.*;

public class Main {
    static List<BreakingUpdateMetadata> list = new ArrayList<>();
    static Set<BreakingGoodInfo> breakingGoodInfoList = new HashSet<>();

    public static void main(String[] args) {
        String fileName = "b8f92ff37d1aed054d8320283fd6d6a492703a55";

//        list = getBreakingCommit(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/benchmark/data/benchmark"));
        list = BuildHelp.getBreakingCommit(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/RQ3/transitive_jsons"));

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

        List<ExplanationStatistics> explanationStatistics = new ArrayList<>();

        Path info = Path.of("breaking_good_stats.json");

        for (BreakingUpdateMetadata breakingUpdate : breakingUpdateList) {

            if (breakingUpdate.project().equals("google-cloud-java")) {
                continue;
            }

//            Path explaining = Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/Explanations/%s.md".formatted(breakingUpdate.breakingCommit()));
//            if (Files.exists(explaining)) {
//                System.out.println("Explanation already exists for breaking update " + breakingUpdate.breakingCommit());
//                continue;
//            }
            Path explaining = Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/Explanations/RemAddMod/%s.md".formatted(breakingUpdate.breakingCommit()));
            if (Files.exists(explaining)) {
                System.out.println("Explanation already exists for breaking update " + breakingUpdate.breakingCommit());
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
                e.printStackTrace();
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

            String logPath = "projects/%s/%s/%s.log".formatted(breakingUpdate.breakingCommit(), breakingUpdate.project(), breakingUpdate.breakingCommit());

            Path oldDependency = jarsFile.resolve("%s/%s-%s.jar".formatted(breakingUpdate.breakingCommit(), breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().previousVersion()));
            Path newDependency = jarsFile.resolve("%s/%s-%s.jar".formatted(breakingUpdate.breakingCommit(), breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().newVersion()));

            ApiMetadata newApiVersion = new ApiMetadata(newDependency.toFile().getName(), newDependency);
            ApiMetadata oldApiVersion = new ApiMetadata(oldDependency.toFile().getName(), oldDependency);

            JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
                    oldApiVersion,
                    newApiVersion
            );

            Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();
            ChangesBetweenVersions changesV2;

            List<BreakingChange> breakingChanges = jApiCmpAnalyze.useJApiCmp_v2();

            bg.setJApiCmpChanges(apiChanges.size());
            System.out.println("Number of changes: " + apiChanges.size());

            Client client = new Client(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/%s/%s".formatted(breakingUpdate.breakingCommit(), breakingUpdate.project())));
            client.setClasspath(List.of(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/%s/%s-%s.jar".formatted(breakingUpdate.breakingCommit(), breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().previousVersion()))));

            CtModel model = client.createModel();
            CombineResults combineResults = new CombineResults(apiChanges, oldApiVersion, newApiVersion, mavenLogAnalyzer, model);
            combineResults.setProject("projects/%s".formatted(breakingUpdate.breakingCommit()));

            try {

                List<BreakingChangeVisitor> visitors = jApiCmpAnalyze.getVisitors(breakingChanges);
                BreakingGoodOptions options = new BreakingGoodOptions();

                changesV2 = combineResults.analyze_v2(visitors, options);

//                Changes changes = combineResults.analyze();
                changesCount(changesV2, bg);
                System.out.println("Project: " + breakingUpdate.project());
                System.out.println("Breaking Commit: " + breakingUpdate.breakingCommit());
                System.out.println("Changes: " + changesV2.brokenChanges().size());

                if (!changesV2.brokenChanges().isEmpty()) {
                    String explanationFolder = list.size() > 1 ? "Explanations/" : "Explanations_tmp/";
                    final var dir = Path.of(explanationFolder);
                    if (Files.notExists(dir)) {
                        Files.createDirectory(dir);
                    }

                    explanationStatistics.add(new ExplanationStatistics(breakingUpdate.project(), breakingUpdate.breakingCommit(), changesV2.brokenChanges().size()));
                    ExplanationTemplate explanationTemplate = new CompilationErrorTemplate(changesV2, explanationFolder + "/" + breakingUpdate.breakingCommit() + ".md");
                    explanationTemplate.generateTemplate();
                    if (Files.exists(Path.of(explanationFolder + "/" + breakingUpdate.breakingCommit() + ".md"))) {
                        bg.setHasExplanation(true);
                        System.out.println("Explanation template generated for breaking update " + breakingUpdate.breakingCommit());
                    } else {
                        System.out.println("Error generating explanation template for breaking update " + breakingUpdate.breakingCommit());
                    }
                    breakingGoodInfoList.add(bg);
                } else {
                    System.out.println("No breaking changes found for breaking update " + breakingUpdate.breakingCommit() + "in the direct dependency.");
                }
                System.out.println("**********************************************************");


                if (!changesV2.brokenChanges().isEmpty()) {
                    System.out.println("RETURNNN");
                    return;
                }

                /*
                 ************************************************************
                 * Handle transitive dependencies and generate explanation
                 * **********************************************************
                 */

                Dependency oldVersion = new Dependency(
                        breakingUpdate.updatedDependency().dependencyGroupID(),
                        breakingUpdate.updatedDependency().dependencyArtifactID(),
                        breakingUpdate.updatedDependency().previousVersion(),
                        "jar", "compile");
                Dependency newVersion = new Dependency(
                        breakingUpdate.updatedDependency().dependencyGroupID(),
                        breakingUpdate.updatedDependency().dependencyArtifactID(),
                        breakingUpdate.updatedDependency().newVersion()
                        , "jar", "compile");
//
//
                TransitiveDependencyAnalysis.compareTransitiveDependency(
                        oldApiVersion,
                        oldVersion,
                        newApiVersion,
                        newVersion,
                        jApiCmpAnalyze,
                        client,
                        apiChanges,
                        mavenLogAnalyzer,
                        breakingUpdate.breakingCommit(),
                        model,
                        logPath

                        );

//                transitive(breakingUpdate, explanationStatistics, oldApiVersion, oldVersion, newApiVersion, newVersion, jApiCmpAnalyze, client, apiChanges, mavenLogAnalyzer, bg);

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

    private static void transitive(BreakingUpdateMetadata breakingUpdate, List<ExplanationStatistics> explanationStatistics, ApiMetadata oldApiVersion, Dependency oldVersion, ApiMetadata newApiVersion, Dependency newVersion, JApiCmpAnalyze jApiCmpAnalyze, Client client, Set<ApiChange> apiChanges, MavenErrorLog mavenLogAnalyzer, BreakingGoodInfo bg) {

    }

    public static record ExplanationStatistics(String project, String commit, int changes) {
    }


}
