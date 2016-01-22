package br.com.infox.epp.modeler.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import org.camunda.bpm.model.bpmn.BpmnModelException;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.instantiation.Delegation;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;

import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.handler.InfoxTaskControllerHandler;

public class TaskNodeFactory {
	public TaskNode createTaskNode(UserTask userTask, ProcessDefinition processDefinition) {
		TaskNode taskNode = new TaskNode(getIdentification(userTask));
		taskNode.setEndTasks(true);
		taskNode.setKey(UUID.randomUUID().toString());
		Task task = new Task(taskNode.getName());
		task.setTaskNode(taskNode);
		task.setKey(UUID.randomUUID().toString());
		task.setTaskController(new TaskController());
		task.getTaskController().setTaskControllerDelegation(new Delegation(InfoxTaskControllerHandler.class.getName()));
		task.getTaskController().getTaskControllerDelegation().setProcessDefinition(processDefinition);
		taskNode.setTasks(new HashSet<Task>());
		taskNode.getTasks().add(task);
		resolveVariables(userTask, task);
		return taskNode;
	}
	
	private String getIdentification(FlowElement element) {
		return element.getName() != null ? element.getName() : element.getId();
	}
	
	@SuppressWarnings("unchecked")
	private void resolveVariables(UserTask userTask, Task task) {
		CamundaFormData formData;
		try {
			formData = userTask.getExtensionElements().getElementsQuery().filterByType(CamundaFormData.class).singleResult();
		} catch (BpmnModelException e) {
			return;
		}
		if (formData != null) {
			task.getTaskController().setVariableAccesses(new ArrayList<VariableAccess>());
			for (CamundaFormField formField : formData.getCamundaFormFields()) {
				String type = VariableType.valueOf(formField.getCamundaType()).name();
				VariableAccess variableAccess = new VariableAccess(formField.getCamundaId(), "read,write", type + ":" + formField.getCamundaId());
				task.getTaskController().getVariableAccesses().add(variableAccess);
			}
		}
	}
}
