package se.kth.java_version;

import java.util.List;
import java.util.Map;
import java.util.Set;

@lombok.Getter
@lombok.Setter
public class JavaVersionFailure {

    // Both Java version incompatibility
    private JavaVersionIncompatibility incompatibility;
    //list of error messages for each java version incompatibility
    Map<JavaVersionIncompatibility, Set<String>> diffVersionErrors;
    //list of java versions in the workflow files
    private Map<String, List<Integer>> javaInWorkflowFiles;

    private Set<String> errorMessages;

    public JavaVersionFailure() {
    }

    public JavaVersionFailure(JavaVersionIncompatibility incompatibility, Map<String, List<Integer>> javaInWorkflowFiles) {
        this.incompatibility = incompatibility;
        this.javaInWorkflowFiles = javaInWorkflowFiles;
    }


}
