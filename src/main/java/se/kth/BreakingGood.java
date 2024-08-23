package se.kth;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.core.ChangesBetweenVersions;
import se.kth.explaining.ExplanationTemplate;
import se.kth.explaining.JavaVersionIncompatibilityTemplate;
import se.kth.java_version.JavaIncompatibilityAnalyzer;
import se.kth.java_version.JavaVersionFailure;
import se.kth.java_version.JavaVersionIncompatibility;
import se.kth.java_version.VersionFinder;
import se.kth.log_Analyzer.MavenLogAnalyzer;
import se.kth.spoon_compare.Client;
import se.kth.werror.WError;
import spoon.reflect.CtModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BreakingGood {

    /**
     * Parse the maven log and return the error log
     *
     * @param logPath Path to the maven log
     * @return The error log
     * @throws IOException If the log could not be read
     */
    public static MavenLogAnalyzer parseLog(File logPath, Path client) throws IOException {
        Path mavenLog = null;
        // Parse log
        if (logPath == null || !Files.exists(logPath.toPath())) {
            String log = executeMvnCleanTest(client);
            if (log != null) {
                mavenLog = Path.of(log);
            }
        } else {
            mavenLog = logPath.toPath();
        }
        return new MavenLogAnalyzer(
                new File(mavenLog.toString()));
    }


    public static CtModel spoonAnalyzer(Client client, ApiMetadata v1, Path project) {
        // Analyze the project
        client.setClasspath(List.of(v1.getFile()));
        return client.createModel();
    }

    /**
     * Executes the 'mvn clean test' command in a specified directory and saves the output to a temporary file.
     *
     * @param directory The directory where the 'mvn clean test' command will be executed. This should be the root directory of a Maven project.
     * @throws IOException          If an I/O error occurs when creating the temporary file or executing the command.
     * @throws InterruptedException If the current thread is interrupted while waiting for the command to finish.
     */
    private static String executeMvnCleanTest(Path directory) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("mvn", "clean", "test");
        processBuilder.directory(new File(directory.toString()));

        File tempFile;
        try {
            tempFile = Files.createTempFile("mvn-clean-test-", ".log").toFile();
            processBuilder.redirectOutput(tempFile);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Maven clean test command failed with exit code " + exitCode);
                return null;
            } else {
                System.out.println("Maven clean test command executed successfully. Output saved to " + tempFile.getAbsolutePath());
                return tempFile.getAbsolutePath();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error executing Maven clean test command: " + e.getMessage());
            return null;
        }
    }


    public static void wErrorAnalysis(String logPath, String client, ApiMetadata oldApiVersion, ApiMetadata newApiVersion) throws IOException {

        ChangesBetweenVersions changes = new ChangesBetweenVersions(oldApiVersion, newApiVersion);
        WError werror = new WError("Explanation.md");
        try {
            werror.analyzeWerror(logPath, client, changes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate the explanation for a Java version incompatibility error
     *
     * @param project       The project path
     * @param logFile       The log file path
     * @param oldApiVersion The old API version
     * @param newApiVersion The new API version
     * @throws IOException If the explanation could not be generated
     */
    public static void javaVersionIncompatibilityErrorExplanation(Path project, Path logFile, ApiMetadata oldApiVersion, ApiMetadata newApiVersion) throws IOException {
        ChangesBetweenVersions changes = new ChangesBetweenVersions(oldApiVersion, newApiVersion);
        Client client = new Client(project);

        VersionFinder versionFinder = new VersionFinder();

//        generateVersionExplanation(changes, client.getSourcePath().toString(), client.getSourcePath().toString() + "/%s.log".formatted(breakingUpdate.breakingCommit()));

        Map<String, List<Integer>> javaVersions = versionFinder.findJavaVersions(client.getSourcePath().toString());
        JavaIncompatibilityAnalyzer javaIncompatibilityAnalyzer = new JavaIncompatibilityAnalyzer();
        Set<String> errorList = javaIncompatibilityAnalyzer.parseErrors(logFile.toString());

        Map<JavaVersionIncompatibility, Set<String>> versionFailures = JavaIncompatibilityAnalyzer.extractVersionErrors(errorList);

        JavaVersionFailure javaVersionFailure = new JavaVersionFailure();
        javaVersionFailure.setJavaInWorkflowFiles(javaVersions);
        javaVersionFailure.setDiffVersionErrors(versionFailures);
        javaVersionFailure.setErrorMessages(errorList);
        javaVersionFailure.setIncompatibilityVersion();


        ExplanationTemplate explanationTemplate = new JavaVersionIncompatibilityTemplate(
                changes, "JavaVersionIncompatibility.md",
                javaVersionFailure
        );
        explanationTemplate.generateTemplate();
    }


}
