package se.kth.core;

import se.kth.breaking_changes.ApiChange;
import se.kth.spoon_compare.SpoonResults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@lombok.Setter
@lombok.Getter
public class BreakingChange {

    ApiChange apiChanges;
    List<SpoonResults> errorInfo;

    public BreakingChange(ApiChange apiChanges) {
        this.apiChanges = apiChanges;
        this.errorInfo = new ArrayList<>();
    }

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
