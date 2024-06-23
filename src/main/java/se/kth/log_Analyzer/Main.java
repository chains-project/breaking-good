package se.kth.log_Analyzer;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        MavenLogAnalyzer mavenLog = new MavenLogAnalyzer(new File("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/10d7545c5771b03dd9f6122bd5973a759eb2cd03/lithium/10d7545c5771b03dd9f6122bd5973a759eb2cd03.log"));

        try {
            MavenErrorLog mavenErrorLog = mavenLog.analyzeCompilationErrors();

            mavenErrorLog.getErrorInfo().forEach((k, v) -> {
                v.forEach(errorInfo -> {
                    System.out.println("Line: " + errorInfo.getClientLinePosition());
                    System.out.println("Error: " + errorInfo.getErrorMessage());
                    System.out.println(errorInfo.getAdditionalInfo());
                    System.out.println("***");
                });
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
