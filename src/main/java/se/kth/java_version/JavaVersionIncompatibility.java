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


    public String mapVersions(String classVersion) {
        return switch (classVersion) {
            case "45.0" -> "Java 1.0";
            case "45.3" -> "Java 1.1";
            case "46.0" -> "Java 1.2";
            case "47.0" -> "Java 1.3";
            case "48.0" -> "Java 1.4";
            case "49.0" -> "Java 5";
            case "50.0" -> "Java 6";
            case "51.0" -> "Java 7";
            case "52.0" -> "Java 8";
            case "53.0" -> "Java 9";
            case "54.0" -> "Java 10";
            case "55.0" -> "Java 11";
            case "56.0" -> "Java 12";
            case "57.0" -> "Java 13";
            case "58.0" -> "Java 14";
            case "59.0" -> "Java 15";
            case "60.0" -> "Java 16";
            case "61.0" -> "Java 17";
            case "62.0" -> "Java 18";
            case "63.0" -> "Java 19";
            case "64.0" -> "Java 20";
            case "65.0" -> "Java 21";
            case "66.0" -> "Java 22";
            case "67.0" -> "Java 23";
            default -> "Unknown version";
        };
    }
}


