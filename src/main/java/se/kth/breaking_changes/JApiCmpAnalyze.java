package se.kth.breaking_changes;

import com.google.common.base.Stopwatch;
import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.japianalysis.BreakingChange;
import se.kth.japianalysis.JApiCmpDeltaVisitor;
import se.kth.japianalysis.JApiCmpToSpoonVisitor;
import se.kth.sponvisitors.BreakingChangeVisitor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtPackage;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@lombok.Setter
@lombok.Getter
public class JApiCmpAnalyze {

    private final ApiMetadata oldJar;
    private final ApiMetadata newJar;


    public JApiCmpAnalyze(ApiMetadata oldJar, ApiMetadata newJar) {
        this.oldJar = oldJar;
        this.newJar = newJar;
    }

    public List<BreakingChangeVisitor> getVisitors(List<BreakingChange> breakingChanges) {
        return breakingChanges.stream()
                .map(BreakingChange::getVisitor)
                .filter(Objects::nonNull)
                .toList();
    }


    public Set<ApiChange> useJApiCmp() {
        BreakingGoodOptions options = new BreakingGoodOptions();

        Options defaultOptions = options.getDefaultOptions();
        JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(defaultOptions);

        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
        JApiCmpArchive newF = new JApiCmpArchive(newJar.getFile().toFile(), newJar.getName());
        JApiCmpArchive old = new JApiCmpArchive(oldJar.getFile().toFile(), oldJar.getName());

        List<JApiClass> jApiClasses = jarArchiveComparator.compare(old, newF);

        BreakingGoodFilter filter = new BreakingGoodFilter(options);
        filter.filter(jApiClasses);

        JApiCmpElements jApiCmpElements = new JApiCmpElements();
        JApiCompareScan.visit(jApiClasses, jApiCmpElements);

        return jApiCmpElements.getChanges();
    }

    public List<BreakingChange> useJApiCmp_v2() {
        BreakingGoodOptions options = new BreakingGoodOptions();

        Options defaultOptions = options.getDefaultOptions();
        JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(defaultOptions);
        comparatorOptions.getClassPathEntries().addAll(oldJar.getClasspath());

        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
        JApiCmpArchive newF = new JApiCmpArchive(newJar.getFile().toFile(), newJar.getName());
        JApiCmpArchive old = new JApiCmpArchive(oldJar.getFile().toFile(), oldJar.getName());

        List<JApiClass> jApiClasses = jarArchiveComparator.compare(old, newF);

        BreakingGoodFilter filter = new BreakingGoodFilter(options);
        filter.filter(jApiClasses);

        CtModel model = oldJar.buildModel();
        CtPackage root = model.getRootPackage();

        // Map the BCs from JApi to Spoon elements
        Stopwatch sw = Stopwatch.createStarted();
        JApiCmpToSpoonVisitor visitor = new JApiCmpToSpoonVisitor(root);
        JApiCmpDeltaVisitor.visit(jApiClasses, visitor);

        return visitor.getBreakingChanges();
    }
}
