package se.kth.breaking_changes;

import java.util.Objects;

@lombok.Getter
@lombok.Setter
public class ApiChange {

    private String oldElement;

    private String newElement;

    private String category;

    private String description;

    private String name;

    public ApiChange(String oldElement, String newElement, String category, String name) {
        this.oldElement = oldElement;
        this.newElement = newElement;
        this.category = category;
        this.name = name;

    }

    public ApiChange() {
    }

    @Override
    public String toString() {
        return "LibraryChange(oldElement=" + this.getOldElement() + ", newElement=" + this.getNewElement() + ", category=" + this.getCategory() + ", name=" + this.getName() + ")";
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        ApiChange thatLibraryChange = (ApiChange) that;
        return this.getOldElement().equals(thatLibraryChange.getOldElement())
                && this.getNewElement().equals(thatLibraryChange.getNewElement())
                && this.getCategory().equals(thatLibraryChange.getCategory())
                && this.getName().equals(thatLibraryChange.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldElement, newElement, category, name);
    }
}
