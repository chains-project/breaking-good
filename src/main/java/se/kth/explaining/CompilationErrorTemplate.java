package se.kth.explaining;

import se.kth.core.ChangesBetweenVersions;
import se.kth.log_Analyzer.ErrorInfo;
import se.kth.sponvisitors.BrokenChanges;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;

public class CompilationErrorTemplate extends ExplanationTemplate {


    public CompilationErrorTemplate(ChangesBetweenVersions changes, String fileName) {
        super(changes, fileName);
    }

    @Override
    public String getHead() {

        return "CI detected that the dependency upgrade from version **%s** to **%s** has failed. Here are details to help you understand and fix the problem:"
                .formatted(changes.oldApiVersion().getName(), changes.newApiVersion().getName());
    }

    public String clientErrorLine(ErrorInfo errorInfo, BrokenChanges brokenChange) {
        if (brokenChange.getBrokenUse().element().toString().contains("This NamingStrategy takes the original class'")) {
            System.out.println(brokenChange.getBrokenUse().element().toString());
        }

        return "            *   An error was detected in line %s which is making use of an outdated API.\n".formatted(errorInfo.getClientLinePosition()) +
                "             ``` java\n" +
                "             %s   %s;\n".formatted(errorInfo.getClientLinePosition(), brokenChange.getBrokenUse().element().toString()) +
                "            ```\n";
    }

    @Override
    public String logLine() {
        return "";
    }


    public String logLineErrorMessage(ErrorInfo errorInfo) {
        try {
            return "            *   >[%s](%s)\n".formatted(errorInfo.getErrorMessage().concat("<br>&nbsp;&nbsp;&nbsp;&nbsp;" + errorInfo.getAdditionalInfo()), errorInfo.getErrorLogGithubLink());
        } catch (
                Exception e) {
            return "";
        }

    }

    public String errorSection(BrokenChanges brokenChange, int instructions) {
        StringBuilder message = new StringBuilder();
        for (ErrorInfo errorInfo : brokenChange.getErrorInfo()) {
            try {
                message.append(logLineErrorMessage(errorInfo)).append(clientErrorLine(errorInfo, brokenChange));
            } catch (Exception e) {
                return "";
            }

        }
        return message.toString();
    }


    /**
     * This method generates the broken element section of the markdown file
     *
     * @return String broken element section
     */
    @Override
    public String brokenElement() {

        String message = "";
        // if there are more than one changes
        if (!changes.brokenChanges().isEmpty()) {

            String instructions = changes.brokenChanges().size() > 1 ? "constructs" : "construct";
            String firstLine = "1. Your client utilizes **%d** %s which has been modified in the new version of the dependency."
                    .formatted(changes.brokenChanges().size(), instructions);

            String text = "";
            for (BrokenChanges changes : changes.brokenChanges()) {
                String category = translateCategory(changes.getBrokenUse().change().toString());
                final var singleChange = generateElementExplanation(changes, category, this.changes.brokenChanges().size());
                text = text.concat(singleChange);
            }
            message = firstLine + "\n" + text;
        }
        return message;
    }

    private String generateElementExplanation(BrokenChanges changes, String category, int instructions) {
        if (instructions > 1) {
            return "   * <details>\n" +
                    "        <summary>%s <b> </b> which has been <b>%s</b> in the new version of the dependency</summary>\n".formatted(getConstruct(changes.getBrokenUse().change()), changes.getBrokenUse().source(), category) +
                    "            \n" +
                    "        * <details>\n" +
                    "          <summary>The failure is identified from the logs generated in the build process. </summary>\n" +
                    "          \n" +
                    errorSection(changes, instructions) +
                    "\n" +
                    "          </details>\n" +
                    "            \n" +
                    newCandidates(changes) +
                    "     </details>\n";
        } else {
            return "   * <summary>%s <b>%s</b> which has been <b>%s</b> in the new version of the dependency</summary>\n".formatted(getConstruct(changes.getBrokenUse().change()), changes.getBrokenUse().source(), category) +
                    "            \n" +
                    "        *  <summary>The failure is identified from the logs generated in the build process. </summary>\n" +
                    "          \n" +
                    errorSection(changes, instructions) +
                    "            \n" +
                    newCandidates(changes);

        }
    }

    private String getName(CtElement bc) {
        return bc instanceof CtNamedElement ne ? ne.getSimpleName() : bc.getShortRepresentation();
    }


    /**
     * This method translates the category of the change to a human-readable format
     *
     * <p>To resolve this issue, there are alternative options available in the new version of the dependency that can replace the incompatible instruction currently used in the client. You can consider substituting the existing instruction with one of the following options provided by the new version of the dependency
     * ``` java
     * net.datafaker.DateAndTime.between(java.sql.Timestamp,java.sql.Timestamp);
     * <p/> ```
     *
     * @return Number of new candidates and their method signature
     */
    public String newCandidates(BrokenChanges brokenChange) {
        if (brokenChange.getNewVariants().isEmpty()) {
            return "";
        }
        int amountVariants = brokenChange.getNewVariants().size();
        StringBuilder message = new StringBuilder();

        if (amountVariants > 1) {
            message.append("        To address this incompatibility, there are ")
                    .append(amountVariants)
                    .append(" alternative options available in the new version of the dependency that can replace the incompatible %s currently used in the client. You can consider substituting the existing %s with one of the following options provided by the new version of the dependency:\n".formatted(brokenChange.getNewVariants().stream().toList().get(0).getInstruction().toLowerCase(), brokenChange.getNewVariants().stream().toList().get(0).getInstruction().toLowerCase()));

            brokenChange.getNewVariants().forEach(v -> {
                message.append("        ``` java\n")
                        .append("        ").append(v.getReference().variantName()).append(";\n")
                        .append("        ```\n")
                ;
            });
        } else {
            message.append(
                    "        To resolve this issue, there are alternative options available in the new version of the dependency that can replace the incompatible %s currently used in the client. You can consider substituting the existing %s with one of the following options provided by the new version of the dependency\n".formatted(brokenChange.getNewVariants().stream().toList().get(0).getInstruction().toLowerCase(), brokenChange.getNewVariants().stream().toList().get(0).getInstruction().toLowerCase()));
            brokenChange.getNewVariants().forEach(v -> {
                if (v.getReference() != null)
                    try {
                        message.append("        ``` java\n")
                                .append("        ").append(v.getReference().variantName()).append(";\n")
                                .append("        ```\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            });
        }
        return message.toString();

        // return"";
    }


}
