package se.kth.java_version;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.core.Changes_V2;
import se.kth.explaining.ExplanationTemplate;
import se.kth.explaining.JavaVersionIncompatibilityTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Main {

    public static void main(String[] args) throws IOException {

        VersionFinder versionFinder = new VersionFinder();
        Map<String, List<Integer>> javaVersions = versionFinder.findJavaVersions("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/11be71ab8713fe987785e9e25e4f3e410e709ab9/camunda-platform-7-mockito");


        Changes_V2 changes = new Changes_V2(
                new ApiMetadata("1.0.0", Path.of("path/to/old/Oldapi.jar")),
                new ApiMetadata("1.0.1", Path.of("path/to/new/Newapi.jar")
                ));

        JavaIncompatibilityAnalyzer javaIncompatibilityAnalyzer = new JavaIncompatibilityAnalyzer();
        Set<String> errorList = javaIncompatibilityAnalyzer.parseErrors("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/11be71ab8713fe987785e9e25e4f3e410e709ab9/camunda-platform-7-mockito/11be71ab8713fe987785e9e25e4f3e410e709ab9.log");
        Map<JavaVersionIncompatibility, Set<String>> versionFailures = JavaIncompatibilityAnalyzer.extractVersionErrors(errorList);


        errorList.forEach(
                error -> {
                    System.out.println("Error:");
                    System.out.println(error.contains(System.lineSeparator()));
                    System.out.println(error.replace(System.lineSeparator(), "\n"));
                    System.out.println("----");
                }
        );

        JavaVersionFailure javaVersionFailure = new JavaVersionFailure();
        javaVersionFailure.setDiffVersionErrors(versionFailures);
        javaVersionFailure.setErrorMessages(errorList);
        javaVersionFailure.setJavaInWorkflowFiles(javaVersions);

        javaVersionFailure.setIncompatibility(new JavaVersionIncompatibility("11", "17", ""));


        ExplanationTemplate explanationTemplate = new JavaVersionIncompatibilityTemplate(
                changes,
                "Explanations/JavaVersionIncompatibility/JavaVersionIncompatibility.md",
                javaVersionFailure
        );

        explanationTemplate.generateTemplate();


    }
}
