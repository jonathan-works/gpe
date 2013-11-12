package br.com.infox.epp.fluxo.xpdl.activities;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.node.Fork;
import org.jdom2.Element;

public class ForkActivityXPDL extends ActivityXPDL {

	private static final long serialVersionUID = 1L;

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
