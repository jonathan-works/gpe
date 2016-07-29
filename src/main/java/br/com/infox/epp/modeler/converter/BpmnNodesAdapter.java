package br.com.infox.epp.modeler.converter;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.CallActivity;
import org.camunda.bpm.model.bpmn.instance.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.SubProcess;

class BpmnNodesAdapter implements BpmnAdapter {
	
	@Override
	public BpmnModelInstance checkAndConvert(BpmnModelInstance bpmnModel) {
		convertSubprocess(bpmnModel);
		convertIntermediateThrowEvent(bpmnModel);
		return bpmnModel;
	}
	
	private void convertIntermediateThrowEvent(BpmnModelInstance bpmnModel) {
		Collection<IntermediateThrowEvent> events = bpmnModel.getModelElementsByType(IntermediateThrowEvent.class);
		for (IntermediateThrowEvent event : events) {
			Class<? extends FlowElement> replacement = null;
			for (EventDefinition eventDefinition : event.getEventDefinitions()) {
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
