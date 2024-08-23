package se.kth;


import picocli.CommandLine;
import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.breaking_changes.BreakingGoodOptions;
import se.kth.breaking_changes.JApiCmpAnalyze;
import se.kth.core.ChangesBetweenVersions;
import se.kth.core.CombineResults;
import util.JsonUtils;
import se.kth.explaining.CompilationErrorTemplate;
import se.kth.explaining.ExplanationTemplate;
import se.kth.japianalysis.BreakingChange;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.log_Analyzer.MavenLogAnalyzer;
import se.kth.sponvisitors.BreakingChangeVisitor;
import se.kth.spoon_compare.Client;
import se.kth.transitive_changes.CompareTransitiveDependency;
import se.kth.transitive_changes.Dependency;
import se.kth.transitive_changes.MavenTree;
import se.kth.transitive_changes.PairTransitiveDependency;
import spoon.reflect.CtModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static se.kth.BreakingGood.javaVersionIncompatibilityErrorExplanation;
import static se.kth.BreakingGood.wErrorAnalysis;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new CLIEntryPoint()).execute(args);
        System.exit(exitCode);
    }

    @CommandLine.Command(subcommands = {Explaining.class, Compare.class}, mixinStandardHelpOptions = true, version = "1.0")
    public static class CLIEntryPoint implements Runnable {
        @Override
        public void run() {
            CommandLine.usage(this, System.out);
        }
    }


    @CommandLine.Command(name = "explaining", mixinStandardHelpOptions = true, version = "0.1")
    private static class Explaining implements Runnable {

        @CommandLine.Option(names = {"-c", "--project"}, paramLabel = "Client project", description = "A client project to analyze.", required = true)
        Path project;

        @CommandLine.Option(names = {"-o", "--old-dependency"}, paramLabel = "Old dependency", description = "The old dependency to analyze.", required = true)
        Path oldDependency;

        @CommandLine.Option(names = {"-n", "--new-dependency"}, paramLabel = "New dependency", description = "The new dependency to analyze.", required = true)
        Path newDependency;

        @CommandLine.Option(names = {"-l", "--log"}, paramLabel = "Maven log", description = "The maven log to analyze.")
        File mavenLog;

        @Override
        public void run() {

            ApiMetadata oldApiMetadata = new ApiMetadata(oldDependency.toFile().getName(), oldDependency);
            ApiMetadata newApiMetadata = new ApiMetadata(newDependency.toFile().getName(), newDependency);


            try {

                MavenLogAnalyzer mavenLogAnalyzer = BreakingGood.parseLog(mavenLog, project);


                // Check if the log contains the -Werror flag
                boolean isWerror = mavenLogAnalyzer.isWerror(mavenLogAnalyzer.getLogFile().getAbsolutePath());

                if (isWerror) {
                    // If the log contain the -Werror flag, analyze the log and generate a new log with the -Werror flag
                    System.out.println("The log contains the -Werror flag");
                    wErrorAnalysis(mavenLogAnalyzer.getLogFile().getAbsolutePath(), project.toString(), oldApiMetadata, newApiMetadata);
                } else {
                    // Check if the log contains a Java version incompatibility error
                    boolean isJavaVersionIncompatibility = mavenLogAnalyzer.isJavaVersionIncompatibilityError(mavenLogAnalyzer.getLogFile().getAbsolutePath());

                    if (isJavaVersionIncompatibility) {
                        System.out.println("The log file contains a Java version incompatibility error.");
                        javaVersionIncompatibilityErrorExplanation(
                                project,
                                mavenLog.toPath(),
                                oldApiMetadata,
                                newApiMetadata);
                    } else {

                        JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(oldApiMetadata, newApiMetadata);
                        Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();
                        List<BreakingChange> breakingChanges = jApiCmpAnalyze.useJApiCmp_v2();
                        Client client = new Client(project);
                        client.setClasspath(List.of(oldDependency));
                        CtModel model = client.createModel();

                        MavenErrorLog errorLog = mavenLogAnalyzer.analyzeCompilationErrors();

                        CombineResults combineResults = new CombineResults(apiChanges, oldApiMetadata, newApiMetadata, errorLog, model);
                        //remove project name folder
                        combineResults.setProject(project.toString().substring(0, project.toString().lastIndexOf("/")));

                        List<BreakingChangeVisitor> visitors = jApiCmpAnalyze.getVisitors(breakingChanges);
                        BreakingGoodOptions options = new BreakingGoodOptions();

                        ChangesBetweenVersions changesV2 = combineResults.analyze_v2(visitors, options);

                        if (!changesV2.brokenChanges().isEmpty()) {
                            ExplanationTemplate explanationTemplate = new CompilationErrorTemplate(changesV2, "Explanation.md");
                            explanationTemplate.generateTemplate();
                        }

                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @CommandLine.Command(name = "compare", mixinStandardHelpOptions = true, version = "0.1")
    private static class Compare implements Runnable {

        @CommandLine.Option(names = {"-o", "--old-dependency"}, paramLabel = "Old dependency", description = "The old dependency to analyze.", required = true)
        Path oldDependency;

        @CommandLine.Option(names = {"-n", "--new-dependency"}, paramLabel = "New dependency", description = "The new dependency to analyze.", required = true)
        Path newDependency;

        @Override
        public void run() {
            ApiMetadata oldApiMetadata = new ApiMetadata(oldDependency.toFile().getName(), oldDependency);
            ApiMetadata newApiMetadata = new ApiMetadata(newDependency.toFile().getName(), newDependency);

            Dependency o = new Dependency("com.google.guava", "guava", "30.1-jre", "jar", "compile");
            Dependency n = new Dependency("com.google.guava", "guava", "30.1.1-jre", "jar", "compile");

            Set<Dependency> v1 = MavenTree.read(oldApiMetadata, o);
            Set<Dependency> v2 = MavenTree.read(newApiMetadata, n);

            Set<PairTransitiveDependency> transitiveDependencies = MavenTree.diff(v1, v2);

            for (PairTransitiveDependency pair : transitiveDependencies) {
                try {
                    CompareTransitiveDependency compareTransitiveDependency = new CompareTransitiveDependency(pair.newVersion(), pair.oldVersion());
                    compareTransitiveDependency.getChangesBetweenDependencies();
                    System.out.println("Breaking changes for " + pair.newVersion() + " and " + pair.oldVersion());
                    System.out.println("Breaking Changes amount: " + compareTransitiveDependency.getBreakingChanges().size());

                    JsonUtils.writeToFile(Path.of("breaking-changes-%s.json".formatted(pair.oldVersion().getArtifactId())), compareTransitiveDependency);

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

}