package se.kth.data;

import java.util.Objects;

@lombok.Getter
@lombok.Setter
public class BreakingGoodInfo {
    String breakingCommit;
    String failureCategory;
    boolean hasExplanation;
    int errorsFromLog;
    int jApiCmpChanges;
    int totalErrorsInExplanation;



    @Override
    public String toString() {
        return "BreakingGoodInfo{" +
                "breakingCommit='" + breakingCommit + '\'' +
                ", failureCategory='" + failureCategory + '\'' +
                ", hasExplanation=" + hasExplanation +
                ", errorsFromLog=" + errorsFromLog +
                ", jApiCmpChanges=" + jApiCmpChanges +
                ", totalErrorsInExplanation=" + totalErrorsInExplanation +
                '}';
    }

    public BreakingGoodInfo(String breakingCommit, String failureCategory, boolean hasExplanation, int errorsFromLog, int jApiCmpChanges, int totalErrorsInExplanation) {
        this.breakingCommit = breakingCommit;
        this.failureCategory = failureCategory;
        this.hasExplanation = hasExplanation;
        this.errorsFromLog = errorsFromLog;
        this.jApiCmpChanges = jApiCmpChanges;
        this.totalErrorsInExplanation = totalErrorsInExplanation;
    }

    public BreakingGoodInfo() {
    }

    @Override
    public int hashCode() {
            return Objects.hash(breakingCommit, failureCategory, hasExplanation, errorsFromLog, jApiCmpChanges, totalErrorsInExplanation);

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BreakingGoodInfo other = (BreakingGoodInfo) obj;
        return breakingCommit.equals(other.breakingCommit)
                && failureCategory.equals(other.failureCategory)
                && hasExplanation == other.hasExplanation
                && errorsFromLog == other.errorsFromLog
                && jApiCmpChanges == other.jApiCmpChanges
                && totalErrorsInExplanation == other.totalErrorsInExplanation;
    }
}
