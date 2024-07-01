package se.kth.explaining;

import se.kth.core.ChangesBetweenVersions;

import se.kth.java_version.JavaVersionFailure;
import se.kth.java_version.JavaVersionIncompatibility;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import static java.lang.Thread.sleep;

public class JavaVersionIncompatibilityTemplate extends ExplanationTemplate {

    private final JavaVersionFailure javaVersionFailure;

    public JavaVersionIncompatibilityTemplate(ChangesBetweenVersions changes, String fileName, JavaVersionFailure javaVersionFailure) {
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
        Set<String> errorMessages = javaVersionFailure.getErrorMessages();

        String message = "<details>\n" +
                "<summary>Here you can find a list of failures identified from the logs generated in the build process</summary>\n" +
                "\n";

        StringBuilder listFiles = new StringBuilder();

        errorMessages.forEach(error -> {
            listFiles.append("*    > %s \n".formatted(error.replace(System.lineSeparator(), "<br>")));
            listFiles.append("\n");
        });
        listFiles.append("</details>\n");

        return message + listFiles;
    }

    @Override
    public String brokenElement() {
        // Get the incompatibility version

        JavaVersionIncompatibility incompatibility = javaVersionFailure.getIncompatibility();


        // Create a string with the incompatibility version
        String CI = "CI uses **%s** (class version **%s**). The new version of the dependency requires **%s** (class version **%s**). \n"
                .formatted(
                        incompatibility.mapVersions(incompatibility.shouldBeVersion()),
                        incompatibility.shouldBeVersion(),
                        incompatibility.mapVersions(incompatibility.wrongVersion())
                        , incompatibility.wrongVersion()
                );

        String files = "To resolve this issue, you need to update the Java version to **%s** in the following files:".formatted(incompatibility.mapVersions(incompatibility.wrongVersion())) +
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
            markdownFile.write(logLine());
            markdownFile.close(); //
            sleep(3000);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
