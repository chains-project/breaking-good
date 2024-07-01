package se.kth.sponvisitors;

import se.kth.breaking_changes.ApiChange;
import se.kth.log_Analyzer.ErrorInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@lombok.Getter
@lombok.Setter
public class BrokenChanges {

    BrokenUse brokenUse;

    Set<ErrorInfo> errorInfo;

    Set<ApiChange> newVariants;


    @Override
    public String toString() {
        return "BrokenChanges{" +
                "brokenUse=" + brokenUse +
                ", errorInfo=" + errorInfo +
                '}';
    }

    public BrokenChanges(BrokenUse brokenUse) {
        this.brokenUse = brokenUse;
        this.errorInfo = new HashSet<>();
        newVariants = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BrokenChanges)) return false;
        BrokenChanges that = (BrokenChanges) o;
        return that.getBrokenUse().usedApiElement().equals(this.getBrokenUse().usedApiElement());

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
