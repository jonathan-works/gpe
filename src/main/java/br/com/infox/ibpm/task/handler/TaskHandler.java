package br.com.infox.ibpm.task.handler;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.faces.FacesMessages;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.def.TaskMgmtDefinition;

import br.com.infox.core.handler.ActionTemplateHandler;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.task.action.TaskPageAction;
import br.com.infox.ibpm.variable.VariableAccessHandler;

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
        if (task != null && task.getSwimlane() != null) {
            this.swimlaneName = task.getSwimlane().getName();
        }
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getSwimlaneName() {
        return swimlaneName;
    }

    public void setSwimlaneName(String swimlaneName) {
        this.swimlaneName = swimlaneName;
        if (swimlaneName == null) {
            task.setSwimlane(null);
        } else {
            if (task.getTaskMgmtDefinition() == null) {
                task.setTaskMgmtDefinition(new TaskMgmtDefinition());
            }
            Swimlane swimlane = task.getTaskMgmtDefinition().getSwimlane(swimlaneName);
            task.setSwimlane(swimlane);
        }
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
            VariableAccess v = new VariableAccess("", "read,write", "null:");
            VariableAccessHandler vh = new VariableAccessHandler(v, task);
            variables.add(vh);
            TaskController taskController = task.getTaskController();
            if (taskController == null) {
                taskController = new TaskController();
                task.setTaskController(taskController);
                taskController.setVariableAccesses(new ArrayList<VariableAccess>());
            }
            taskController.getVariableAccesses().add(v);
            ProcessBuilder.instance().getTypeFitter().setTypeList(null);
        }
    }

    private boolean checkNullVariables() {
        for (VariableAccessHandler vah : variables) {
            if ("null".equals(vah.getType())) {
                FacesMessages.instance().add("É obrigatório selecionar um tipo!");
                return true;
            }
        }
        return false;
    }

    public void removeVar(VariableAccessHandler v) {
        task.getTaskController().getVariableAccesses().remove(v.getVariableAccess());
        variables.remove(v);
        if (v.getType().equals(TaskPageAction.TASK_PAGE_COMPONENT_NAME)) {
            hasTaskPage = null;
        }
        ProcessBuilder.instance().getTypeFitter().setTypeList(null);
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
        types.add("number");
        types.add("numberMoney");
        return populatePreviousVariables(new TaskHandlerVisitor(false, types));
    }

    public List<String> getPreviousBoolVariables() {
        List<String> types = new ArrayList<String>();
        types.add("sim_nao");
        return populatePreviousVariables(new TaskHandlerVisitor(false, types));
    }

    public Boolean hasTaskPage() {
        if (hasTaskPage == null) {
            if (variables != null) {
                for (VariableAccessHandler va : variables) {
                    if (va.getType().equals(TaskPageAction.TASK_PAGE_COMPONENT_NAME)) {
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

}
