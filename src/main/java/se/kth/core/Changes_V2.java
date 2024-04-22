package se.kth.core;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.sponvisitors.BrokenChanges;
import se.kth.sponvisitors.BrokenUse;

import java.util.HashSet;
import java.util.Set;

public record Changes_V2(ApiMetadata oldApiVersion, ApiMetadata newApiVersion, Set<BrokenChanges> uses) {
    public Changes_V2(ApiMetadata oldApiVersion, ApiMetadata newApiVersion) {
        this(newApiVersion, oldApiVersion, new HashSet<>());
    }
}
