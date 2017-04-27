package br.com.infox.epp.fluxo.definicao.modeler;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.DataObject;
import org.camunda.bpm.model.bpmn.instance.DataObjectReference;
import org.camunda.bpm.model.bpmn.instance.DataOutputAssociation;
import org.camunda.bpm.model.bpmn.instance.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.SignalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.TimerEventDefinition;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.scheduler.def.CreateTimerAction;

import br.com.infox.ibpm.node.handler.NodeHandler;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.jbpm.event.EventHandler;

public class ConfiguracoesNos {
	
	private static enum Position {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
	}
	
	public static void resolverMarcadoresBpmn(ProcessDefinition processDefinition, BpmnModelInstance bpmnModel) {
		for (Node node : processDefinition.getNodes()) {
			if (node.getNodeType().equals(NodeType.Task)) {
				resolverTimer((TaskNode) node, bpmnModel);
				resolverEvento((TaskNode) node, bpmnModel);
				resolverDocumento((TaskNode) node, bpmnModel);
			}
			
			if (node.getNodeType().equals(NodeType.Task) || node.getNodeType().equals(NodeType.ProcessState)) {
				resolverSinalBoundary(node, bpmnModel);
			} else if (node.getNodeType().equals(NodeType.StartState)) {
				resolverSinalStart((StartState) node, bpmnModel);
			}
		}
	}
	
	private static void resolverDocumento(TaskNode node, BpmnModelInstance bpmnModel) {
		boolean createDataObject = false;
		if (node.getTasks() != null) {
			for (org.jbpm.taskmgmt.def.Task task : node.getTasks()) {
				List<VariableAccess> variables = task.getTaskController() == null ? null : task.getTaskController().getVariableAccesses();
				if (containsVariavelDocumento(variables)) {
					createDataObject = true;
					break;
				}
			}
		}
		
		if (!createDataObject) {
			Event event = node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
			createDataObject = event != null && event.getAction(NodeHandler.GENERATE_DOCUMENTO_ACTION_NAME) != null;
		}

		UserTask task = bpmnModel.getModelElementById(node.getKey());
		DataOutputAssociation dataOutputAssociation = task.getDataOutputAssociations().isEmpty() ? null : task.getDataOutputAssociations().iterator().next();
		
		if (createDataObject && dataOutputAssociation == null) {
			DataObject dataObject = bpmnModel.newInstance(DataObject.class);
			DataObjectReference dataObjectReference = bpmnModel.newInstance(DataObjectReference.class);
			dataOutputAssociation = bpmnModel.newInstance(DataOutputAssociation.class);
			task.getParentElement().addChildElement(dataObjectReference);
			task.getParentElement().addChildElement(dataObject);
			task.getDataOutputAssociations().add(dataOutputAssociation);
			dataOutputAssociation.setTarget(dataObjectReference);
			dataObjectReference.setDataObject(dataObject);
			
			BpmnDiagram diagram = DiagramUtil.getDefaultDiagram(bpmnModel);
			BpmnShape shape = bpmnModel.newInstance(BpmnShape.class);
			shape.setBpmnElement(dataObjectReference);
			diagram.getBpmnPlane().addChildElement(shape);
			
			Bounds bounds = bpmnModel.newInstance(Bounds.class);
			shape.setBounds(bounds);
			bounds.setWidth(36);
			bounds.setHeight(50);
			Bounds taskBounds = task.getDiagramElement().getBounds();
			bounds.setX(taskBounds.getX() - bounds.getWidth() / 2 + taskBounds.getWidth() / 2);
			bounds.setY(taskBounds.getY() + taskBounds.getHeight() + bounds.getHeight());
			
			BpmnEdge edge = bpmnModel.newInstance(BpmnEdge.class);
			edge.setBpmnElement(dataOutputAssociation);
			diagram.getBpmnPlane().addChildElement(edge);
			
			Waypoint waypoint1 = bpmnModel.newInstance(Waypoint.class);
			waypoint1.setX(taskBounds.getX() + taskBounds.getWidth() / 2);
			waypoint1.setY(taskBounds.getY() + taskBounds.getHeight());
			
			Waypoint waypoint2 = bpmnModel.newInstance(Waypoint.class);
			waypoint2.setX(bounds.getX() + bounds.getWidth() / 2);
			waypoint2.setY(bounds.getY());
			
			edge.getWaypoints().add(waypoint1);
			edge.getWaypoints().add(waypoint2);
		} else if (!createDataObject && dataOutputAssociation != null) {
			DataObjectReference reference = (DataObjectReference) dataOutputAssociation.getTarget();
			DataObject object = reference.getDataObject();
			task.removeChildElement(dataOutputAssociation);
			reference.getParentElement().removeChildElement(reference);
			object.getParentElement().removeChildElement(object);
		}
	}

	private static boolean containsVariavelDocumento(List<VariableAccess> variables) {
		if (variables != null) {
			for (VariableAccess variable : variables) {
				VariableType variableType = VariableType.valueOf(variable.getMappedName().split(":")[0]);
				if (variableType == VariableType.EDITOR || variableType == VariableType.FILE) {
					return true;
				}
			}
		}
		return false;
	}

	private static void resolverTimer(TaskNode node, BpmnModelInstance bpmnModel) {
		UserTask userTask = bpmnModel.getModelElementById(node.getKey());
		BoundaryEvent boundaryEvent = getBoundaryEvent(userTask, TimerEventDefinition.class);
		boolean hasTimers = hasTimers(node);
		if (hasTimers && boundaryEvent == null) {
			BoundaryEvent event = createBoundaryEvent(userTask, Position.BOTTOM_RIGHT);
			event.getEventDefinitions().add(bpmnModel.newInstance(TimerEventDefinition.class));
		} else if (!hasTimers && boundaryEvent != null) {
			boundaryEvent.getParentElement().removeChildElement(boundaryEvent);
		}
	}

	private static BoundaryEvent getBoundaryEvent(Activity activity, Class<? extends EventDefinition> eventDefinitionClass) {
		for (BoundaryEvent boundaryEvent : activity.getParentElement().getChildElementsByType(BoundaryEvent.class)) {
			if (boundaryEvent.getAttachedTo().equals(activity)) {
				if (eventDefinitionClass == null && boundaryEvent.getEventDefinitions().isEmpty()) {
					return boundaryEvent;
				} else if (eventDefinitionClass != null) {
					for (EventDefinition eventDefinition : boundaryEvent.getEventDefinitions()) {
						if (eventDefinitionClass.isInstance(eventDefinition)) {
							return boundaryEvent;
						}
					}
				}
			}
		}
		
		return null;
	}

	private static void resolverEvento(TaskNode node, BpmnModelInstance bpmnModel) {
		UserTask userTask = bpmnModel.getModelElementById(node.getKey());
		BoundaryEvent boundaryEvent = getBoundaryEvent(userTask, null);
		boolean hasShowableEvents = hasShowableEvents(node);
		if (hasShowableEvents && boundaryEvent == null) {
			createBoundaryEvent(userTask, Position.BOTTOM_LEFT);
		} else if (!hasShowableEvents && boundaryEvent != null) {
			boundaryEvent.getParentElement().removeChildElement(boundaryEvent);
		}
	}

	private static void resolverSinalBoundary(Node node, BpmnModelInstance bpmnModel) {
		Activity activity = bpmnModel.getModelElementById(node.getKey());
		BoundaryEvent boundaryEvent = getBoundaryEvent(activity, SignalEventDefinition.class);
		Map<String, Event> events = node.getEvents();
		boolean removerBoundaryEvent = boundaryEvent != null;
		
		if (events != null) {
			for (Event event : events.values()) {
				if (event.isListener()) {
					removerBoundaryEvent = false;
					if (boundaryEvent == null) {
						boundaryEvent = createBoundaryEvent(activity, Position.TOP_RIGHT);
						boundaryEvent.getEventDefinitions().add(bpmnModel.newInstance(SignalEventDefinition.class));
					}
					break;
				}
			}
		}
		
		if (removerBoundaryEvent) {
			boundaryEvent.getParentElement().removeChildElement(boundaryEvent);
		}
	}
	
	private static void resolverSinalStart(StartState startState, BpmnModelInstance bpmnModel) {
		StartEvent startEvent = bpmnModel.getModelElementById(startState.getKey());
		SignalEventDefinition signalEventDefinition = null;
		for (EventDefinition eventDefinition : startEvent.getEventDefinitions()) {
			if (eventDefinition.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_SIGNAL_EVENT_DEFINITION)) {
				signalEventDefinition = (SignalEventDefinition) eventDefinition;
				break;
			}
		}
		
		Map<String, Event> events = startState.getEvents();
		boolean removerSignalDefinition = signalEventDefinition != null;
		
		if (events != null) {
			for (Event event : events.values()) {
				if (event.isListener()) {
					removerSignalDefinition = false;
					if (signalEventDefinition == null) {
						startEvent.getEventDefinitions().add(bpmnModel.newInstance(SignalEventDefinition.class));
					}
					break;
				}
			}
		}
		
		if (removerSignalDefinition) {
			startEvent.getEventDefinitions().remove(signalEventDefinition);
		}
	}
	
	private static boolean hasTimers(TaskNode node) {
		if (node.hasEvent(Event.EVENTTYPE_NODE_ENTER)) {
			Event event = node.getEvent(Event.EVENTTYPE_NODE_ENTER);
			if (event.getActions() != null) {
				for (Action action : event.getActions()) {
					if (action instanceof CreateTimerAction) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static boolean hasShowableEvents(TaskNode node) {
		Map<String, Event> events = node.getEvents();
		if (events != null) {
			for (Event event : events.values()) {
				EventHandler handler = new EventHandler(event);
				if (!EventHandler.isIgnoreEvent(event) && handler.getActions() != null && !handler.getActions().isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static BoundaryEvent createBoundaryEvent(Activity activity, Position position) {
		BpmnModelInstance bpmnModel = (BpmnModelInstance) activity.getModelInstance();
		
		BoundaryEvent boundaryEvent = bpmnModel.newInstance(BoundaryEvent.class);
		boundaryEvent.setAttachedTo(activity);
		activity.getParentElement().addChildElement(boundaryEvent);
		
		BpmnDiagram diagram = DiagramUtil.getDefaultDiagram(bpmnModel);
		BpmnShape shape = bpmnModel.newInstance(BpmnShape.class);
		diagram.getBpmnPlane().addChildElement(shape);
		shape.setBpmnElement(boundaryEvent);
		Bounds bounds = bpmnModel.newInstance(Bounds.class);
		shape.setBounds(bounds);
		
		Bounds activityBounds = ((BpmnShape) activity.getDiagramElement()).getBounds();
		bounds.setWidth(36);
		bounds.setHeight(36);
		
		switch (position) {
		case TOP_RIGHT:
			bounds.setX(activityBounds.getX() + activityBounds.getWidth() - (bounds.getWidth() / 2));
			bounds.setY(activityBounds.getY() - (bounds.getHeight() / 2));
			break;
		case TOP_LEFT:
			bounds.setX(activityBounds.getX() - (bounds.getWidth() / 2));
			bounds.setY(activityBounds.getY() - (bounds.getHeight() / 2));
			break;
		case BOTTOM_RIGHT:
			bounds.setX(activityBounds.getX() + activityBounds.getWidth() - (bounds.getWidth() / 2));
			bounds.setY(activityBounds.getY() + activityBounds.getHeight() - (bounds.getHeight() / 2));
			break;
		case BOTTOM_LEFT:
			bounds.setX(activityBounds.getX() - (bounds.getWidth() / 2));
			bounds.setY(activityBounds.getY() + activityBounds.getHeight() - (bounds.getHeight() / 2));
			break;
		default:
			break;
		}
		
		return boundaryEvent;
	}
}
