package br.com.infox.ibpm.jbpm.xpdl.activities;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.node.Fork;
import org.jdom.Element;

public class ForkActivityXPDL extends ActivityXPDL {

	public ForkActivityXPDL(Element element, String name) {
		super(element, name);
	}

	@Override
	public Node toNode() {
		if (node == null) {
			node = new Fork();
			node.setName(this.getName());
		}
		return node;
	}

}
