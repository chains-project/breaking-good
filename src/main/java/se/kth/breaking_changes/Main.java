package se.kth.breaking_changes;

import java.nio.file.Path;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        JApiCmpAnalyze jApiCmpAnalyze = new JApiCmpAnalyze(
                Path.of("/Users/frank/Documents/Work/PHD/Tools/bump_experiments/jars/0abf7148300f40a1da0538ab060552bca4a2f1d8/jasperreports-6.18.1.jar"),
                Path.of("/Users/frank/Documents/Work/PHD/Tools/bump_experiments/jars/0abf7148300f40a1da0538ab060552bca4a2f1d8/jasperreports-6.19.1.jar")
        );

        Set<ApiChange> apiChanges = jApiCmpAnalyze.useJApiCmp();

        for (ApiChange apiChange : apiChanges) {
            System.out.println(apiChange);
        }


    }
}
