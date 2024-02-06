package se.kth.core;

import se.kth.breaking_changes.ApiChange;

import java.util.Set;

@lombok.Getter
@lombok.Setter
public class CombineResults {

    private Set<ApiChange> apiChanges;

    public CombineResults(Set<ApiChange> apiChanges) {
        this.apiChanges = apiChanges;
    }




    public static void main(String[] args) {
        System.out.println("Hello world!");

    }







}
