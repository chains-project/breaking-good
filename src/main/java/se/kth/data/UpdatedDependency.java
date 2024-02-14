package se.kth.data;

//

/**
 * Represents an updated dependency
 *
 * @param dependencyGroupID
 * @param dependencyArtifactID
 * @param previousVersion
 * @param newVersion
 * @param dependencyScope
 * @param versionUpdateType
 * @param dependencySection
 * @param githubCompareLink
 * @param mavenSourceLinkPre
 * @param mavenSourceLinkBreaking
 * @param updatedFileType
 */
public record UpdatedDependency(
        String dependencyGroupID,
        String dependencyArtifactID,
        String previousVersion,
        String newVersion,
        String dependencyScope,
        String versionUpdateType,
        String dependencySection,
        String githubCompareLink,
        String mavenSourceLinkPre,
        String mavenSourceLinkBreaking,
        String updatedFileType
) {
}
