package se.kth.java_version;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.core.Changes_V2;
import se.kth.data.BreakingUpdateMetadata;
import se.kth.data.BuildHelp;
import se.kth.explaining.ExplanationTemplate;
import se.kth.explaining.JavaVersionIncompatibilityTemplate;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static se.kth.data.JavaVersionFinder.readProjectList;

public class Main {

    public static void main(String[] args) {

        Map<String, List<Integer>> javaVersions = VersionFinder.findJavaVersions("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/11be71ab8713fe987785e9e25e4f3e410e709ab9/camunda-platform-7-mockito");


        Changes_V2 changes = new Changes_V2(
                new ApiMetadata("1.0.0", Path.of("path/to/old/Oldapi.jar")),
                new ApiMetadata("1.0.1", Path.of("path/to/new/Newapi.jar")
                ));

        JavaVersionFailure javaVersionFailure = new JavaVersionFailure();
        javaVersionFailure.setJavaInWorkflowFiles(javaVersions);
        javaVersionFailure.setErrorMessages("Error message");
        javaVersionFailure.setIncompatibility(new JavaVersionIncompatibility("11", "17"));


        ExplanationTemplate explanationTemplate = new JavaVersionIncompatibilityTemplate(
                changes,
                "Explanations/JavaVersionIncompatibility/JavaVersionIncompatibility.md",
                javaVersionFailure
        );

        explanationTemplate.generateTemplate();




    }
}
