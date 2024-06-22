package se.kth.data;

import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.breaking_changes.BreakingGoodOptions;
import se.kth.breaking_changes.JApiCmpAnalyze;
import se.kth.core.Changes_V2;
import se.kth.core.CombineResults;
import se.kth.explaining.CompilationErrorTemplate;
import se.kth.explaining.ExplanationTemplate;
import se.kth.explaining.JavaVersionIncompatibilityTemplate;
import se.kth.japianalysis.BreakingChange;
import se.kth.java_version.JavaIncompatibilityAnalyzer;
import se.kth.java_version.JavaVersionFailure;
import se.kth.java_version.JavaVersionIncompatibility;
import se.kth.java_version.VersionFinder;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.sponvisitors.BreakingChangeVisitor;
import se.kth.spoon_compare.Client;
import spoon.reflect.CtModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static se.kth.data.BuildHelp.*;
import static se.kth.java_version.JavaVersion.generateVersionExplanation;


public class Main {
    static List<BreakingUpdateMetadata> list = new ArrayList<>();
    static Set<BreakingGoodInfo> breakingGoodInfoList = new HashSet<>();

    public static void main(String[] args) {
        String fileName = "0cdcc1f1319311f383676a89808c9b8eb190145c";

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

        List<ExplanationStatistics> explanationStatistics = new ArrayList<>();

        Path info = Path.of("breaking_good_stats.json");

        for (BreakingUpdateMetadata breakingUpdate : breakingUpdateList) {

            if (breakingUpdate.project().equals("google-cloud-java")) {
                continue;
            }

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


            generateJavaVersionIncompatibilityTemplate(breakingUpdate, oldApiVersion, newApiVersion);

//            generateCompilationExplanations(breakingUpdate, explanationStatistics, oldApiVersion, newApiVersion, bg, mavenLogAnalyzer);

        } catch (Exception e) {
            System.out.println("Error processing breaking update " + breakingUpdate.breakingCommit());
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }
    }

    private static void generateJavaVersionIncompatibilityTemplate(BreakingUpdateMetadata breakingUpdate, ApiMetadata oldApiVersion, ApiMetadata newApiVersion) throws IOException {

        List<String> projects = readJavaIncompatibilityFile("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/client_java_version_failure.txt");

        if (!projects.contains(breakingUpdate.breakingCommit())) {
            return;
        }

        Changes_V2 changes = new Changes_V2(oldApiVersion, newApiVersion);
        Client client = new Client(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/%s/%s".formatted(breakingUpdate.breakingCommit(), breakingUpdate.project())));

        VersionFinder versionFinder = new VersionFinder();

//        generateVersionExplanation(changes, client.getSourcePath().toString(), client.getSourcePath().toString() + "/%s.log".formatted(breakingUpdate.breakingCommit()));

        Map<String, List<Integer>> javaVersions = versionFinder.findJavaVersions(client.getSourcePath().toString());
        JavaIncompatibilityAnalyzer javaIncompatibilityAnalyzer = new JavaIncompatibilityAnalyzer();
        Set<String> errorList = javaIncompatibilityAnalyzer.parseErrors(client.getSourcePath().toString() + "/%s.log".formatted(breakingUpdate.breakingCommit()));
        Map<JavaVersionIncompatibility, Set<String>> versionFailures = JavaIncompatibilityAnalyzer.extractVersionErrors(errorList);



        JavaVersionFailure javaVersionFailure = new JavaVersionFailure();
        javaVersionFailure.setJavaInWorkflowFiles(javaVersions);
        javaVersionFailure.setDiffVersionErrors(versionFailures);
        javaVersionFailure.setErrorMessages(errorList);
        javaVersionFailure.setIncompatibilityVersion();


        ExplanationTemplate explanationTemplate = new JavaVersionIncompatibilityTemplate(
                changes,
                "Explanations/JavaVersionIncompatibility/%s.md".formatted(breakingUpdate.breakingCommit()),
                javaVersionFailure
        );

        explanationTemplate.generateTemplate();
    }

    private static void generateCompilationExplanations(BreakingUpdateMetadata breakingUpdate, List<ExplanationStatistics> explanationStatistics, ApiMetadata oldApiVersion, ApiMetadata newApiVersion, BreakingGoodInfo bg, MavenErrorLog mavenLogAnalyzer) {
        JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
                oldApiVersion,
                newApiVersion
        );

        Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();

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

            Changes_V2 changesV2 = combineResults.analyze_v2(visitors, options);

//                Changes changes = combineResults.analyze();
            changesCount(changesV2, bg);
            System.out.println("Project: " + breakingUpdate.project());
            System.out.println("Breaking Commit: " + breakingUpdate.breakingCommit());
            System.out.println("Changes: " + changesV2.brokenChanges().size());

            String explanationFolder = list.size() > 1 ? "Explanations/" : "Explanations_tmp/";
            final var dir = Path.of(explanationFolder);
            if (Files.notExists(dir)) {
                Files.createDirectory(dir);
            }

            explanationStatistics.add(new ExplanationStatistics(breakingUpdate.project(), breakingUpdate.breakingCommit(), changesV2.brokenChanges().size()));
            ExplanationTemplate explanationTemplate = new CompilationErrorTemplate(changesV2, explanationFolder + "/" + breakingUpdate.breakingCommit() + ".md");
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
    }

    public static record ExplanationStatistics(String project, String commit, int changes) {
    }

    public static List<String> readJavaIncompatibilityFile(String filePath) {

        File file = new File(filePath);
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }


}
