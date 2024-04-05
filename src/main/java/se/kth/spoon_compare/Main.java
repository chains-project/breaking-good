package se.kth.spoon_compare;

import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.log_Analyzer.MavenLogAnalyzer;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtFieldImpl;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {


    public static void main(String[] args) {

        MavenLogAnalyzer mavenLog = new MavenLogAnalyzer(new File("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/5cf5a482bd430d81257b4ecd85b3d4f7da911621/jakartaee-mvc-sample/5cf5a482bd430d81257b4ecd85b3d4f7da911621.log"));


        try {
            MavenErrorLog log = mavenLog.analyzeCompilationErrors();
            String project = "/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/5cf5a482bd430d81257b4ecd85b3d4f7da911621";

            log.getErrorInfo().forEach((k, v) -> {
                System.out.println(k);
//                v.forEach(System.out::println);
                SpoonAnalyzer spoonAnalyzer = new SpoonAnalyzer("net.sf.jasperreports", v);
                List<SpoonResults> results = spoonAnalyzer.applySpoon(project + k);

                System.out.println("Amount of instructions " + results.size());
                results.forEach(r->{
                    System.out.println("********");
                    System.out.println(((CtFieldImpl<?>)r.getCtElement()).getSimpleName());
                });

            });


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
