package br.com.infox.ibpm.task.handler;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.faces.FacesMessages;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.instantiation.Delegation;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.def.TaskMgmtDefinition;

import br.com.infox.epp.documento.list.associative.AssociativeModeloDocumentoList;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.assignment.SingleActorAssignmentHandler;
import br.com.infox.ibpm.variable.VariableAccessHandler;
import br.com.infox.jbpm.action.ActionTemplateHandler;
import br.com.infox.seam.util.ComponentUtil;

public class TaskHandler implements Serializable {

    private static final long serialVersionUID = 9033256144150197159L;
    private Task task;
    private String swimlaneName;
    private boolean dirty;
    private List<VariableAccessHandler> variables;
    private Boolean hasTaskPage;
    private VariableAccessHandler currentVariable;

    public TaskHandler(Task task) {
        this.task = task;
        if (task != null){
        	if (task.getSwimlane() != null) {
        		this.swimlaneName = task.getSwimlane().getName();
        	}
        	// Para as tarefas já existentes
        	if (task.getTaskController() != null && task.getTaskController().getTaskControllerDelegation() == null) {
        		Delegation delegation = new Delegation(InfoxTaskControllerHandler.class.getName());
        		delegation.setProcessDefinition(task.getProcessDefinition());
        		task.getTaskController().setTaskControllerDelegation(delegation);
        	}
        }
    }
    
    public boolean isExpressionAssigned(){
    	return getTask() != null && getTask().getSwimlane() == null;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getAssigneeExpression(){
    	return task == null || task.getAssignmentDelegation() == null ? null : task.getAssignmentDelegation().getConfiguration();
    }
    public void setAssigneeExpression(String expression){
    	if (task != null){
			task.setAssignmentDelegation(createAssignmentDelegation(expression));
    	}
    }
    
    public String getPooledActorsExpression(){
    	return task == null ? null : task.getPooledActorsExpression();
    }
    public void setPooledActorsExpression(String expression){
    	if (task != null){
    		task.setPooledActorsExpression(expression);
    	}
    }

    public void setAssignmentType(String assignmentType){
    	switch (assignmentType) {
		case "assignee":
			setAssigneeExpression("");
			break;
		case "pooledActorsExpression":
			setPooledActorsExpression("");
			break;
		case "swimlane":
			task.setSwimlane(task.getTaskMgmtDefinition().getSwimlanes().values().iterator().next());
			break;
		default:
			break;
		}
    }
    
    public String getAssignmentType(){
    	if (getTask() != null){
    		Task task = getTask();
    		if (task.getSwimlane() != null){
    			return "swimlane";
    		}
    		if (task.getPooledActorsExpression() != null){
    			return "pooledActorsExpression";
    		}
    		if (task.getAssignmentDelegation() != null) {
				if (SingleActorAssignmentHandler.class.getName().equals(task.getAssignmentDelegation().getClassName())) {
					return "assignee";
				}
			}
    	}
    	return null;
    }
    
    public String getSwimlaneName() {
        return task == null || task.getSwimlane() == null ? null : task.getSwimlane().getName();
    }

    public void setSwimlaneName(String swimlaneName) {
        this.swimlaneName = swimlaneName;
        if (swimlaneName == null) {
        	task.setSwimlane(null);
        } else {
            if (task.getTaskMgmtDefinition() == null) {
                task.setTaskMgmtDefinition(new TaskMgmtDefinition());
            }
            task.setSwimlane(task.getTaskMgmtDefinition().getSwimlane(swimlaneName));
        }
    }

	private Delegation createAssignmentDelegation(String configuration) {
		Delegation assignmentDelegation = new Delegation(SingleActorAssignmentHandler.class.getName());
		assignmentDelegation.setConfigType("constructor");
		assignmentDelegation.setProcessDefinition(task.getProcessDefinition());
		assignmentDelegation.setConfiguration(configuration);
		return assignmentDelegation;
	}

    public boolean isDirty() {
        return dirty;
    }

    public List<VariableAccessHandler> getVariables() {
        if (task != null && variables == null) {
            variables = VariableAccessHandler.getList(task);
        }
        return variables;
    }

    public void setCurrentVariable(String name) {
        AssociativeModeloDocumentoList associativeModeloDocumentoList = ComponentUtil.getComponent(AssociativeModeloDocumentoList.NAME);
        associativeModeloDocumentoList.refreshModelosAssociados();
        if (variables == null) {
            return;
        }
        for (VariableAccessHandler v : variables) {
            if (v.getName().equals(name)) {
                currentVariable = v;
            }
        }
    }

    public void setCurrentVariable(VariableAccessHandler var) {
        currentVariable = var;
        AssociativeModeloDocumentoList associativeModeloDocumentoList = ComponentUtil.getComponent(AssociativeModeloDocumentoList.NAME);
        associativeModeloDocumentoList.refreshModelosAssociados();
        StringBuilder sb = new StringBuilder();
        sb.append("#{modeloDocumento.set('").append(currentVariable.getName()).append("'");
        if (currentVariable.getModeloList() != null) {
            for (Integer i : currentVariable.getModeloList()) {
                sb.append(",").append(i);
            }
        }
        sb.append(")}");
        ActionTemplateHandler.instance().setCurrentActionTemplate(sb.toString());
    }

    public VariableAccessHandler getCurrentVariable() {
        return currentVariable;
    }

    public static List<TaskHandler> createList(TaskNode node) {
        List<TaskHandler> ret = new ArrayList<TaskHandler>();
        if (node.getTasks() != null) {
            for (Object t : node.getTasks()) {
                ret.add(new TaskHandler((Task) t));
            }
        }
        return ret;
    }

    @SuppressWarnings(UNCHECKED)
    public Task update() {
        if (task.getTaskController() != null) {
            List<VariableAccess> variableAccesses = task.getTaskController().getVariableAccesses();
            variableAccesses.clear();
            for (VariableAccessHandler v : variables) {
                variableAccesses.add(v.update());
            }
        }
        return task;
    }

    @SuppressWarnings(UNCHECKED)
    public void newVar() {
        if (!checkNullVariables()) {
            VariableAccess v = new VariableAccess("", "read,write", VariableType.NULL.name() + ":");
            VariableAccessHandler vh = new VariableAccessHandler(v, task);
            variables.add(vh);
            TaskController taskController = task.getTaskController();
            if (taskController == null) {
                taskController = new TaskController();
                task.setTaskController(taskController);
                taskController.setVariableAccesses(new ArrayList<VariableAccess>());
                Delegation delegation = new Delegation(InfoxTaskControllerHandler.class.getName());
                delegation.setProcessDefinition(task.getProcessDefinition());
                taskController.setTaskControllerDelegation(delegation);
            }
            taskController.getVariableAccesses().add(v);
            ProcessBuilder.instance().getTaskFitter().setTypeList(null);
        }
    }

    private boolean checkNullVariables() {
        for (VariableAccessHandler vah : variables) {
            if (VariableType.NULL.equals(vah.getType())) {
                FacesMessages.instance().add("É obrigatório selecionar um tipo!");
                return true;
            }
        }
        return false;
    }

    public void removeVar(VariableAccessHandler variableAccessHandler) {
        task.getTaskController().getVariableAccesses().remove(variableAccessHandler.getVariableAccess());
        variableAccessHandler.removeTaskAction(variableAccessHandler.getName());
        variables.remove(variableAccessHandler);
        if (variableAccessHandler.getType() == VariableType.TASK_PAGE) {
            hasTaskPage = null;
        }
        ProcessBuilder.instance().getTaskFitter().setTypeList(null);
    }

    private List<String> populatePreviousVariables(TaskHandlerVisitor visitor) {
        accept(visitor);
        return visitor.getVariables();
    }

    public List<String> getPreviousVariables() {
        return populatePreviousVariables(new TaskHandlerVisitor(false));
    }

    public List<String> getPreviousNumberVariables() {
        List<String> types = new ArrayList<String>();
        types.add(VariableType.INTEGER.name());
        types.add(VariableType.MONETARY.name());
        return populatePreviousVariables(new TaskHandlerVisitor(false, types));
    }

    public List<String> getPreviousBoolVariables() {
        List<String> types = new ArrayList<String>();
        types.add(VariableType.BOOLEAN.name());
        return populatePreviousVariables(new TaskHandlerVisitor(false, types));
    }

    public Boolean hasTaskPage() {
        if (hasTaskPage == null) {
            if (variables != null) {
                for (VariableAccessHandler va : variables) {
                    if (VariableType.TASK_PAGE.equals(va.getType())) {
                        return true;
                    }
                }
            }
            hasTaskPage = false;
        }
        return hasTaskPage;
    }

    public void clearHasTaskPage() {
        this.hasTaskPage = null;
    }

    public void accept(TaskHandlerVisitor visitor) {
        visitor.visit(this.task);
    }

    public void processVarTypeChange(VariableAccessHandler var) {
        clearHasTaskPage();
        var.limparModelos();
        if (!var.podeIniciarVazia()) {
        	var.setIniciaVazia(false);
        }
    }

    public List<String> getTransitions() {
	    List<String> transitions = new ArrayList<>();
	    List<Transition> leavingTransitions = this.task.getTaskNode().getLeavingTransitions();
	    for (Transition leavingTransition : leavingTransitions) {
	        transitions.add(leavingTransition.getName());
	    }
	    return transitions;
	}
}