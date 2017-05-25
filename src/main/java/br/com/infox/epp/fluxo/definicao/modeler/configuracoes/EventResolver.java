package br.com.infox.epp.fluxo.definicao.modeler.configuracoes;

import java.util.List;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;

import br.com.infox.epp.fluxo.definicao.modeler.ModeladorConstants;
import br.com.infox.jbpm.event.EventHandler;

class EventResolver {
    private BpmnModelInstance bpmnModel;
    private static String[] ENTER_EVENTS = {
        Event.EVENTTYPE_NODE_ENTER
    };
    
    private static String[] EXIT_EVENTS = {
            Event.EVENTTYPE_NODE_LEAVE
        };
    
    EventResolver(BpmnModelInstance bpmnModel) {
        this.bpmnModel = bpmnModel;
    }
    
    void resolverEventos(Node node) {
        boolean hasEnterEvent = hasEvent(node, ENTER_EVENTS);
        boolean hasExitEvent = hasEvent(node, EXIT_EVENTS);
        String attributeValue;
        if (hasEnterEvent && hasExitEvent) {
            attributeValue = "enter,exit";
        } else if (hasEnterEvent) {
            attributeValue = "enter";
        } else if (hasExitEvent) {
            attributeValue = "exit";
        } else {
            attributeValue = "none";
        }
        
        FlowNode flowNode = bpmnModel.getModelElementById(node.getKey());
        flowNode.setAttributeValueNs(ModeladorConstants.INFOX_BPMN_NAMESPACE, "events", attributeValue);
    }
    
    private boolean hasEvent(Node node, String[] eventTypes) {
        for (String eventType : eventTypes) {
            Event event = node.getEvent(eventType);
            if (event == null || EventHandler.isIgnoreEvent(event)) {
                continue;
            }
            
            List<Action> actions = new EventHandler(event).getActions();
            if (actions != null && !actions.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
