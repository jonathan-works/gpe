package br.com.infox.epp.fluxo.definicao.modeler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.CallActivity;
import org.camunda.bpm.model.bpmn.instance.CatchEvent;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.SubProcess;
import org.camunda.bpm.model.bpmn.instance.ThrowEvent;

import com.google.common.base.Strings;

class BpmnNodesAdapter implements BpmnAdapter {
	
	@Override
	public BpmnModelInstance checkAndConvert(BpmnModelInstance bpmnModel) {
		convertSubprocess(bpmnModel);
		convertEvents(bpmnModel);
		checkNodeNames(bpmnModel);
		return bpmnModel;
	}
	
	private void checkNodeNames(BpmnModelInstance bpmnModel) {
	    Map<String, List<FlowNode>> nodesByName = new HashMap<>();
	    
	    for (FlowNode node : bpmnModel.getModelElementsByType(FlowNode.class)) {
	        if (!Strings.isNullOrEmpty(node.getName())) {
	            if (!nodesByName.containsKey(node.getName())) {
	                nodesByName.put(node.getName(), new ArrayList<FlowNode>());
	            }
	            nodesByName.get(node.getName()).add(node);
	        }
	    }
	    
	    for (String name : nodesByName.keySet()) {
	        List<FlowNode> nodes = nodesByName.get(name);
	        if (nodes.size() > 1) {
	            for (int i = 1; i < nodes.size(); i++) {
	                FlowNode node = nodes.get(i);
	                int j = i + 1;
	                while (nodesByName.containsKey(name + " " + j)) {
	                    j++;
	                }
	                node.setName(name + " " + j);
	            }
	        }
	    }
    }

    private void convertEvents(BpmnModelInstance bpmnModel) {
		Collection<ThrowEvent> throwEvents = bpmnModel.getModelElementsByType(ThrowEvent.class);
		for (ThrowEvent event : throwEvents) {
		    if (!event.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_END_EVENT)) {
		        convertEvent(event, event.getEventDefinitions());
		    }
		}
		
		Collection<CatchEvent> catchEvents = bpmnModel.getModelElementsByType(CatchEvent.class);
        for (CatchEvent event : catchEvents) {
            if (!event.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_START_EVENT)) {
                convertEvent(event, event.getEventDefinitions());
            }
        }
	}
	
	private void convertEvent(Event event, Collection<EventDefinition> eventDefinitions) {
	    Class<? extends FlowElement> replacement = null;
        for (EventDefinition eventDefinition : eventDefinitions) {
            if (eventDefinition.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_MESSAGE_EVENT_DEFINITION)) {
                replacement = SendTask.class;
                break;
            }
        }
        if (replacement == null) {
            replacement = ServiceTask.class;
        }
        replaceElement(event, replacement);
	}

	private void convertSubprocess(BpmnModelInstance bpmnModel) {
		Collection<CallActivity> callActivities = bpmnModel.getModelElementsByType(CallActivity.class);
		for (CallActivity callActivity : callActivities) {
			replaceElement(callActivity, SubProcess.class);
		}
	}
	
	private void replaceElement(FlowElement originalElement, Class<? extends FlowElement> newElementType) {
		FlowElement newElement = originalElement.getModelInstance().newInstance(newElementType);
		newElement.setId(originalElement.getId());
		newElement.setName(originalElement.getName());
		originalElement.replaceWithElement(newElement);
	}
}
