package se.kth.log_Analyzer.test_failures;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import se.kth.log_Analyzer.MavenErrorLog;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestFailure {


    private static final String surefireFolder = "target/surefire-reports";


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


    public static List<File> getTestFiles(Path clientPath) {
        Path testFilesPath = clientPath.resolve(surefireFolder);
        return Arrays.stream(Objects.requireNonNull(testFilesPath.toFile().listFiles())).filter(file -> file.getName().endsWith("xml")).toList();
    }

    public static void parserSurefireTests(File logFile, Path client) throws IOException {
        List<File> testFilesList = getTestFiles(client);
        for (File testFile : testFilesList) {

            try {
                parseTest(testFile);
            } catch (ParserConfigurationException | SAXException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Size of test files: " + testFilesList.size());
    }


    public static void parseTest(File testFile) throws IOException, ParserConfigurationException, SAXException {


        /*
         * 1. Parse the test file
         */
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(testFile);
        doc.getDocumentElement().normalize();

        /*
         * 2. Extract the test name
         */
        String testName = doc.getElementsByTagName("testcase").item(0).getAttributes().getNamedItem("name").getNodeValue();

        /*
         * 3. Extract the error message
         */
        NodeList testCaseList = doc.getElementsByTagName("testcase");
        for (int i = 0; i < testCaseList.getLength(); i++) {
            Node node = testCaseList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String name = element.getAttribute("name");
                String classname = element.getAttribute("classname");
                String time = element.getAttribute("time");

                NodeList errorList = ((Element) node).getElementsByTagName("error");
                for (int j = 0; j < errorList.getLength(); j++) {
                    Node errorNode = errorList.item(j);
                    if (errorNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element errorElement = (Element) errorNode;
                        String message = errorElement.getAttribute("message");
                        String type = errorElement.getAttribute("type");
                        String errorText = errorElement.getTextContent();
                        System.out.println("Test Name: " + name);
                        System.out.println("Class Name: " + classname);
                        System.out.println("Time: " + time);
                        System.out.println("Error Message: " + message);
                        System.out.println("Error Type: " + type);
                        System.out.println("Error Text: " + errorText);
                        parseError(errorText);
                    }
                }
            }
        }


    }
    
    public static void parseError(String input){

            // Pattern to match the Java line number
            Pattern lineNumberPattern = Pattern.compile(".*\\.java:(\\d+).*");

            // Pattern to match the exception and associated message
            Pattern exceptionPattern = Pattern.compile("Caused by: (.*)");

            Matcher lineNumberMatcher = lineNumberPattern.matcher(input);
            Matcher exceptionMatcher = exceptionPattern.matcher(input);

            // Find the line number
            if (lineNumberMatcher.find()) {
                String lineNumber = lineNumberMatcher.group(1);
                System.out.println("Line Number of Java File: " + lineNumber);
            } else {
                System.out.println("Java Line Number not found.");
            }

            // Find the exception and associated message
            if (exceptionMatcher.find()) {
                String exceptionMessage = exceptionMatcher.group(1);
                System.out.println("Exception and Associated Message: " + exceptionMessage);
            } else {
                System.out.println("Exception not found.");
            }
        }


    public static void main(String[] args) {
        // TODO code application logic here
        File logFile = new File("examples/log/1eb6d9d5b2a07720a0839457cee81e066dd932f2.log");
//        final var testFiles = getTestFiles(Path.of("/Users/frank/Downloads/Test_Failures/webapp"));
        try {
            parserSurefireTests(logFile, Path.of("/Users/frank/Downloads/Test_Failures/webapp"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


//        try {
//            extractErrorInfo(logFile.getAbsolutePath());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }
}

