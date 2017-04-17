package br.com.infox.epp.fluxo.definicao.modeler;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.Collaboration;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.Fork;
import org.jbpm.graph.node.Join;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.fluxo.entity.DefinicaoProcesso;
import br.com.infox.epp.fluxo.service.HistoricoProcessDefinitionService;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.jpdl.JpdlXmlWriter;
import br.com.infox.ibpm.util.BpmUtil;
import br.com.infox.jbpm.event.JbpmEvents;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class BpmnJpdlService {
	
	@Inject
	private InfoxMessages infoxMessages;
	@Inject
	private HistoricoProcessDefinitionService historicoProcessDefinitionService;
	@Inject
	@GenericDao
	private Dao<DefinicaoProcesso, Long> definicaoProcessoDao;

	public BpmnModelInstance createInitialBpmn(String processName) {
    	String processKey = BpmUtil.generateKey();
    	ProcessBuilder builder = Bpmn.createProcess(processKey);
    	String sequenceFlowKey = BpmUtil.generateKey();
    	builder
    		.name(processName)
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
    	
    	Collaboration collaboration = bpmn.newInstance(Collaboration.class);
    	collaboration.setId(BpmUtil.generateKey());
    	collaboration.setClosed(false);
    	Participant participant = bpmn.newInstance(Participant.class);
    	participant.setId(BpmUtil.generateKey());
    	participant.setName(processName);
    	participant.setProcess(process);
    	collaboration.getParticipants().add(participant);
    	bpmn.getDefinitions().addChildElement(collaboration);
    	
    	return bpmn;
    }
    
    public ProcessDefinition createInitialProcessDefinition(String processName) {
    	ProcessDefinition processDefinition = ProcessDefinition.createNewProcessDefinition();
    	processDefinition.setKey(BpmUtil.generateKey());
    	processDefinition.setName(processName);
    	updateDefinitionsFromBpmn(createInitialBpmn(processName), processDefinition);
    	Swimlane laneSolicitante = processDefinition.getTaskMgmtDefinition().getSwimlanes().values().iterator().next();
    	laneSolicitante.setActorIdExpression("#{actor.id}");
    	
    	Task startTask = new Task("Tarefa inicial");
        startTask.setKey(BpmUtil.generateKey());
        startTask.setSwimlane(laneSolicitante);
        processDefinition.getTaskMgmtDefinition().setStartTask(startTask);
        
        String[] supportedEvents = processDefinition.getSupportedEventTypes();
		for (String eventType : supportedEvents) {
			Script action = new Script();
			Event event = processDefinition.getEvent(eventType);
			if (event == null) {
				event = new Event(eventType);
				processDefinition.addEvent(event);
			}
			action.setAsync(false);
			if (action instanceof Script) {
				action.setExpression(JbpmEvents.PATH_TO_JBPM_EVENTS_RAISER);
			}
			event.addAction(action);
		}
        return processDefinition;
    }
	
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public DefinicaoProcesso atualizarDefinicao(DefinicaoProcesso definicaoProcesso, String newProcessDefinitionXml, String newBpmnXml, String newSvg) {
    	BpmnModelInstance bpmnModel = Bpmn.readModelFromStream(new ByteArrayInputStream(newBpmnXml.getBytes(StandardCharsets.UTF_8)));
    	ProcessDefinition processDefinition = loadOrCreateProcessDefinition(newProcessDefinitionXml);
    	updateDefinitionsFromBpmn(bpmnModel, processDefinition);
    	atualizarNomeFluxo(definicaoProcesso.getFluxo().getFluxo(), bpmnModel, processDefinition);
    	ConfiguracoesTarefa.resolverMarcadoresBpmn(processDefinition, bpmnModel);
    	
    	newProcessDefinitionXml = JpdlXmlWriter.toString(processDefinition);
    	// Validar consistência do JPDL
		InfoxJpdlXmlReader.readProcessDefinition(newProcessDefinitionXml);

		historicoProcessDefinitionService.registrarHistorico(definicaoProcesso);
		definicaoProcesso.setXml(newProcessDefinitionXml);
		definicaoProcesso.setBpmn(Bpmn.convertToString(bpmnModel));
		definicaoProcesso.setSvg(newSvg);
		return definicaoProcessoDao.update(definicaoProcesso);
	}
    
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public DefinicaoProcesso importarBpmn(DefinicaoProcesso definicaoProcesso, String bpmn) {
	    BpmnCleaner cleaner = new BpmnCleaner();
	    bpmn = cleaner.cleanBpmn(bpmn);
	    
		BpmnModelInstance bpmnModel = Bpmn.readModelFromStream(new ByteArrayInputStream(bpmn.getBytes(StandardCharsets.UTF_8)));
		BpmnAdapter[] adapters = getAdapters();
		for (BpmnAdapter adapter : adapters) {
			bpmnModel = adapter.checkAndConvert(bpmnModel);
		}

		if (bpmnModel.getModelElementsByType(Process.class).size() != 1) {
			throw new BusinessRollbackException("O BPMN deve conter apenas 1 processo");
		}
		if (bpmnModel.getModelElementsByType(Participant.class).size() != 1) {
			throw new BusinessRollbackException("O BPMN deve conter apenas 1 participante");
		}
		
    	ProcessDefinition processDefinition = loadOrCreateProcessDefinition(definicaoProcesso.getXml());
    	updateDefinitionsFromBpmn(bpmnModel, processDefinition);

    	Process process = bpmnModel.getModelElementsByType(Process.class).iterator().next();
    	processDefinition.setKey(process.getId());
		atualizarNomeFluxo(definicaoProcesso.getFluxo().getFluxo(), bpmnModel, processDefinition);
		
		String newProcessDefinitionXml = JpdlXmlWriter.toString(processDefinition);
		// Validar consistência do JPDL
		InfoxJpdlXmlReader.readProcessDefinition(newProcessDefinitionXml);
		
		historicoProcessDefinitionService.registrarHistorico(definicaoProcesso);
		definicaoProcesso.setXml(newProcessDefinitionXml);
		definicaoProcesso.setBpmn(Bpmn.convertToString(bpmnModel));
		definicaoProcesso.setSvg(null);
		return definicaoProcessoDao.update(definicaoProcesso);
	}

	public void atualizarNomeFluxo(String nomeFluxo, BpmnModelInstance bpmnModel, ProcessDefinition processDefinition) {
		Process process = bpmnModel.getModelElementsByType(Process.class).iterator().next();
		process.setName(nomeFluxo);
		processDefinition.setName(nomeFluxo);
		Participant participant = bpmnModel.getModelElementsByType(Participant.class).iterator().next();
		participant.setName(nomeFluxo);
	}
	
	private void updateDefinitionsFromBpmn(BpmnModelInstance bpmnModel, ProcessDefinition processDefinition) {
		BpmnJpdlTranslation translation = new BpmnJpdlTranslation(bpmnModel, processDefinition);
		for (Node node : translation.getNodesToRemove()) {
			processDefinition.removeNode(node);
		}
		for (Swimlane swimlane : translation.getSwimlanesToRemove()) {
			processDefinition.getTaskMgmtDefinition().getSwimlanes().remove(swimlane.getName());
			translation.getSwimlanes().remove(swimlane.getKey());
		}
		for (Transition transition : translation.getTransitionsToRemove()) {
			removeListeners(transition.getFrom(), transition);
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
	}
	
	private void updateTransitions(BpmnJpdlTranslation translation, BpmnModelInstance bpmnModel, ProcessDefinition processDefinition) {
		for (SequenceFlow sequenceFlow : bpmnModel.getModelElementsByType(SequenceFlow.class)) {
			Transition transition = translation.getJpdlTransitions().get(sequenceFlow.getId());
			if (!transition.getName().equals(NodeFactory.getLabel(sequenceFlow))) {
				transition.setName(NodeFactory.getLabel(sequenceFlow));
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
			ReflectionsUtil.setValue(swimlane, "name", lane.getName() != null ? lane.getName() : lane.getId());
			processDefinition.getTaskMgmtDefinition().addSwimlane(swimlane);
		}
	}
	
	private void updateNodes(BpmnJpdlTranslation translation, BpmnModelInstance bpmnModel, ProcessDefinition processDefinition) {
		for (FlowNode flowNode : bpmnModel.getModelElementsByType(FlowNode.class)) {
			if (flowNode instanceof BoundaryEvent) {
				continue;
			}
			Node node = processDefinition.getNode(flowNode.getId());
			Node newCandidateNode = NodeFactory.createNode(flowNode, processDefinition);
			if (!newCandidateNode.getClass().equals(node.getClass())) {
				copyAndRemoveNode(processDefinition, node, newCandidateNode);
			}
			String label = NodeFactory.getLabel(flowNode);
			if (label.equals(flowNode.getId()) && !label.equals(node.getName())) {
				// workaround para o comportamento do método hasNode do ProcessDefinition
				int index = processDefinition.getNodes().indexOf(node);
				processDefinition.removeNode(node);
				node.setName(label);
				processDefinition.addNode(node);
				processDefinition.reorderNode(processDefinition.getNodes().indexOf(node), index);
			} else {
				node.setName(label);
			}
			if (node.getNodeType().equals(NodeType.Task)) {
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
		int index = processDefinition.getNodes().indexOf(oldNode);
		processDefinition.removeNode(oldNode);
		processDefinition.addNode(newNode);
		processDefinition.reorderNode(processDefinition.getNodes().indexOf(newNode), index);
	}

	private void createSwimlanes(BpmnJpdlTranslation translation, ProcessDefinition processDefinition) {
		for (Lane lane : translation.getNewLanes()) {
			Swimlane swimlane = new Swimlane(lane.getName() != null ? lane.getName() : lane.getId());
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
			return createInitialProcessDefinition(BpmUtil.generateKey());
		} else {
			return new InfoxJpdlXmlReader(new StringReader(xml)).readProcessDefinition();
		}
	}
	
	private void removeListeners(Node node, Transition transition) {
        Map<String, Event> events = node.getEvents();
        if (events == null) return;
        List<Event> removeEvents = new ArrayList<>();
        for (Event event : events.values()) {
            if (event.isListener() && event.getConfiguration().contains(transition.getKey())) {
                removeEvents.add(event);
            }
        }
        for (Event event : removeEvents) {
            node.removeEvent(event);
        }
    }

	private BpmnAdapter[] getAdapters() {
		return new BpmnAdapter[] {
			new BizagiBpmnAdapter(),
			new BpmnNodesAdapter(),
			new BpmnDiagramAdapter()
		};
	}
}
