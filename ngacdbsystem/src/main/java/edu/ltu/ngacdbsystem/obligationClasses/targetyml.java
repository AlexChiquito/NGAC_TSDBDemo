package edu.ltu.ngacdbsystem.obligationClasses;

import java.util.List;

public class targetyml {
    
    public targetyml(List<policyElementsyml> policyElements){
        this.policyElements = policyElements;
    }

    public targetyml(){}

    private List<policyElementsyml> policyElements;
}
