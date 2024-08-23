package se.kth.core;

import japicmp.model.JApiChangeStatus;
import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.breaking_changes.BreakingGoodOptions;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.sponvisitors.BreakingChangeVisitor;
import se.kth.sponvisitors.BrokenChanges;
import se.kth.sponvisitors.BrokenUse;
import se.kth.spoon_compare.SpoonAnalyzer;
import spoon.reflect.CtModel;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.CtExecutableReferenceImpl;

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

    CtModel model;

    public CombineResults(Set<ApiChange> apiChanges, ApiMetadata oldVersion, ApiMetadata newVersion, MavenErrorLog mavenLog, CtModel model) {
        Objects.requireNonNull(oldVersion);
        Objects.requireNonNull(newVersion);
        Objects.requireNonNull(mavenLog);
        Objects.requireNonNull(model);


        this.apiChanges = apiChanges;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.mavenLog = mavenLog;
        this.model = model;
    }

    public CombineResults(ApiMetadata oldVersion, ApiMetadata newVersion, MavenErrorLog mavenLog, CtModel model) {

        Objects.requireNonNull(oldVersion);
        Objects.requireNonNull(newVersion);
        Objects.requireNonNull(mavenLog);
        Objects.requireNonNull(model);

        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.mavenLog = mavenLog;
        this.model = model;
    }


    public ChangesBetweenVersions analyze_v2(List<BreakingChangeVisitor> breakingChangeVisitors, BreakingGoodOptions opts) throws IOException {

        Set<BrokenUse> results = new HashSet<>();

        try {
            // client.setClasspath(List.of(oldVersion.getFile()));
            mavenLog.getErrorInfo().forEach((k, v) -> {
                SpoonAnalyzer spoonAnalyzer = new SpoonAnalyzer(v, model);
                results.addAll(spoonAnalyzer.applySpoonV2(breakingChangeVisitors, opts, project + k));
            });

            Set<BrokenChanges> changes = addErrorInfo(results);
            return new ChangesBetweenVersions(oldVersion, newVersion, changes);

        } catch (Exception e) {
            System.out.println("Error identifying breaking changes in client " + e.toString());
            throw new RuntimeException(e);
        }
    }


    public Set<BrokenChanges> addErrorInfo(Set<BrokenUse> results) {
        Set<BrokenChanges> brokenChanges = new HashSet<>();

        results.forEach(brokenUse -> {
            BrokenChanges brokenChange = new BrokenChanges(brokenUse);
            mavenLog.getErrorInfo().forEach((k, v) -> {
                v.forEach(errorInfo -> {
                    if (brokenUse.element().getPosition().getLine() == Integer.parseInt(errorInfo.getClientLinePosition())) {
                        brokenChange.addErrorInfo(errorInfo);
                    }
                });
            });
            brokenChanges.add(brokenChange);
            Set<ApiChange> newVariants = findNewVariant(brokenUse);
            brokenChange.setNewVariants(newVariants);
        });

        return brokenChanges;
    }

    private void addBrokenUse(Set<BrokenChanges> results, BrokenChanges brokenChange) {
        for (BrokenChanges b : results) {
            if (b.getBrokenUse().usedApiElement().toString().equals(brokenChange.getBrokenUse().usedApiElement().toString())) {
                b.getErrorInfo().addAll(brokenChange.getErrorInfo());
                return;
            }
        }
        results.add(brokenChange);
    }

    public Set<ApiChange> findNewVariant(BrokenUse apiChange) {

        try {
//            String name = ((CtExecutableReferenceImpl<?>) apiChange.usedApiElement()).getSimpleName();

            String fullName =  fullyQualifiedName(apiChange.source());

//            System.out.println("Name: " + name + " Full Name: " + fullName);

            Set<ApiChange> newVariants = new HashSet<>();
            apiChanges.forEach(apiChange1 -> {
                if (apiChange1.getName().equals(fullName) && !apiChange1.getChangeType().equals(JApiChangeStatus.REMOVED)) {
                    newVariants.add(apiChange1);
                }
            });
            return newVariants;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String fullyQualifiedName(CtReference ref) {
        String fqn = "";

        if (ref instanceof CtTypeReference<?> tRef)
            fqn = tRef.getSimpleName();
        else if (ref instanceof CtExecutableReference<?> eRef)
            fqn = eRef.getSimpleName();
        else if (ref instanceof CtFieldReference<?> fRef)
            fqn = fRef.getSimpleName();

        return fqn;
    }
}