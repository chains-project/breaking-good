package se.kth;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.breaking_changes.BreakingGoodOptions;
import se.kth.breaking_changes.JApiCmpAnalyze;
import se.kth.core.ChangesBetweenVersions;
import se.kth.core.CombineResults;
import se.kth.japianalysis.BreakingChange;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.sponvisitors.BreakingChangeVisitor;
import se.kth.spoon_compare.Client;
import spoon.reflect.CtModel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class BreakingGoodMain {


    public void categorizeBreakingChanges(String logFilePath) {

    }


    public static ChangesBetweenVersions brokenChanges(ApiMetadata newApi, ApiMetadata oldApi, Client client, MavenErrorLog mavenLogAnalyzer) {

        JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
                oldApi,
                newApi
        );

        ChangesBetweenVersions changesV2;

        List<BreakingChange> breakingChanges = jApiCmpAnalyze.useJApiCmp_v2();

        client.setClasspath(List.of(Path.of(oldApi.getFile().toString())));

        CtModel model = client.createModel();
        CombineResults combineResults = new CombineResults( oldApi, newApi, mavenLogAnalyzer, model);
        combineResults.setProject(client.getSourcePath().toString());

        try {

            List<BreakingChangeVisitor> visitors = jApiCmpAnalyze.getVisitors(breakingChanges);
            BreakingGoodOptions options = new BreakingGoodOptions();

            changesV2 = combineResults.analyze_v2(visitors, options);

            System.out.println("**********************************************************");

            return changesV2;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
