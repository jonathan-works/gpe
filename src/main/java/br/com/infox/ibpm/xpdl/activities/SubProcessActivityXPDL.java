package br.com.infox.ibpm.xpdl.activities;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.ProcessState;
import org.jdom2.Element;

public class SubProcessActivityXPDL extends ActivityXPDL implements AssignTaskXPDL {

	private static final long serialVersionUID = 1L;

	public SubProcessActivityXPDL(Element element, String name) {
		super(element, name);
	}

	@Override
	public ProcessState toNode() {
		if(node == null) {
			node = new ProcessState();
			node.setName(this.getName());
		}
		return (ProcessState) node;
	}
	
	@Override
	public void assignTask(ProcessDefinition definition) {
		ProcessState temp = toNode();
		ProcessDefinition subProc = ProcessDefinition.createNewProcessDefinition();
		subProc.setDescription(temp.getName());
		subProc.setName(temp.getName());
		temp.setSubProcessDefinition(subProc);
	}

}
