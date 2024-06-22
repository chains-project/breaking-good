package se.kth.log_Analyzer;

import java.util.Objects;

@lombok.Getter
@lombok.Setter
public class ErrorInfo {

    String clientLinePosition;
    String clientFilePath;
    String errorMessage;
    String additionalInfo;
    String clientLineContent;
    String clientGithubLink;
    int errorLinePositionInFile;
    String errorLogGithubLink;

    public ErrorInfo(String clientLinePosition, String clientFilePath, String errorMessage, int errorLinePositionInFile,String additionalInfo) {
        this.clientLinePosition = clientLinePosition;
        this.clientFilePath = clientFilePath;
        this.errorMessage = errorMessage;
        this.clientLineContent = "";
        this.additionalInfo = additionalInfo;
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
