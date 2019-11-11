package br.com.infox.epp.fluxo.definicao.modeler.configuracoes;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;

public class ConfiguracoesNos {
	
	public static void resolverMarcadoresBpmn(ProcessDefinition processDefinition, BpmnModelInstance bpmnModel) {
		BoundaryEventResolver boundaryEventResolver = new BoundaryEventResolver(bpmnModel);
		EventResolver eventResolver = new EventResolver(bpmnModel);
		for (Node node : processDefinition.getNodes()) {
			boundaryEventResolver.resolverBoundaryEvents(node);
			eventResolver.resolverEventos(node);
		}
		
		new DocumentoResolver(processDefinition, bpmnModel).resolverMarcadoresDocumentos();
	}
}
