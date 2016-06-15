package br.com.infox.epp.modeler.converter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.Decision;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.Fork;
import org.jbpm.graph.node.Join;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.jpdl.JpdlXmlWriter;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class BpmnJpdlService {
	
	@Inject
	private FluxoManager fluxoManager;

	public Fluxo atualizarDefinicaoJpdl(Fluxo fluxo) {
		fluxo.setXml(JpdlXmlWriter.toString(getUpdatedJbpmDefinitionFromBpmn(fluxo)));
		return fluxoManager.update(fluxo);
	}
	
	public ProcessDefinition getUpdatedJbpmDefinitionFromBpmn(Fluxo fluxo) {
		BpmnModelInstance bpmn = Bpmn.readModelFromStream(new ByteArrayInputStream(fluxo.getBpmn().getBytes(StandardCharsets.UTF_8)));
		Document jpdlDoc = loadXml(fluxo.getXml());
		
		List<String> jpdlNodeKeys = new ArrayList<>();
		List<String> bpmnNodeKeys = new ArrayList<>();
		Map<String, Element> jpdlTransitions = new HashMap<>();
		List<String> bpmnTransitionKeys = new ArrayList<>();
		List<String> jpdlSwimlaneKeys = new ArrayList<>();
		List<String> bpmnSwimlaneKeys = new ArrayList<>();
		
		for (Element transition : jpdlDoc.getRootElement().getDescendants(new ElementFilter("transition", jpdlDoc.getRootElement().getNamespace()))) {
			jpdlTransitions.put(transition.getAttributeValue("key"), transition);
		}
		for (SequenceFlow sequenceFlow : bpmn.getModelElementsByType(SequenceFlow.class)) {
			bpmnTransitionKeys.add(sequenceFlow.getId());
		}

		removeTransitions(jpdlTransitions, bpmnTransitionKeys);
		
		XMLOutputter out = new XMLOutputter();
		ProcessDefinition processDefinition = new InfoxJpdlXmlReader(new StringReader(out.outputString(jpdlDoc))).readProcessDefinition();
		
		for (Swimlane swimlane : processDefinition.getTaskMgmtDefinition().getSwimlanes().values()) {
			jpdlSwimlaneKeys.add(swimlane.getKey());
		}
		for (Lane lane : bpmn.getModelElementsByType(Lane.class)) {
			bpmnSwimlaneKeys.add(lane.getId());
		}
		for (Node node : processDefinition.getNodes()) {
			jpdlNodeKeys.add(node.getKey());
		}
		for (FlowNode flowNode : bpmn.getModelElementsByType(FlowNode.class)) {
			bpmnNodeKeys.add(flowNode.getId());
		}
		
		resolveLanes(bpmn, jpdlSwimlaneKeys, bpmnSwimlaneKeys, processDefinition);
		Map<String, Swimlane> swimlaneKeyMap = new HashMap<>();
		for (Swimlane swimlane : processDefinition.getTaskMgmtDefinition().getSwimlanes().values()) {
			swimlaneKeyMap.put(swimlane.getKey(), swimlane);
		}
		
		resolveNodes(bpmn, jpdlNodeKeys, bpmnNodeKeys, swimlaneKeyMap, processDefinition);
		resolveTransitions(bpmn, jpdlTransitions, bpmnTransitionKeys, processDefinition);

		return processDefinition;
	}

	private void resolveTransitions(BpmnModelInstance bpmn, Map<String, Element> jpdlTransitions,
			List<String> bpmnTransitionKeys, ProcessDefinition processDefinition) {
		for (String bpmnTransitionKey : bpmnTransitionKeys) {
			if (!jpdlTransitions.keySet().contains(bpmnTransitionKey)) {
				SequenceFlow sequenceFlow = bpmn.getModelElementById(bpmnTransitionKey);
				Node from = processDefinition.getNode(sequenceFlow.getSource().getId());
				Node to = processDefinition.getNode(sequenceFlow.getTarget().getId());
				Transition transition = new Transition(getLabel(sequenceFlow));
				transition.setKey(sequenceFlow.getId());
				transition.setFrom(from);
				transition.setTo(to);
				from.addLeavingTransition(transition);
				to.addArrivingTransition(transition);
			}
		}
	}

	private void resolveNodes(BpmnModelInstance bpmn, List<String> jpdlNodeKeys, List<String> bpmnNodeKeys, Map<String, Swimlane> swimlaneKeyMap, ProcessDefinition processDefinition) {
		for (String jpdlNodeKey : jpdlNodeKeys) {
			if (!bpmnNodeKeys.contains(jpdlNodeKey)) {
				processDefinition.removeNode(processDefinition.getNode(jpdlNodeKey));
			}
		}
		for (String bpmnNodeKey : bpmnNodeKeys) {
			if (!jpdlNodeKeys.contains(bpmnNodeKey)) {
				Node node = createNode((FlowNode) bpmn.getModelElementById(bpmnNodeKey), processDefinition);
				processDefinition.addNode(node);
				if (node.getNodeType().equals(NodeType.Task)) {
					for (Lane lane : bpmn.getModelElementsByType(Lane.class)) {
						for (FlowNode flowNode : lane.getFlowNodeRefs()) {
							if (flowNode.getId().equals(node.getKey())) {
								((TaskNode) node).getTasks().iterator().next().setSwimlane(swimlaneKeyMap.get(lane.getId()));
								break;
							}
						}
					}
				}
			}
		}
	}

	private void resolveLanes(BpmnModelInstance bpmn, List<String> jpdlSwimlaneKeys, List<String> bpmnSwimlaneKeys, ProcessDefinition processDefinition) {
		for (String jpdlSwimlaneKey : jpdlSwimlaneKeys) {
			if (!bpmnSwimlaneKeys.contains(jpdlSwimlaneKey)) {
				processDefinition.getTaskMgmtDefinition().getSwimlanes().remove(jpdlSwimlaneKey);
			}
		}
		for (String bpmnSwimlaneKey : bpmnSwimlaneKeys) {
			if (!jpdlSwimlaneKeys.contains(bpmnSwimlaneKey)) {
				Lane lane = bpmn.getModelElementById(bpmnSwimlaneKey);
				Swimlane swimlane = new Swimlane(lane.getName());
				swimlane.setKey(lane.getId());
				swimlane.setTaskMgmtDefinition(processDefinition.getTaskMgmtDefinition());
				processDefinition.getTaskMgmtDefinition().addSwimlane(swimlane);
			}
		}
	}

	private void removeTransitions(Map<String, Element> jpdlTransitions, List<String> bpmnTransitionKeys) {
		Iterator<Entry<String, Element>> it = jpdlTransitions.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Element> entry = it.next();
			if (!bpmnTransitionKeys.contains(entry.getKey())) {
				entry.getValue().detach();
				it.remove();
			}
		}
	}
	
	private Node createNode(FlowNode flowNode, ProcessDefinition processDefinition) {
		Node node = null;
		if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_START_EVENT)) {
			node = new StartState(getLabel(flowNode));
		} else if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_END_EVENT)) {
			node = new EndState(getLabel(flowNode));
		} else if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_USER_TASK)) {
			node = new TaskNodeFactory().createTaskNode((UserTask) flowNode, processDefinition);
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
			}
		}
		node.setKey(flowNode.getId());
		return node;
	}
	
	private String getLabel(FlowElement element) {
		return element.getName() != null ? element.getName() : element.getId();
	}

	private Document loadXml(String xml) {
		if (xml == null) {
			ProcessDefinition processDefinition = fluxoManager.createInitialProcessDefinition();
			xml = JpdlXmlWriter.toString(processDefinition);
		}
		SAXBuilder builder = new SAXBuilder();
		try {
			return builder.build(new StringReader(xml));
		} catch (JDOMException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
