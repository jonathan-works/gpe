package br.com.infox.epp.fluxo.definicao.modeler;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

interface BpmnAdapter {
	BpmnModelInstance checkAndConvert(BpmnModelInstance bpmnModel);
}
