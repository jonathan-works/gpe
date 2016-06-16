package br.com.infox.epp.modeler.converter;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.DiagramElement;
import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.Decision;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskMgmtDefinition;

import com.google.common.base.Strings;

public class BpmnJpdlConverter {
	
	public ProcessDefinition convert(InputStream bpmnXmlStream) {
		ProcessDefinition processDefinition = new ProcessDefinition();
		BpmnModelInstance model = Bpmn.readModelFromStream(bpmnXmlStream);
		Definitions definitions = model.getDefinitions();
		Process process = null;
		StartEvent startEvent = null;
		for (Process p : definitions.getChildElementsByType(Process.class)) {
			startEvent = (StartEvent) p.getUniqueChildElementByType(StartEvent.class);
			if (startEvent != null) {
				process = p;
				break;
			}
		}
		if (process.getName() == null) {
			throw new BpmnJpdlConverterException("A definição de processo deve ter um nome");
		}
		processDefinition.setName(process.getName());
		processDefinition.addDefinition(new TaskMgmtDefinition());
		
		visit(startEvent, processDefinition, null, new HashMap<String, Boolean>());
		resolveDecisionExpressions(model, processDefinition);
		resolveLanes(process, processDefinition);
		createDefaultEvents(processDefinition);
		return processDefinition;
	}
	
	public ProcessDefinition convert(String bpmnXml) {
		return convert(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
	}

	private void visit(FlowNode root, ProcessDefinition processDefinition, Transition origin, Map<String, Boolean> markedNodes) {
		markedNodes.put(root.getId(), true);
		Node node = processDefinition.getNode(NodeFactory.getLabel(root));
		if (node == null) {
			node = NodeFactory.createNode(root, processDefinition);
			if (node == null) {
				throw new BpmnJpdlConverterException("Elemento desconhecido: " + root.getElementType().getTypeName());
			}
			processDefinition.addNode(node);
		}
		
		if (origin != null) {
			origin.setTo(node);
			node.addArrivingTransition(origin);
		}
		
		for (SequenceFlow sequenceFlow : root.getOutgoing()) {
			Transition transition = new Transition(NodeFactory.getLabel(sequenceFlow));
			transition.setKey(sequenceFlow.getId());
			node.addLeavingTransition(transition);
			if (!markedNodes.containsKey(sequenceFlow.getTarget().getId())) {
				visit(sequenceFlow.getTarget(), processDefinition, transition, markedNodes);
			} else {
				Node to = processDefinition.getNode(sequenceFlow.getTarget().getId());
				transition.setTo(to);
				to.addArrivingTransition(transition);
			}
		}
	}

	private void resolveDecisionExpressions(BpmnModelInstance modelInstance, ProcessDefinition processDefinition) {
		Collection<ExclusiveGateway> gateways = modelInstance.getModelElementsByType(ExclusiveGateway.class);
		for (ExclusiveGateway gateway : gateways) {
			Decision decision = (Decision) processDefinition.getNode(gateway.getId());
			if (decision.getLeavingTransitions() != null) {
				SequenceFlow defaultSequenceFlow = gateway.getDefault();
				for (Transition transition : decision.getLeavingTransitions()) {
					if (defaultSequenceFlow != null && defaultSequenceFlow.getId().equals(transition.getKey())) {
						transition.setCondition(null);
					} else {
						SequenceFlow sequenceFlow = modelInstance.getModelElementById(transition.getKey());
						if (sequenceFlow.getConditionExpression() != null && !Strings.isNullOrEmpty(sequenceFlow.getConditionExpression().getTextContent())) {
							transition.setCondition(sequenceFlow.getConditionExpression().getTextContent());
						} else {
							transition.setCondition(null);
						}
					}
				}
			}
		}
	}
	
	private void resolveLanes(Process process, ProcessDefinition processDefinition) {
		if (!process.getLaneSets().isEmpty()) {
			LaneSet laneSet = process.getLaneSets().iterator().next();
			for (Lane lane : laneSet.getLanes()) {
				Swimlane swimlane = new Swimlane(lane.getName());
				swimlane.setKey(lane.getId());
				processDefinition.getTaskMgmtDefinition().addSwimlane(swimlane);
				Collection<FlowNode> flowNodes = lane.getFlowNodeRefs();
				if (flowNodes.isEmpty()) {
					flowNodes = getNodesInLaneGraphically(lane, (Definitions) process.getParentElement());
				}
				for (FlowNode flowNode : flowNodes) {
					if (flowNode.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_USER_TASK)) {
						TaskNode taskNode = (TaskNode) processDefinition.getNode(NodeFactory.getLabel(flowNode));
						for (Object task : taskNode.getTasks()) {
							((Task) task).setSwimlane(swimlane);
						}
					}
				}
			}
		}
	}

	private Collection<FlowNode> getNodesInLaneGraphically(Lane lane, Definitions definitions) {
		List<FlowNode> flowNodes = new ArrayList<>();
		BpmnDiagram diagram = !definitions.getBpmDiagrams().isEmpty() ? definitions.getBpmDiagrams().iterator().next() : null;
		if (diagram != null) {
			BpmnPlane plane = diagram.getBpmnPlane();
			BpmnShape laneShape = getShapeForElement(lane.getId(), diagram);
			if (laneShape != null) {
				Bounds laneBounds = laneShape.getBounds();
				Rectangle laneRectangle = new Rectangle(laneBounds.getX().intValue(), laneBounds.getY().intValue(), 
						laneBounds.getWidth().intValue(), laneBounds.getHeight().intValue());
				for (DiagramElement diagramElement : plane.getDiagramElements()) {
					if (!(diagramElement instanceof BpmnShape) || diagramElement.equals(laneShape)) {
						continue;
					}
					BpmnShape shape = (BpmnShape) diagramElement;
					Bounds shapeBounds = shape.getBounds();
					Rectangle shapeRectangle = new Rectangle(shapeBounds.getX().intValue(), shapeBounds.getY().intValue(), 
							shapeBounds.getWidth().intValue(), shapeBounds.getHeight().intValue());
					if (laneRectangle.contains(shapeRectangle) && shape.getBpmnElement() instanceof FlowNode) {
						flowNodes.add((FlowNode) shape.getBpmnElement());
					}
				}
			}
		}
		return flowNodes;
	}
	
	private BpmnShape getShapeForElement(String elementId, BpmnDiagram diagram) {
		BpmnPlane plane = diagram.getBpmnPlane();
		for (DiagramElement diagramElement : plane.getDiagramElements()) {
			if (!(diagramElement instanceof BpmnShape)) {
				continue;
			}
			BpmnShape shape = (BpmnShape) diagramElement;
			if (shape.getBpmnElement().getId().equals(elementId)) {
				return shape;
			}
		}
		return null;
	}
	
	private void createDefaultEvents(ProcessDefinition processDefinition) {
		String[] events = {
			Event.EVENTTYPE_SUPERSTATE_ENTER, Event.EVENTTYPE_PROCESS_START, Event.EVENTTYPE_BEFORE_SIGNAL, Event.EVENTTYPE_TASK_END,
			Event.EVENTTYPE_SUBPROCESS_CREATED, Event.EVENTTYPE_TASK_CREATE, Event.EVENTTYPE_TRANSITION, Event.EVENTTYPE_TASK_ASSIGN,
			Event.EVENTTYPE_AFTER_SIGNAL, Event.EVENTTYPE_TIMER, Event.EVENTTYPE_TASK_START, Event.EVENTTYPE_SUBPROCESS_END,
			Event.EVENTTYPE_NODE_LEAVE, Event.EVENTTYPE_PROCESS_END, Event.EVENTTYPE_SUPERSTATE_LEAVE, Event.EVENTTYPE_NODE_ENTER
		};
		
		for (String eventType : events) {
			Event event = new Event(eventType);
			Script script = new Script();
			script.setExpression("br.com.infox.jbpm.event.JbpmEvents.raiseEvent(executionContext)");
			event.addAction(script);
			processDefinition.addEvent(event);
		}
	}
}
