package se.kth.java_version;

import se.kth.core.ChangesBetweenVersions;

import se.kth.explaining.ExplanationTemplate;
import se.kth.explaining.JavaVersionIncompatibilityTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaVersion {


    public static void generateVersionExplanation(ChangesBetweenVersions changes, String projectPath, String logFile) throws IOException {
        VersionFinder versionFinder = new VersionFinder();
        Map<String, List<Integer>> javaVersions = versionFinder.findJavaVersions(projectPath);

        JavaIncompatibilityAnalyzer javaIncompatibilityAnalyzer = new JavaIncompatibilityAnalyzer();
        Set<String> errorList = javaIncompatibilityAnalyzer.parseErrors(logFile);
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

        javaVersionFailure.setIncompatibilityVersion();


        ExplanationTemplate explanationTemplate = new JavaVersionIncompatibilityTemplate(
                changes,
                "Explanations/JavaVersionIncompatibility/JavaVersionIncompatibility.md",
                javaVersionFailure
        );

        explanationTemplate.generateTemplate();
    }
}
