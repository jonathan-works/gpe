package br.com.infox.epp.modeler.converter;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.Fork;
import org.jbpm.graph.node.Join;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.jpdl.JpdlXmlWriter;
import br.com.infox.ibpm.util.BpmUtil;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class BpmnJpdlService {
	
	@Inject
	private FluxoManager fluxoManager;
	@Inject
	private InfoxMessages infoxMessages;

	public BpmnModelInstance createInitialBpmn() {
    	String processKey = BpmUtil.generateKey();
    	ProcessBuilder builder = Bpmn.createProcess(processKey);
    	String sequenceFlowKey = BpmUtil.generateKey();
    	builder
    		.startEvent(BpmUtil.generateKey()).name(infoxMessages.get("process.node.first"))
    		.sequenceFlowId(sequenceFlowKey).condition(infoxMessages.get("process.node.last"), "")
    		.endEvent(BpmUtil.generateKey()).name(infoxMessages.get("process.node.last"));
    	
    	BpmnModelInstance bpmn = builder.done();
    	
    	((SequenceFlow) bpmn.getModelElementById(sequenceFlowKey)).removeConditionExpression();
    	
    	Process process = bpmn.getModelElementById(processKey);
    	process.setExecutable(true);
    	LaneSet laneSet = bpmn.newInstance(LaneSet.class);
    	process.getLaneSets().add(laneSet);
    	Lane solicitante = bpmn.newInstance(Lane.class);
    	solicitante.setId(BpmUtil.generateKey());
    	solicitante.setName("Solicitante");
    	laneSet.getLanes().add(solicitante);
    	
    	return bpmn;
    }
    
    public ProcessDefinition createInitialProcessDefinition() {
    	ProcessDefinition processDefinition = getUpdatedJbpmDefinitionFromBpmn(createInitialBpmn(), loadOrCreateProcessDefinition(null));
    	Swimlane laneSolicitante = processDefinition.getTaskMgmtDefinition().getSwimlanes().values().iterator().next();
    	laneSolicitante.setActorIdExpression("#{actor.id}");
    	
    	Task startTask = new Task("Tarefa inicial");
        startTask.setKey(BpmUtil.generateKey());
        startTask.setSwimlane(laneSolicitante);
        processDefinition.getTaskMgmtDefinition().setStartTask(startTask);
        return processDefinition;
    }
	
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Fluxo atualizarDefinicaoJpdl(Fluxo fluxo) {
		fluxo.setXml(JpdlXmlWriter.toString(getUpdatedJbpmDefinitionFromBpmn(fluxo.getBpmn(), fluxo.getXml())));
		return fluxoManager.update(fluxo);
	}
	
	private ProcessDefinition getUpdatedJbpmDefinitionFromBpmn(String bpmnXml, String jpdlXml) {
		BpmnModelInstance bpmnModel = Bpmn.readModelFromStream(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
		ProcessDefinition processDefinition = loadOrCreateProcessDefinition(jpdlXml);
		return getUpdatedJbpmDefinitionFromBpmn(bpmnModel, processDefinition);
	}
	
	private ProcessDefinition getUpdatedJbpmDefinitionFromBpmn(BpmnModelInstance bpmnModel, ProcessDefinition processDefinition) {
		BpmnJpdlTranslation translation = new BpmnJpdlTranslation(bpmnModel, processDefinition);
		for (Node node : translation.getNodesToRemove()) {
			processDefinition.removeNode(node);
		}
		for (Swimlane swimlane : translation.getSwimlanesToRemove()) {
			processDefinition.getTaskMgmtDefinition().getSwimlanes().remove(swimlane.getName());
			translation.getSwimlanes().remove(swimlane.getKey());
		}
		for (Transition transition : translation.getTransitionsToRemove()) {
			translation.getJpdlTransitions().remove(transition);
			transition.getFrom().removeLeavingTransition(transition);
			transition.getTo().removeArrivingTransition(transition);
			translation.getJpdlTransitions().remove(transition.getKey());
		}
		
		createSwimlanes(translation, processDefinition);
		createNodes(translation, processDefinition);
		createTransitions(translation, processDefinition);
		
		updateSwimlanes(translation, bpmnModel, processDefinition);
		updateNodes(translation, bpmnModel, processDefinition);
		updateTransitions(translation, bpmnModel, processDefinition);
		
		return processDefinition;
	}
	
	private void updateTransitions(BpmnJpdlTranslation translation, BpmnModelInstance bpmnModel, ProcessDefinition processDefinition) {
		for (SequenceFlow sequenceFlow : bpmnModel.getModelElementsByType(SequenceFlow.class)) {
			Transition transition = translation.getJpdlTransitions().get(sequenceFlow.getId());
			if (!transition.getName().equals(NodeFactory.getLabel(sequenceFlow))) {
				transition.setName(NodeFactory.getLabel(sequenceFlow));
			}
			if (sequenceFlow.getConditionExpression() != null) {
				transition.setCondition(sequenceFlow.getConditionExpression().getTextContent());
			}
			
			Node oldTo = transition.getTo();
			Node oldFrom = transition.getFrom();
			Node newTo = processDefinition.getNode(sequenceFlow.getTarget().getId());
			Node newFrom = processDefinition.getNode(sequenceFlow.getSource().getId());
			
			oldTo.removeArrivingTransition(transition);
			oldFrom.removeLeavingTransition(transition);
			transition.setFrom(newFrom);
			transition.setTo(newTo);
			newTo.addArrivingTransition(transition);
			newFrom.addLeavingTransition(transition);
		}
	}

	private void updateSwimlanes(BpmnJpdlTranslation translation, BpmnModelInstance bpmnModel, ProcessDefinition processDefinition) {
		for (Lane lane : bpmnModel.getModelElementsByType(Lane.class)) {
			Swimlane swimlane = translation.getSwimlanes().get(lane.getId());
			processDefinition.getTaskMgmtDefinition().getSwimlanes().remove(swimlane.getName());
			ReflectionsUtil.setValue(swimlane, "name", lane.getName());
			processDefinition.getTaskMgmtDefinition().addSwimlane(swimlane);
		}
	}
	
	private void updateNodes(BpmnJpdlTranslation translation, BpmnModelInstance bpmnModel, ProcessDefinition processDefinition) {
		for (FlowNode flowNode : bpmnModel.getModelElementsByType(FlowNode.class)) {
			Node node = processDefinition.getNode(flowNode.getId());
			node.setName(NodeFactory.getLabel(flowNode));
			if (node instanceof ProcessState) {
				ReflectionsUtil.setValue(node, "subProcessName", flowNode.getName());
			} else if (node.getNodeType().equals(NodeType.Task)) {
				TaskNode taskNode = (TaskNode) node;
				if (taskNode.getTasks().size() > 1) {
					throw new BusinessRollbackException("Nós de tarefa com mais de uma task não são suportados");
				}
				Lane lane = translation.getNodesToLanes().get(node.getKey());
				Task task = taskNode.getTasks().iterator().next();
				task.setSwimlane(translation.getSwimlanes().get(lane.getId()));
				task.setName(taskNode.getName());
			} else if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_PARALLEL_GATEWAY)) {
				ParallelGateway gateway = (ParallelGateway) flowNode;
				if (node.getNodeType().equals(NodeType.Fork) && gateway.getGatewayDirection() == GatewayDirection.Converging) {
					copyAndRemoveNode(processDefinition, node, new Join());
				} else if (node.getNodeType().equals(NodeType.Join) && gateway.getGatewayDirection() == GatewayDirection.Diverging) {
					copyAndRemoveNode(processDefinition, node, new Fork());
				}
			}
		}
	}

	private void copyAndRemoveNode(ProcessDefinition processDefinition, Node oldNode, Node newNode) {
		newNode.setName(oldNode.getName());
		newNode.setKey(oldNode.getKey());
		newNode.setDescription(oldNode.getDescription());
		if (oldNode.getArrivingTransitions() != null) {
			for (Transition transition : oldNode.getArrivingTransitions()) {
				newNode.addArrivingTransition(transition);
			}
		}
		if (oldNode.getLeavingTransitions() != null) {
			for (Transition transition : oldNode.getLeavingTransitions()) {
				newNode.addLeavingTransition(transition);
			}
		}
		if (oldNode.getEvents() != null) {
			for (Event event : oldNode.getEvents().values()) {
				newNode.addEvent(event);
			}
		}
		processDefinition.removeNode(oldNode);
		processDefinition.addNode(newNode);
	}

	private void createSwimlanes(BpmnJpdlTranslation translation, ProcessDefinition processDefinition) {
		for (Lane lane : translation.getNewLanes()) {
			Swimlane swimlane = new Swimlane(lane.getName());
			swimlane.setKey(lane.getId());
			swimlane.setTaskMgmtDefinition(processDefinition.getTaskMgmtDefinition());
			processDefinition.getTaskMgmtDefinition().addSwimlane(swimlane);
			translation.getSwimlanes().put(swimlane.getKey(), swimlane);
		}
	}
	
	private void createNodes(BpmnJpdlTranslation translation, ProcessDefinition processDefinition) {
		for (FlowNode flowNode : translation.getNewNodes()) {
			Node node = NodeFactory.createNode(flowNode, processDefinition);
			processDefinition.addNode(node);
			if (node.getNodeType().equals(NodeType.Task)) {
				if (translation.getNodesToLanes().containsKey(node.getKey())) {
					Lane lane = translation.getNodesToLanes().get(node.getKey());
					Swimlane swimlane = translation.getSwimlanes().get(lane.getId());
					for (Task task : ((TaskNode) node).getTasks()) {
						task.setSwimlane(swimlane);
					}
				}
			}
		}
	}
	
	private void createTransitions(BpmnJpdlTranslation translation, ProcessDefinition processDefinition) {
		for (SequenceFlow sequenceFlow : translation.getNewTransitions()) {
			Node from = processDefinition.getNode(sequenceFlow.getSource().getId());
			Node to = processDefinition.getNode(sequenceFlow.getTarget().getId());
			Transition transition = new Transition(NodeFactory.getLabel(sequenceFlow));
			if (sequenceFlow.getConditionExpression() != null) {
				transition.setCondition(sequenceFlow.getConditionExpression().getTextContent());
			}
			transition.setKey(sequenceFlow.getId());
			transition.setFrom(from);
			transition.setTo(to);
			from.addLeavingTransition(transition);
			to.addArrivingTransition(transition);
			
			translation.getJpdlTransitions().put(transition.getKey(), transition);
		}
	}

	private ProcessDefinition loadOrCreateProcessDefinition(String xml) {
		if (xml == null) {
			ProcessDefinition processDefinition = ProcessDefinition.createNewProcessDefinition();
			processDefinition.setKey(BpmUtil.generateKey());
			processDefinition.setName(processDefinition.getKey());
			return processDefinition;
		} else {
			return new InfoxJpdlXmlReader(new StringReader(xml)).readProcessDefinition();
		}
	}
}
