package se.kth.explaining;

import se.kth.core.BreakingChange;
import se.kth.core.Changes;
import se.kth.spoon_compare.SpoonResults;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;

public class CompilationErrorTemplate extends ExplanationTemplate {


    public CompilationErrorTemplate(Changes changes, String fileName) {
        super(changes, fileName);
    }

    @Override
    public String getHead() {

        return "CI detected that the dependency upgrade from version **%s** to **%s** has failed. Here are details to help you understand and fix the problem:"
                .formatted(changes.oldApiVersion().getName(), changes.newApiVersion().getName());
    }

    public String clientErrorLine(SpoonResults spoonResults) {
        return "            *   An error was detected in line %s which is making use of an outdated API.\n".formatted(spoonResults.getErrorInfo().getClientLinePosition()) +
                "             ``` java\n" +
                "             %s   %s;\n".formatted(spoonResults.getErrorInfo().getClientLinePosition(), spoonResults.getClientLine()) +
                "            ```\n";
    }

    @Override
    public String logLine() {
        return "";
    }


    public String logLineErrorMessage(SpoonResults spoonResults) {
        try {
            return "            *   >[%s](%s)\n".formatted(spoonResults.getErrorInfo().getErrorMessage(), spoonResults.getErrorInfo().getErrorLogGithubLink());
        } catch (
                Exception e) {

            return "";
        }

    }

    public String errorSection(BreakingChange breakingChange, int instructions) {
        StringBuilder message = new StringBuilder();
        for (SpoonResults spoonResults : breakingChange.getErrorInfo()) {
            try {
                message.append(logLineErrorMessage(spoonResults)).append(clientErrorLine(spoonResults));
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
        if (!changes.changes().isEmpty()) {
            String instructions = changes.changes().size() > 1 ? "instructions" : "instruction";
            String firstLine = "1. Your client utilizes **%d** %s which has been modified in the new version of the dependency."
                    .formatted(changes.changes().size(), instructions);

            String text = "";
            for (BreakingChange changes : changes.changes()) {
                String category = translateCategory(changes.getApiChanges().getChangeType().toString());
                final var singleChange = generateElementExplanation(changes, category, this.changes.changes().size());
                text = text.concat(singleChange);
            }
            message = firstLine + "\n" + text;
        }
        return message;
    }

    private String generateElementExplanation(BreakingChange changes, String category, int instructions) {
        if (instructions > 1) {
            return "   * <details>\n" +
                    "        <summary>%s <b>%s</b> which has been <b>%s</b> in the new version of the dependency</summary>\n".formatted(changes.getApiChanges().getInstruction(), changes.getErrorInfo().get(0).getElement(), category) +
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
            return "   * <summary>%s <b>%s</b> which has been <b>%s</b> in the new version of the dependency</summary>\n".formatted(changes.getApiChanges().getInstruction(), changes.getErrorInfo().get(0).getElement(), category) +
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
     * @param breakingChange BreakingChange
     * @return Number of new candidates and their method signature
     */
    public String newCandidates(BreakingChange breakingChange) {
        if (breakingChange.getApiChanges().getNewVariants().isEmpty()) {
            return "";
        }
        int amountVariants = breakingChange.getApiChanges().getNewVariants().size();
        StringBuilder message = new StringBuilder();

        if (amountVariants > 1) {
            message.append("        To address this incompatibility, there are ")
                    .append(amountVariants)
                    .append(" alternative options available in the new version of the dependency that can replace the incompatible %s currently used in the client. You can consider substituting the existing %s with one of the following options provided by the new version of the dependency:\n".formatted(breakingChange.getApiChanges().getInstruction().toLowerCase(), breakingChange.getApiChanges().getInstruction().toLowerCase()));

            breakingChange.getApiChanges().getNewVariants().forEach(v -> {
                message.append("        ``` java\n")
                        .append("        ").append(v.getReference().variantName()).append(";\n")
                        .append("        ```\n")
                ;
            });
        } else {
            message.append(
                    "        To resolve this issue, there are alternative options available in the new version of the dependency that can replace the incompatible %s currently used in the client. You can consider substituting the existing %s with one of the following options provided by the new version of the dependency\n".formatted(breakingChange.getApiChanges().getInstruction().toLowerCase(), breakingChange.getApiChanges().getInstruction().toLowerCase()));
            breakingChange.getApiChanges().getNewVariants().forEach(v -> {
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
    }


}
