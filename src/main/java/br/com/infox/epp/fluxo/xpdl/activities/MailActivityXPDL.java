package br.com.infox.epp.fluxo.xpdl.activities;

import org.jbpm.graph.def.Node;
import org.jdom2.Element;

import br.com.infox.ibpm.node.InfoxMailNode;

public class MailActivityXPDL extends ActivityXPDL {

	private static final long serialVersionUID = 1L;

	public MailActivityXPDL(Element element, String name) {
		super(element, name);
	}

	@Override
	public Node toNode() {
		if(node == null) {
			node = new InfoxMailNode();
			node.setName(this.getName());
		}
		return node;
	}
}
