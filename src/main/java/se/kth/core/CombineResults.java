package se.kth.core;

import se.kth.breaking_changes.ApiChange;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.log_Analyzer.MavenLogAnalyzer;
import se.kth.spoon_compare.SpoonAnalyzer;
import se.kth.spoon_compare.SpoonResults;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@lombok.Getter
@lombok.Setter
public class CombineResults {

    private Set<ApiChange> apiChanges;

    MavenLogAnalyzer mavenLog;

    String dependencyGroupID;

    String project;

    Set<ApiChange> breakingChanges;


    public CombineResults(Set<ApiChange> apiChanges) {
        this.apiChanges = apiChanges;
    }


    public static void main(String[] args) {
        System.out.println("Hello world!");

    }

    public Set<BreakingChange> analyze() throws IOException {
        MavenErrorLog log = mavenLog.analyzeCompilationErrors();
        Set<BreakingChange> change = new HashSet<>();


        log.getErrorInfo().forEach((k, v) -> {
            SpoonAnalyzer spoonAnalyzer = new SpoonAnalyzer(dependencyGroupID, v);
            List<SpoonResults> results = spoonAnalyzer.applySpoon(project + k);
            findBreakingChanges(results, change);

        });

        return change;
    }

    public void findBreakingChanges(List<SpoonResults> spoonResults, Set<BreakingChange> change) {
        spoonResults.forEach(spoonResult -> {
            apiChanges.forEach(apiChange -> {
                if (apiChange.getName().equals(spoonResult.getName())) {
                    change.add(new BreakingChange(apiChange, spoonResult.getErrorInfo()));
                }
            });
        });
    }
}
