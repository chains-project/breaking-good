package se.kth.transitive_changes;

import se.kth.log_Analyzer.ErrorInfo;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.spoon_compare.Client;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpoonTransitiveComparison {


    MavenErrorLog mavenErrorLog;
    Client clientProject;
    Client sourceClient;
    CtModel clientModel;
    CtModel sourceModel;
    List<String> packageNames;


    public SpoonTransitiveComparison(MavenErrorLog mavenErrorLog, Client clientProject, Client sourceClient) {
        this.mavenErrorLog = mavenErrorLog;
        this.clientProject = clientProject;
        this.sourceClient = sourceClient;
        clientModel = clientProject.createModel();
        sourceModel = sourceClient.createModel();

    }

    public SpoonTransitiveComparison(MavenErrorLog mavenErrorLog, Client clientProject, Client sourceClient, CtModel clientModel, List<String> packageNames) {
        this.mavenErrorLog = mavenErrorLog;
        this.clientProject = clientProject;
        this.sourceClient = sourceClient;
        this.clientModel = clientModel;
        sourceModel = sourceClient.createModel();
        this.packageNames = packageNames;

    }


    public Set<MatchElements> findTransitiveChanges() {

        Set<MatchElements> match = new HashSet<>();
        mavenErrorLog.getErrorInfo().forEach((k, v) -> {
            List<CtElement> elements = getElementsByClass(k, v);
            System.out.println(elements.size());
            if (!elements.isEmpty()) {
                System.out.println("ELEMENTS: " + elements.size());
                for (CtElement e : elements) {
                    System.out.println("ELEMENT: " + e);
                }
            }
//            match.addAll(findElements(elements));
        });
        return match;
    }

    private static List<Object> getElementFromSourcePosition(CtModel model, Map<Integer, String> startLines, List<String> depGrpId) {
        Set<String> elements = new HashSet<>();
        Set<String> elementStrings = new HashSet<>();
        Map<String, String> elementLines = new HashMap<>();
        Map<String, String> elementPatterns = new HashMap<>();
        CtType<?> clazz = model.getAllTypes().iterator().next();

        for (CtElement e : clazz.getElements(new TypeFilter<>(CtElement.class))) {

            if (!e.isImplicit() && e.getPosition().isValidPosition() && startLines.containsKey(e.getPosition().getLine())) {
                if (e instanceof CtInvocation<?>) {
                    elements.add(String.valueOf(((CtInvocation<?>) e).getExecutable()));
                    String parsedElement = parseProject(((CtInvocation<?>) e).getExecutable(), depGrpId);
                    if (parsedElement != null) {
                        elementStrings.add(parsedElement);
                        elementLines.put(parsedElement, startLines.get(e.getPosition().getLine()));
//                        elementPatterns.put(parsedElement, replacePatterns(startLines.get(e.getPosition().getLine())));
                    }
                }
                if (e instanceof CtConstructorCall<?>) {
                    elements.add(String.valueOf(((CtConstructorCall<?>) e).getExecutable()));
                    String parsedElement = parseProject(((CtConstructorCall<?>) e).getExecutable(), depGrpId);
                    if (parsedElement != null) {
                        elementStrings.add(parsedElement);
                        elementLines.put(parsedElement, startLines.get(e.getPosition().getLine()));
//                        elementPatterns.put(parsedElement, replacePatterns(startLines.get(e.getPosition().getLine())));
                    }
                }
            }
        }
        // This is not good coding. Just adding the outputs to a list this way to quickly get the results,
        // without worrying about creating classes.
        return new ArrayList<>(List.of(elementStrings, elements, elementLines, elementPatterns));
    }

    private static String parseProject(CtElement e, List<String> dependencyGrpID) {
        CtElement parent = e.getParent(new TypeFilter<>(CtClass.class));

        while (parent != null) {

            for (String pack : dependencyGrpID) {
                System.out.println("PACK: " + pack);
                if (String.valueOf(parent).contains(pack)) {
                    int openingParenthesisIndex = String.valueOf(e).indexOf('(');
                    if (openingParenthesisIndex != -1) {
                        return String.valueOf(e).substring(0, openingParenthesisIndex);
                    }
                    return String.valueOf(e);
                }
                parent = parent.getParent(new TypeFilter<>(CtClass.class));
            }

//            if (String.valueOf(parent).contains(dependencyGrpID)) {
//                int openingParenthesisIndex = String.valueOf(e).indexOf('(');
//                if (openingParenthesisIndex != -1) {
//                    return String.valueOf(e).substring(0, openingParenthesisIndex);
//                }
//                return String.valueOf(e);
//            }
//            parent = parent.getParent(new TypeFilter<>(CtClass.class));
        }
        return null;
    }


    private List<CtElement> getElementsByClass(String file, Set<ErrorInfo> errorInfo) {

        List<String> linesInClient = errorInfo.stream().map(ErrorInfo::getClientLinePosition).toList();
        List<CtElement> elements = new ArrayList<>();


        CtType<?> clazz = clientModel.getAllTypes().iterator().next();

        for (CtElement e : clazz.getElements(new TypeFilter<>(CtElement.class))) {
            if (!e.isImplicit() && e.getPosition().isValidPosition() && linesInClient.contains(String.valueOf(e.getPosition().getLine()))) {
                if (e instanceof CtInvocation<?>) {
//                    elements.add(String.valueOf(((CtInvocation<?>) e).getExecutable()));
                    String parsedElement = parseProject(((CtInvocation<?>) e).getExecutable(), packageNames);
                    if (parsedElement != null) {
//                        elementStrings.add(parsedElement);
//                        elementLines.put(parsedElement, startLines.get(e.getPosition().getLine()));
//                        elementPatterns.put(parsedElement, replacePatterns(startLines.get(e.getPosition().getLine())));
                        elements.add(e);
                    }
                }
                if (e instanceof CtConstructorCall<?>) {
//                    elements.add(String.valueOf(((CtConstructorCall<?>) e).getExecutable()));
                    String parsedElement = parseProject(((CtConstructorCall<?>) e).getExecutable(), packageNames);
                    if (parsedElement != null) {
//                        elementStrings.add(parsedElement);
//                        elementLines.put(parsedElement, startLines.get(e.getPosition().getLine()));
//                        elementPatterns.put(parsedElement, replacePatterns(startLines.get(e.getPosition().getLine())));
                        elements.add(e);
                    }
                }
            }
        }

//        CompletableFuture<List<CtElement>> future = CompletableFuture.supplyAsync(() -> {
//            List<String> lines = errorInfo.stream().map(ErrorInfo::getClientLinePosition).collect(Collectors.toList());
//            return clientModel.filterChildren(element ->
//                    !SpoonAnalyzer.shouldBeIgnored(element)
//                            && element.getPosition().isValidPosition()
//                            && element.getPosition().toString().contains(file)
//                            && lines.contains(String.valueOf(element.getPosition().getLine()))
//            ).list();
//        });
//
//        try {
//            return future.get(5, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            return List.of();
        return elements;
    }

    private List<MatchElements> findElements(List<CtElement> elements) {
        CompletableFuture<List<MatchElements>> future = CompletableFuture.supplyAsync(() -> {
            return elements.stream().map(c -> {
                Class<?> a = c.getClass();
                final List<? extends CtElement> elements1 = sourceModel.getElements(new TypeFilter<>(c.getClass()));
                for (CtElement element : elements1) {
                    if (element.toString().equals(c.toString())) {
                        return new MatchElements(c, element);
                    }
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        });

        try {
            // Esperar un máximo de 30 segundos por la respuesta
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            // En caso de cualquier excepción (tiempo excedido, interrupción, etc.), devolver una lista vacía
            return List.of();
        }
    }

}


