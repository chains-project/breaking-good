package se.kth.log_Analyzer;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenLogAnalyzer {

    private final File logFile;

    private String projectURL;

    public MavenLogAnalyzer(File logFile) {
        this.logFile = logFile;
    }

    public MavenLogAnalyzer(File logFile, String projectURL) {
        this.logFile = logFile;
        this.projectURL = projectURL;
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

            int lineNumberInFile = 0;
            while ((line = reader.readLine()) != null) {
                lineNumberInFile++;
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

                        MavenErrorLog.ErrorInfo errorInfo = new MavenErrorLog.ErrorInfo(String.valueOf(lineNumber), currentPath, line, lineNumberInFile);
                        errorInfo.setErrorLogGithubLink(generateLogsLink(projectURL, 4, lineNumberInFile));
                        mavenErrorLogs.addErrorInfo(currentPath, errorInfo);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mavenErrorLogs;
    }

    private String generateLogsLink(String projectURL, int step, int lineNumber) {
        return "https://github.com/chains-project/breaking-good/actions/runs/8110103454/job/22166641300#step:%d:%d".formatted(step, lineNumber);
    }
}