package br.com.infox.epp.modeler;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.Collaboration;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.SubProcess;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.camunda.bpm.model.xml.ModelInstance;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;

import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.node.InfoxMailNode;
import br.com.infox.ibpm.util.BpmUtil;
import br.com.infox.seam.exception.BusinessRollbackException;

public class JpdlBpmnConverter {
	
	private static final int FLOW_NODE_INTER_DISTANCE = 200;
	private static final int LANE_HEIGHT = 200;
	
	private ProcessDefinition processDefinition;
	private StartEvent startEvent;
	private List<Transition> jpdlTransitions;
	private Map<String, Lane> nodesToLanes = new HashMap<>();
	private double maxWidth = 0d;
	private List<FlowNode> orderedNodes = new ArrayList<>();
	private BpmnShape lastLaneShape;
	private Map<String, BpmnShape> laneShapes = new HashMap<>();
	private double maxXFromPreviousLane = 0d;
	
	public String convert(String processDefinitionXml) {
		processDefinition = new InfoxJpdlXmlReader(new StringReader(processDefinitionXml)).readProcessDefinition();
		BpmnModelInstance bpmnModel = Bpmn.createProcess(processDefinition.getKey()).name(processDefinition.getName()).done();

		resolveLanes(bpmnModel, processDefinition.getKey());
		jpdlTransitions = new ArrayList<>();
		Process process = bpmnModel.getModelElementById(processDefinition.getKey());
		createFlowElements(process);
		resolveTransitions(process);
		
		startEvent = bpmnModel.getModelElementById(processDefinition.getStartState().getKey());
		createDiagram(bpmnModel);
		
		String bpmn = Bpmn.convertToString(bpmnModel);
		return bpmn;
	}
	
	private void resolveTransitions(Process process) {
		ModelInstance bpmnModel = process.getModelInstance();
		for (Transition transition : jpdlTransitions) {
			SequenceFlow sequenceFlow = bpmnModel.newInstance(SequenceFlow.class);
			FlowNode source = transition.getFrom() != null ? (FlowNode) bpmnModel.getModelElementById(transition.getFrom().getKey()) : null;
			FlowNode target = transition.getTo() != null ? (FlowNode) bpmnModel.getModelElementById(transition.getTo().getKey()) : null;
			
			sequenceFlow.setName(transition.getName());
			sequenceFlow.setId(transition.getKey());
			sequenceFlow.setSource(source);
			sequenceFlow.setTarget(target);
			process.addChildElement(sequenceFlow);
			
			if (source != null) {
				source.getOutgoing().add(sequenceFlow);
			}
			if (target != null) {
				target.getIncoming().add(sequenceFlow);
			}
		}
	}

	private void createFlowElements(Process process) {
		ModelInstance modelInstance = process.getModelInstance();
		for (Node node : processDefinition.getNodes()) {
			FlowNode flowNode;
			if (node.getNodeType().equals(Node.NodeType.Task)) {
				flowNode = modelInstance.newInstance(UserTask.class);
			} else if (node.getNodeType().equals(Node.NodeType.Decision)) {
				flowNode = modelInstance.newInstance(ExclusiveGateway.class);
			} else if (node.getNodeType().equals(Node.NodeType.Fork)) {
				flowNode = modelInstance.newInstance(ParallelGateway.class);
				((ParallelGateway) flowNode).setGatewayDirection(GatewayDirection.Diverging);
			} else if (node.getNodeType().equals(Node.NodeType.Join)) {
				flowNode = modelInstance.newInstance(ParallelGateway.class);
				((ParallelGateway) flowNode).setGatewayDirection(GatewayDirection.Converging);
			} else if (node.getNodeType().equals(Node.NodeType.StartState)) {
				flowNode = modelInstance.newInstance(StartEvent.class);
			} else if (node.getNodeType().equals(Node.NodeType.EndState)) {
				flowNode = modelInstance.newInstance(EndEvent.class);
			} else if (node instanceof ProcessState) {
				flowNode = modelInstance.newInstance(SubProcess.class);
			} else if (node instanceof InfoxMailNode) {
				flowNode = modelInstance.newInstance(SendTask.class);
			} else if (node.getNodeType().equals(Node.NodeType.Node)) { // Deve ser o último if pois outros nós, como o subprocesso, também têm tipo Node
				flowNode = modelInstance.newInstance(ServiceTask.class);
			} else {
				throw new BusinessRollbackException("Tipo de nó desconhecido: " + node.getClass().getCanonicalName());
			}

			flowNode.setId(node.getKey());
			flowNode.setName(node.getName());
			process.addChildElement(flowNode);
			
			if (node.getNodeType().equals(Node.NodeType.Task)) {
				TaskNode taskNode = (TaskNode) node;
				Swimlane swimlane = taskNode.getTasks().iterator().next().getSwimlane();
				Lane lane = modelInstance.getModelElementById(swimlane.getKey());
				lane.getFlowNodeRefs().add(flowNode);
				nodesToLanes.put(node.getKey(), lane);
			}
			
			if (node.getLeavingTransitions() != null) {
				for (Transition transition : node.getLeavingTransitions()) {
					jpdlTransitions.add(transition);
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
			collaboration.setClosed(false);
			Participant participant = modelInstance.newInstance(Participant.class);
			participant.setId(BpmUtil.generateKey());
			participant.setProcess((Process) modelInstance.getDefinitions().getModelInstance().getModelElementById(processId));
			participant.setName(process.getName());
			collaboration.getParticipants().add(participant);
			modelInstance.getDefinitions().addChildElement(collaboration);
		}
		for (Swimlane swimlane : processDefinition.getTaskMgmtDefinition().getSwimlanes().values()) {
			Lane lane = modelInstance.newInstance(Lane.class);
			lane.setName(swimlane.getName());
			lane.setId(swimlane.getKey());
			laneSet.getLanes().add(lane);
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

		for (FlowNode flowNode : modelInstance.getModelElementsByType(FlowNode.class)) {
			if (!orderedNodes.contains(flowNode)) {
				orderedNodes.add(flowNode);
			}
		}
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
		participantBounds.setX(DiagramUtil.PARTICIPANT_X);
		participantBounds.setY(DiagramUtil.PARTICIPANT_Y);
		participantBounds.setWidth(maxWidth + DiagramUtil.PARTICIPANT_LANE_OFFSET);
		participantBounds.setHeight(LANE_HEIGHT * lanes.size());
		
		for (Lane lane : lanes) {
			if (!totalNodes.containsKey(lane.getId()) || totalNodes.get(lane.getId()) == 0) {
				createLaneShape(lane, plane);
			}
			laneShapes.get(lane.getId()).getBounds().setWidth(participantBounds.getWidth() - DiagramUtil.PARTICIPANT_LANE_OFFSET);
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
		bounds.setWidth(flowNode instanceof Activity ? DiagramUtil.ACTIVITY_WIDTH : DiagramUtil.GENERAL_FLOWNODE_WIDTH);
		bounds.setHeight(flowNode instanceof Activity ? DiagramUtil.ACTIVITY_HEIGHT : DiagramUtil.GENERAL_FLOWNODE_HEIGHT);
		
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
		bounds.setX(laneShape.getBounds().getX() + DiagramUtil.FLOW_NODE_X_OFFSET + (FLOW_NODE_INTER_DISTANCE * (totalNodes - 1)));
		bounds.setY(laneShape.getBounds().getY() + DiagramUtil.FLOW_NODE_Y_OFFSET);
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
		laneBounds.setX(DiagramUtil.PARTICIPANT_X + DiagramUtil.PARTICIPANT_LANE_OFFSET);
		laneBounds.setY(lastLaneShape == null ? DiagramUtil.PARTICIPANT_Y : lastLaneShape.getBounds().getY() + LANE_HEIGHT);
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