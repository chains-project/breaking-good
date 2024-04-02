package se.kth.breaking_changes;

import se.kth.data.BreakingUpdateMetadata;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static se.kth.data.Main.getBreakingCommit;

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


//        List<BreakingUpdateMetadata> list =
//                getBreakingCommit(Path.of("/Users/frank/Documents/Work/PHD/chains-project/paper/bump/data/benchmark"));
//
//
//        for (BreakingUpdateMetadata breakingUpdate : list) {
//            boolean result = ApiMetadata.download(breakingUpdate.updatedDependency().dependencyGroupID(),
//                    breakingUpdate.updatedDependency().dependencyArtifactID(),
//                    breakingUpdate.updatedDependency().newVersion());
//
//            if (result) {
//                System.out.println("Downloaded " + breakingUpdate.updatedDependency().dependencyArtifactID() + "-" + breakingUpdate.updatedDependency().newVersion());
//            } else {
//                System.out.println("Failed to download " + breakingUpdate.updatedDependency().dependencyArtifactID() + "-" + breakingUpdate.updatedDependency().newVersion());
//            }
//
//             result = ApiMetadata.download(breakingUpdate.updatedDependency().dependencyGroupID(),
//                    breakingUpdate.updatedDependency().dependencyArtifactID(),
//                    breakingUpdate.updatedDependency().previousVersion());
//
//            if (result) {
//                System.out.println("Downloaded " + breakingUpdate.updatedDependency().dependencyArtifactID() + "-" + breakingUpdate.updatedDependency().newVersion());
//            } else {
//                System.out.println("Failed to download " + breakingUpdate.updatedDependency().dependencyArtifactID() + "-" + breakingUpdate.updatedDependency().newVersion());
//            }

//        }


    }


}
