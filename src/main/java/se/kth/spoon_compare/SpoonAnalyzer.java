package se.kth.spoon_compare;

import se.kth.log_Analyzer.MavenErrorLog;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.*;

@lombok.Getter
@lombok.Setter
public class SpoonAnalyzer {

    private String dependencyGroupID;

    private Set<MavenErrorLog.ErrorInfo> mavenErrorLog;

    public SpoonAnalyzer(String dependencyGroupID, Set<MavenErrorLog.ErrorInfo> mavenErrorLog) {
        this.dependencyGroupID = dependencyGroupID;
        this.mavenErrorLog = mavenErrorLog;
    }

    public List<SpoonResults> applySpoon(String projectFilePath) {
        Launcher spoon = new Launcher();
        spoon.addInputResource(projectFilePath);
        spoon.buildModel();

        return getElementFromSourcePosition(spoon.getModel(), dependencyGroupID);
    }


    public List<SpoonResults> getElementFromSourcePosition(CtModel model, String depGrpId) {

        CtType<?> clazz = model.getAllTypes().iterator().next();
        List<SpoonResults> results = new ArrayList<>();


        for (CtElement e : clazz.getElements(new TypeFilter<>(CtElement.class))) {
            if (!e.isImplicit() && e.getPosition().isValidPosition() && isInvalidLine(e.getPosition().getLine())) {
                SpoonResults spoonResults = new SpoonResults();
                MavenErrorLog.ErrorInfo mavenErrorLog = getMavenErrorLog(e.getPosition().getLine());
////
                if (e instanceof CtInvocation<?>) {
                    String parsedElement = parseProject(((CtInvocation<?>) e).getExecutable(), depGrpId);
                    if (parsedElement != null) {
                        spoonResults.setElement(String.valueOf(((CtInvocation<?>) e).getExecutable()));
                        spoonResults.setName(parsedElement);
                        spoonResults.setLine(mavenErrorLog.getClientLinePosition());
                        spoonResults.setPattern(replacePatterns(mavenErrorLog.getErrorMessage()));
                        spoonResults.setErrorInfo(mavenErrorLog);
                        results.add(spoonResults);

                    }
                }
                if (e instanceof CtConstructorCall<?>) {
                    String parsedElement = parseProject(((CtConstructorCall<?>) e).getExecutable(), depGrpId);
                    if (parsedElement != null) {
                        spoonResults.setElement(String.valueOf(((CtConstructorCall<?>) e).getExecutable()));
                        spoonResults.setName(parsedElement);
                        spoonResults.setLine(mavenErrorLog.getClientLinePosition());
                        spoonResults.setPattern(replacePatterns(mavenErrorLog.getErrorMessage()));
                        spoonResults.setErrorInfo(mavenErrorLog);
                        results.add(spoonResults);

                    }
                }
            }
        }
        return results;
    }

    private MavenErrorLog.ErrorInfo getMavenErrorLog(int line) {
        return mavenErrorLog.stream().filter(mavenErrorLog -> mavenErrorLog.getClientLinePosition().equals(String.valueOf(line))).findFirst().orElse(null);
    }

    private boolean isInvalidLine(int line) {
        return mavenErrorLog.stream().anyMatch(mavenErrorLog -> mavenErrorLog.getClientLinePosition().equals(String.valueOf(line)));
    }


    public String replacePatterns(String line) {
        Map<String, String> patternMap = new HashMap<>();
        patternMap.put("package \\S+ does not exist", "package does not exist");
        patternMap.put("cannot access \\S+", "cannot access");
        patternMap.put("incompatible types: [^\\n]+ cannot be converted to", "incompatible types: cannot be converted to");
        patternMap.put("incompatible types: [^\\n]+ is not a functional interface", "incompatible types: is not a functional interface");
        patternMap.put("method [^\\n]+ cannot be applied to given types", "method cannot be applied to given types");
        patternMap.put("constructor \\S+ in class \\S+ cannot be applied to given types", "constructor in class cannot be applied to given types");
        patternMap.put("[^\\n]+ has private access in", "has private access in");
        patternMap.put("no suitable constructor found for", "no suitable constructor found for");
        patternMap.put("no suitable method found for", "no suitable method found for");
        patternMap.put("is not abstract and does not override abstract method", "is not abstract and does not override abstract method");
        patternMap.put("unreported exception \\S+ must be caught or declared to be thrown", "unreported exception must be caught or declared to be thrown");
        patternMap.put("incompatible types: bad return type in lambda expression", "incompatible types: bad return type in lambda expression");
        patternMap.put("cannot find symbol", "cannot find symbol");
        patternMap.put("method does not override or implement a method from a supertype", "method does not override or implement a method from a supertype");
        patternMap.put("reference to \\S+ is ambiguous", "reference to is ambiguous");
        patternMap.put("static import only from classes and interfaces", "static import only from classes and interfaces");
        patternMap.put("exception \\S+ is never thrown in body of corresponding try statement", "exception is never thrown in body of corresponding try statement");
        patternMap.put("Couldn't retrieve \\S+ annotation", "Couldn't retrieve annotation");

        for (Map.Entry<String, String> entry : patternMap.entrySet()) {
            String patternToRemove = entry.getKey();
            String patternToSub = entry.getValue();
            if (line.matches(".*" + patternToRemove + ".*")) {
                return patternToSub;
            }
        }
        return line;
    }

    private String parseProject(CtElement e, String dependencyGrpID) {
        CtElement parent = e.getParent(new TypeFilter<>(CtClass.class));
        while (parent != null) {
            if (String.valueOf(parent).contains(dependencyGrpID)) {
                int openingParenthesisIndex = String.valueOf(e).indexOf('(');
                if (openingParenthesisIndex != -1) {
                    return String.valueOf(e).substring(0, openingParenthesisIndex);
                }
                return String.valueOf(e);
            }
            parent = parent.getParent(new TypeFilter<>(CtClass.class));
        }
        return null;
    }
}
