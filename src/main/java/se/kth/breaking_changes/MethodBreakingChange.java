package se.kth.breaking_changes;

import japicmp.model.JApiBehavior;
import japicmp.model.JApiMethod;

import static java.util.stream.Collectors.joining;

public class MethodBreakingChange extends AbstractApiChange {

    private final JApiBehavior jApiMethod;

    public MethodBreakingChange(JApiBehavior jApiMethod) {
        this.jApiMethod = jApiMethod;
    }

    /**
     * This method returns the method name in the format of returnType methodName(params)
     *
     * @return String representing the method name
     */
    @Override
    public String variantName() {
        JApiMethod method = ((JApiMethod) jApiMethod);

        String[] fullReturnTypeName = method.getReturnType().getNewReturnType().split("\\.");
        String returnTypeClass = fullReturnTypeName[fullReturnTypeName.length - 1].equals("n.a.") ? "void" : fullReturnTypeName[fullReturnTypeName.length - 1];

        String params = method.getParameters().stream().map(jApiParameter -> {
            String[] fullParameterTypeName = jApiParameter.getType().split("\\.");
            return fullParameterTypeName[fullParameterTypeName.length - 1];
        }).collect(joining(","));

        return "%s %s(%s)".formatted(returnTypeClass, method.getName(), params);
    }

    @Override
    public String toString() {
        return "MethodBreakingChange{" +
                "jApiMethod=" + jApiMethod +
                '}';
    }
}
