package se.kth.werror;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import se.kth.core.ChangesBetweenVersions;

import se.kth.explaining.WErrorTemplate;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.log_Analyzer.MavenLogAnalyzer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WError {

    private final String failOnWarningTag = "failOnWarning";
    private final String wError = "Werror";
    private final String fileName = "pom.xml";
    private final String explanationName;

    public WError(String explanationName) {
        this.explanationName = explanationName;
    }


    public NodeList parsePOM(File pom, String tag) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(pom.getAbsolutePath());

        // get the root element
        return doc.getElementsByTagName(tag);
    }

    private List<File> findPomFiles(File file) {
        List<File> pomFiles = new ArrayList<>();

        List<File> search = List.of(Objects.requireNonNull(file.listFiles()));

        for (File f : search) {
            if (f.isDirectory()) {
                pomFiles.addAll(findPomFiles(f));
            } else {
                if (f.getName().equals(fileName)) {
                    pomFiles.add(f);
                }
            }
        }

        return pomFiles;
    }


    public List<File> findWerror(File file) {
        List<File> pomFiles = findPomFiles(file);
        List<File> werrorFiles = new ArrayList<>();

        for (File pom : pomFiles) {
            try {
                NodeList fileOnWarningsNode = parsePOM(pom, failOnWarningTag);

                if (fileOnWarningsNode.getLength() > 0) {
                    werrorFiles.add(pom);
                }

            } catch (ParserConfigurationException | IOException | SAXException e) {
                System.out.println("Error parsing the file " + pom.getAbsolutePath() + " " + e.getMessage());
            }
        }
        return werrorFiles;
    }


    public void analyzeWerror(String log, String client, ChangesBetweenVersions changes) throws IOException {
        // prepare the log file

        MavenLogAnalyzer mavenLog = new MavenLogAnalyzer(new File(log));

        boolean isWerror = mavenLog.isWerror(mavenLog.getLogFile().getAbsolutePath());
        if (!isWerror) {
            System.out.println("No Werror found in the log file");
            return;
        }

        // Extract the Werror line from the log file
        MavenErrorLog wError = mavenLog.extractWerrorLine(mavenLog.getLogFile().getAbsolutePath());


        // Extract the warning lines from the log file
        MavenErrorLog errorLog = mavenLog.extractWarningLines(mavenLog.getLogFile().getAbsolutePath());


        // find error in the client root folder.
        List<File> werrorFiles = findWerror(new File(client));

        // Create a WErrorMetadata object with the extracted information
        WErrorMetadata wErrorMetadata = new WErrorMetadata(wError, errorLog, !werrorFiles.isEmpty(), werrorFiles, clientName(client));

        // Create a WErrorTemplate object with the WErrorMetadata object and the log file name
        WErrorTemplate wErrorTemplate = new WErrorTemplate(wErrorMetadata, explanationName, changes);
        wErrorTemplate.generateTemplate();

    }

    private String clientName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

}
