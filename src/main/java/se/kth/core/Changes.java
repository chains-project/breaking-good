package se.kth.core;

import se.kth.breaking_changes.ApiMetadata;

import java.util.HashSet;
import java.util.Set;

public record Changes(ApiMetadata oldApiVersion, ApiMetadata newApiVersion, Set<BreakingChange> changes) {
    public Changes(ApiMetadata oldApiVersion, ApiMetadata newApiVersion) {
        this(newApiVersion, oldApiVersion, new HashSet<>());
    }
}
