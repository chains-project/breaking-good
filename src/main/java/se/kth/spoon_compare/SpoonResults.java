package se.kth.spoon_compare;


import se.kth.log_Analyzer.ErrorInfo;
import spoon.reflect.declaration.CtElement;

import java.util.Objects;

@lombok.Getter
@lombok.Setter
public class SpoonResults {

    String name;
    String element;
    String clientLine;
    String pattern;
    ErrorInfo errorInfo;
    CtElement ctElement;


    public SpoonResults(String name, String element, String clientLine, String pattern, ErrorInfo errorInfo, CtElement ctElement) {
        this.name = name;
        this.element = element;
        this.clientLine = clientLine;
        this.pattern = pattern;
        this.errorInfo = errorInfo;
        this.ctElement = ctElement;
    }

    public SpoonResults() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpoonResults that = (SpoonResults) o;

        if (!Objects.equals(name, that.name)) return false;
        if (element != null ? !element.equals(that.element) : that.element != null) return false;
        if (clientLine != null ? !clientLine.equals(that.clientLine) : that.clientLine != null) return false;
        if (pattern != null ? !pattern.equals(that.pattern) : that.pattern != null) return false;
        return errorInfo != null ? errorInfo.equals(that.errorInfo) : that.errorInfo == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (element != null ? element.hashCode() : 0);
        result = 31 * result + (clientLine != null ? clientLine.hashCode() : 0);
        result = 31 * result + (pattern != null ? pattern.hashCode() : 0);
        result = 31 * result + (errorInfo != null ? errorInfo.hashCode() : 0);
        return result;
    }
}