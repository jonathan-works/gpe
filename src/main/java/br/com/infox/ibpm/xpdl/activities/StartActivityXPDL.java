package br.com.infox.ibpm.xpdl.activities;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.StartState;
import org.jbpm.taskmgmt.def.Task;
import org.jdom.Element;

public class StartActivityXPDL extends ActivityXPDL implements AssignTaskXPDL {

	private static final long serialVersionUID = 1L;

	public StartActivityXPDL(Element element, String name) {
		super(element, name);
	}

	@Override
	public Node toNode() {
		if(node == null) {
			node = new StartState();
			node.setName(this.getName());
		}
		return node;
	}
	
	@Override
	public void assignTask(ProcessDefinition definition) {
		Task task = new Task();
		task.setProcessDefinition(definition);
		task.setTaskMgmtDefinition(definition.getTaskMgmtDefinition());
		task.setName(getName());
		if (getLane() != null) {
			task.setSwimlane(getLane().toSwimlane());
		} else {
			task.setSwimlane(definition.getTaskMgmtDefinition().getSwimlanes().values().iterator()
					.next());
		}
		definition.getTaskMgmtDefinition().setStartTask(task);
	}

}
