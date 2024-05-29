package se.kth.java_version;

public record JavaVersionIncompatibility(
        String wrongVersion,
        String shouldBeVersion,
        String errorMessage
) {

    public JavaVersionIncompatibility {
        if (wrongVersion == null || shouldBeVersion == null) {
            throw new IllegalArgumentException("Version cannot be null");
        }
    }

    public String toString() {
        return "Expected version: " + wrongVersion + ", Actual version: " + shouldBeVersion;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JavaVersionIncompatibility other = (JavaVersionIncompatibility) obj;
        return wrongVersion.equals(other.wrongVersion) && shouldBeVersion.equals(other.shouldBeVersion);
    }

    public int hashCode() {
        return wrongVersion.hashCode() + shouldBeVersion.hashCode();
    }
}
