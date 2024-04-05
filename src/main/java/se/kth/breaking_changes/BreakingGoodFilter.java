package se.kth.breaking_changes;

import japicmp.output.OutputFilter;

public class BreakingGoodFilter extends OutputFilter {
    public BreakingGoodFilter(BreakingGoodOptions options) {
        super(BreakingGoodOptions.getDefaultOptions());
    }
}
