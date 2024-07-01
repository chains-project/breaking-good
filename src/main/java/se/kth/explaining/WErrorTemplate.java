package se.kth.explaining;

import se.kth.core.ChangesBetweenVersions;
import se.kth.log_Analyzer.ErrorInfo;
import se.kth.werror.WErrorMetadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static java.lang.Thread.sleep;

public class WErrorTemplate extends ExplanationTemplate {

    WErrorMetadata errorMetadata;

    public WErrorTemplate(WErrorMetadata errorMetadata, String fileName) {
        super(fileName);
        this.errorMetadata = errorMetadata;
    }

    public WErrorTemplate(WErrorMetadata errorMetadata, String fileName, ChangesBetweenVersions changes) {
        super(changes, fileName);
        this.errorMetadata = errorMetadata;
    }

    @Override
    public String getHead() {

        return "CI detected that the dependency upgrade from version **%s** to **%s** has failed. Here are details to help you understand and fix the problem: \n"
                .formatted(changes.oldApiVersion().getName(), changes.newApiVersion().getName());
    }

    @Override
    public String logLine() {

        Map<String, Set<ErrorInfo>> errorInfo = errorMetadata.getWarningList().getErrorInfo();

        String message = "<details>\n" +
                "<summary>Here you can find a list of warnings identified from the logs generated in the build process</summary>\n" +
                "\n";

        StringBuilder listFiles = new StringBuilder();

        errorInfo.forEach((key, value) -> {

            value.forEach(error -> {
                listFiles.append("*    > %s \n".formatted(error.getErrorMessage().replace(System.lineSeparator(), "<br>")));
                listFiles.append("\n");
            });
        });

        listFiles.append("</details>\n");

        return message + listFiles;
    }

    @Override
    public String brokenElement() {

        String externalOptionEnable = "1. This occurs because the option **failureOnWarning** is activated in the dependency configuration file: \n" +
                "   * To solve this problem, you need to add the option **failureOnWarning** in pom.xml file.\n" +
                "    ```xml\n" +
                "    <plugin>\n" +
                "      <groupId>org.apache.maven.plugins</groupId>\n" +
                "      <artifactId>maven-compiler-plugin</artifactId>\n" +
                "      <version>LATEST</version>\n" +
                "      <configuration>\n" +
                "           <failOnWarning>false</failOnWarning>\n" +
                "      </configuration>\n" +
                "    </plugin>\n" +
                "    ```";

        String internalOptionEnable = "1. This occurs because the option **failureOnWarning** is activated in the configuration file : \n";

        String errormessage = errorMetadata.getIsClientConfigProblem() ? internalOptionEnable : externalOptionEnable;

        StringBuilder brokenElement = new StringBuilder(errormessage);

        for (File file : errorMetadata.getWErrorFiles()) {
            File currentFile = file;
            String rootPath = "";
            while (currentFile.getParentFile() != null) {
                currentFile = currentFile.getParentFile();
                rootPath = currentFile.getName() + "/" + rootPath;
                if (currentFile.getName().equals(errorMetadata.getClient())) {
                    break;
                }
            }
            brokenElement.append("   - %s%s\n".formatted(rootPath, file.getName()));
        }

        return brokenElement.toString();
    }

    @Override
    public void generateTemplate() {

        FileWriter markdownFile = null;
        try {
            markdownFile = new FileWriter(fileName);
            markdownFile.write(getHead());
            markdownFile.write("\n");
            markdownFile.write(brokenElement());
            markdownFile.write("\n");
            markdownFile.write(logLine());
            markdownFile.close(); //
            sleep(3000);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
