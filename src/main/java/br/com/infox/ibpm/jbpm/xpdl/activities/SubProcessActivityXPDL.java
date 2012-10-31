package br.com.infox.ibpm.jbpm.xpdl.activities;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.ProcessState;
import org.jdom.Element;

public class SubProcessActivityXPDL extends ActivityXPDL implements AssignTaskXPDL {

	private static final long serialVersionUID = 1L;

	public SubProcessActivityXPDL(Element element, String name) {
		super(element, name);
	}

	@Override
	public Node toNode() {
		if(node == null) {
			node = new ProcessState();
			node.setName(this.getName());
		}
		return node;
	}
	
	@Override
	public void assignTask(ProcessDefinition definition) {
		if (node != null) {
			ProcessState temp = (ProcessState)node;
			ProcessDefinition subProc = ProcessDefinition.createNewProcessDefinition();
			subProc.setDescription(temp.getName());
			subProc.setName(temp.getName());
			temp.setSubProcessDefinition(subProc);
		}
	}

}
