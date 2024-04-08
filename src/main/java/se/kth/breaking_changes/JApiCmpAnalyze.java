package se.kth.breaking_changes;

import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.output.OutputFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

@lombok.Setter
@lombok.Getter
public class JApiCmpAnalyze {
    private static final Logger log = LoggerFactory.getLogger(JApiCmpAnalyze.class);


    private final ApiMetadata oldJar;
    private final ApiMetadata newJar;


    public JApiCmpAnalyze(ApiMetadata oldJar, ApiMetadata newJar) {
        this.oldJar = oldJar;
        this.newJar = newJar;
    }


    public Set<ApiChange> useJApiCmp() {
        BreakingGoodOptions options = new BreakingGoodOptions();

        Options defaultOptions = options.getDefaultOptions();
        JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(defaultOptions);

        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
        JApiCmpArchive newF = new JApiCmpArchive(newJar.getFile().toFile(), newJar.getName());
        JApiCmpArchive old = new JApiCmpArchive(oldJar.getFile().toFile(), oldJar.getName());

        List<JApiClass> jApiClasses = jarArchiveComparator.compare(old, newF);

        for (JApiClass jApiClass : jApiClasses) {

        }

        BreakingGoodFilter filter = new BreakingGoodFilter(options);
        filter.filter(jApiClasses);

        JApiCmpElements jApiCmpElements = new JApiCmpElements();
        JApiCompareScan.visit(jApiClasses, jApiCmpElements);

        jApiCmpElements.getAllChanges().forEach(apiChange -> {
            log.info("{}", apiChange);
        });
        return jApiCmpElements.getChanges();
    }
}
