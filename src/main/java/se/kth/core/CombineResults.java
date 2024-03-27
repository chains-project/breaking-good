package se.kth.core;

import japicmp.model.JApiChangeStatus;
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

    public Changes analyze() throws IOException {
        MavenErrorLog log = mavenLog.analyzeCompilationErrors();
        Set<BreakingChange> change = new HashSet<>();


        log.getErrorInfo().forEach((k, v) -> {
            SpoonAnalyzer spoonAnalyzer = new SpoonAnalyzer(dependencyGroupID, v);
            List<SpoonResults> results = spoonAnalyzer.applySpoon(project + k);
            System.out.printf("Amount of instructions %d%n", results.size());
            findBreakingChanges(results, change);
        });
        return new Changes("1.0.0", "1.0.1", change);
    }

    public void findBreakingChanges(List<SpoonResults> spoonResults, Set<BreakingChange> change) {
        spoonResults.forEach(spoonResult -> {
            apiChanges.forEach(apiChange -> {
                if (apiChange.getChangeType().equals(JApiChangeStatus.REMOVED) && apiChange.getName().equals(spoonResult.getName())) {
                    for (BreakingChange breakingChange : change) {
                        if (breakingChange.getApiChanges().getName().equals(apiChange.getName())) {
                            breakingChange.getErrorInfo().add(spoonResult);
                            return;
                        }
                    }
                    BreakingChange breakingChange = new BreakingChange(apiChange);
                    breakingChange.getErrorInfo().add(spoonResult);
                    change.add(breakingChange);
                    // find new variants
                    Set<ApiChange> newVariants =  findNewVariant(apiChange);
                    apiChange.setNewVariants(newVariants);

                }
            });
        });
    }

    public Set<ApiChange> findNewVariant(ApiChange apiChange) {

        Set<ApiChange> newVariants = new HashSet<>();
        apiChanges.forEach(apiChange1 -> {
            if (apiChange1.getName().contains("between")) {
                System.out.println("between");
            }
            if (apiChange1.getName().equals(apiChange.getName()) && !apiChange1.getChangeType().equals(JApiChangeStatus.REMOVED)) {
                System.out.println("New Element  " + apiChange1.getName());
                System.out.println("Old element  " + apiChange.getName());
                newVariants.add(apiChange1);
            }
        });
        return newVariants;
    }
}