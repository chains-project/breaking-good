package se.kth.spoon_compare;

import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.breaking_changes.JApiCmpAnalyze;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.log_Analyzer.MavenLogAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class Main {


    public static void main(String[] args) {

        MavenLogAnalyzer mavenLog = new MavenLogAnalyzer(new File("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/5cf5a482bd430d81257b4ecd85b3d4f7da911621/jakartaee-mvc-sample/5cf5a482bd430d81257b4ecd85b3d4f7da911621.log"));


        try {

            ApiMetadata apiMetadata = new ApiMetadata(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/5cf5a482bd430d81257b4ecd85b3d4f7da911621/jakarta.mvc-api-1.1.0.jar"));
            ApiMetadata apiMetadata1 = new ApiMetadata(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/5cf5a482bd430d81257b4ecd85b3d4f7da911621/jakarta.mvc-api-2.0.1.jar"));

            JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
                    apiMetadata,
                    apiMetadata1
            );


            Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();


            MavenErrorLog log = mavenLog.analyzeCompilationErrors();
            String project = "/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/5cf5a482bd430d81257b4ecd85b3d4f7da911621";

            log.getErrorInfo().forEach((k, v) -> {
                System.out.println(k);
//                v.forEach(System.out::println);
                SpoonAnalyzer spoonAnalyzer = new SpoonAnalyzer(v, apiChanges,null);
                List<SpoonResults> results = spoonAnalyzer.applySpoon(project + k);

                System.out.println("Amount of instructions: " + results.size());
            });


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
