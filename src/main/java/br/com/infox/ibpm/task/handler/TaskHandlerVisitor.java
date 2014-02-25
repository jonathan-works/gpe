package br.com.infox.ibpm.task.handler;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;

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

    @SuppressWarnings(UNCHECKED)
    public void visit(Node n) {
        addVariables(n.getArrivingTransitions());
    }

    @SuppressWarnings(UNCHECKED)
    public void visit(Task t) {
        visitedTasks.add(t);
        Node n = (Node) t.getParent();
        visit((Node) t.getParent());
        Set<Transition> transitions = n.getArrivingTransitions();
        addVariables(transitions);
    }

    @SuppressWarnings(UNCHECKED)
    private void addVariables(Set<Transition> transitions) {
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
            }
            // TODO: Esse equals funciona?
            if (!type.equals(NodeType.StartState)) {
                addVariables(from.getArrivingTransitions());
            }
        }
    }

    @SuppressWarnings(UNCHECKED)
    private void addTaskNodeVariables(TaskNode tn) {
        boolean filtered = types != null && !types.isEmpty();
        for (Object o : tn.getTasks()) {
            Task tsk = (Task) o;
            TaskController tc = tsk.getTaskController();
            if (tc != null) {
                List<VariableAccess> accesses = tc.getVariableAccesses();
                for (VariableAccess v : accesses) {
                    String mappedName = v.getMappedName();
                    if (v.isWritable() && !mappedName.startsWith("page:")) {
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

}
