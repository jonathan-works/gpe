package br.com.infox.epp.modeler.converter;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;
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
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.Fork;
import org.jbpm.graph.node.Join;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskMgmtDefinition;

import com.google.common.base.Strings;

import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.ibpm.node.DecisionNode;

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
		resolveLanes(process, processDefinition);
		createDefaultEvents(processDefinition);
		return processDefinition;
	}
	
	public ProcessDefinition convert(String bpmnXml) {
		return convert(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
	}

	private void visit(FlowNode root, ProcessDefinition processDefinition, Transition origin, Map<String, Boolean> markedNodes) {
		markedNodes.put(root.getId(), true);
		Node node = processDefinition.getNode(getIdentification(root));
		if (node == null) {
			node = createNode(root, processDefinition, node);
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
			Transition transition = new Transition(getIdentification(sequenceFlow));
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
		if (node instanceof DecisionNode) {
			setExpression((DecisionNode) node, (ExclusiveGateway) root);
		}
	}

	private Node createNode(FlowNode flowNode, ProcessDefinition processDefinition, Node node) {
		if (flowNode.getElementType().getTypeName().equals("startEvent")) {
			node = new StartState(getIdentification(flowNode));
		} else if (flowNode.getElementType().getTypeName().equals("endEvent")) {
			node = new EndState(getIdentification(flowNode));
		} else if (flowNode.getElementType().getTypeName().equals("userTask")) {
			node = new TaskNodeFactory().createTaskNode((UserTask) flowNode, processDefinition);
		} else if (flowNode.getElementType().getTypeName().equals("serviceTask")) {
			node = new Node(getIdentification(flowNode));
		} else if (flowNode.getElementType().getTypeName().equals("exclusiveGateway")) {
			node = new DecisionNode(getIdentification(flowNode));
		} else if (flowNode.getElementType().getTypeName().equals("subProcess")) {
			if (flowNode.getName() == null) {
				throw new BpmnJpdlConverterException("O subprocesso deve possuir um nome");
			}
			node = new ProcessState(flowNode.getName());
			ReflectionsUtil.setValue(node, "subProcessName", flowNode.getName());
		} else if (flowNode.getElementType().getTypeName().equals("intermediateThrowEvent")) {
			node = new Node(getIdentification(flowNode));
		} else if (flowNode.getElementType().getTypeName().equals("parallelGateway")) {
			GatewayDirection direction = ((ParallelGateway) flowNode).getGatewayDirection();
			if (direction == GatewayDirection.Diverging) {
				node = new Fork(getIdentification(flowNode));
			} else if (direction == GatewayDirection.Converging) {
				node = new Join(getIdentification(flowNode));
			}
		}
		node.setKey(flowNode.getId());
		return node;
	}
	
	private String getIdentification(FlowElement element) {
		return element.getName() != null ? element.getName() : element.getId();
	}
	
	private void setExpression(DecisionNode decisionNode, ExclusiveGateway decisionBpmn) {
		if (decisionBpmn.getDefault() == null) {
			throw new BpmnJpdlConverterException("Uma transição padrão deve estar configurada no gateway '" + getIdentification(decisionBpmn) + "'");
		}
		String defaultTransition = decisionBpmn.getDefault().getId();
		StringBuilder sb = new StringBuilder("#{");
		for (SequenceFlow sequenceFlow : decisionBpmn.getOutgoing()) {
			if (sequenceFlow.equals(decisionBpmn.getDefault())) {
				continue;
			}
			if (sequenceFlow.getConditionExpression() == null) {
				String template = "A transição ''{0}'' ({1} -> {2}) deve ter uma condição configurada";
				throw new BpmnJpdlConverterException(MessageFormat.format(template, getIdentification(sequenceFlow), getIdentification(sequenceFlow.getSource()), getIdentification(sequenceFlow.getTarget())));
			}
			String condition = sequenceFlow.getConditionExpression().getTextContent();
			if (!Strings.isNullOrEmpty(condition)) {
				condition = condition.substring(2, condition.length() - 1);
				sb.append(condition);
				sb.append(" ? '");
				sb.append(sequenceFlow.getId());
				sb.append("' : ");
			}
		}
		sb.append("'" + defaultTransition + "'}");
		decisionNode.setDecisionExpression(sb.toString());
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
					if (flowNode.getElementType().getTypeName().equals("userTask")) {
						TaskNode taskNode = (TaskNode) processDefinition.getNode(getIdentification(flowNode));
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
