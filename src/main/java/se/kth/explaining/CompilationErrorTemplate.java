package se.kth.explaining;

import japicmp.model.JApiMethod;
import se.kth.core.BreakingChange;
import se.kth.core.Changes;
import se.kth.spoon_compare.SpoonResults;

import static java.util.stream.Collectors.joining;

public class CompilationErrorTemplate extends ExplanationTemplate {


    public CompilationErrorTemplate(Changes changes, String fileName) {
        super(changes, fileName);
    }

    @Override
    public String getHead() {

        BreakingChange breakingChange = changes.changes().iterator().next();

        return "CI detected that the dependency upgrade from version **%s** to **%s** has failed. Here are details to help you understand and fix the problem:"
                .formatted(breakingChange.getApiChanges().getOldVersion().getName(), breakingChange.getApiChanges().getNewVersion().getName());
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
        return "            *   >[%s](%s)\n".formatted(spoonResults.getErrorInfo().getErrorMessage(), spoonResults.getErrorInfo().getErrorLogGithubLink());

    }

    public String errorSection(BreakingChange breakingChange, int instructions) {
        StringBuilder message = new StringBuilder();
        for (SpoonResults spoonResults : breakingChange.getErrorInfo()) {
            message.append(logLineErrorMessage(spoonResults)).append(clientErrorLine(spoonResults));
        }

        return message.toString();
    }


    /**
     * This method generates the broken element section of the markdown file
     * @return
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

            breakingChange.getApiChanges().getNewVariants().forEach(apiChange -> {
                JApiMethod method = ((JApiMethod) apiChange.getBehavior());
                message.append("        ``` java\n")
                        .append("        ").append(methodName(method)).append(";\n")
                        .append("        ```\n")
                ;
            });
        } else {
            message.append(
                    "        To resolve this issue, there are alternative options available in the new version of the dependency that can replace the incompatible %s currently used in the client. You can consider substituting the existing %s with one of the following options provided by the new version of the dependency\n".formatted(breakingChange.getApiChanges().getInstruction().toLowerCase(), breakingChange.getApiChanges().getInstruction().toLowerCase()));
            breakingChange.getApiChanges().getNewVariants().forEach(apiChange -> {
                JApiMethod method = ((JApiMethod) apiChange.getBehavior());
                message.append("        ``` java\n")
                        .append("        ").append(methodName(method)).append(";\n")
                        .append("        ```\n");
            });
        }
        return message.toString();
    }

    /**
     * This method returns the method name in the format of returnType methodName(params)
     *
     * @param method JApiMethod
     * @return String
     */
    private String methodName(JApiMethod method) {

        String[] fullReturnTypeName = method.getReturnType().getNewReturnType().split("\\.");
        String returnTypeClass = fullReturnTypeName[fullReturnTypeName.length - 1].equals("n.a.") ? "void" : fullReturnTypeName[fullReturnTypeName.length - 1];

        String params = method.getParameters().stream().map(jApiParameter -> {
            String[] fullParameterTypeName = jApiParameter.getType().split("\\.");
            return fullParameterTypeName[fullParameterTypeName.length - 1];
        }).collect(joining(","));

        return "%s %s(%s)".formatted(returnTypeClass, method.getName(), params);
    }


}
