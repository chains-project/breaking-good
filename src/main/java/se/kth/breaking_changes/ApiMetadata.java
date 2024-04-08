package se.kth.breaking_changes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;

/**
 * Represents the metadata of an API
 * Contains the name of the API and the path to the file
 */
@lombok.Setter
@lombok.Getter
public class ApiMetadata {

    private static final Logger log = LoggerFactory.getLogger(ApiMetadata.class);

    String name;

    Path file;

    private static final Map<String, String> repositoryUrls = Map.of(
            "mavenCentral", "https://repo1.maven.org/maven2/",
            "jenkins", "https://repo.jenkins-ci.org/artifactory/releases/"
    );

    public ApiMetadata(String name, Path file) {
        this.name = name;
        this.file = file;

    }

    public ApiMetadata(Path file) {
        this.file = file;
        this.name = file.getFileName().toString();
    }

    public String getName() {
        return name.contains(".jar") ? name.substring(0, name.indexOf(".jar")) : name;
    }
}

