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
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.jpdl.JpdlXmlWriter;
import br.com.infox.ibpm.util.BpmUtil;

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
			transition.getFrom().removeLeavingTransition(transition);
		}
		
		createSwimlanes(translation, processDefinition);
		createNodes(translation, processDefinition);
		createTransitions(translation, processDefinition);
		
		return processDefinition;
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
			transition.setKey(sequenceFlow.getId());
			transition.setFrom(from);
			transition.setTo(to);
			from.addLeavingTransition(transition);
			to.addArrivingTransition(transition);
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
