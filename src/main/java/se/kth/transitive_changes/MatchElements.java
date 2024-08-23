package se.kth.transitive_changes;

import lombok.Getter;
import lombok.Setter;
import se.kth.log_Analyzer.ErrorInfo;
import spoon.reflect.declaration.CtElement;

import java.util.Objects;

@Getter
@Setter
public class MatchElements {

    private final CtElement clientElement;
    private final CtElement sourceElement;
    static ErrorInfo errorInfo;


    public MatchElements(CtElement clientElement, CtElement sourceElement) {
        this.clientElement = clientElement;
        this.sourceElement = sourceElement;
    }

    @Override
    public String toString() {
        return "MatchElements{" +
                "clientElement=" + clientElement +
                ", sourceElement=" + sourceElement +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchElements that = (MatchElements) o;
        return clientElement.equals(that.clientElement) && sourceElement.equals(that.sourceElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientElement, sourceElement);
    }


}
