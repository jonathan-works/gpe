package br.com.infox.ibpm.xpdl.activities;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jdom2.Element;

public class TaskActivityXPDL extends ActivityXPDL implements AssignTaskXPDL {

	public TaskActivityXPDL(Element element, String name) {
		super(element, name);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public TaskNode toNode() {
		if (node == null) {
			node = new TaskNode();
			node.setName(this.getName());
		}
		return (TaskNode) node;
	}

	@Override
	public void assignTask(ProcessDefinition definition) {
		TaskNode temp = toNode();
		Task t = new Task();
		t.setProcessDefinition(definition);
		t.setTaskMgmtDefinition(definition.getTaskMgmtDefinition());
		t.setName(temp.getName());
		if (getLane() != null) {
			t.setSwimlane(getLane().toSwimlane());
		} else {
			t.setSwimlane((Swimlane) definition.getTaskMgmtDefinition().getSwimlanes().values().iterator()
					.next());
		}
		temp.setEndTasks(true);
		temp.addTask(t);
	}

}
