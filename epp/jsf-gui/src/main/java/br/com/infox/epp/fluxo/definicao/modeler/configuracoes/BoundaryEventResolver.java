package br.com.infox.epp.fluxo.definicao.modeler.configuracoes;

import java.util.Map;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.SignalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.TimerEventDefinition;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.scheduler.def.CreateTimerAction;

import br.com.infox.epp.fluxo.definicao.modeler.DiagramUtil;

class BoundaryEventResolver {
    private BpmnModelInstance bpmnModel;
    
    private static enum Position {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
    }
    
    BoundaryEventResolver(BpmnModelInstance bpmnModel) {
        this.bpmnModel = bpmnModel;
    }
    
    void resolverBoundaryEvents(Node node) {
        if (node.getNodeType().equals(NodeType.Task)) {
            resolverTimer((TaskNode) node);
        }
        
        if (node.getNodeType().equals(NodeType.Task) || node.getNodeType().equals(NodeType.ProcessState)) {
            resolverSinalBoundary(node);
        } else if (node.getNodeType().equals(NodeType.StartState)) {
            resolverSinalStart((StartState) node);
        }
    }
    
    private void resolverTimer(TaskNode node) {
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

    private BoundaryEvent getBoundaryEvent(Activity activity, Class<? extends EventDefinition> eventDefinitionClass) {
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

    private void resolverSinalBoundary(Node node) {
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
    
    private void resolverSinalStart(StartState startState) {
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
    
    private boolean hasTimers(TaskNode node) {
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
    
    private BoundaryEvent createBoundaryEvent(Activity activity, Position position) {
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
