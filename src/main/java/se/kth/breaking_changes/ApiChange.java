package se.kth.breaking_changes;

import japicmp.model.JApiBehavior;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiCompatibilityChange;
import spoon.reflect.reference.CtReference;

import java.util.Objects;
import java.util.Set;

/**
 * Represents a method-level breaking change
 * Result from the comparison of two versions of an API
  */
@lombok.Getter
@lombok.Setter
public class ApiChange {

    private String Element;

    private String category;

    private String description;

    private String name;

    private String newLongName;

    private JApiChangeStatus changeType;

    private JApiCompatibilityChange compatibilityChange;

    private AbstractApiChange reference;

    Set<ApiChange> newVariants;

    String instruction;

    public ApiChange(String category, String name) {
        this.category = category;
        this.name = name;
    }

    public ApiChange(String category, String name, String newLongName, JApiChangeStatus changeType, ApiMetadata newVersion, ApiMetadata oldVersion, JApiBehavior behavior, String instruction) {
        this.category = category;
        this.name = name;
        this.newLongName = newLongName;
        this.changeType = changeType;
        this.instruction = instruction;
    }

    public ApiChange() {
    }

    @Override
    public String toString() {
        return "ApiChange{" +
                "category='" + category.toString() + '\'' +
                ", name='" + name + '\'' +
                ", newLongName='" + newLongName + '\'' +
                ", changeType=" + changeType.toString() +
                ", instruction='" + instruction + '\'' +
                ", compatibilityChange=" + compatibilityChange.getType() +
                '}';
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        ApiChange thatLibraryChange = (ApiChange) that;
        return this.getCompatibilityChange().equals(thatLibraryChange.getCompatibilityChange())
                && this.getName().equals(thatLibraryChange.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, name);
    }
}
