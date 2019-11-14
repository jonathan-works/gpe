package br.com.infox.ibpm.task.handler;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;

import br.com.infox.ibpm.process.definition.variable.VariableType;

public class VariableCollector {

    private static final int NODE = 0x1;
    private static final int TRANSITIONS = 0x2;
    private final Node node;
    private final Collection<Transition> transitionsToVisit;
    private final ArrayList<Node> visitedNodes;
    private final ArrayList<Transition> visitedTransitions;
    private final ArrayList<VariableAccess> declaredVariables;
    private final int mode;

    public VariableCollector(final Node node) {
        this.node = node;
        this.visitedNodes = new ArrayList<>();
        this.visitedTransitions = new ArrayList<>();
        this.declaredVariables = new ArrayList<>();
        this.transitionsToVisit = null;
        this.mode = NODE;
    }

    public VariableCollector(final Collection<Transition> transitionsToVisit) {
        this.node = null;
        this.visitedNodes = new ArrayList<>();
        this.visitedTransitions = new ArrayList<>();
        this.declaredVariables = new ArrayList<>();
        this.transitionsToVisit = transitionsToVisit;
        this.mode = TRANSITIONS;
    }

    public VariableCollector(final Transition... transitionsToVisit) {
        this(Arrays.asList(transitionsToVisit));
    }

    public List<VariableAccess> getDeclaredVariables() {
        switch (mode) {
            case NODE:
                visit(node);
            break;
            case TRANSITIONS:
                visit(transitionsToVisit);
            break;
            default:
            break;
        }
        return this.declaredVariables;
    }

    public List<VariableAccess> getVariablesOfTypes(
            final VariableType... restrictionTypes) {
        switch (mode) {
            case NODE:
                visit(node, restrictionTypes);
            break;
            case TRANSITIONS:
                visit(transitionsToVisit, restrictionTypes);
            break;
            default:
            break;
        }
        return this.declaredVariables;
    }

    private void visit(final Node node, final VariableType... restrictionTypes) {
        if (!(node instanceof StartState) && !visitedNodes.contains(node)) {
            if (node instanceof TaskNode) {
                populateVariableAccessForNode((TaskNode) node, restrictionTypes);
            }
            visitedNodes.add(node);

            visit(node.getArrivingTransitions(), restrictionTypes);
        }
    }

    private void visit(final Collection<Transition> arrivingTransitions,
            final VariableType... restrictionTypes) {
    	if(arrivingTransitions == null)
    		return;
        final ArrayList<Transition> transitions = new ArrayList<>(arrivingTransitions);
        transitions.removeAll(visitedTransitions);
        for (final Transition transition : transitions) {
            visit(transition, restrictionTypes);
        }
    }

    private void visit(final Transition transition,
            final VariableType... restrictionTypes) {
        if (!visitedTransitions.contains(transition)) {
            visitedTransitions.add(transition);
            visit(transition.getFrom(), restrictionTypes);
        }
    }

    private void populateVariableAccessForNode(final TaskNode node,
            final VariableType... restrictionTypes) {
        for (final Task task : (Set<Task>) node.getTasks()) {
            populateVariableAccessForTask(task, restrictionTypes);
        }
    }

    private void populateVariableAccessForTask(final Task task,
            final VariableType... restrictionTypes) {
        populateVariableAccessForTaskController(task.getTaskController(), restrictionTypes);
    }

    @SuppressWarnings(UNCHECKED)
    private void populateVariableAccessForTaskController(
            final TaskController taskController,
            final VariableType... restrictionTypes) {
        final List<VariableType> varTypeRestriction = Arrays.asList(restrictionTypes);
        if (taskController != null) {
            for (final VariableAccess variableAccess : (List<VariableAccess>) taskController.getVariableAccesses()) {
                final String[] split = variableAccess.getMappedName().split(":");
                if (!declaredVariables.contains(variableAccess)
                        && (varTypeRestriction.size() == 0 || varTypeRestriction.contains(VariableType.valueOf(split[0])))) {
                    declaredVariables.add(variableAccess);
                }
            }
        }
    }
}
