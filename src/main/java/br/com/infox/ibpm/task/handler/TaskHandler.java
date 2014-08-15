package br.com.infox.ibpm.task.handler;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.seam.faces.FacesMessages;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.instantiation.Delegation;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.def.TaskMgmtDefinition;

import br.com.infox.epp.documento.list.associative.AssociativeModeloDocumentoList;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoManager;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.process.definition.variable.VariableType;
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
    private StatusProcesso statusProcesso;

    public TaskHandler(Task task) {
        this.task = task;
        if (task != null && task.getSwimlane() != null) {
            this.swimlaneName = task.getSwimlane().getName();
        }
        
        if (task.hasEvent(Event.EVENTTYPE_TASK_CREATE)) {
        	Event event = task.getEvent(Event.EVENTTYPE_TASK_CREATE);
        	List<?> actions = event.getActions();
        	for (Object object : actions) {
				Action action = (Action) object;
				Delegation actionDelegation = action.getActionDelegation();
				if (actionDelegation != null && StatusHandler.class.getName().equals(actionDelegation.getClassName())) {
					String configuration = actionDelegation.getConfiguration();
					Pattern pattern = Pattern.compile("<statusProcesso>(\\d+)</statusProcesso>");
					Matcher matcher = pattern.matcher(configuration);
					if (matcher.find()) {
						String status = matcher.group(1);
						StatusProcessoManager manager = ComponentUtil.getComponent(StatusProcessoManager.NAME);
						this.statusProcesso = manager.find(Integer.parseInt(status, 10));
					}
				}
			}
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

    public void removeVar(VariableAccessHandler v) {
        task.getTaskController().getVariableAccesses().remove(v.getVariableAccess());
        variables.remove(v);
        if (v.getType() == VariableType.TASK_PAGE) {
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
    }

	private Event createNewStatusProcessoEvent(StatusProcesso statusProcesso) {
		Event event = new Event(Event.EVENTTYPE_TASK_CREATE);
		Action action = new Action();
		action.setName("setStatusProcessoAction");
		Delegation delegation = new Delegation(StatusHandler.class.getName());
		delegation.setConfigType("constructor");
		delegation.setConfiguration(MessageFormat.format(
				"<statusProcesso>{0}</statusProcesso>",
				statusProcesso.getIdStatusProcesso()));
		action.setActionDelegation(delegation);
		event.addAction(action);
		return event;
	}
	
	private Action retrieveStatusProcessoEvent(Event event) {
		List<?> actions = event.getActions();
		Action result = null;
		for (Object object : actions) {
			Action action = (Action) object;
			if ("setStatusProcessoAction".equals(action.getName())) {
				result = action;
				break;
			}
		}
		return result;
	}
	
	public StatusProcesso getStatusProcesso() {
		return statusProcesso;
	}

	public void setStatusProcesso(StatusProcesso statusProcesso) {
		Action action = null;
		if (this.task.hasEvent(Event.EVENTTYPE_TASK_CREATE)) {
			action = retrieveStatusProcessoEvent(this.task.getEvent(Event.EVENTTYPE_TASK_CREATE));
		}
		Event event = null;
		if (action != null) {
			Delegation actionDelegation = action.getActionDelegation();
			if (actionDelegation != null && StatusHandler.class.getName().equals(actionDelegation.getClassName())) {
				event = this.task.getEvent(Event.EVENTTYPE_TASK_CREATE);
				actionDelegation.setConfigType("constructor");
				actionDelegation.setConfiguration(MessageFormat.format(
						"<statusProcesso>{0}</statusProcesso>",
						statusProcesso.getIdStatusProcesso()));
			}
		} else {
			event = createNewStatusProcessoEvent(statusProcesso);
			this.task.addEvent(event);
		}
		
		this.statusProcesso = statusProcesso;
	}
}