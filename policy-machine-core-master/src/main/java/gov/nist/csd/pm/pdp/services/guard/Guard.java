package gov.nist.csd.pm.pdp.services.guard;

import gov.nist.csd.pm.exceptions.PMAuthorizationException;
import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.operations.OperationSet;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pdp.decider.Decider;
import gov.nist.csd.pm.pdp.decider.PReviewDecider;
import gov.nist.csd.pm.pdp.services.UserContext;
import gov.nist.csd.pm.pip.graph.model.nodes.Node;

import java.util.Arrays;
import java.util.Set;

import static gov.nist.csd.pm.operations.Operations.RESET;
import static gov.nist.csd.pm.pip.graph.model.nodes.NodeType.PC;
import static gov.nist.csd.pm.pip.graph.model.nodes.Properties.REP_PROPERTY;

public class Guard {

    protected PAP pap;
    protected Decider decider;
    private OperationSet resourceOps;

    public Guard(PAP pap, OperationSet resourceOps) {
        this.pap = pap;
        this.resourceOps = resourceOps;
        this.decider = new PReviewDecider(pap.getGraphAdmin(), pap.getProhibitionsAdmin(), resourceOps);
    }

    private void assertUserCtx(UserContext userCtx) throws PMException {
        if(userCtx == null) {
            throw new PMException("no user context provided to the PDP");
        }
    }

    boolean hasPermissions(UserContext userCtx, String target, String... permissions) throws PMException {
        // assert that the user context is not null
        assertUserCtx(userCtx);

        // if checking the permissions on a PC, check the permissions on the rep node for the PC
        Node node = pap.getGraphAdmin().getNode(target);
        if (node.getType().equals(PC)) {
            if (!node.getProperties().containsKey(REP_PROPERTY)) {
                throw new PMException("unable to check permissions for policy class " + node.getName() + ", rep property not set");
            }

            target = node.getProperties().get(REP_PROPERTY);
        }

        // check for permissions
        Set<String> allowed = decider.list(userCtx.getUser(), userCtx.getProcess(), target);
        if(permissions.length == 0) {
            return !allowed.isEmpty();
        } else {
            return  allowed.containsAll(Arrays.asList(permissions));
        }
    }

    public void checkReset(UserContext userCtx) throws PMException {
        // check that the user can reset the graph
        if (!hasPermissions(userCtx, pap.getGraphAdmin().getSuperPolicy().getSuperPolicyClassRep().getName(), RESET)) {
            throw new PMAuthorizationException("unauthorized permissions to reset the prohibitions");
        }
    }

}
