package se.kth.transitive_changes;

import se.kth.breaking_changes.*;
import se.kth.core.ChangesBetweenVersions;
import se.kth.core.CombineResults;
import se.kth.explaining.RemAddModdIndirectTemplate;
import se.kth.explaining.TransitiveExplanationTemplate;
import se.kth.japianalysis.BreakingChange;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.sponvisitors.BreakingChangeVisitor;
import se.kth.spoon_compare.Client;
import se.kth.spoon_compare.SpoonResults;
import spoon.Launcher;
import spoon.reflect.CtModel;
import util.MavenCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static se.kth.transitive_changes.Dependency.findUniqueDependencies;
import static se.kth.transitive_changes.GetConstructByName.getConstructs;

public class TransitiveDependencyAnalysis {

    JApiCmpAnalyze jApiCmpAnalyzer;
    Client client;
    MavenErrorLog mavenLogAnalyzer;

    public TransitiveDependencyAnalysis(JApiCmpAnalyze jApiCmpAnalyzer, Client client, MavenErrorLog mavenLogAnalyzer) {
        this.jApiCmpAnalyzer = jApiCmpAnalyzer;
        this.client = client;
        this.mavenLogAnalyzer = mavenLogAnalyzer;
    }


    public static void compareTransitiveDependency(
            ApiMetadata oldApiVersion,
            Dependency oldVersion,
            ApiMetadata newApiVersion,
            Dependency newVersion,
            JApiCmpAnalyze jApiCmpAnalyze,
            Client client, Set<ApiChange> apiChanges,
            MavenErrorLog mavenLogAnalyzer,
            String commitId,
            CtModel modelClientApp,
            String logFilePath
    ) {

        ChangesBetweenVersions changesV2 = null;
        CombineResults combineResults;
        List<BreakingChangeVisitor> visitors;
        CtModel model;
        BreakingGoodOptions options;
        Set<Dependency> v1 = MavenTree.read(oldApiVersion, oldVersion);
        Set<Dependency> v2 = MavenTree.read(newApiVersion, newVersion);
//
        Set<PairTransitiveDependency> transitiveDependencies = MavenTree.diff(v1, v2);


        for (PairTransitiveDependency pair : transitiveDependencies) {
            try {
                System.out.println("Comparing " + pair.newVersion() + " and " + pair.oldVersion());
                CompareTransitiveDependency compareTransitiveDependency = new CompareTransitiveDependency(pair.newVersion(), pair.oldVersion());
                List<BreakingChange> changes = compareTransitiveDependency.getChangesBetweenDependencies();
                visitors = jApiCmpAnalyze.getVisitors(changes);
                options = new BreakingGoodOptions();

                ApiMetadata oldTransitiveDep = compareTransitiveDependency.getOldApiMetadata();

                client.setClasspath(List.of(oldTransitiveDep.getFile()));

                model = client.createModel();
                combineResults = new CombineResults(apiChanges, oldApiVersion, newApiVersion, mavenLogAnalyzer, model);
                String folderPath = client.getSourcePath().toString().lastIndexOf("/") > 0 ?
                        client.getSourcePath().toString().substring(0, client.getSourcePath().toString().lastIndexOf("/")) : client.getSourcePath().toString();
                combineResults.setProject(folderPath);

                changesV2 = combineResults.analyze_v2(visitors, options);

                System.out.println("Breaking changes for " + pair.newVersion() + " and " + pair.oldVersion());
                System.out.println("Breaking Changes amount: " + changesV2.brokenChanges().size());

                if (!changesV2.brokenChanges().isEmpty()) {
                    generateExplanation(changesV2, pair, commitId);
                    break;
                } else {
                    System.out.println("No breaking changes found for " + oldVersion + " and " + newVersion);
                    //find added and removed dependencies

                }
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("Error comparing " + pair.newVersion() + " and " + pair.oldVersion()+ " "+e.getMessage());
            }
        }

        if (changesV2 == null || changesV2.brokenChanges().isEmpty()) {
            boolean removed = dep(v1, v2, mavenLogAnalyzer, client, commitId, oldApiVersion, newApiVersion, "Removed", modelClientApp, logFilePath);

            if (!removed) {
                boolean added = dep(v2, v1, mavenLogAnalyzer, client, commitId, newApiVersion, oldApiVersion, "Added", modelClientApp, logFilePath);
                if (!added) {
                    return;
                }
            } else {
                return;
            }

        }
    }

    public static void generateExplanation(ChangesBetweenVersions changes, PairTransitiveDependency currentPair, String commitId) throws IOException {

        if (!Files.exists(Path.of("Explanations/Transitive"))) {
            Files.createDirectory(Path.of("Explanations/Transitive"));
        }

        TransitiveExplanationTemplate explanationTemplate = new TransitiveExplanationTemplate(changes, currentPair,
                "Explanations/Transitive/%s.md".formatted(commitId));
        explanationTemplate.generateTemplate();
    }

    public static boolean dep(
            Set<Dependency> v1,
            Set<Dependency> v2,
            MavenErrorLog mavenErrorLog,
            Client client,
            String commitId,
            ApiMetadata oldApiVersion,
            ApiMetadata newApiVersion,
            String action, CtModel modelClientApp,
            String logFilePath) {

        Map<Dependency, List<MatchElements>> matchElements = new HashMap<>();

        //Dependencies removed in the new version
        Set<Dependency> removed = findUniqueDependencies(v2, v1);
        System.out.printf("Removed dependencies: %s%n", removed.size());
        Dependency analyzed = null;
        Set<SpoonResults> results = new HashSet<>();

        for (Dependency dependency : removed) {

            try {
                System.out.println(dependency);
                Path sourceFolder = null;
                Path source = Path.of("source/" + dependency.getArtifactId() + "-" + dependency.getVersion() + "-sources");
                if (!Files.exists(source)) {
                    sourceFolder = Files.createDirectory(source);
                } else {
                    sourceFolder = source;
                }
                extractClient(dependency, sourceFolder);

                // source code of dependency added
                Client sourceClient = new Client(sourceFolder);
                CtModel model = sourceClient.createModel();
                Launcher launcher = new Launcher();

                String clientPath = client.getSourcePath().toString().lastIndexOf("/") > 0 ?
                        client.getSourcePath().toString().substring(0, client.getSourcePath().toString().lastIndexOf("/")) : client.getSourcePath().toString();

                //get all constructs
                results = getConstructs(model, Path.of(clientPath), logFilePath);
                analyzed = dependency;
                if (!results.isEmpty()) {
                    break;
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error comparing " + dependency.getArtifactId() + " " + e.getMessage());
            }
        }

        if (!results.isEmpty()) {
            try {
                generateExplanation(results, analyzed, mavenErrorLog, client, commitId, oldApiVersion, newApiVersion, action);
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;

    }

    public static void extractClient(Dependency sourceDependency, Path output) throws IOException, InterruptedException {


//        Files.createDirectory(path);

//        Path sourcePath = Files.createDirectory((output));

        File sources = Download.getJarFile(sourceDependency.getGroupId(), sourceDependency.getArtifactId(), sourceDependency.getVersion(), output, "sources");

        final var log = Path.of("log.txt");

        assert sources != null;
        MavenCommand.executeCommand("tar -xf %s".formatted(sources.getAbsolutePath()), log, output);

    }

    public static void generateExplanation(Set<SpoonResults> spoon, Dependency dependency, MavenErrorLog mavenErrorLog, Client client,
                                           String commitId,
                                           ApiMetadata oldVersion,
                                           ApiMetadata newVersion, String action) throws IOException {

        ChangesBetweenVersions versions = new ChangesBetweenVersions(oldVersion, newVersion);

        RemAddModdIndirectTemplate explanationTemplate = new RemAddModdIndirectTemplate(
                versions,
                "Explanations/RemAddMod/%s.md".formatted(commitId),
                dependency,
                action, spoon);
        explanationTemplate.generateTemplate();
    }

}
