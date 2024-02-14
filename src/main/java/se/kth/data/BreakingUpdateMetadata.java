package se.kth.data;

public record BreakingUpdateMetadata(
        String url,
        String project,
        String projectOrganisation,
        String breakingCommit,
        String prAuthor,
        String preCommitAuthor,
        String breakingCommitAuthor,
        UpdatedDependency updatedDependency,
        String preCommitReproductionCommand,
        String breakingUpdateReproductionCommand,
        String javaVersionUsedForReproduction,
        String failureCategory
) {
}
