package br.com.infox.ibpm.jbpm.xpdl.activities;

import org.jbpm.graph.def.Node;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.xpdl.element.ParallelNodeXPDL;

public class ParallelActivityXPDL extends ActivityXPDL {

	private static final long serialVersionUID = 1L;

	public ParallelActivityXPDL(Element element, String name) {
		super(element, name);
	}

	@Override
	public Node toNode() {
		if(node == null) {
			node = new ParallelNodeXPDL();
			node.setName(this.getName());
		}
		return node;
	}

}
