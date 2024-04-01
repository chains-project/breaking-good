package se.kth.explaining;

import se.kth.core.Changes;

import java.io.FileWriter;
import java.io.IOException;

import static java.lang.Thread.sleep;

public abstract class ExplanationTemplate {

    protected Changes changes;

    protected String fileName;

    public ExplanationTemplate(Changes changes, String fileName) {
        this.changes = changes;
        this.fileName = fileName;

    }

    public abstract String getHead();

    public abstract String logLine();

    public abstract String brokenElement();

    public String translateCategory(String category) {
        return switch (category) {
            case "[METHOD_REMOVED]":
            case "REMOVED":
                yield "removed";
            case "[METHOD_ADDED]":
                yield "added";
            default:
                yield "";
        };
    }

    public void generateTemplate() {

        if(changes.changes().isEmpty()){
            return;
        }

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

    ;


}