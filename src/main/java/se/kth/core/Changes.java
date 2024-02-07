package se.kth.core;

import java.util.HashSet;
import java.util.Set;

public record Changes(String newApiVersion, String oldApiVersion, Set<BreakingChange> changes) {
    public Changes(String newApiVersion, String oldApiVersion) {
        this(newApiVersion, oldApiVersion, new HashSet<>());
    }
}
