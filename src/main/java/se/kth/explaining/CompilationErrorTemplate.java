package se.kth.explaining;

import se.kth.core.BreakingChange;
import se.kth.core.Changes;

public class CompilationErrorTemplate extends ExplanationTemplate {


    public CompilationErrorTemplate(Changes changes, BreakingChange breakingChange) {
        super(changes, breakingChange);
    }

    @Override
    public String getHead() {

        return "CI detected that the dependency upgrade from version **%s** to **%s** has failed. Here are details to help you understand and fix the problem:"
                .formatted(breakingChange.getApiChanges().getOldVersion().getName(), breakingChange.getApiChanges().getNewVersion().getName());
    }

    @Override
    public String clientError() {
        return ("2. An error was detected in line %s which is making use of an outdated API.\n " +
                "``` java\n %s   %s;\n ```").formatted(breakingChange.getErrorInfo().getErrorInfo().getClientLinePosition(),
                breakingChange.getErrorInfo().getErrorInfo().getClientLinePosition(), breakingChange.getErrorInfo().getClientLine());

    }

    @Override
    public String logLine() {
        String a = "3. Logs provide additional information\n" +
                "\n" +
                ">[ERROR] /biapi/src/main/java/xdev/tableexport/export/ReportBuilder.java:[369,81] incompatible types: int cannot be converted to java.lang.Float.\n";
        return ("3. You can find more information in the logs\n " +
                "\n" +
                ">%s.").formatted(breakingChange.getErrorInfo().getErrorInfo().getErrorMessage());
    }

    @Override
    public String type() {


        return "1. Your client utilizes the instruction **%s** which has been modified in the new version of the dependency."
                .formatted(breakingChange.getApiChanges().getOldElement());
    }


}
