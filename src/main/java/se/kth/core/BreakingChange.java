package se.kth.core;

import se.kth.breaking_changes.ApiChange;
import se.kth.spoon_compare.SpoonResults;

@lombok.Setter
@lombok.Getter
public class BreakingChange {

    public BreakingChange(ApiChange apiChanges, SpoonResults errorInfo) {
        this.apiChanges = apiChanges;
        this.errorInfo = errorInfo;
    }

    ApiChange apiChanges;
    SpoonResults errorInfo;

    public BreakingChange() {
    }

    public String toString() {
        return "BreakingChange{" + "apiChanges=" + apiChanges.toString() + ", errorInfo=" + errorInfo.toString() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BreakingChange that = (BreakingChange) o;
        return !apiChanges.getName().equals(that.apiChanges.getName());
    }

    @Override
    public int hashCode() {
        return apiChanges.getName().hashCode();
    }
}
