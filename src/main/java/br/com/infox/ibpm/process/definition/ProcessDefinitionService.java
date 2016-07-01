package br.com.infox.ibpm.process.definition;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.modeler.converter.BpmnJpdlService;
import br.com.infox.epp.modeler.converter.JpdlBpmnConverter;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.jpdl.JpdlXmlWriter;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ProcessDefinitionService {

	@Inject
	private FluxoManager fluxoManager;
	@Inject
	private TarefaManager tarefaManager;
	@Inject
	private BpmnJpdlService bpmnJpdlService;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Fluxo atualizarDefinicao(Fluxo fluxo, Collection<Tarefa> tarefasModificadas) {
		fluxo = fluxoManager.update(fluxo);
		if (tarefasModificadas != null) {
			for (Tarefa tarefa : tarefasModificadas) {
				tarefaManager.update(tarefa);
			}
		}
		return fluxo;
	}
	
	public Fluxo loadDefinicoes(Fluxo fluxo) {
		if (fluxo.getXml() == null) {
			fluxo.setXml(JpdlXmlWriter.toString(bpmnJpdlService.createInitialProcessDefinition(fluxo.getFluxo())));
			fluxo.setBpmn(new JpdlBpmnConverter().convert(fluxo.getXml()));
			return fluxoManager.update(fluxo);
		}
		
		if (fluxo.getBpmn() == null) {
			fluxo.setBpmn(new JpdlBpmnConverter().convert(fluxo.getXml()));
		}
		
		BpmnModelInstance bpmnModel = Bpmn.readModelFromStream(new ByteArrayInputStream(fluxo.getBpmn().getBytes(StandardCharsets.UTF_8)));
		ProcessDefinition processDefinition = InfoxJpdlXmlReader.readProcessDefinition(fluxo.getXml());
    	bpmnJpdlService.atualizarNomeFluxo(fluxo, bpmnModel, processDefinition);
    	fluxo.setBpmn(Bpmn.convertToString(bpmnModel));
    	fluxo.setXml(JpdlXmlWriter.toString(processDefinition));
    	
    	return fluxoManager.update(fluxo);
	}
}
