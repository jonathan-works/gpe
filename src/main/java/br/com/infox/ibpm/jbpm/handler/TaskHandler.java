/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.jbpm.handler;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.faces.FacesMessages;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.def.TaskMgmtDefinition;

import br.com.infox.bpm.action.TaskPageAction;
import br.com.infox.ibpm.jbpm.ProcessBuilder;


public class TaskHandler {

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
		sb.append("#{modeloDocumento.set('")
			.append(currentVariable.getName())
			.append("'");
		if (currentVariable.getModeloList() != null) {
			for (Integer i : currentVariable.getModeloList()) {
				sb.append(",")
					.append(i);
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
			for (Task t : node.getTasks()) {
				ret.add(new TaskHandler(t));
			}
		}
		return ret;
	}
	
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

	public void newVar() {
		if(!checkNullVariables()) {
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
			ProcessBuilder.instance().setTypeList(null);
		}
	}
	
	private boolean checkNullVariables() {
		for(VariableAccessHandler vah : variables) {
			if(vah.getType().equals("null")) {
				FacesMessages.instance().add("� obrigat�rio selecionar um tipo!");
				return true;
			}
		}
		return false;
	}

	public void removeVar(VariableAccessHandler v) {
		task.getTaskController().getVariableAccesses().remove(v.getVariableAccess());
		variables.remove(v);
		if(v.getType().equals(TaskPageAction.TASK_PAGE_COMPONENT_NAME)) {
			hasTaskPage = null;
		}
		ProcessBuilder.instance().setTypeList(null);
	}

	public List<String> getPreviousVariables() {
		TaskHandlerVisitor visitor = new TaskHandlerVisitor(false);
		accept(visitor);
		return visitor.getVariables();
	}
	
	public Boolean hasTaskPage() {
		if(hasTaskPage == null) {
			for (VariableAccessHandler va : variables) {
				if(va.getType().equals(TaskPageAction.TASK_PAGE_COMPONENT_NAME)) {
					return true;
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