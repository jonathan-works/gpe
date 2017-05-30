package br.com.infox.epp.fluxo.definicao;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.epp.fluxo.definicao.modeler.BpmnJpdlService;
import br.com.infox.epp.fluxo.definicao.modeler.EppBpmn;
import br.com.infox.epp.fluxo.definicao.modeler.JpdlBpmnConverter;
import br.com.infox.epp.fluxo.definicao.modeler.configuracoes.ConfiguracoesNos;
import br.com.infox.epp.fluxo.entity.DefinicaoProcesso;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.service.HistoricoProcessDefinitionService;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.jpdl.JpdlXmlWriter;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DefinicaoProcessoService {
    
    @Inject
    @GenericDao
    private Dao<DefinicaoProcesso, Long> definicaoProcessoDao;
    @Inject
    private BpmnJpdlService bpmnJpdlService;
    @Inject
    private HistoricoProcessDefinitionService historicoProcessDefinitionService;
    @Inject
    private TarefaManager tarefaManager;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DefinicaoProcesso criarDefinicaoProcesso(Fluxo fluxo) {
        DefinicaoProcesso definicaoProcesso = new DefinicaoProcesso();
        definicaoProcesso.setFluxo(fluxo);
        fluxo.setDefinicaoProcesso(definicaoProcesso);
        return definicaoProcessoDao.persist(definicaoProcesso);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DefinicaoProcesso atualizarDefinicaoProcesso(DefinicaoProcesso definicaoProcesso, String newProcessDefinitionXml, Collection<Tarefa> tarefasModificadas) {
        historicoProcessDefinitionService.registrarHistorico(definicaoProcesso);
        
        ProcessDefinition newProcessDefinition = new InfoxJpdlXmlReader(new StringReader(newProcessDefinitionXml)).readProcessDefinition();
        BpmnModelInstance bpmnModel = EppBpmn.readModelFromStream(new ByteArrayInputStream(definicaoProcesso.getBpmn().getBytes(StandardCharsets.UTF_8)));
        ConfiguracoesNos.resolverMarcadoresBpmn(newProcessDefinition, bpmnModel);
        definicaoProcesso.setBpmn(EppBpmn.convertToString(bpmnModel));
        definicaoProcesso.setXml(newProcessDefinitionXml);
        definicaoProcesso = definicaoProcessoDao.update(definicaoProcesso);
        
        if (tarefasModificadas != null) {
            for (Tarefa tarefa : tarefasModificadas) {
                tarefaManager.update(tarefa);
            }
        }
        
        return definicaoProcesso;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DefinicaoProcesso loadDefinicoes(DefinicaoProcesso definicaoProcesso) {
        if (definicaoProcesso.getXml() == null) {
            definicaoProcesso.setXml(JpdlXmlWriter.toString(bpmnJpdlService.createInitialProcessDefinition(definicaoProcesso.getFluxo().getFluxo())));
            definicaoProcesso.setBpmn(new JpdlBpmnConverter().convert(definicaoProcesso.getXml()));
            return definicaoProcessoDao.update(definicaoProcesso);
        }
        
        if (definicaoProcesso.getBpmn() == null) {
            definicaoProcesso.setBpmn(new JpdlBpmnConverter().convert(definicaoProcesso.getXml()));
            definicaoProcesso = definicaoProcessoDao.update(definicaoProcesso);
        }
        
        BpmnModelInstance bpmnModel = EppBpmn.readModelFromStream(new ByteArrayInputStream(definicaoProcesso.getBpmn().getBytes(StandardCharsets.UTF_8)));
        ProcessDefinition processDefinition = InfoxJpdlXmlReader.readProcessDefinition(definicaoProcesso.getXml());
        if (!processDefinition.getName().equals(definicaoProcesso.getFluxo().getFluxo())) {
            bpmnJpdlService.atualizarNomeFluxo(definicaoProcesso.getFluxo().getFluxo(), bpmnModel, processDefinition);
            definicaoProcesso.setBpmn(EppBpmn.convertToString(bpmnModel));
            definicaoProcesso.setXml(JpdlXmlWriter.toString(processDefinition));
            return definicaoProcessoDao.update(definicaoProcesso);
        }
        
        return definicaoProcesso;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DefinicaoProcesso update(DefinicaoProcesso definicaoProcesso){
        return definicaoProcessoDao.update(definicaoProcesso);
    }
}
