package br.com.infox.ibpm.xpdl.activities;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.StartState;
import org.jbpm.taskmgmt.def.Task;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.handler.TaskHandler;

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
		if (node != null) {
			Task t = new Task();
			t.setProcessDefinition(definition);
			t.setTaskMgmtDefinition(definition.getTaskMgmtDefinition());
			t.setName(node.getName());
			if (getLane() != null) {
				t.setSwimlane(getLane().toSwimlane());
			} else {
				t.setSwimlane(definition.getTaskMgmtDefinition().getSwimlanes().values().iterator()
						.next());
			}
			TaskHandler startTaskHandler = new TaskHandler(t);
			definition.getTaskMgmtDefinition().setStartTask(startTaskHandler.getTask());
		}
	}

}
