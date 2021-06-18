package edu.ltu.ngacdbsystem.obligationClasses;

import java.util.List;

public class eventyml {
    
    public eventyml(subjectyml subjectyml, List<String> operationsyml, targetyml targetyml){
        this.subject = subjectyml;
        this.operations = operationsyml;
        this.target = targetyml;
    }

    public eventyml(){}

    private subjectyml subject;
    private List<String> operations;
    private targetyml target;
}
