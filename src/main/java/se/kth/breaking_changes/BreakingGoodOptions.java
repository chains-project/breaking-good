package se.kth.breaking_changes;

import japicmp.cli.JApiCli;
import japicmp.config.Options;
import japicmp.model.AccessModifier;
import japicmp.util.Optional;

public class BreakingGoodOptions {

    Options options;

    public static Options getDefaultOptions() {
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
        return defaultOptions;
    }


    public Options getOptions() {
        return options == null ? getDefaultOptions() : options;
    }
}
