package se.kth.java_version;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.core.ChangesBetweenVersions;


import java.io.IOException;
import java.nio.file.Path;

import static se.kth.java_version.JavaVersion.generateVersionExplanation;


public class Main {

    public static void main(String[] args) throws IOException {

        String projectPath = "/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/11be71ab8713fe987785e9e25e4f3e410e709ab9/camunda-platform-7-mockito";
        String logFile = "/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/bad55510363bde900a60f13ecc744e0c244397d4/micronaut-openapi-codegen/bad55510363bde900a60f13ecc744e0c244397d4.log";
        ChangesBetweenVersions changes = new ChangesBetweenVersions(
                new ApiMetadata("1.0.0", Path.of("path/to/old/Oldapi.jar")),
                new ApiMetadata("1.0.1", Path.of("path/to/new/Newapi.jar")
                ));
        generateVersionExplanation(changes, projectPath, logFile);


    }


}
