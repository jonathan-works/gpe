package br.com.infox.epp.modeler.converter;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.instance.Collaboration;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;

import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.node.DecisionNode;
import br.com.infox.ibpm.node.InfoxMailNode;

public class JpdlBpmnConverter {
	
	private Map<String, Boolean> marked;
	private List<DecisionNode> decisions;
	private List<TaskNode> taskNodes;
	
	public BpmnModelInstance convert(String processDefinitionXml) {
		clear();
		ProcessDefinition processDefinition = new InfoxJpdlXmlReader(new StringReader(processDefinitionXml)).readProcessDefinition();
		ProcessBuilder builder = Bpmn.createProcess(processDefinition.getKey()).name(processDefinition.getName());
		AbstractFlowNodeBuilder<?, ?> flowNodeBuilder = builder.startEvent(processDefinition.getStartState().getKey());

		for (Node node : processDefinition.getNodes()) {
			if (!marked.containsKey(node.getKey())) {
				visit(node, flowNodeBuilder, null);
			}
		}
		
		BpmnModelInstance modelInstance = builder.done();
		resolveDecisionExpressions(modelInstance);
		resolveLanes(modelInstance, processDefinition.getKey());
		return modelInstance;
	}
	
	private void visit(Node node, AbstractFlowNodeBuilder<?, ?> parentBuilder, Transition originTransition) {
		marked.put(node.getKey(), true);
		AbstractFlowNodeBuilder<?, ?> currentBuilder = null;
		
		if (originTransition != null) {
			parentBuilder.sequenceFlowId(originTransition.getKey()).condition(originTransition.getName(), "");
		}
		
		if (node instanceof StartState) {
			currentBuilder = parentBuilder;
		} else if (node instanceof DecisionNode) {
			currentBuilder = parentBuilder.exclusiveGateway(node.getKey());
			decisions.add((DecisionNode) node);
		} else if (node.getNodeType().equals(NodeType.Task)) {
			currentBuilder = parentBuilder.userTask(node.getKey());
			taskNodes.add((TaskNode) node);
		} else if (node.getNodeType().equals(NodeType.Node)) {
			currentBuilder = parentBuilder.serviceTask(node.getKey());
		} else if (node instanceof EndState) {
			currentBuilder = parentBuilder.endEvent(node.getKey());
		} else if (node instanceof ProcessState) {
			currentBuilder = parentBuilder.subProcess(node.getKey());
		} else if (node instanceof InfoxMailNode) {
			currentBuilder = parentBuilder.serviceTask(node.getKey());
		}
		
		if (node.getLeavingTransitions() != null) {
			for (Transition transition : node.getLeavingTransitions()) {
				Node next = transition.getTo();
				if (next != null) {
					if (!marked.containsKey(next.getKey())) {
						visit(next, currentBuilder, transition);
					} else {
						currentBuilder.sequenceFlowId(transition.getKey()).condition(transition.getName(), "").connectTo(next.getKey());
					}
				}
			}
		}

		currentBuilder.done();
	}
	
	private void resolveDecisionExpressions(BpmnModelInstance modelInstance) {
		for (DecisionNode node : decisions) {
			String decisionExpression = node.getDecisionExpression();
			if (decisionExpression == null) {
				continue;
			}
			ExclusiveGateway gateway = modelInstance.getModelElementById(node.getKey());
			decisionExpression = decisionExpression.substring(2, decisionExpression.length() - 1);
			String[] conditionSequencePairs = decisionExpression.split(":");
			for (String conditionSequencePair : conditionSequencePairs) {
				String[] conditionSequencePairsSplitted = conditionSequencePair.split("\\?");
				if (conditionSequencePairsSplitted.length == 1) {
					String unparsedSequenceId = conditionSequencePairsSplitted[0].trim();
					String sequenceId = unparsedSequenceId.substring(1, unparsedSequenceId.length() - 1);
					SequenceFlow sequenceFlow = findSequenceFlow(sequenceId, modelInstance);
					if (sequenceFlow != null) {
						gateway.setDefault(sequenceFlow);
					}
				} else {
					String condition = conditionSequencePairsSplitted[0].trim();
					String unparsedSequenceId = conditionSequencePairsSplitted[1].trim();
					String sequenceId = unparsedSequenceId.substring(1, unparsedSequenceId.length() - 1);
					SequenceFlow sequenceFlow = findSequenceFlow(sequenceId, modelInstance);
					ConditionExpression conditionExpression = modelInstance.newInstance(ConditionExpression.class);
					conditionExpression.setTextContent("#{" + condition + "}");
					sequenceFlow.setConditionExpression(conditionExpression);
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
			collaboration.setId("key_" + UUID.randomUUID());
			Participant participant = modelInstance.newInstance(Participant.class);
			participant.setId("key_" + UUID.randomUUID());
			participant.setProcess((Process) modelInstance.getDefinitions().getModelInstance().getModelElementById(processId));
			collaboration.addChildElement(participant);
			modelInstance.getDefinitions().addChildElement(collaboration);
		}
		for (TaskNode taskNode : taskNodes) {
			Swimlane swimlane = taskNode.getTask(taskNode.getName()).getSwimlane();
			if (swimlane != null) {
				UserTask userTask = modelInstance.getModelElementById(taskNode.getKey());
				Lane lane = modelInstance.getModelElementById(swimlane.getKey());
				if (lane == null) {
					lane = modelInstance.newInstance(Lane.class);
					lane.setName(swimlane.getName());
					lane.setId(swimlane.getKey());
					laneSet.getLanes().add(lane);
				}
				lane.getFlowNodeRefs().add(userTask);
			}
		}
	}
	
	private SequenceFlow findSequenceFlow(String sequenceFlowIdentification, BpmnModelInstance modelInstance) {
		SequenceFlow sequenceFlow = modelInstance.getModelElementById(sequenceFlowIdentification);
		if (sequenceFlow != null) {
			return sequenceFlow;
		}
		Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
		for (SequenceFlow flow : sequenceFlows) {
			if (sequenceFlowIdentification.equals(flow.getName())) {
				return flow;
			}
		}
		return null;
	}
	
	private void clear() {
		decisions = new LinkedList<>();
		marked = new HashMap<>();
		taskNodes = new LinkedList<>();
	}
	
	public static void main(String[] args) {
		InputStream is = JpdlBpmnConverter.class.getResourceAsStream("teste.xml");
		Scanner s = new Scanner(is);
		StringBuilder sb = new StringBuilder();
		while (s.hasNextLine()) {
			sb.append(s.nextLine());
			sb.append("\n");
		}
		s.close();
		new JpdlBpmnConverter().convert(sb.toString());
	}
}
