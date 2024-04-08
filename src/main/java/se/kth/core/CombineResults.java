package se.kth.core;

import japicmp.model.JApiChangeStatus;
import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.spoon_compare.Client;
import se.kth.spoon_compare.SpoonAnalyzer;
import se.kth.spoon_compare.SpoonResults;
import spoon.reflect.CtModel;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@lombok.Getter
@lombok.Setter
public class CombineResults {

    private Set<ApiChange> apiChanges;

    MavenErrorLog mavenLog;

    String dependencyGroupID;

    String project;

    Set<ApiChange> breakingChanges;

    ApiMetadata oldVersion;

    ApiMetadata newVersion;

    Client client;

    public CombineResults(Set<ApiChange> apiChanges, ApiMetadata oldVersion, ApiMetadata newVersion, MavenErrorLog mavenLog, Client client) {
        Objects.requireNonNull(apiChanges);
        Objects.requireNonNull(oldVersion);
        Objects.requireNonNull(newVersion);
        Objects.requireNonNull(mavenLog);


        this.apiChanges = apiChanges;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.mavenLog = mavenLog;
        this.client = client;
    }

    public Changes analyze() throws IOException {

        Set<BreakingChange> change = new HashSet<>();
        try {
            client.setClasspath(List.of(oldVersion.getFile()));
            CtModel model = client.createModel();
            mavenLog.getErrorInfo().forEach((k, v) -> {
                SpoonAnalyzer spoonAnalyzer = new SpoonAnalyzer(v, apiChanges, model);
                List<SpoonResults> results = spoonAnalyzer.applySpoon(project + k);
//            System.out.printf("Amount of instructions %d%n", results.size());
                findBreakingChanges(results, change);
            });
        } catch (Exception e) {
            System.out.println("Error creating model");
        }


        return new Changes(oldVersion, newVersion, change);
    }

    public void findBreakingChanges(List<SpoonResults> spoonResults, Set<BreakingChange> change) {
        spoonResults.forEach(spoonResult -> {
            apiChanges.forEach(apiChange -> {
                if (apiChange.getChangeType().equals(JApiChangeStatus.REMOVED) && apiChange.getName().equals(spoonResult.getName())) {
                    for (BreakingChange breakingChange : change) {
                        if (breakingChange.getApiChanges().getName().equals(apiChange.getName())) {
                            breakingChange.addErrorInfo(spoonResult);
                            return;
                        }
                    }
                    BreakingChange breakingChange = new BreakingChange(apiChange);
                    breakingChange.addErrorInfo(spoonResult);
                    change.add(breakingChange);
                    // find new variants
                    Set<ApiChange> newVariants = findNewVariant(apiChange);
                    apiChange.setNewVariants(newVariants);

                }
            });
        });
    }

    public Set<ApiChange> findNewVariant(ApiChange apiChange) {

        Set<ApiChange> newVariants = new HashSet<>();
        apiChanges.forEach(apiChange1 -> {
            if (apiChange1.getName().equals(apiChange.getName()) && !apiChange1.getChangeType().equals(JApiChangeStatus.REMOVED)) {
                newVariants.add(apiChange1);
            }
        });
        return newVariants;
    }
}