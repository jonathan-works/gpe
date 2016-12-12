package br.com.infox.epp.modeler.converter;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

interface BpmnAdapter {
	BpmnModelInstance checkAndConvert(BpmnModelInstance bpmnModel);
}
