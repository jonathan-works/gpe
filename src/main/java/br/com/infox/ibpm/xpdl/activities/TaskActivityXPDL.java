package br.com.infox.ibpm.xpdl.activities;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jdom.Element;

public class TaskActivityXPDL extends ActivityXPDL implements AssignTaskXPDL {

	private static final long serialVersionUID = 1L;

	public TaskActivityXPDL(Element element, String name) {
		super(element, name);
	}

	@Override
	public Node toNode() {
		if (node == null) {
			node = new TaskNode();
			node.setName(this.getName());
		}
		return node;
	}

	@Override
	public void assignTask(ProcessDefinition definition) {
		if (node != null) {
			TaskNode temp = (TaskNode)node;
			Task t = new Task();
			t.setProcessDefinition(definition);
			t.setTaskMgmtDefinition(definition.getTaskMgmtDefinition());
			t.setName(temp.getName());
			if (getLane() != null) {
				t.setSwimlane(getLane().toSwimlane());
			} else {
				t.setSwimlane(definition.getTaskMgmtDefinition().getSwimlanes().values().iterator()
						.next());
			}
			temp.setEndTasks(true);
			temp.addTask(t);
		}
	}

}
