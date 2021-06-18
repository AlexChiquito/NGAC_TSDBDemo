package gov.nist.csd.pm.epp;

import gov.nist.csd.pm.epp.events.EventContext;
import gov.nist.csd.pm.epp.functions.FunctionExecutor;
import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.operations.OperationSet;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pdp.PDP;
import gov.nist.csd.pm.pdp.services.UserContext;
import gov.nist.csd.pm.pip.graph.Graph;
import gov.nist.csd.pm.pip.graph.dag.searcher.DepthFirstSearcher;
import gov.nist.csd.pm.pip.graph.dag.searcher.Direction;
import gov.nist.csd.pm.pip.graph.dag.visitor.Visitor;
import gov.nist.csd.pm.pip.graph.model.nodes.Node;
import gov.nist.csd.pm.pip.graph.model.nodes.NodeType;
import gov.nist.csd.pm.pip.obligations.model.*;
import gov.nist.csd.pm.pip.obligations.model.actions.*;
import gov.nist.csd.pm.pip.obligations.model.functions.Function;
import gov.nist.csd.pm.pip.prohibitions.model.ContainerCondition;
import gov.nist.csd.pm.pip.prohibitions.model.Prohibition;

import java.util.*;

import static gov.nist.csd.pm.pip.graph.model.nodes.NodeType.*;

public class EPP {

    private PAP pap;
    private PDP pdp;
    private FunctionEvaluator functionEvaluator;

    public EPP(PAP pap, PDP pdp, EPPOptions eppOptions) throws PMException {
        this.pap = pap;
        this.pdp = pdp;
        this.functionEvaluator = new FunctionEvaluator();
        if (eppOptions != null) {
            for (FunctionExecutor executor : eppOptions.getExecutors()) {
                this.functionEvaluator.addFunctionExecutor(executor);
            }
        }
    }
    
    public PDP getPDP() {
        return pdp;
    }

    public List<String> processEventFilters(EventContext eventCtx) throws PMException {
        List<Obligation> obligations = pap.getObligationsAdmin().getAll();
        List<String> filters = new ArrayList<String>();
        for(Obligation obligation : obligations) {
            if (!obligation.isEnabled()) {
                continue;
            }
            //System.out.println("Obligation!");
            UserContext definingUser = new UserContext(obligation.getUser());

            List<Rule> rules = obligation.getRules();
            for(Rule rule : rules) {
                //System.out.println("rule apply?");
                //System.out.println(eventCtx.getUserCtx().getUser()+ " " + rule.getEventPattern().getSubject().getUser());
                if(!eventCtx.matchesPattern(rule.getEventPattern(), pap.getGraphAdmin())) {
                    continue;
                }
                //System.out.println("yes");
                ResponsePattern responsePattern = rule.getResponsePattern();
                if (eventCtx.getUserCtx().getUser().equals(rule.getEventPattern().getSubject().getUser())){
                    filters.addAll(responsePattern.getFilters());
                    //System.out.println(filters);
                }
                responsePattern.apply(pdp, pap, functionEvaluator, definingUser, eventCtx, rule, obligation.getLabel());
            }
        }
        return filters;
    }

    public List<String> processEvent(EventContext eventCtx) throws PMException {
        List<Obligation> obligations = pap.getObligationsAdmin().getAll();
        List<String> filterList = new ArrayList<String>();
        for(Obligation obligation : obligations) {
            if (!obligation.isEnabled()) {
                continue;
            }

            UserContext definingUser = new UserContext(obligation.getUser());

            List<Rule> rules = obligation.getRules();
            for(Rule rule : rules) {
                if(!eventCtx.matchesPattern(rule.getEventPattern(), pap.getGraphAdmin())) {
                    continue;
                }

                ResponsePattern responsePattern = rule.getResponsePattern();
                if (rule.getResponsePattern().getFilters() != null){
                    filterList.addAll(rule.getResponsePattern().getFilters());
                }else{
                    responsePattern.apply(pdp, pap, functionEvaluator, definingUser, eventCtx, rule, obligation.getLabel());
                }
                
            }
        }
        return filterList;
    }
}
