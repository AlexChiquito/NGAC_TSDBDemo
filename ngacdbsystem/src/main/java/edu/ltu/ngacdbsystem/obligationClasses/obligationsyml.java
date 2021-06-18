package edu.ltu.ngacdbsystem.obligationClasses;

import java.util.List;

public class obligationsyml {
    
    public obligationsyml(String label, List<rulesyml> rules){
        this.label = label;
        this.rules = rules;
    }

    public obligationsyml(){}

    private String label;
    private List<rulesyml> rules;


}
