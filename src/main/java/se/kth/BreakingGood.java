package se.kth;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.log_Analyzer.MavenLogAnalyzer;
import se.kth.spoon_compare.Client;
import spoon.reflect.CtModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class BreakingGood {

    /**
     * Parse the maven log and return the error log
     *
     * @param logPath Path to the maven log
     * @return The error log
     * @throws IOException If the log could not be read
     */
    public static MavenErrorLog parseLog(Path logPath, Path client) throws IOException {
        // Parse log
        if (!Files.exists(logPath)) {
            String log = executeMvnCleanTest(client);
            if (log != null) {
                logPath = Path.of(log);
            }
        }
        MavenLogAnalyzer mavenLogAnalyzer = new MavenLogAnalyzer(
                new File(logPath.toString()));
        return mavenLogAnalyzer.analyzeCompilationErrors();
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
            if (exitCode != 0) {
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


}
