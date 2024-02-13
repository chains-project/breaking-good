package se.kth.breaking_changes;

import java.nio.file.Path;


@lombok.Setter
public class ApiMetadata {
    String name;

    Path file;

    public ApiMetadata(String name, Path file) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name.contains(".jar") ? name.substring(0, name.indexOf(".jar")) : name;
    }

}
