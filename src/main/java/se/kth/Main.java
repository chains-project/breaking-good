package se.kth;


import picocli.CommandLine;
import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.JApiCmpAnalyze;
import se.kth.core.Changes;
import se.kth.core.CombineResults;
import se.kth.explaining.CompilationErrorTemplate;
import se.kth.explaining.ExplanationTemplate;
import se.kth.log_Analyzer.MavenLogAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Explaining()).execute(args);
        System.exit(exitCode);
    }

    @CommandLine.Command(name = "explaining", mixinStandardHelpOptions = true, version = "0.1")
    private static class Explaining implements Runnable {

        @CommandLine.Option(
                names = {"-c", "--client"},
                paramLabel = "Client project",
                description = "A client project to analyze.",
                required = true
        )
        Path client;

        @CommandLine.Option(
                names = {"-o", "--old-dependency"},
                paramLabel = "Old dependency",
                description = "The old dependency to analyze.",
                required = true
        )
        Path oldDependency;

        @CommandLine.Option(
                names = {"-n", "--new-dependency"},
                paramLabel = "New dependency",
                description = "The new dependency to analyze.",
                required = true
        )
        Path newDependency;

        @CommandLine.Option(
                names = {"-l", "--log"},
                paramLabel = "Maven log",
                description = "The maven log to analyze.",
                required = false
        )
        File mavenLog;

        @CommandLine.Option(
                names = {"-g", "--group-id"},
                paramLabel = "Dependency group ID",
                description = "The group ID of the dependency to analyze.",
                required = false
        )
        String dependencyGroupID;

        @Override
        public void run() {
            JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
                    oldDependency,
                    newDependency);

            Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();

            CombineResults combineResults = new CombineResults(apiChanges);
            combineResults.setDependencyGroupID(dependencyGroupID);
            combineResults.setProject(client.toString());
            combineResults.setMavenLog(new MavenLogAnalyzer(mavenLog));

            try {
                Changes changes = combineResults.analyze();
                changes.changes().forEach(change -> {
                            ExplanationTemplate explanationTemplate = new CompilationErrorTemplate(changes, "Explanation");
                            explanationTemplate.generateTemplate();
                        }
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}