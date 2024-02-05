package se.kth.log_Analyzer;

import java.util.Objects;

public record MavenErrorLog(
        int clientLinePosition,
        String clientFilePath,
        String errorMessage
) {


    @Override
    public String toString() {
        return """
                Error at line %d in file %s:
                    %s
                """.formatted(
                clientLinePosition,
                clientFilePath,
                errorMessage
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientLinePosition, clientFilePath, errorMessage);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MavenErrorLog other = (MavenErrorLog) obj;
        return clientLinePosition == other.clientLinePosition
                && clientFilePath.equals(other.clientFilePath)
                && errorMessage.equals(other.errorMessage);
    }


}
