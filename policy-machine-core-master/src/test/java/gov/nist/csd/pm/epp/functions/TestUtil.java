package gov.nist.csd.pm.epp.functions;

import gov.nist.csd.pm.epp.EPPOptions;
import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.operations.OperationSet;
import gov.nist.csd.pm.pap.GraphAdmin;
import gov.nist.csd.pm.pap.ObligationsAdmin;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.ProhibitionsAdmin;
import gov.nist.csd.pm.pdp.PDP;
import gov.nist.csd.pm.pdp.services.UserContext;
import gov.nist.csd.pm.pip.graph.Graph;
import gov.nist.csd.pm.pip.graph.MemGraph;
import gov.nist.csd.pm.pip.graph.model.nodes.Node;
import gov.nist.csd.pm.pip.graph.model.nodes.NodeType;
import gov.nist.csd.pm.pip.obligations.MemObligations;
import gov.nist.csd.pm.pip.prohibitions.MemProhibitions;

class TestUtil {
    static TestContext getTestCtx() throws PMException {
        PDP pdp = PDP.newPDP(
                new PAP(new GraphAdmin(new MemGraph()), new ProhibitionsAdmin(new MemProhibitions()), new ObligationsAdmin(new MemObligations())),
                new EPPOptions(),
                new OperationSet("read", "write", "execute"));
        Graph graph = pdp.getGraphService(new UserContext("super"));
        Node pc1 = graph.createPolicyClass("pc1", null);
        Node oa1 = graph.createNode("oa1", NodeType.OA, null, pc1.getName());
        Node o1 = graph.createNode("o1", NodeType.O, null, oa1.getName());
        Node ua1 = graph.createNode("ua1", NodeType.UA, null, pc1.getName());
        Node u1 = graph.createNode("u1", NodeType.U, null, ua1.getName());

        graph.associate(ua1.getName(), oa1.getName(), new OperationSet("read", "write"));

        return new TestContext(pdp, u1, ua1, o1, oa1, pc1);
    }

    static class TestContext {
        PDP pdp;
        Node u1;
        Node ua1;
        Node o1;
        Node oa1;
        Node pc1;

        public TestContext(PDP pdp, Node u1, Node ua1, Node o1, Node oa1, Node pc1) {
            this.pdp = pdp;
            this.u1 = u1;
            this.ua1 = ua1;
            this.o1 = o1;
            this.oa1 = oa1;
            this.pc1 = pc1;
        }

        public PDP getPdp() {
            return pdp;
        }

        public void setPdp(PDP pdp) {
            this.pdp = pdp;
        }

        public Node getU1() {
            return u1;
        }

        public void setU1(Node u1) {
            this.u1 = u1;
        }

        public Node getUa1() {
            return ua1;
        }

        public void setUa1(Node ua1) {
            this.ua1 = ua1;
        }

        public Node getO1() {
            return o1;
        }

        public void setO1(Node o1) {
            this.o1 = o1;
        }

        public Node getOa1() {
            return oa1;
        }

        public void setOa1(Node oa1) {
            this.oa1 = oa1;
        }

        public Node getPc1() {
            return pc1;
        }

        public void setPc1(Node pc1) {
            this.pc1 = pc1;
        }
    }
}
