package br.com.infox.ibpm.task.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;

import br.com.infox.ibpm.process.definition.variable.VariableType;

public class TaskHandlerVisitor {

    private boolean isMapped;
    private List<String> types;
    private List<String> variableList = new ArrayList<String>();
    private List<Task> visitedTasks = new ArrayList<Task>();
    private List<Transition> visitedTransitions = new ArrayList<Transition>();

    public TaskHandlerVisitor(boolean isMapped) {
        this.isMapped = isMapped;
    }

    public TaskHandlerVisitor(boolean isMapped, List<String> types) {
        this.isMapped = isMapped;
        this.types = types;
    }

    public List<String> getVariables() {
        return variableList;
    }

    public void visit(Node n) {
        addVariables(n.getArrivingTransitions());
    }

    public void visit(Task t) {
        visitedTasks.add(t);
        Node n = (Node) t.getParent();
        visit((Node) t.getParent());
        Collection<Transition> transitions = n.getArrivingTransitions();
        addVariables(transitions);
    }

    private void addVariables(Collection<Transition> transitions) {
        if (transitions == null) {
            return;
        }
        for (Transition transition : transitions) {
            if (visitedTransitions.contains(transition)) {
                continue;
            } else {
                visitedTransitions.add(transition);
            }
            Node from = transition.getFrom();
            NodeType type = from.getNodeType();
            if (NodeType.Task.equals(type)) {
                TaskNode tn = (TaskNode) from;
                addTaskNodeVariables(tn);
            }else if (NodeType.StartState.equals(type) && from.getProcessDefinition().getTaskMgmtDefinition().getStartTask() != null) {
            	addTaskVariables(false, from.getProcessDefinition().getTaskMgmtDefinition().getStartTask());
            }
            
            if (!NodeType.StartState.equals(type)) {
                addVariables(from.getArrivingTransitions());
            }
        }
    }

    private void addTaskNodeVariables(TaskNode tn) {
        boolean filtered = types != null && !types.isEmpty();
        for (Task tsk : tn.getTasks()) {
            addTaskVariables(filtered, tsk);
        }
    }

	private void addTaskVariables(boolean filtered, Task tsk) {
		TaskController tc = tsk.getTaskController();
		if (tc != null && tc.getVariableAccesses() != null) {
		    List<VariableAccess> accesses = tc.getVariableAccesses();
		    for (VariableAccess v : accesses) {
		        String mappedName = v.getMappedName();
		        if (v.isWritable() && !mappedName.startsWith(VariableType.PAGE.name() + ":") 
		                && ((isMapped && !mappedName.startsWith(VariableType.NULL.name() + ":")) || !isMapped)) {
		            String name;
		            if (isMapped) {
		                name = mappedName;
		            } else {
		                name = v.getVariableName();
		            }
		            if (name != null && !"".equals(name)
		                    && !variableList.contains(name)) {
		                if (filtered) {
		                    if (types.contains(mappedName.split(":")[0])) {
		                        variableList.add(name);
		                    }
		                } else {
		                    variableList.add(name);
		                }
		            }
		        }
		    }
		}
		if (!visitedTasks.contains(tsk)) {
		    visit(tsk);
		}
	}

}
