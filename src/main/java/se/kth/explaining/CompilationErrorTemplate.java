package se.kth.explaining;

import se.kth.core.BreakingChange;
import se.kth.core.Changes;

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

    @Override
    public String clientError() {
        return "";
//        return ("3. An error was detected in line %s which is making use of an outdated API.\n " +
//                "``` java\n %s   %s;\n ```").formatted(breakingChange.getErrorInfo().getErrorInfo().getClientLinePosition(),
//                breakingChange.getErrorInfo().getErrorInfo().getClientLinePosition(), breakingChange.getErrorInfo().getClientLine());

    }

    public String clientErrorLine(BreakingChange breakingChange) {
        return "            *   An error was detected in line %s which is making use of an outdated API.\n".formatted(breakingChange.getErrorInfo().getErrorInfo().getClientLinePosition()) +
                "             ``` java\n" +
                "             %s   %s;\n".formatted(breakingChange.getErrorInfo().getErrorInfo().getClientLinePosition(), breakingChange.getErrorInfo().getClientLine()) +
                "            ```\n";
    }

    @Override
    public String logLine() {
        return "";
    }


    public String logLineew(BreakingChange breakingChange) {
        return "            *   >[%s](%s)\n".formatted(breakingChange.getErrorInfo().getErrorInfo().getErrorMessage(), breakingChange.getErrorInfo().getErrorInfo().getErrorLogGithubLink());
    }


    @Override
    public String brokenElement() {

//        String message = "1. Your client utilizes the instruction **%s** which has been modified in the new version of the dependency."
//                .formatted(breakingChange.getApiChanges().getOldElement());

        String message = "";
        // if there are more than one changes
        if (!changes.changes().isEmpty()) {
            String firstLine = "1. Your client utilizes **%d** instructions which has been modified in the new version of the dependency."
                    .formatted(changes.changes().size());

            String text = "";
            for (BreakingChange changes : changes.changes()) {

                String category = translateCategory(changes.getApiChanges().getCategory());

                String singleChange = "   * <details>\n" +
                        "        <summary>Instruction <b>%s</b> which has been <b>%s</b> in the new version of the dependency</summary>\n".formatted(changes.getErrorInfo().getElement(), category) +
                        "            \n" +
                        "        * <details>\n" +
                        "          <summary>The failure is identified from the logs generated in the build process. </summary>\n" +
                        "          \n" +
                        logLineew(changes) +
                        clientErrorLine(changes) +
                        "\n" +
                        "          </details>\n" +
                        "            \n" +
                        "     </details>\n";

                text = text.concat(singleChange);

            }


            message = firstLine + "\n" + text;


        }

        return message;
    }


}
