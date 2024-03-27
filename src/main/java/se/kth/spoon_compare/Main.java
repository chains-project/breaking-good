package se.kth.spoon_compare;

import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.log_Analyzer.MavenLogAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {


    public static void main(String[] args) {

        MavenLogAnalyzer mavenLog = new MavenLogAnalyzer(new File("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/0abf7148300f40a1da0538ab060552bca4a2f1d8/biapi/0abf7148300f40a1da0538ab060552bca4a2f1d8.log"));


        try {
            MavenErrorLog log = mavenLog.analyzeCompilationErrors();
            String proyect = "/Users/frank/Documents/Work/PHD/Tools";

            log.getErrorInfo().forEach((k, v) -> {
                System.out.println(k);
                v.forEach(System.out::println);
                SpoonAnalyzer spoonAnalyzer = new SpoonAnalyzer("net.sf.jasperreports", v);
                List<SpoonResults> results = spoonAnalyzer.applySpoon(proyect + k);

                results.forEach(System.out::println);

            });


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
