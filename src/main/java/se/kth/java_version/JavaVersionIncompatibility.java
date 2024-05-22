package se.kth.java_version;

public record JavaVersionIncompatibility (
        String expectedVersion,
        String actualVersion
) {

    public JavaVersionIncompatibility {
        if (expectedVersion == null || actualVersion == null) {
            throw new IllegalArgumentException("Version cannot be null");
        }
    }

    public String toString() {
        return "Expected version: " + expectedVersion + ", Actual version: " + actualVersion;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JavaVersionIncompatibility other = (JavaVersionIncompatibility) obj;
        return expectedVersion.equals(other.expectedVersion) && actualVersion.equals(other.actualVersion);
    }

    public int hashCode() {
        return expectedVersion.hashCode() + actualVersion.hashCode();
    }

    public static void main(String[] args) {
        JavaVersionIncompatibility incompatibility = new JavaVersionIncompatibility("1.8", "1.7");
        System.out.println(incompatibility);
    }
}
