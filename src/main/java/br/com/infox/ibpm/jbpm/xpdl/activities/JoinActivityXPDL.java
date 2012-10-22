package br.com.infox.ibpm.jbpm.xpdl.activities;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.node.Join;
import org.jdom.Element;

public class JoinActivityXPDL extends ActivityXPDL {

	public JoinActivityXPDL(Element element, String name) {
		super(element, name);
	}

	@Override
	public Node toNode() {
		if (node == null) {
			node = new Join();
			node.setName(this.getName());
		}
		return node;
	}

}