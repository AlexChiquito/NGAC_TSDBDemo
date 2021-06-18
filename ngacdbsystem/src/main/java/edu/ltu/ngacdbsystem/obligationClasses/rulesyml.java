package edu.ltu.ngacdbsystem.obligationClasses;

public class rulesyml {
    
    public rulesyml(String label, eventyml eventyml, responseyml responseyml){
        this.label = label;
        this.event = eventyml;
        this.response = responseyml;
    }

    public rulesyml(){}

    private String label;
    private eventyml event;
    private responseyml response;
}
