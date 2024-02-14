package se.kth.data;

import com.fasterxml.jackson.databind.type.MapType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    public static List<BreakingUpdateMetadata> getBreakingCommit(Path benchmarkDir) {

        File[] breakingUpdates = benchmarkDir.toFile().listFiles();
        MapType buJsonType = JsonUtils.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
        List<BreakingUpdateMetadata> breakingUpdateList = new ArrayList<>();

        if (breakingUpdates != null) {
            for (File breakingUpdate : breakingUpdates) {
                // read each breaking update json file
                BreakingUpdateMetadata bu = JsonUtils.readFromFile(breakingUpdate.toPath(), BreakingUpdateMetadata.class);
                // convert to BreakingUpdate object
                breakingUpdateList.add(bu);
            }
        } else {
            System.out.println("No breaking update found in " + benchmarkDir);
        }
        return breakingUpdateList;
    }

    public static void main(String[] args) {
        List<BreakingUpdateMetadata> list = getBreakingCommit(Path.of("/Users/frank/Documents/Work/PHD/chains-project/paper/bump/data/benchmark"));

        List<BreakingUpdateMetadata> compilationErrors = list.stream().filter(b -> b.failureCategory().equals("COMPILATION_FAILURE")).toList();

        generateTemplate(compilationErrors);
    }

    public boolean downloadJar() {


        return true;
    }

    public static void generateTemplate(List<BreakingUpdateMetadata> breakingUpdateList) {

        Path jars = Path.of("/Users/frank/Documents/Work/PHD/Tools/bump_experiments/jars");


        for (BreakingUpdateMetadata breakingUpdate : breakingUpdateList) {

            try {
                JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
                        jars.resolve("%s/%s-%s.jar".formatted(breakingUpdate.breakingCommit(), breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().previousVersion())),
                        jars.resolve("%s/%s-%s.jar".formatted(breakingUpdate.breakingCommit(), breakingUpdate.updatedDependency().dependencyArtifactID(), breakingUpdate.updatedDependency().newVersion()))
                );
                Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();

                CombineResults combineResults = new CombineResults(apiChanges);
//
                combineResults.setDependencyGroupID("net.datafaker");

                combineResults.setProject("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/examples");

                combineResults.setMavenLog(new MavenLogAnalyzer(new File("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/examples/1ef97ea6c5b6e34151fe6167001b69e003449f95.log")));

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

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

//        JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
//                Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/examples/datafaker-1.3.0.jar"),
//                Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/examples/datafaker-1.4.0.jar")
//        );
//
//        Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();
//
//        CombineResults combineResults = new CombineResults(apiChanges);
//
//        combineResults.setDependencyGroupID("net.datafaker");
//
//        combineResults.setProject("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/examples");
//
//        combineResults.setMavenLog(new MavenLogAnalyzer(new File("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/examples/1ef97ea6c5b6e34151fe6167001b69e003449f95.log")));
//
//        try {
//            Changes changes = combineResults.analyze();
//
//
//            changes.changes().forEach(change -> {
//                        ExplanationTemplate explanationTemplate = new CompilationErrorTemplate(changes, change);
//                        explanationTemplate.generateTemplate();
//                    }
//            );
//
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
