package Script;

public record TreeComparison(String oldVersion, String newVersion, int majorChanges, int minorChanges, int patchChanges,
                             int versionOnlyChanges, int scopeOnlyChanges) {
}
