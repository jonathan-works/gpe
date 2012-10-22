package br.com.infox.ibpm.jbpm.xpdl.activities;

import org.jbpm.graph.def.Node;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.node.MailNode;

public class MailActivityXPDL extends ActivityXPDL {

	public MailActivityXPDL(Element element, String name) {
		super(element, name);
	}

	@Override
	public Node toNode() {
		if(node == null) {
			node = new MailNode();
			node.setName(this.getName());
		}
		return node;
	}

}
