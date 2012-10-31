package br.com.infox.ibpm.jbpm.xpdl.activities;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.node.EndState;
import org.jdom.Element;

public class EndActivityXPDL extends ActivityXPDL {

	private static final long serialVersionUID = 1L;

	public EndActivityXPDL(Element element, String name) {
		super(element, name);
	}

	@Override
	public Node toNode() {
		if(node == null) {
			node = new EndState();
			node.setName(this.getName());
		}
		return node;
	}

}
