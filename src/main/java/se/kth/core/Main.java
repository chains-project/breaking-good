package se.kth.core;

import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.JApiCmpAnalyze;
import se.kth.explaining.CompilationErrorTemplate;
import se.kth.explaining.ExplanationTemplate;
import se.kth.log_Analyzer.MavenLogAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public class Main {





    public static void main(String[] args) {

        JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
                Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/examples/datafaker-1.3.0.jar"),
                Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/examples/datafaker-1.4.0.jar")
        );

        Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();

        CombineResults combineResults = new CombineResults(apiChanges);

        combineResults.setDependencyGroupID("net.datafaker");

        combineResults.setProject("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/examples");

        combineResults.setMavenLog(new MavenLogAnalyzer(new File("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/examples/1ef97ea6c5b6e34151fe6167001b69e003449f95.log")));

        try {
            Changes changes = combineResults.analyze();



            changes.changes().forEach(change -> {
                        ExplanationTemplate explanationTemplate = new CompilationErrorTemplate(changes, "change");
                        explanationTemplate.generateTemplate();
                    }
            );


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
