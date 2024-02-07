package se.kth.explaining;

import se.kth.core.BreakingChange;
import se.kth.core.Changes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public abstract class ExplanationTemplate {

    protected Changes changes;
    BreakingChange breakingChange;

    public ExplanationTemplate(Changes changes, BreakingChange breakingChange) {
        this.changes = changes;
        this.breakingChange = breakingChange;

    }

    public abstract String getHead();

    public abstract String clientError();

    public abstract String logLine();

    public abstract String type();

    public void generateTemplate() {
        FileWriter markdownFile = null;
        try {
            markdownFile = new FileWriter("README" + new Date().getTime() + ".md");
            markdownFile.write(getHead());
            markdownFile.write("\n");
            markdownFile.write(type());
            markdownFile.write("\n");
            markdownFile.write(clientError());
            markdownFile.write("\n");
            markdownFile.write(logLine());
            markdownFile.write("\n");
            markdownFile.close(); //
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ;


}