package se.kth;


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

        JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
//                Path.of("/Users/frank/Documents/Work/PHD/Tools/bump_experiments/jars/0abf7148300f40a1da0538ab060552bca4a2f1d8/jasperreports-6.18.1.jar"),
//                Path.of("/Users/frank/Documents/Work/PHD/Tools/bump_experiments/jars/0abf7148300f40a1da0538ab060552bca4a2f1d8/jasperreports-6.18.1.jar"),
                Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/examples/org.kohsuke.github-api.1.93.jar"),
                Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/examples/org.kohsuke.github-api.1.313.jar")
        );

        Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();

        CombineResults combineResults = new CombineResults(apiChanges);

        combineResults.setDependencyGroupID("org.kohsuke");

        combineResults.setProject("/Users/frank/Documents/Work/PHD/Tools");

        combineResults.setMavenLog(new MavenLogAnalyzer(new File("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/5769bdad76925da568294cb8a40e7d4469699ac3.log")));

        try {
            Changes changes = combineResults.analyze();



            changes.changes().forEach(change -> {
                        ExplanationTemplate explanationTemplate = new CompilationErrorTemplate(changes, change);
                        explanationTemplate.generateTemplate();
                    }
            );


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}