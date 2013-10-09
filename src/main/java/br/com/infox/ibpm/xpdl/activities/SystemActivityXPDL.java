package br.com.infox.ibpm.xpdl.activities;

import org.jbpm.graph.def.Node;
import org.jdom2.Element;

public class SystemActivityXPDL extends ActivityXPDL {

	private static final long serialVersionUID = 1L;

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
