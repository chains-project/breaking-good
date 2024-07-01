package se.kth.core;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.sponvisitors.BrokenChanges;

import java.util.HashSet;
import java.util.Set;

public record ChangesBetweenVersions(ApiMetadata oldApiVersion, ApiMetadata newApiVersion, Set<BrokenChanges> brokenChanges) {
    public ChangesBetweenVersions(ApiMetadata oldApiVersion, ApiMetadata newApiVersion) {
        this(newApiVersion, oldApiVersion, new HashSet<>());
    }
}
