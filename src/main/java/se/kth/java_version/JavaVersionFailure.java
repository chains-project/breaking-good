package se.kth.java_version;

import java.util.List;
import java.util.Map;

@lombok.Getter
@lombok.Setter
public class JavaVersionFailure {

    // Both Java version incompatibility
    private JavaVersionIncompatibility incompatibility;
    //list of error messages
    private String errorMessages;
    //list of java versions in the workflow files
    private Map<String, List<Integer>> javaInWorkflowFiles;


    public JavaVersionFailure() {
    }


    public JavaVersionFailure(JavaVersionIncompatibility incompatibility, String errorMessages, Map<String, List<Integer>> javaInWorkflowFiles) {
        this.incompatibility = incompatibility;
        this.errorMessages = errorMessages;
        this.javaInWorkflowFiles = javaInWorkflowFiles;
    }



}
