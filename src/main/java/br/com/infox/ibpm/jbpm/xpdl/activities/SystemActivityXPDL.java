package br.com.infox.ibpm.jbpm.xpdl.activities;

import org.jbpm.graph.def.Node;
import org.jdom.Element;

public class SystemActivityXPDL extends ActivityXPDL {

	public SystemActivityXPDL(Element element, String name) {
		super(element, name);
	}

	@Override
	public Node toNode() {
		if(node == null) {
			node = new Node();
			node.setName(this.getName());
		}
		return node;
	}

}
