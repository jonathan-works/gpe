package br.com.infox.epp.modeler.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.def.Swimlane;

public class BpmnJpdlTranslation {
	
	private BpmnModelInstance bpmnModel;
	private ProcessDefinition processDefinition;
	
	private Map<String, Transition> jpdlTransitions;
	private Map<String, Integer> nodeOrder;
	
	private List<FlowNode> newNodes;
	private List<Node> nodesToRemove;
	
	private List<SequenceFlow> newTransitions;
	private List<Transition> transitionsToRemove;
	
	private List<Lane> newLanes;
	private List<Swimlane> swimlanesToRemove;
	
	private Map<String, Lane> nodesToLanes;
	private Map<String, Swimlane> swimlanes;
	
	public BpmnJpdlTranslation(BpmnModelInstance bpmnModel, ProcessDefinition processDefinition) {
		this.bpmnModel = bpmnModel;
		this.processDefinition = processDefinition;
		loadNodes();
		loadTransitions();
		loadLanes();
	}

	private void loadLanes() {
		newLanes = new ArrayList<>();
		swimlanesToRemove = new ArrayList<>();
		nodesToLanes = new HashMap<>();
		swimlanes = new HashMap<>();
		Map<String, Lane> lanes = new HashMap<>();
		
		for (Lane lane : bpmnModel.getModelElementsByType(Lane.class)) {
			lanes.put(lane.getId(), lane);
			for (FlowNode flowNode : lane.getFlowNodeRefs()) {
				nodesToLanes.put(flowNode.getId(), lane);
			}
		}
		if (processDefinition.getTaskMgmtDefinition() != null && processDefinition.getTaskMgmtDefinition().getSwimlanes() != null) {
			for (Swimlane swimlane : processDefinition.getTaskMgmtDefinition().getSwimlanes().values()) {
				swimlanes.put(swimlane.getKey(), swimlane);
			}
		}
		
		for (String laneId : lanes.keySet()) {
			if (!swimlanes.containsKey(laneId)) {
				newLanes.add(lanes.get(laneId));
			}
		}
		for (String swimlaneKey : swimlanes.keySet()) {
			if (!lanes.containsKey(swimlaneKey)) {
				swimlanesToRemove.add(swimlanes.get(swimlaneKey));
			}
		}
	}

	private void loadTransitions() {
		newTransitions = new ArrayList<>();
		transitionsToRemove = new ArrayList<>();
		jpdlTransitions = new HashMap<>();
		Map<String, SequenceFlow> sequenceFlows = new HashMap<>();
		Map<String, Transition> transitions = new HashMap<>();
		
		for (SequenceFlow sequenceFlow : bpmnModel.getModelElementsByType(SequenceFlow.class)) {
			sequenceFlows.put(sequenceFlow.getId(), sequenceFlow);
		}
		if (processDefinition.getNodes() != null) {
			for (Node node : processDefinition.getNodes()) {
				if (node.getLeavingTransitions() != null) {
					for (Transition transition : node.getLeavingTransitions()) {
						transitions.put(transition.getKey(), transition);
						jpdlTransitions.put(transition.getKey(), transition);
					}
				}
			}
		}
		
		for (String sequenceFlowId : sequenceFlows.keySet()) {
			if (!transitions.containsKey(sequenceFlowId)) {
				newTransitions.add(sequenceFlows.get(sequenceFlowId));
			}
		}
		for (String transitionKey : transitions.keySet()) {
			if (!sequenceFlows.containsKey(transitionKey)) {
				transitionsToRemove.add(transitions.get(transitionKey));
			}
		}
	}

	private void loadNodes() {
		newNodes = new ArrayList<>();
		nodesToRemove = new ArrayList<>();
		nodeOrder = new HashMap<>();
		Map<String, FlowNode> flowNodes = new HashMap<>();
		Map<String, Node> nodes = new HashMap<>();
		
		for (FlowNode flowNode : bpmnModel.getModelElementsByType(FlowNode.class)) {
			flowNodes.put(flowNode.getId(), flowNode);
		}
		if (processDefinition.getNodes() != null) {
			int i = 0;
			for (Node node : processDefinition.getNodes()) {
				nodes.put(node.getKey(), node);
				nodeOrder.put(node.getKey(), i++);
			}
		}
		
		for (String flowNodeId : flowNodes.keySet()) {
			if (!nodes.containsKey(flowNodeId)) {
				newNodes.add(flowNodes.get(flowNodeId));
			}
		}
		for (String nodeKey : nodes.keySet()) {
			if (!flowNodes.containsKey(nodeKey)) {
				nodesToRemove.add(nodes.get(nodeKey));
			}
		}
	}
	
	public List<Node> getNodesToRemove() {
		return nodesToRemove;
	}
	
	public List<Transition> getTransitionsToRemove() {
		return transitionsToRemove;
	}
	
	public List<Swimlane> getSwimlanesToRemove() {
		return swimlanesToRemove;
	}
	
	public List<Lane> getNewLanes() {
		return newLanes;
	}
	
	public List<FlowNode> getNewNodes() {
		return newNodes;
	}
	
	public List<SequenceFlow> getNewTransitions() {
		return newTransitions;
	}
	
	public Map<String, Lane> getNodesToLanes() {
		return nodesToLanes;
	}
	
	public Map<String, Swimlane> getSwimlanes() {
		return swimlanes;
	}
	
	public Map<String, Transition> getJpdlTransitions() {
		return jpdlTransitions;
	}
	
	public Map<String, Integer> getNodeOrder() {
		return nodeOrder;
	}
}	
