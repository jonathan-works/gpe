package br.com.infox.ibpm.node.converter;

import java.util.List;

import org.jbpm.graph.def.Node;

import br.com.infox.jboss.util.ComponentUtil;

public final class NodeConverter {

    private NodeConverter() {
        super();
    }

    public static Node getAsObject(String nodeString) {
        List<Node> nodes = ComponentUtil.getComponent("processNodes");
        for (Node node : nodes) {
            if (node.toString().equals(nodeString)) {
                return node;
            }
        }
        return null;
    }

}
