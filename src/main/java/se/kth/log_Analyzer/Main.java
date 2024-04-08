package se.kth.log_Analyzer;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");


        MavenLogAnalyzer mavenLog = new MavenLogAnalyzer(new File("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/0abf7148300f40a1da0538ab060552bca4a2f1d8/biapi/0abf7148300f40a1da0538ab060552bca4a2f1d8.log"));

        try {
           MavenErrorLog mavenErrorLog =  mavenLog.analyzeCompilationErrors();

            mavenErrorLog.getErrorInfo().forEach((k, v) -> {
                System.out.println("Path: " + k);
                v.forEach(errorInfo -> {
                    System.out.println("Line: " + errorInfo.getClientLinePosition());
                    System.out.println("Error: " + errorInfo.getErrorMessage());
                });
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
