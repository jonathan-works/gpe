package br.com.infox.epp.modeler.converter;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.Collaboration;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.camunda.bpm.model.xml.ModelInstance;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.Decision;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import com.google.common.base.Strings;

import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.node.InfoxMailNode;
import br.com.infox.ibpm.util.BpmUtil;

public class JpdlBpmnConverter {
	
	private static final int FLOW_NODE_Y_OFFSET = 10;
	private static final int FLOW_NODE_INTER_DISTANCE = 200;
	private static final int ACTIVITY_WIDTH = 120;
	private static final int ACTIVITY_HEIGHT = 100;
	private static final int GENERAL_FLOWNODE_HEIGHT = 50;
	private static final int GENERAL_FLOWNODE_WIDTH = GENERAL_FLOWNODE_HEIGHT;
	private static final int LANE_HEIGHT = 200;
	private static final int PARTICIPANT_X = 200;
	private static final int PARTICIPANT_Y = 150;
	private static final int PARTICIPANT_LANE_OFFSET = 30;
	private static final int FLOW_NODE_X_OFFSET = 30;
	
	private ProcessDefinition processDefinition;
	private List<TaskNode> taskNodes = new LinkedList<>();
	private StartEvent startEvent;
	private Map<String, Lane> nodesToLanes = new HashMap<>();
	private double maxWidth = 0d;
	private List<FlowNode> orderedNodes = new ArrayList<>();
	private BpmnShape lastLaneShape;
	private Map<String, BpmnShape> laneShapes = new HashMap<>();
	private double maxXFromPreviousLane = 0d;
	
	public String convert(String processDefinitionXml) {
		processDefinition = new InfoxJpdlXmlReader(new StringReader(processDefinitionXml)).readProcessDefinition();
		ProcessBuilder builder = Bpmn.createProcess(processDefinition.getKey()).name(processDefinition.getName());
		AbstractFlowNodeBuilder<?, ?> flowNodeBuilder = builder.startEvent(processDefinition.getStartState().getKey());

		visit(processDefinition.getStartState(), flowNodeBuilder, null, new HashMap<String, Boolean>());
		
		BpmnModelInstance modelInstance = builder.done();
		startEvent = modelInstance.getModelElementById(processDefinition.getStartState().getKey());
		resolveDecisionExpressions(modelInstance);
		resolveLanes(modelInstance, processDefinition.getKey());
		createDiagram(modelInstance);
		
		Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
		for (SequenceFlow sequenceFlow : sequenceFlows) {
			if (sequenceFlow.getConditionExpression() != null && Strings.isNullOrEmpty(sequenceFlow.getConditionExpression().getRawTextContent())) {
				sequenceFlow.removeConditionExpression();
			}
		}

		modelInstance.getDefinitions().getDomElement().registerNamespace("xsi", BpmnModelConstants.XSI_NS);
		String bpmn = Bpmn.convertToString(modelInstance);
		bpmn = fixConditionExpressionsType(bpmn);
		return bpmn;
	}
	
	private String fixConditionExpressionsType(String bpmn) {
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc;
		try {
			doc = saxBuilder.build(new StringReader(bpmn));
		} catch (JDOMException | IOException e) {
			throw new RuntimeException(e);
		}

		Element definitions = doc.getRootElement();
		Namespace xsi = null;
		for (Namespace namespace : definitions.getNamespacesInScope()) {
			if (namespace.getURI().equals(BpmnModelConstants.XSI_NS)) {
				xsi = namespace;
				break;
			}
		}
		if (xsi == null) {
			xsi = Namespace.getNamespace("xsi", BpmnModelConstants.XSI_NS);
			definitions.addNamespaceDeclaration(xsi);
		}
		
		IteratorIterable<Element> conditions = definitions.getDescendants(new ElementFilter(BpmnModelConstants.BPMN_ELEMENT_CONDITION_EXPRESSION, definitions.getNamespace()));
		for (Element condition : conditions) {
			condition.setAttribute(BpmnModelConstants.XSI_ATTRIBUTE_TYPE, "tFormalExpression", xsi);
		}
		
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		return out.outputString(doc);
	}

	private void visit(Node node, AbstractFlowNodeBuilder<?, ?> parentBuilder, Transition originTransition, Map<String, Boolean> markedNodes) {
		markedNodes.put(node.getKey(), true);
		AbstractFlowNodeBuilder<?, ?> currentBuilder = null;
		
		if (originTransition != null) {
			parentBuilder.sequenceFlowId(originTransition.getKey()).condition(originTransition.getName(), "");
		}
		
		if (node.getNodeType().equals(NodeType.StartState)) {
			currentBuilder = parentBuilder;
		} else if (node.getNodeType().equals(NodeType.Decision)) {
			currentBuilder = parentBuilder.exclusiveGateway(node.getKey());
		} else if (node.getNodeType().equals(NodeType.Task)) {
			currentBuilder = parentBuilder.userTask(node.getKey());
			taskNodes.add((TaskNode) node);
		} else if (node.getNodeType().equals(NodeType.Node)) {
			currentBuilder = parentBuilder.serviceTask(node.getKey());
		} else if (node.getNodeType().equals(NodeType.EndState)) {
			currentBuilder = parentBuilder.endEvent(node.getKey());
		} else if (node instanceof ProcessState) {
			currentBuilder = parentBuilder.subProcess(node.getKey());
		} else if (node instanceof InfoxMailNode) {
			currentBuilder = parentBuilder.serviceTask(node.getKey());
		} else if (node.getNodeType().equals(NodeType.Fork)) {
			currentBuilder = parentBuilder.parallelGateway(node.getKey()).gatewayDirection(GatewayDirection.Diverging);
		} else if (node.getNodeType().equals(NodeType.Join)) {
			currentBuilder = parentBuilder.parallelGateway(node.getKey()).gatewayDirection(GatewayDirection.Converging);
		}
		
		currentBuilder.name(node.getName());
		
		if (node.getLeavingTransitions() != null) {
			for (Transition transition : node.getLeavingTransitions()) {
				Node next = transition.getTo();
				if (next != null) {
					if (!markedNodes.containsKey(next.getKey())) {
						visit(next, currentBuilder, transition, markedNodes);
					} else {
						currentBuilder.sequenceFlowId(transition.getKey()).condition(transition.getName(), "").connectTo(next.getKey());
					}
				}
			}
		}
	}
	
	private void resolveLanes(BpmnModelInstance modelInstance, String processId) {
		Process process = modelInstance.getModelElementById(processId);
		LaneSet laneSet;
		if (process.getLaneSets().isEmpty()) {
			laneSet = modelInstance.newInstance(LaneSet.class);
			process.getLaneSets().add(laneSet);
		} else {
			laneSet = process.getLaneSets().iterator().next();
		}
		Collaboration collaboration = (Collaboration) modelInstance.getDefinitions().getUniqueChildElementByType(Collaboration.class);
		if (collaboration == null) {
			collaboration = modelInstance.newInstance(Collaboration.class);
			collaboration.setId(BpmUtil.generateKey());
			Participant participant = modelInstance.newInstance(Participant.class);
			participant.setId(BpmUtil.generateKey());
			participant.setProcess((Process) modelInstance.getDefinitions().getModelInstance().getModelElementById(processId));
			collaboration.addChildElement(participant);
			modelInstance.getDefinitions().addChildElement(collaboration);
		}
		for (Swimlane swimlane : processDefinition.getTaskMgmtDefinition().getSwimlanes().values()) {
			Lane lane = modelInstance.newInstance(Lane.class);
			lane.setName(swimlane.getName());
			lane.setId(swimlane.getKey());
			laneSet.getLanes().add(lane);
		}
		for (TaskNode taskNode : taskNodes) {
			Swimlane swimlane = taskNode.getTask(taskNode.getName()).getSwimlane();
			if (swimlane != null) {
				UserTask userTask = modelInstance.getModelElementById(taskNode.getKey());
				Lane lane = modelInstance.getModelElementById(swimlane.getKey());
				lane.getFlowNodeRefs().add(userTask);
				nodesToLanes.put(userTask.getId(), lane);
			}
		}
	}
	
	private void resolveDecisionExpressions(BpmnModelInstance modelInstance) {
		Collection<ExclusiveGateway> gateways = modelInstance.getModelElementsByType(ExclusiveGateway.class);
		for (ExclusiveGateway gateway : gateways) {
			Decision decision = (Decision) processDefinition.getNode(gateway.getId());
			if (decision.getLeavingTransitions() != null) {
				gateway.setDefault((SequenceFlow) modelInstance.getModelElementById(decision.getDefaultLeavingTransition().getKey()));
				for (Transition transition : decision.getLeavingTransitions()) {
					SequenceFlow sequenceFlow = modelInstance.getModelElementById(transition.getKey());
					if (!Strings.isNullOrEmpty(transition.getCondition())) {
						ConditionExpression expression = modelInstance.newInstance(ConditionExpression.class);
						expression.setTextContent(transition.getCondition());
						sequenceFlow.setConditionExpression(expression);
					} else {
						sequenceFlow.removeConditionExpression();
					}
				}
			}
		}
	}
	
	private void pseudoTopologicalSort(FlowNode node, Map<String, Boolean> markedNodes) {
		if (markedNodes.containsKey(node.getId())) {
			return;
		}
		markedNodes.put(node.getId(), true);
		for (SequenceFlow sequenceFlow : node.getOutgoing()) {
			pseudoTopologicalSort(sequenceFlow.getTarget(), markedNodes);
		}
		orderedNodes.add(0, node);
	}
	
	private Lane findBestLane(FlowNode node) {
		int index = orderedNodes.indexOf(node);
		FlowNodeLaneBean leftBean = new FlowNodeLaneBean();
		for (int i = index; i >= 0; i--) {
			FlowNode current = orderedNodes.get(i);
			if (nodesToLanes.containsKey(current.getId())) {
				leftBean.lane = nodesToLanes.get(current.getId());
				break;
			}
			leftBean.distance++;
		}
		
		FlowNodeLaneBean rightBean = new FlowNodeLaneBean();
		for (int i = index; i < orderedNodes.size(); i++) {
			FlowNode current = orderedNodes.get(i);
			if (nodesToLanes.containsKey(current.getId())) {
				rightBean.lane = nodesToLanes.get(current.getId());
				break;
			}
			rightBean.distance++;
		}
		
		if (leftBean.lane != null && rightBean.lane != null) {
			if (leftBean.distance <= rightBean.distance) {
				return leftBean.lane;
			} else {
				return rightBean.lane;
			}
		} else if (leftBean.lane != null) {
			return leftBean.lane;
		} else if (rightBean.lane != null) {
			return rightBean.lane;
		} else {
			return node.getModelInstance().getModelElementsByType(Lane.class).iterator().next();
		}
	}
	
	private void createDiagram(BpmnModelInstance modelInstance) {
		if (!modelInstance.getDefinitions().getBpmDiagrams().isEmpty()) {
			return;
		}
		
		pseudoTopologicalSort(startEvent, new HashMap<String, Boolean>());
		
		BpmnDiagram diagram = modelInstance.newInstance(BpmnDiagram.class);
		modelInstance.getDefinitions().getBpmDiagrams().add(diagram);
		Collaboration collaboration = modelInstance.getModelElementsByType(Collaboration.class).iterator().next();
		BpmnPlane plane = modelInstance.newInstance(BpmnPlane.class);
		plane.setBpmnElement(collaboration);
		diagram.setBpmnPlane(plane);
		
		Map<String, Integer> totalNodes = new HashMap<>();
		for (FlowNode flowNode : orderedNodes) {		
			createNodeShape(flowNode, plane, totalNodes);
		}
		
		Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);
		Participant participant = collaboration.getParticipants().iterator().next();
		BpmnShape participantShape = modelInstance.newInstance(BpmnShape.class);
		participantShape.setBpmnElement(participant);
		plane.addChildElement(participantShape);
		Bounds participantBounds = modelInstance.newInstance(Bounds.class);
		participantShape.setBounds(participantBounds);
		participantBounds.setX(PARTICIPANT_X);
		participantBounds.setY(PARTICIPANT_Y);
		participantBounds.setWidth(maxWidth + PARTICIPANT_LANE_OFFSET);
		participantBounds.setHeight(LANE_HEIGHT * lanes.size());
		
		for (Lane lane : lanes) {
			if (!totalNodes.containsKey(lane.getId()) || totalNodes.get(lane.getId()) == 0) {
				createLaneShape(lane, plane);
			}
			laneShapes.get(lane.getId()).getBounds().setWidth(participantBounds.getWidth() - PARTICIPANT_LANE_OFFSET);
		}
		
		Collection<FlowNode> nodes = modelInstance.getModelElementsByType(FlowNode.class);
		for (FlowNode node : nodes) {
			if (nodesToLanes.containsKey(node.getId())) {
				for (SequenceFlow sequenceFlow : node.getOutgoing()) {
					if (nodesToLanes.containsKey(sequenceFlow.getTarget().getId())) {
						BpmnEdge edge = modelInstance.newInstance(BpmnEdge.class);
						plane.addChildElement(edge);
						edge.setBpmnElement(sequenceFlow);
						Waypoint source = modelInstance.newInstance(Waypoint.class);
						Waypoint target = modelInstance.newInstance(Waypoint.class);
						edge.addChildElement(source);
						edge.addChildElement(target);
						Bounds sourceBounds = ((BpmnShape) sequenceFlow.getSource().getDiagramElement()).getBounds();
						Bounds targetBounds = ((BpmnShape) sequenceFlow.getTarget().getDiagramElement()).getBounds();
						
						if (sourceBounds.getX() > targetBounds.getX()) {
							source.setX(sourceBounds.getX());
							target.setX(targetBounds.getX() + targetBounds.getWidth());
						} else  {
							source.setX(sourceBounds.getX() + sourceBounds.getWidth());
							target.setX(targetBounds.getX());
						}
						source.setY(sourceBounds.getY() + sourceBounds.getHeight() / 2);
						target.setY(targetBounds.getY() + targetBounds.getHeight() / 2);
					}
				}
			}
		}
	}
	
	private void createNodeShape(FlowNode flowNode, BpmnPlane plane, Map<String, Integer> totalNodesForLane) {
		ModelInstance modelInstance = flowNode.getModelInstance();
		BpmnShape shape = modelInstance.newInstance(BpmnShape.class);
		plane.addChildElement(shape);
		shape.setBpmnElement(flowNode);
		Bounds bounds = modelInstance.newInstance(Bounds.class);
		shape.setBounds(bounds);
		bounds.setWidth(flowNode instanceof Activity ? ACTIVITY_WIDTH : GENERAL_FLOWNODE_WIDTH);
		bounds.setHeight(flowNode instanceof Activity ? ACTIVITY_HEIGHT : GENERAL_FLOWNODE_HEIGHT);
		
		Lane lane = nodesToLanes.get(flowNode.getId());
		if (lane == null) {
			lane = findBestLane(flowNode);
			nodesToLanes.put(flowNode.getId(), lane);
		}
		int totalNodes = 1;
		if (!totalNodesForLane.containsKey(lane.getId())) {
			totalNodesForLane.put(lane.getId(), totalNodes);
		} else {
			totalNodes = totalNodesForLane.get(lane.getId()) + 1;
			totalNodesForLane.put(lane.getId(), totalNodes);
		}
		
		BpmnShape lastLane = lastLaneShape;
		BpmnShape laneShape = createLaneShape(lane, plane);
		bounds.setX(laneShape.getBounds().getX() + FLOW_NODE_X_OFFSET + (FLOW_NODE_INTER_DISTANCE * (totalNodes - 1)));
		bounds.setY(laneShape.getBounds().getY() + FLOW_NODE_Y_OFFSET);
		if (lastLane != null && !lastLane.equals(laneShape)) {
			bounds.setX(bounds.getX() + maxXFromPreviousLane);
		} else {
			maxXFromPreviousLane = bounds.getX();
		}
		
		if (bounds.getX() > maxWidth) {
			maxWidth = bounds.getX();
		}
		
	}
	
	private BpmnShape createLaneShape(Lane lane, BpmnPlane plane) {
		BpmnShape laneShape = laneShapes.get(lane.getId());
		if (laneShape != null) {
			return laneShape;
		}
		BpmnModelInstance modelInstance = (BpmnModelInstance) lane.getModelInstance();
		laneShape = modelInstance.newInstance(BpmnShape.class);
		plane.addChildElement(laneShape);
		laneShape.setBpmnElement(lane);
		Bounds laneBounds = modelInstance.newInstance(Bounds.class);
		laneShape.setBounds(laneBounds);
		laneBounds.setX(PARTICIPANT_X + PARTICIPANT_LANE_OFFSET);
		laneBounds.setY(lastLaneShape == null ? PARTICIPANT_Y : lastLaneShape.getBounds().getY() + LANE_HEIGHT);
		laneBounds.setHeight(LANE_HEIGHT);
		laneShapes.put(lane.getId(), laneShape);
		lastLaneShape = laneShape;
		return laneShape;
	}
	
	private static class FlowNodeLaneBean {
		private int distance = 0;
		private Lane lane;
	}
}