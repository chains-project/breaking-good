package se.kth.explaining;

import se.kth.core.ChangesBetweenVersions;
import se.kth.log_Analyzer.ErrorInfo;
import se.kth.spoon_compare.SpoonResults;
import se.kth.transitive_changes.Dependency;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.Thread.sleep;

public class RemAddModdIndirectTemplate extends ExplanationTemplate {

    private final Dependency dependency;
    String action;
    Set<SpoonResults> results;

    public RemAddModdIndirectTemplate(ChangesBetweenVersions changes, String fileName, Dependency dependency, String action, Set<SpoonResults> results) {
        super(changes, fileName);
        this.dependency = dependency;
        this.action = action;
        this.results = results;
    }

    @Override
    public String getHead() {

        String header = "CI detected that the dependency upgrade from version **%s** to **%s** has failed. Here are details to help you understand and fix the problem:"
                .formatted(changes.oldApiVersion().getName(), changes.newApiVersion().getName());

        String indirectDependency = "\n" +
                "\n" +
                "Your code depends on indirect dependency **%s-%s** which has been %s remove in the new version of the updated dependency."
                        .formatted(dependency.getArtifactId(), dependency.getVersion(), action);
        return header + indirectDependency;
    }


    //
    public String clientErrorLine(ErrorInfo errorInfo, SpoonResults brokenChange) {
        return "            *   An error was detected in line %s which is making use of an outdated API.\n".formatted(errorInfo.getClientLinePosition()) +
                "             ``` java\n" +
                "             %s   %s;\n".formatted(errorInfo.getClientLinePosition(), brokenChange.getCtElement().toString()) +
                "            ```\n";
    }

    @Override
    public String logLine() {
        return "";
    }

    @Override
    public String brokenElement() {

        String message = "";
        // if there are more than one changes
        if (!results.isEmpty()) {
            String instructions = results.size() > 1 ? "constructs" : "construct";
            String firstLine = "1. Your client utilizes **%d** %s from the indirect dependency."
                    .formatted(results.size(), instructions);

            List<String> names = new ArrayList<>();
            String text = "";
            for (SpoonResults changes : results) {
                if (names.contains(changes.getName())) {
                    continue;
                } else {
                    names.add(changes.getName());
                }
                final var singleChange = generateElementExplanation(changes, this.changes.brokenChanges().size());
                text = text.concat(singleChange);
            }
            message = firstLine + "\n" + text;
        }
        return message;
    }

    private String generateElementExplanation(SpoonResults changes, int instructions) {
        if (instructions > 1) {
            return "   * <details>\n" +
                    "        <summary><b>%s</b> is used from the removed indirect dependency</summary>\n".formatted(changes.getName()) +
                    "            \n" +
                    "        * <details>\n" +
                    "          <summary>The failure is identified from the logs generated in the build process. </summary>\n" +
                    "          \n" +
                    errorSection(changes, instructions) +
                    "\n" +
                    "          </details>\n" +
                    "            \n" +

                    "     </details>\n";
        } else {
            return "   * <summary><b>%s</b>  is used from the removed indirect dependency</summary>\n".formatted(changes.getName()) +
                    "            \n" +
                    "        *  <summary>The failure is identified from the logs generated in the build process. </summary>\n" +
                    "          \n" +
                    errorSection(changes, instructions) +
                    "            \n";


        }
    }


    public String logLineErrorMessage(ErrorInfo errorInfo) {
        try {
            return "            *   >[%s](%s)\n".formatted(errorInfo.getErrorMessage().concat("<br>&nbsp;&nbsp;&nbsp;&nbsp;" + errorInfo.getAdditionalInfo()), errorInfo.getErrorLogGithubLink());
        } catch (
                Exception e) {
            return "";
        }

    }

    public String errorSection(SpoonResults brokenChange, int instructions) {
        StringBuilder message = new StringBuilder();
        ErrorInfo errorInfo = brokenChange.getErrorInfo();
        try {
            message.append(logLineErrorMessage(errorInfo)).append(clientErrorLine(errorInfo, brokenChange));
        } catch (Exception e) {
            return "";
        }


        return message.toString();
    }


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
