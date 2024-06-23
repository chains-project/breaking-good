package se.kth.java_version;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaIncompatibilityAnalyzer {

    static String errorPattern = "class file has wrong version (\\d+\\.\\d+), should be (\\d+\\.\\d+)";
    String startLine = "[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin";
    String endLine = "[ERROR] -> [Help 1]";

    /**
     * Parse the log file and extract the error messages
     *
     * @param logFilePath The path to the log file
     * @return A set of error messages
     * @throws IOException
     */
    public Set<String> parseErrors(String logFilePath) throws IOException {
        Set<String> errors = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(logFilePath));
        StringBuilder currentError = null;
        String line;

        // Define the regular expression pattern to find error lines
        Pattern errorPattern = Pattern.compile("\\[ERROR\\] /[\\w\\-/]+\\.java");
        boolean parseStart = false;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(endLine)) {
                break;
            }
            if (line.startsWith(startLine)) {
                parseStart = true;
            }
            if (parseStart) {


                Matcher matcher = errorPattern.matcher(line);
                if (line.startsWith("[ERROR]") && matcher.find()) {
                    if (currentError != null) {
                        errors.add(currentError.toString().trim());
                    }
                    currentError = new StringBuilder();
                    currentError.append(line);
                } else if (currentError != null && !line.trim().isEmpty() && line.contains("  ")) {
                    // Line with more than one space
                    currentError.append(System.lineSeparator()).append(line);
                } else {
                    // Line that doesn't follow the pattern
                    if (currentError != null) {
                        errors.add(currentError.toString().trim());
                        currentError = null;
                    }
                }
            }

        }

        if (currentError != null) {
            errors.add(currentError.toString().trim());
        }

        reader.close();
        return errors;
    }

    public static Map<JavaVersionIncompatibility, Set<String>> extractVersionErrors(Set<String> errors) {
        Map<JavaVersionIncompatibility, Set<String>> versionErrors = new HashMap<>();

        Pattern versionPattern = Pattern.compile("class file has wrong version (\\d+\\.\\d+), should be (\\d+\\.\\d+)");

        for (String error : errors) {
            Matcher versionMatcher = versionPattern.matcher(error);
            while (versionMatcher.find()) {
                JavaVersionIncompatibility j = new JavaVersionIncompatibility(versionMatcher.group(1), versionMatcher.group(2), error);

                if (versionErrors.containsKey(j)) {
                    versionErrors.get(j).add(error);
                } else {
                    Set<String> errorSet = new HashSet<>();
                    errorSet.add(error);
                    versionErrors.put(j, errorSet);
                }
            }
        }
        return versionErrors;
    }
}



