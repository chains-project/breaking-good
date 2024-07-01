package se.kth.transitive_changes;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.log_Analyzer.ErrorInfo;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.spoon_compare.Client;
import se.kth.spoon_compare.SpoonResults;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetConstructByName {

    MavenErrorLog mavenErrorLog;
    Client clientProject;
    Client sourceClient;
    static CtModel clientModel;
    CtModel sourceModel;
    String logFilePath;
    ApiMetadata oldApiVersion;

    public GetConstructByName(MavenErrorLog mavenErrorLog, Client clientProject) {
        this.mavenErrorLog = mavenErrorLog;
        this.clientProject = clientProject;
    }


    public static void main(String[] args) throws IOException {
        Map<String, Map<Integer, String>> lines = extractLineNumbersWithPaths("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/8ab7a7214f9ac1d130b416fae7280cfda533a54f/code-coverage-api-plugin/8ab7a7214f9ac1d130b416fae7280cfda533a54f.log");

        Path projectPath = Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/8ab7a7214f9ac1d130b416fae7280cfda533a54f");
        Launcher launcher = new Launcher();
//        launcher.addInputResource("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/source/htmlunit-2.70.0-sources");
////        List<Path> path = List.of(Path.of("/Users/frank/Downloads/tmp/asto-v1.10.0-sources.jar"));
////        String[] cp2 = path.stream().map(p -> p.toAbsolutePath().toString()).toList().toArray(new String[0]);
////        launcher.getEnvironment().setSourceClasspath(cp2);
        launcher.buildModel();


//        getConstructs(launcher, lines, projectPath);


//        GetConstructByName getConstructByName = new GetConstructByName(mavenErrorLog, clientProject);
//        getConstructByName.extract();
    }

    /**
     * Extracts the constructs from the log file
     *
     * @param launcher    the spoon launcher
     * @param projectPath the path to the project
     * @param logFilePath the path to the log file
     */
    public static Set<SpoonResults> getConstructs(CtModel launcher, Path projectPath, String logFilePath) {

        Map<String, Map<Integer, String>> lines = extractLineNumbersWithPaths(logFilePath);


        List<String> packages = new ArrayList<>();

        launcher.getRootPackage().getFactory().CompilationUnit().getMap().forEach((k, v) -> {
//
            String p = v.getPackageDeclaration().toString().replace("package ", "").replace(";", "");
            packages.add(p);
            packages.add(v.getPackageDeclaration().toString());
//
        });

        Set<SpoonResults> spoonedResults = new HashSet<>();

        Set<String> spoonedElements = new HashSet<>();
        Set<String> allCtElements = new HashSet<>();
        Map<String, String> elementLines = new HashMap<>();
        Map<String, String> elementPatterns = new HashMap<>();
        Set<CtElement> returnElements = new HashSet<>();

        for (Map.Entry<String, Map<Integer, String>> entry : lines.entrySet()) {
            List<Object> spoonResult = applySpoon(projectPath + entry.getKey(), entry.getValue(),
                    packages);
            spoonedElements.addAll((Collection<? extends String>) spoonResult.get(0));
            allCtElements.addAll((Collection<? extends String>) spoonResult.get(1));
            elementLines.putAll((Map<? extends String, ? extends String>) spoonResult.get(2));
            elementPatterns.putAll((Map<? extends String, ? extends String>) spoonResult.get(3));
            returnElements.addAll((Collection<? extends CtElement>) spoonResult.get(4));
            spoonedResults.addAll((Collection<? extends SpoonResults>) spoonResult.get(4));
        }

        return spoonedResults;
    }

    private static List<Object> applySpoon(String projectFilePath, Map<Integer, String> lineNumbers, List<String> depGrpID) {
        Launcher spoon = new Launcher();
        spoon.addInputResource(projectFilePath);
        List<Path> path = List.of(Path.of("/Users/frank/Downloads/tmp/asto-v1.10.0-sources.jar"));
//        String[] cp = path.stream().map(p -> p.toAbsolutePath().toString()).toList().toArray(new String[0]);
//        spoon.getEnvironment().setSourceClasspath(cp);
        spoon.buildModel();


        return getElementFromSourcePosition(spoon.getModel(), lineNumbers, depGrpID);
    }


    private static Map<String, Map<Integer, String>> extractLineNumbersWithPaths(String logFilePath) {
        Map<String, Map<Integer, String>> lineNumbersWithPaths = new HashMap<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(logFilePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.ISO_8859_1);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            String currentPath = null;
            Pattern errorPattern = Pattern.compile("\\[ERROR\\] .*:\\[(\\d+),\\d+\\]");
            Pattern pathPattern = Pattern.compile("/[^:/]+(/[^\\[\\]:]+)");

            while ((line = reader.readLine()) != null) {
                Map<Integer, String> lines = new HashMap<>();
                Matcher matcher = errorPattern.matcher(line);
                if (matcher.find()) {
                    Integer lineNumber = Integer.valueOf(matcher.group(1));
                    Matcher pathMatcher = pathPattern.matcher(line);
                    lines.put(lineNumber, line);
                    if (pathMatcher.find()) {
                        currentPath = pathMatcher.group();
                    }
                    if (currentPath != null) {
                        if (lineNumbersWithPaths.containsKey(currentPath))
                            lineNumbersWithPaths.get(currentPath).putAll(lines);
                        else {
                            lineNumbersWithPaths.put(currentPath, lines);
                        }
                    }
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineNumbersWithPaths;
    }

    private static List<Object> getElementFromSourcePosition(CtModel model, Map<Integer, String> startLines, List<String> depGrpId) {
        Set<String> elements = new HashSet<>();
        Set<String> elementStrings = new HashSet<>();
        Map<String, String> elementLines = new HashMap<>();
        Map<String, String> elementPatterns = new HashMap<>();
        CtType<?> clazz = model.getAllTypes().iterator().next();

        Set<SpoonResults> returnElements = new HashSet<>();

        for (CtElement e : clazz.getElements(new TypeFilter<>(CtElement.class))) {

            if (!e.isImplicit() && e.getPosition().isValidPosition() && startLines.containsKey(e.getPosition().getLine())) {
                if (e instanceof CtInvocation<?>) {
                    elements.add(String.valueOf(((CtInvocation<?>) e).getExecutable()));
                    String parsedElement = parseProject(((CtInvocation<?>) e).getExecutable(), depGrpId);
                    if (parsedElement != null) {
                        elementStrings.add(parsedElement);
                        elementLines.put(parsedElement, startLines.get(e.getPosition().getLine()));
                        elementPatterns.put(parsedElement, replacePatterns(startLines.get(e.getPosition().getLine())));
                        CtInvocation invocation = (CtInvocation) e;
                        SpoonResults spoonResults = new SpoonResults();
                        spoonResults.setName(invocation.getExecutable().getSimpleName());
                        spoonResults.setCtElement(invocation);
                        spoonResults.setClientLine(invocation.toString());
                        spoonResults.setElement(invocation.getExecutable().getSignature());
                        ErrorInfo errorInfo = new ErrorInfo(
                                String.valueOf(e.getPosition().getLine()), "", startLines.get(e.getPosition().getLine()), startLines.get(e.getPosition().getLine()));
                        spoonResults.setErrorInfo(errorInfo);
                        returnElements.add(spoonResults);
                    }
                }
                if (e instanceof CtConstructorCall<?>) {
                    elements.add(String.valueOf(((CtConstructorCall<?>) e).getExecutable()));
                    String parsedElement = parseProject(((CtConstructorCall<?>) e).getExecutable(), depGrpId);
                    if (parsedElement != null) {
                        elementStrings.add(parsedElement);
                        elementLines.put(parsedElement, startLines.get(e.getPosition().getLine()));
                        elementPatterns.put(parsedElement, replacePatterns(startLines.get(e.getPosition().getLine())));
                        CtConstructorCall ctConstructorCall = (CtConstructorCall) e;
                        SpoonResults spoonResults = new SpoonResults();
                        spoonResults.setName(ctConstructorCall.getExecutable().getDeclaringType().getSimpleName());
                        spoonResults.setCtElement(ctConstructorCall);
                        spoonResults.setClientLine(ctConstructorCall.toString());
                        spoonResults.setElement(ctConstructorCall.getExecutable().getSignature());
                        ErrorInfo errorInfo = new ErrorInfo(
                                String.valueOf(e.getPosition().getLine()), "", startLines.get(e.getPosition().getLine()), startLines.get(e.getPosition().getLine()));
                        spoonResults.setErrorInfo(errorInfo);
                        returnElements.add(spoonResults);

                    }
                }
            }
        }
        // This is not good coding. Just adding the outputs to a list this way to quickly get the results,
        // without worrying about creating classes.
        return new ArrayList<>(List.of(elementStrings, elements, elementLines, elementPatterns, returnElements));
    }

    private static String parseProject(CtElement e, List<String> dependencyGrpID) {
        CtElement parent = e.getParent(new TypeFilter<>(CtClass.class));
        while (parent != null) {
            for (String pack : dependencyGrpID) {
                if (String.valueOf(parent).contains(pack)) {
                    int openingParenthesisIndex = String.valueOf(e).indexOf('(');
                    if (openingParenthesisIndex != -1) {
                        return String.valueOf(e).substring(0, openingParenthesisIndex);
                    }
                    return String.valueOf(e);
                }

            }
            parent = parent.getParent(new TypeFilter<>(CtClass.class));
        }
        return null;
    }


    public static String replacePatterns(String line) {
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

}
