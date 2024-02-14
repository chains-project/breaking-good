package se.kth.log_Analyzer;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenLogAnalyzer {

    private final File logFile;

    public MavenLogAnalyzer(File logFile) {
        this.logFile = logFile;
    }


    public MavenErrorLog analyzeCompilationErrors() throws IOException {

        return extractLineNumbersWithPaths(logFile.getAbsolutePath());
    }


    private MavenErrorLog extractLineNumbersWithPaths(String logFilePath) throws IOException {

        MavenErrorLog mavenErrorLogs = new MavenErrorLog();

        try {
            FileInputStream fileInputStream = new FileInputStream(logFilePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.ISO_8859_1);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            String currentPath = null;
            Pattern errorPattern = Pattern.compile("\\[ERROR\\] .*:\\[(\\d+),\\d+\\]");
            Pattern pathPattern = Pattern.compile("/[^:/]+(/[^\\[\\]:]+)");

            while ((line = reader.readLine()) != null) {
                Map<Integer, String> lines = new HashMap<>();
                Matcher matcher = errorPattern.matcher(line);
                if (matcher.find()) {
                    Integer lineNumber = Integer.valueOf(matcher.group(1));
                    Matcher pathMatcher = pathPattern.matcher(line);
                    lines.put(lineNumber, line);
                    if (pathMatcher.find()) {
                        currentPath = pathMatcher.group();

                    }
                    if (currentPath != null) {
                        mavenErrorLogs.addErrorInfo(currentPath, new MavenErrorLog.ErrorInfo(String.valueOf(lineNumber), currentPath, line));
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mavenErrorLogs;
    }
}