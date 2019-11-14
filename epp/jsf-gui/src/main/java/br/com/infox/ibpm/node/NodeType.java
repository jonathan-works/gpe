package br.com.infox.ibpm.node;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.SuperState;
import org.jbpm.graph.node.ProcessState;

/**
 * Discriminadores da coluna class_ da tabela jbpm_node, de acordo com as informações do arquivo Node.hbm.xml 
 * em org/jbpm/graph/def no JAR jbpm-jpdl
 * @author gabriel
 *
 */
public enum NodeType {
    C("Subprocesso"), D("Decisão"), E("Término"), F("Separação"), J("Junção"), K("Tarefa"), M("Email"), N("Sistema"),
    R("Início"), S("State"), U("Super State");
    
    private String label;
    
    private NodeType(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    public static NodeType getNodeType(Node node) {
    	if (node.getNodeType().equals(org.jbpm.graph.def.Node.NodeType.Node)) {
    		return N;
    	} else if (node.getNodeType().equals(org.jbpm.graph.def.Node.NodeType.Decision)) {
    		return D;
    	} else if (node.getNodeType().equals(org.jbpm.graph.def.Node.NodeType.EndState)) {
    		return E;
    	} else if (node.getNodeType().equals(org.jbpm.graph.def.Node.NodeType.Fork)) {
    		return F;
    	} else if (node.getNodeType().equals(org.jbpm.graph.def.Node.NodeType.Join)) {
    		return J;
    	} else if (node.getNodeType().equals(org.jbpm.graph.def.Node.NodeType.StartState)) {
    		return R;
    	} else if (node.getNodeType().equals(org.jbpm.graph.def.Node.NodeType.State)) {
    		return S;
    	} else if (node.getNodeType().equals(org.jbpm.graph.def.Node.NodeType.Task)) {
    		return K;
    	} else if (node instanceof InfoxMailNode) {
    		return M;
    	} else if (node instanceof ProcessState) {
    		return C;
    	} else if (node instanceof SuperState) {
    		return U;
    	}
    	
    	throw new IllegalArgumentException("Nó desconhecido: " + node);
    }
}
