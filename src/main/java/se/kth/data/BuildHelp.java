package se.kth.data;

import com.fasterxml.jackson.databind.type.MapType;
import se.kth.core.Changes_V2;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.log_Analyzer.MavenLogAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BuildHelp {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    public static List<BreakingUpdateMetadata> getBreakingCommit(Path benchmarkDir) {
        File[] breakingUpdates = null;

        if (Files.isDirectory(benchmarkDir)) {
            breakingUpdates = benchmarkDir.toFile().listFiles();
        } else {
            breakingUpdates = new File[]{benchmarkDir.toFile()};
        }

        MapType buJsonType = JsonUtils.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
        List<BreakingUpdateMetadata> breakingUpdateList = new ArrayList<>();

        if (breakingUpdates != null) {
            for (File breakingUpdate : breakingUpdates) {
                // read each breaking update json file
                BreakingUpdateMetadata bu = JsonUtils.readFromFile(breakingUpdate.toPath(), BreakingUpdateMetadata.class);
                // convert to BreakingUpdate object
                breakingUpdateList.add(bu);
            }
        } else {
            System.out.println("No breaking update found in " + benchmarkDir);
        }
        return breakingUpdateList;
    }


    public static void metadata() {
        final var path = Path.of("breaking_good_stats.json");
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void changesCount(Changes_V2 changes, BreakingGoodInfo bg) {
        int breakingChanges = 0;
        for (var change : changes.brokenChanges()) {
            breakingChanges += change.getErrorInfo().size();
        }
        bg.setTotalErrorsInExplanation(breakingChanges);
    }

    public static MavenErrorLog mavenLogParser(BreakingUpdateMetadata breakingUpdate, BreakingGoodInfo bg) throws IOException {
        MavenLogAnalyzer mavenLogAnalyzer = new MavenLogAnalyzer(
                new File("projects/%s/%s/%s.log".formatted(breakingUpdate.breakingCommit(), breakingUpdate.project(), breakingUpdate.breakingCommit())));

        MavenErrorLog errorLog = mavenLogAnalyzer.analyzeCompilationErrors();
        AtomicInteger errorsCount = new AtomicInteger();
        errorLog.getErrorInfo().forEach((k, v) -> {
            System.out.println("Path: " + k);
            errorsCount.addAndGet(v.size());
            v.forEach(errorInfo -> {
                System.out.println("Line: " + errorInfo.getClientLinePosition());
                System.out.println("Error: " + errorInfo.getErrorMessage());
            });
        });

        bg.setErrorsFromLog(errorsCount.get());
        return errorLog;
    }





}
