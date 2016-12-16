package br.com.infox.epp.modeler;

import java.text.MessageFormat;
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

import br.com.infox.ibpm.node.InfoxMailNode;
import br.com.infox.ibpm.task.handler.InfoxTaskControllerHandler;
import br.com.infox.ibpm.util.BpmUtil;
import br.com.infox.seam.exception.BusinessRollbackException;

class NodeFactory {
	static Node createNode(FlowNode flowNode, ProcessDefinition processDefinition) {
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
			node = new ProcessState(getLabel(flowNode));
		} else if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_PARALLEL_GATEWAY)) {
			GatewayDirection direction = ((ParallelGateway) flowNode).getGatewayDirection();
			if (direction == GatewayDirection.Diverging) {
				node = new Fork(getLabel(flowNode));
			} else if (direction == GatewayDirection.Converging) {
				node = new Join(getLabel(flowNode));
			} else {
				if (flowNode.getIncoming().size() > 1 && flowNode.getOutgoing().size() == 1) {
					node = new Join(getLabel(flowNode));
				} else if (flowNode.getIncoming().size() == 1 && flowNode.getOutgoing().size() > 1) {
					node = new Fork(getLabel(flowNode));
				} else {
					if (!Strings.isNullOrEmpty(flowNode.getName())) {
						String msg = "Impossível determinar se o gateway paralelo com id ''{0}'' e nome ''{1}'' é Fork ou Join";
						throw new BusinessRollbackException(MessageFormat.format(msg, flowNode.getId(), flowNode.getName()));
					} else {
						String msg = "Impossível determinar se o gateway paralelo com id ''{0}'' é Fork ou Join";
						throw new BusinessRollbackException(MessageFormat.format(msg, flowNode.getId()));
					}
				}
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
	
	static String getLabel(FlowElement element) {
		return element.getName() != null ? element.getName() : element.getId();
	}
}
