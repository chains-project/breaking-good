package se.kth.log_Analyzer.Models;


import java.util.List;

public record TestFailureModel(
        String testName,
        List<String> stackTrace
) {
}
