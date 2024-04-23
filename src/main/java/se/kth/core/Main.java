package se.kth.core;

import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.breaking_changes.JApiCmpAnalyze;
import se.kth.explaining.CompilationErrorTemplate;
import se.kth.explaining.ExplanationTemplate;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.log_Analyzer.MavenLogAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public class Main {


    public static void main(String[] args) {

        Path oldDependency = Path.of("/Users/frank/Downloads/Test_Failures/log4j-api-2.16.0.jar");
        Path newDependency = Path.of("/Users/frank/Downloads/Test_Failures/log4j-api-2.18.0.jar");

        ApiMetadata apiMetadata = new ApiMetadata(oldDependency);
        ApiMetadata apiMetadata1 = new ApiMetadata(newDependency);

        JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
                apiMetadata,
                apiMetadata1
        );

        Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();
        try {
            MavenErrorLog errorLog = new MavenLogAnalyzer(new File("/Users/frank/Downloads/Test_Failures/webapp/1eb6d9d5b2a07720a0839457cee81e066dd932f2.log")).analyzeCompilationErrors();

            ApiMetadata newApiVersion = new ApiMetadata(newDependency.toFile().getName(), newDependency);
            ApiMetadata oldApiVersion = new ApiMetadata(oldDependency.toFile().getName(), oldDependency);

            CombineResults combineResults = new CombineResults(apiChanges, newApiVersion, oldApiVersion, errorLog,null);

            combineResults.setDependencyGroupID("org.apache.logging.log4j");

            combineResults.setProject("/Users/frank/Downloads/Test_Failures");

            Changes changes = combineResults.analyze();
            changes.changes().forEach(change -> {
//                        ExplanationTemplate explanationTemplate = new CompilationErrorTemplate(changes, "change");
//                        explanationTemplate.generateTemplate();
                    }
            );


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
