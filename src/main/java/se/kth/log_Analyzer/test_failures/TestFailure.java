package se.kth.log_Analyzer.test_failures;

import se.kth.log_Analyzer.MavenErrorLog;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestFailure {
    public MavenErrorLog analyzeLogs() throws Exception {
        return null;
    }

    private static void extractErrorInfo(String logPath) throws IOException {
        System.out.println("Extracting runningInfo info from log file: " + logPath);

        String testName = "^\\[INFO] Running (.+)";
        Pattern testNamePattern = Pattern.compile(testName);
        String runningInfo = "Tests run: (\\d+), Failures: (\\d+), Errors: (\\d+), Skipped: (\\d+)";

        Map<String, Object> errorInfo = new HashMap<>();

        // Extract runningInfo from log file
        FileInputStream fileInputStream = new FileInputStream(logPath);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.ISO_8859_1);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line;

        //start reading the file
        while ((line = reader.readLine()) != null) {
            Matcher matcher = testNamePattern.matcher(line);
            String name = "";

            // Check if the test name is found
            boolean found = matcher.find();
            if (found) {
                name = matcher.group(1);
                errorInfo.put(name, name);
                // Check if the test run info in the next line
                Matcher errorMatcher = Pattern.compile(runningInfo).matcher(reader.readLine());
                boolean errorFound = errorMatcher.find();
                if (errorFound) {//found runningInfo line
                    int failures = Integer.parseInt(errorMatcher.group(2));
                    int errors = Integer.parseInt(errorMatcher.group(3));
                    //verify if the test failed
                    int totalErrors = failures + errors;

                    if (totalErrors > 0) {
                        System.out.println("--------------------------------------------------------------------------------------------------------");
                        System.out.println("Line: " + line);
                        for (int i = 0; i < totalErrors; i++) {
                            while (!(line = reader.readLine()).isEmpty()) {
                                System.out.println(line);
                            }
                        }
                        System.out.println("--------------------------------------------------------------------------------------------------------");
                    }


                }
            }
        }
        reader.close();

    }

    public static void main(String[] args) {
        // TODO code application logic here
        File logFile = new File("examples/log/1eb6d9d5b2a07720a0839457cee81e066dd932f2.log");
        try {
            extractErrorInfo(logFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

