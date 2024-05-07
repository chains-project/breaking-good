package se.kth;


import picocli.CommandLine;
import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.breaking_changes.JApiCmpAnalyze;
import se.kth.core.Changes;
import se.kth.core.CombineResults;
import se.kth.data.JsonUtils;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.spoon_compare.Client;
import se.kth.transitive_changes.CompareTransitiveDependency;
import se.kth.transitive_changes.Dependency;
import se.kth.transitive_changes.MavenTree;
import se.kth.transitive_changes.PairTransitiveDependency;
import spoon.reflect.CtModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

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
            Client client = new Client(project);

            JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(oldApiMetadata, newApiMetadata);

            Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();

            try {
                MavenErrorLog errorLog = BreakingGood.parseLog(mavenLog.toPath(), project);

                CtModel model = BreakingGood.spoonAnalyzer(client, oldApiMetadata, project);

                CombineResults combineResults = new CombineResults(apiChanges, oldApiMetadata, newApiMetadata, errorLog, model);
                //remove project name folder
                combineResults.setProject(project.toString().substring(0, project.toString().lastIndexOf("/")));
                Changes changes = combineResults.analyze();

//                ExplanationTemplate explanationTemplate = new CompilationErrorTemplate(changes, "Explanations/" + project.toFile().getName() + ".md");
//                explanationTemplate.generateTemplate();

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

            Set<Dependency> v1 = MavenTree.read(oldApiMetadata);
            Set<Dependency> v2 = MavenTree.read(newApiMetadata);

            Set<PairTransitiveDependency> transitiveDependencies = MavenTree.diff(v1, v2);

            for (PairTransitiveDependency pair : transitiveDependencies) {
                try {
                    CompareTransitiveDependency compareTransitiveDependency = new CompareTransitiveDependency(pair.newVersion(), pair.oldVersion());
                    compareTransitiveDependency.compareDependency();
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