package se.kth.log_Analyzer;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenLogAnalyzer {

    private final File logFile;

    public MavenLogAnalyzer(File logFile) {
        this.logFile = logFile;
    }


    public Set<MavenErrorLog> analyzeCompilationErrors() throws IOException {

        return extractLineNumbersWithPaths(logFile.getAbsolutePath());
    }


    private Set<MavenErrorLog> extractLineNumbersWithPaths(String logFilePath) throws IOException {
        Map<String, Map<Integer, String>> lineNumbersWithPaths = new HashMap<>();
        Set<MavenErrorLog> mavenErrorLogs = new HashSet<>();


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
                        if (currentPath != null) {
                            lineNumbersWithPaths.put(currentPath, lines);
                            MavenErrorLog errorLog = new MavenErrorLog(Integer.parseInt(matcher.group(1)), currentPath, line);
                            mavenErrorLogs.add(errorLog);
                        }
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mavenErrorLogs;
    }

    public record ErrorLine(String name, String line, String pattern) {

    }

}