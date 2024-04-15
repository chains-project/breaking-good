package se.kth.spoon_compare;

import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * We create a class to represent a client based in the Maracas Process.
 * This class is used to represent a client.
 * It is used to test the comparison of the Spoon library.
 * It is used to create Spoon model and add Jar files to the model.
 */
@Getter
@Setter
public class Client {

    // Path to the source file

    private final Path sourcePath;

    private List<Path> classpath = Collections.emptyList();

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public  Client(Path sourcePath) {
        this.sourcePath = sourcePath;
    }

    public CtModel createModel() {
        Stopwatch sw = Stopwatch.createStarted();
        Launcher launcher;

        // Attempting to get the proper source folders to analyze
        if (Files.exists(sourcePath.resolve("pom.xml"))) {
            launcher = new MavenLauncher(sourcePath.toString(), MavenLauncher.SOURCE_TYPE.ALL_SOURCE, new String[0]);
        } else {
            launcher = new Launcher();
            launcher.getEnvironment().setComplianceLevel(11);
            launcher.addInputResource(sourcePath.toString());
        }

        //we use same parameter similar to the Maracas project
        // Ignore missing types/classpath related errors
        launcher.getEnvironment().setNoClasspath(true);
        // Ignore duplicate declarations
        launcher.getEnvironment().setIgnoreDuplicateDeclarations(true);
        // Ignore files with any syntax errors or JLS violations
        launcher.getEnvironment().setIgnoreSyntaxErrors(true);

        String[] cp = classpath.stream().map(p -> p.toAbsolutePath().toString()).toList().toArray(new String[0]);
        launcher.getEnvironment().setSourceClasspath(cp);

        CtModel spoonModel = launcher.buildModel();
        System.out.println("Building Spoon model for " + this + " [classpath=" + classpath + "] took " + sw.elapsed().toMillis() + "ms");
        log.info("Building Spoon model for {} [classpath={}] took {}ms", this, classpath, sw.elapsed().toMillis());
        return spoonModel;
    }

    @Override
    public String toString() {
        return "Client{" +
                "sourcePath=" + sourcePath +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return sourcePath.equals(client.sourcePath);
    }

    @Override
    public int hashCode() {
        return sourcePath.hashCode();
    }


}
