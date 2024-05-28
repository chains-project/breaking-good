package se.kth.explaining;

import se.kth.core.Changes_V2;
import se.kth.java_version.JavaVersionFailure;
import se.kth.java_version.JavaVersionIncompatibility;

import java.io.FileWriter;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class JavaVersionIncompatibilityTemplate extends ExplanationTemplate {

    private final JavaVersionFailure javaVersionFailure;

    public JavaVersionIncompatibilityTemplate(Changes_V2 changes, String fileName, JavaVersionFailure javaVersionFailure) {
        super(changes, fileName);
        this.javaVersionFailure = javaVersionFailure;
    }

    @Override
    public String getHead() {
        return "CI detected that the dependency upgrade from version **%s** to **%s** has failed.".
                formatted(changes.oldApiVersion().getName(), changes.newApiVersion().getName())
                + " \nThe new version of the dependency require a different version of Java. \n";
    }

    @Override
    public String logLine() {
        return "";
    }

    @Override
    public String brokenElement() {
        // Get the incompatibility version
        JavaVersionIncompatibility incompatibility = javaVersionFailure.getIncompatibility();
        // Create a string with the incompatibility version
        String CI = "CI uses **Java %s**. The new version of the dependency requires **Java %s**. \n"
                .formatted(incompatibility.actualVersion(), incompatibility.expectedVersion());

        String files = "To resolve this issue, you need to update the Java version in the following files:" +
                " \n";

        StringBuilder listFiles = new StringBuilder();

        javaVersionFailure.getJavaInWorkflowFiles().forEach((file, versions) -> {
            listFiles.append("- `%s`".formatted(file));
            listFiles.append("\n");
        });

        return CI + "\n" + files + listFiles.toString();

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
            markdownFile.write("\n");
            markdownFile.close(); //
            sleep(3000);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
