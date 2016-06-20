package br.com.infox.epp.modeler.converter;

import java.util.HashSet;

import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.Decision;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.Fork;
import org.jbpm.graph.node.Join;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.instantiation.Delegation;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;

import com.google.common.base.Strings;

import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.ibpm.node.InfoxMailNode;
import br.com.infox.ibpm.task.handler.InfoxTaskControllerHandler;
import br.com.infox.ibpm.util.BpmUtil;
import br.com.infox.seam.exception.BusinessRollbackException;

public class NodeFactory {
	public static Node createNode(FlowNode flowNode, ProcessDefinition processDefinition) {
		Node node = null;
		if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_START_EVENT)) {
			node = new StartState(getLabel(flowNode));
		} else if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_END_EVENT)) {
			node = new EndState(getLabel(flowNode));
		} else if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_USER_TASK)) {
			node = new TaskNode(getLabel(flowNode));
			TaskNode taskNode = (TaskNode) node;
			taskNode.setEndTasks(true);
			Task task = new Task(taskNode.getName());
			task.setTaskNode(taskNode);
			task.setKey(BpmUtil.generateKey());
			task.setTaskController(new TaskController());
			task.getTaskController().setTaskControllerDelegation(new Delegation(InfoxTaskControllerHandler.class.getName()));
			task.getTaskController().getTaskControllerDelegation().setProcessDefinition(processDefinition);
			taskNode.setTasks(new HashSet<Task>());
			taskNode.getTasks().add(task);
		} else if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_SERVICE_TASK)) {
			node = new Node(getLabel(flowNode));
		} else if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_EXCLUSIVE_GATEWAY)) {
			node = new Decision(getLabel(flowNode));
		} else if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_SUB_PROCESS)) {
			if (flowNode.getName() == null) {
				node = new ProcessState();
			} else {
				node = new ProcessState(flowNode.getName());
				ReflectionsUtil.setValue(node, "subProcessName", flowNode.getName());
			}
		} else if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_INTERMEDIATE_THROW_EVENT)) {
			node = new Node(getLabel(flowNode));
		} else if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_PARALLEL_GATEWAY)) {
			GatewayDirection direction = ((ParallelGateway) flowNode).getGatewayDirection();
			if (direction == GatewayDirection.Diverging) {
				node = new Fork(getLabel(flowNode));
			} else if (direction == GatewayDirection.Converging) {
				node = new Join(getLabel(flowNode));
			} else {
				throw new BusinessRollbackException("Tipo de nó fork/join não informado (id: " + flowNode.getId() +
					(!Strings.isNullOrEmpty(flowNode.getName()) ? ", nome: " + flowNode.getName() : "") + "): " + direction);
			}
		} else if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_SEND_TASK)) {
			node = new InfoxMailNode();
			node.setName(getLabel(flowNode));
			node.setKey(flowNode.getId());
		}
		
		if (node == null) {
			throw new BusinessRollbackException("Tipo de nó desconhecido: " + flowNode.getElementType().getTypeName());
		}
		
		node.setKey(flowNode.getId());
		return node;
	}
	
	public static String getLabel(FlowElement element) {
		return element.getName() != null ? element.getName() : element.getId();
	}
}
