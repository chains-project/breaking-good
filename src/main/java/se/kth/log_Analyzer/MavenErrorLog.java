package se.kth.log_Analyzer;

import java.util.*;

@lombok.Getter
@lombok.Setter
public class MavenErrorLog {

    Map<String, Set<ErrorInfo>> errorInfo;


    public MavenErrorLog() {
        this.errorInfo = new HashMap<>();
    }

    public void addErrorInfo(String currentPath, ErrorInfo errorInfo) {
        if (this.errorInfo.containsKey(currentPath)) {
            for (ErrorInfo error : this.errorInfo.get(currentPath)) {
                if (error.clientLinePosition.equals(errorInfo.clientLinePosition)
                        && error.clientFilePath.equals(errorInfo.clientFilePath)
                        && error.errorMessage.equals(errorInfo.errorMessage)) {
                    return;
                }
            }
            this.errorInfo.get(currentPath).add(errorInfo);
        } else {
            Set<ErrorInfo> errors = this.errorInfo.computeIfAbsent(currentPath, k -> new HashSet<>());
            errors.add(errorInfo);
        }
    }

    @lombok.Getter
    @lombok.Setter
    public static class ErrorInfo {

        String clientLinePosition;
        String clientFilePath;
        String errorMessage;
        String clientLineContent;
        String clientGithubLink;
        int errorLinePositionInFile;
        String errorLogGithubLink;

        public ErrorInfo(String clientLinePosition, String clientFilePath, String errorMessage, int errorLinePositionInFile) {
            this.clientLinePosition = clientLinePosition;
            this.clientFilePath = clientFilePath;
            this.errorMessage = errorMessage;
            this.clientLineContent = "";
            this.errorLinePositionInFile = errorLinePositionInFile;

        }

        public ErrorInfo(String clientLinePosition, String clientFilePath, String errorMessage, String clientLineContent) {
            this.clientLinePosition = clientLinePosition;
            this.clientFilePath = clientFilePath;
            this.errorMessage = errorMessage;
            this.clientLineContent = clientLineContent;
        }

        public ErrorInfo(String clientLinePosition, String clientFilePath, String errorMessage, String clientLineContent, String clientGithubLink, int errorLinePositionInFile) {
            this.clientLinePosition = clientLinePosition;
            this.clientFilePath = clientFilePath;
            this.errorMessage = errorMessage;
            this.clientLineContent = clientLineContent;
            this.clientGithubLink = clientGithubLink;
            this.errorLinePositionInFile = errorLinePositionInFile;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ErrorInfo errorInfo = (ErrorInfo) o;

            if (!Objects.equals(clientLinePosition, errorInfo.clientLinePosition))
                return false;
            if (!Objects.equals(clientFilePath, errorInfo.clientFilePath))
                return false;
            return Objects.equals(errorMessage, errorInfo.errorMessage);
        }
    }
}