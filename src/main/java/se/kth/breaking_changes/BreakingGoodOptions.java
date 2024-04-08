package se.kth.breaking_changes;

import japicmp.cli.JApiCli;
import japicmp.config.Options;
import japicmp.model.AccessModifier;
import japicmp.model.JApiCompatibilityChange;
import japicmp.model.JApiCompatibilityChangeType;
import japicmp.util.Optional;

import java.util.HashSet;
import java.util.Set;

@lombok.Setter
@lombok.Getter
public class BreakingGoodOptions {

    private Options options;

    // based en study from `Maracas` and `JApiCmp`
    private Set<JApiCompatibilityChangeType> exclude = new HashSet<>();

    public Options getDefaultOptions() {
        Options defaultOptions = Options.newDefault();
        defaultOptions.setAccessModifier(AccessModifier.PROTECTED);
        defaultOptions.setOutputOnlyModifications(true);
        defaultOptions.setClassPathMode(JApiCli.ClassPathMode.TWO_SEPARATE_CLASSPATHS);
        defaultOptions.setIgnoreMissingClasses(true);
        String[] excl = {"(*.)?tests(.*)?", "(*.)?test(.*)?",
                "@org.junit.After", "@org.junit.AfterClass",
                "@org.junit.Before", "@org.junit.BeforeClass",
                "@org.junit.Ignore", "@org.junit.Test",
                "@org.junit.runner.RunWith"};

        for (String e : excl) {
            defaultOptions.addExcludeFromArgument(Optional.of(e), false);
        }


        this.exclude.add(JApiCompatibilityChangeType.CLASS_NO_LONGER_PUBLIC);

        // We don't care about source- and binary-compatible changes (except ADA...)
//        this.exclude.add(JApiCompatibilityChangeType.METHOD_ADDED_TO_PUBLIC_CLASS);
//        this.exclude.add(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE);

        // We don't care about super-type changes as they're just redundant
        this.exclude.add(JApiCompatibilityChangeType.METHOD_REMOVED_IN_SUPERCLASS);
        this.exclude.add(JApiCompatibilityChangeType.FIELD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS);
        this.exclude.add(JApiCompatibilityChangeType.FIELD_REMOVED_IN_SUPERCLASS);
//        this.exclude.add(JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_SUPERCLASS);
        this.exclude.add(JApiCompatibilityChangeType.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE);

        return defaultOptions;
    }


    public Options getOptions() {
        return options == null ? getDefaultOptions() : options;
    }

}
