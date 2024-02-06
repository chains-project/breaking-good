package se.kth.spoon_compare;



@lombok.Getter
@lombok.Setter
public class SpoonResults {

    String name;
    String element;
    String line;
    String pattern;

    public SpoonResults(String name, String element, String line, String pattern) {
        this.name = name;
        this.element = element;
        this.line = line;
        this.pattern = pattern;
    }

    public SpoonResults() {
    }

    public String toString() {
        return "SpoonResults :   name=" + this.getName() + ", element=" + this.getElement() + ", line=" + this.getLine() + ", pattern=" + this.getPattern();
    }
}