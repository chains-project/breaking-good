package se.kth.core;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Util {


    public boolean isFolder(String path) {
     return Files.isDirectory(Paths.get(path));
    }

}
