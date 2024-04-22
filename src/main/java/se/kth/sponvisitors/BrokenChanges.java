package se.kth.sponvisitors;

import se.kth.log_Analyzer.ErrorInfo;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@lombok.Getter
@lombok.Setter
public class BrokenChanges {

    BrokenUse brokenUse;

    Set<ErrorInfo> errorInfo;


    @Override
    public String toString() {
        return "BrokenChanges{" +
                "brokenUse=" + brokenUse +
                ", errorInfo=" + errorInfo +
                '}';
    }

    public BrokenChanges(BrokenUse brokenUse) {
        this.brokenUse = brokenUse;
        this.errorInfo = new HashSet<>()    ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BrokenChanges that = (BrokenChanges) o;

        if (!Objects.equals(brokenUse, that.brokenUse)) return false;
        return Objects.equals(errorInfo, that.errorInfo);
    }

    @Override
    public int hashCode() {
        int result = brokenUse != null ? brokenUse.hashCode() : 0;
        result = 31 * result + (errorInfo != null ? errorInfo.hashCode() : 0);
        return result;
    }

    public void addErrorInfo(ErrorInfo errorInfo) {
        this.errorInfo.add(errorInfo);
    }
}
