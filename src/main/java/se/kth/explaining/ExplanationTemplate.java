package se.kth.explaining;

import japicmp.model.JApiCompatibilityChangeType;
import se.kth.core.Changes_V2;

import java.io.FileWriter;
import java.io.IOException;

import static java.lang.Thread.sleep;

public abstract class ExplanationTemplate {

    protected Changes_V2 changes;

    protected String fileName;

    public ExplanationTemplate(Changes_V2 changes, String fileName) {
        this.changes = changes;
        this.fileName = fileName;

    }

    public abstract String getHead();

    public abstract String logLine();

    public abstract String brokenElement();

    public String translateCategory(String category) {
        return switch (category) {
            case "METHOD_REMOVED":
            case "REMOVED":
                yield "removed";
            case "[METHOD_ADDED]":
                yield "added";
            default:
                yield "";
        };
    }

    public String getConstruct(JApiCompatibilityChangeType apiUse) {
        return switch (apiUse) {
            case METHOD_ADDED_TO_INTERFACE,
                    METHOD_REMOVED,
                    METHOD_NOW_FINAL,
                    METHOD_NOW_ABSTRACT,
                    METHOD_RETURN_TYPE_CHANGED,
                    METHOD_REMOVED_IN_SUPERCLASS:
                yield "Method";
            case FIELD_REMOVED,
                    FIELD_NOW_FINAL,
                    FIELD_NO_LONGER_STATIC,
                    FIELD_NOW_STATIC,
                    FIELD_LESS_ACCESSIBLE,
                    ANNOTATION_DEPRECATED_ADDED,
                    FIELD_TYPE_CHANGED,
                    FIELD_STATIC_AND_OVERRIDES_STATIC,
                    FIELD_GENERICS_CHANGED:
                yield "Field";
            case CLASS_LESS_ACCESSIBLE,
                    CLASS_NOW_ABSTRACT,
                    CLASS_NOW_FINAL,
                    CLASS_NOW_CHECKED_EXCEPTION,
                    CLASS_REMOVED,
                    SUPERCLASS_ADDED,
                    SUPERCLASS_REMOVED,
                    CLASS_TYPE_CHANGED,
                    CLASS_GENERIC_TEMPLATE_CHANGED,
                    CLASS_GENERIC_TEMPLATE_GENERICS_CHANGED:
                yield "Class";

            case INTERFACE_REMOVED,
                    INTERFACE_ADDED,
                    METHOD_ABSTRACT_ADDED_TO_CLASS,
                    METHOD_NEW_DEFAULT:
                yield "Interface";
            case CONSTRUCTOR_REMOVED:
                yield "Constructor";    // Constructor

            default:
                yield "";
        };
    }

    public void generateTemplate() {

        if (changes.brokenChanges().isEmpty()) {
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