package se.kth.spoon_compare;

import se.kth.breaking_changes.ApiChange;
import se.kth.log_Analyzer.ErrorInfo;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.declaration.CtFieldImpl;

import java.util.*;

@lombok.Getter
@lombok.Setter
public class SpoonAnalyzer {

    private String dependencyGroupID;
    private Set<ErrorInfo> mavenErrorLog;
    private Set<ApiChange> apiChanges;
    private List<Integer> errorLines = new ArrayList<>();
    private CtModel model;


    public SpoonAnalyzer(Set<ErrorInfo> mavenErrorLog, Set<ApiChange> apiChanges, CtModel model) {
        this.mavenErrorLog = mavenErrorLog;
        this.apiChanges = apiChanges;
        this.model = model;

        errorLines.addAll(mavenErrorLog.stream().map(m -> Integer.parseInt(m.getClientLinePosition())).toList());

    }

    private static boolean shouldBeIgnored(CtElement element) {
        return element instanceof CtComment || element.isImplicit();
    }

    public List<SpoonResults> applySpoon(String fileInClient) {
        // filter elements for breaking positions
        List<CtElement> elements = model.filterChildren(element ->
                !shouldBeIgnored(element)
                        && element.getPosition().isValidPosition()
                        && errorLines.contains(element.getPosition().getLine())
                        && element.getPosition().toString().contains(fileInClient)
        ).list();



        BreakingGoodScanner scanner = new BreakingGoodScanner(apiChanges, mavenErrorLog);
        scanner.scan(elements);
        return scanner.getResults();

    }


    public List<SpoonResults> getElementFromSourcePosition(CtModel model, String depGrpId) {

        CtType<?> clazz = model.getAllTypes().iterator().next();
        List<SpoonResults> results = new ArrayList<>();

        clazz.getElements(new TypeFilter<>(CtElement.class));
        for (CtElement e : clazz.getElements(new TypeFilter<>(CtElement.class))) {

            if (!e.isImplicit() && e.getPosition().isValidPosition() && isInvalidLine(e.getPosition().getLine())) {
                SpoonResults spoonResults = new SpoonResults();
                ErrorInfo mavenErrorLog = getMavenErrorLog(e.getPosition().getLine());

                if (e instanceof CtInvocation<?>) {

//                    String parsedElement = parseProject(((CtInvocation<?>) e).getExecutable(), depGrpId);
//                    if (parsedElement != null) {
                    spoonResults.setElement(String.valueOf(((CtInvocation<?>) e).getExecutable()));
                    spoonResults.setName(((CtInvocation<?>) e).getExecutable().getSimpleName());
                    spoonResults.setClientLine(e.toString());
                    spoonResults.setPattern(replacePatterns(mavenErrorLog.getErrorMessage()));
                    spoonResults.setErrorInfo(mavenErrorLog);
                    spoonResults.setCtElement(e);
                    results.add(spoonResults);

//                    }
                }
                if (e instanceof CtConstructorCall<?>) {
//                    String parsedElement = parseProject(((CtConstructorCall<?>) e).getExecutable(), depGrpId);
//                    if (parsedElement != null) {
                    spoonResults.setElement(String.valueOf(((CtConstructorCall<?>) e).getExecutable()));
                    spoonResults.setName(String.valueOf(((CtConstructorCall<?>) e).getExecutable()));
                    spoonResults.setClientLine(e.toString());
                    spoonResults.setPattern(replacePatterns(mavenErrorLog.getErrorMessage()));
                    spoonResults.setErrorInfo(mavenErrorLog);
                    spoonResults.setCtElement(e);
                    results.add(spoonResults);
                    System.out.println(((CtConstructorCall<?>) e).getExecutable());


//                    }
                }
                if (e instanceof CtClass<?>) {
                    spoonResults.setElement(String.valueOf(e));
                    spoonResults.setName(String.valueOf(e));
                    spoonResults.setClientLine(e.toString());
                    spoonResults.setPattern(replacePatterns(mavenErrorLog.getErrorMessage()));
                    spoonResults.setErrorInfo(mavenErrorLog);
                    spoonResults.setCtElement(e);
                    results.add(spoonResults);
                    System.out.println(((CtClass<?>) e).getReference());
                }

                if (e instanceof CtFieldImpl<?>) {
                    spoonResults.setElement(String.valueOf(e));
                    spoonResults.setName(String.valueOf(e));
                    spoonResults.setClientLine(e.toString());
                    spoonResults.setPattern(replacePatterns(mavenErrorLog.getErrorMessage()));
                    spoonResults.setErrorInfo(mavenErrorLog);
                    spoonResults.setCtElement(e);
                    results.add(spoonResults);
                    System.out.println(((CtFieldImpl<?>) e).getType());
                    System.out.println(Arrays.toString(((CtFieldImpl<?>) e).getReferencedTypes().toArray()));
                }
                if (e instanceof CtMethod<?>) {
                    System.out.println(((CtMethod<?>) e).getSignature());

                }

                if (e instanceof CtType<?>) {
                    System.out.println(((CtType<?>) e).getReference());
                }
            }
        }
        return results;
    }

    private ErrorInfo getMavenErrorLog(int line) {
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
