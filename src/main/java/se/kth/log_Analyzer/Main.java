package se.kth.log_Analyzer;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");


        MavenLogAnalyzer mavenLog = new MavenLogAnalyzer(new File("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/0abf7148300f40a1da0538ab060552bca4a2f1d8.log"));

        try {
            mavenLog.analyzeCompilationErrors();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
