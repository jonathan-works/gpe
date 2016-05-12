package br.com.infox.epp.processo.service;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.joda.time.DateTime;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;

@Stateless
public class IniciarProcessoService {

	@Inject
	private ProcessoManager processoManager;
    @Inject
    private NaturezaManager naturezaManager;
    @Inject
    private PastaManager pastaManager;
    @Inject
    private MetadadoProcessoManager metadadoProcessoManager;

    public void iniciarProcesso(Processo processo) throws DAOException {
        iniciarProcesso(processo, null, null, null, true);
    }
    
    public void iniciarProcesso(Processo processo, String transitionName) throws DAOException {
        iniciarProcesso(processo, null, null, transitionName, true);
    }
    
    public void iniciarProcesso(Processo processo, Map<String, Object> variaveis) throws DAOException {
        iniciarProcesso(processo, variaveis, null, null, true);
    }
    
    public void iniciarProcesso(Processo processo, Map<String, Object> variaveis, boolean createDefaultFolders) throws DAOException {
        iniciarProcesso(processo, variaveis, null, null, createDefaultFolders);
    }
    
    public ProcessInstance iniciarProcesso(Processo processo, Map<String, Object> variaveis, List<MetadadoProcesso> metadados, String transitionName, 
            boolean createDefaultFolders) throws DAOException {
        processo.setDataInicio(DateTime.now().toDate());
        if (processo.getIdProcesso() == null) {
            processoManager.persist(processo);
        }
        createMetadadosProcesso(processo, metadados);
        adicionarVariaveisDefault(processo, variaveis);
        ProcessInstance processInstance = criarProcessInstance(processo, variaveis);
        movimentarProcesso(processInstance, transitionName);
        processo.setIdJbpm(processInstance.getId());
        naturezaManager.lockNatureza(processo.getNaturezaCategoriaFluxo().getNatureza());
        processoManager.update(processo);
        if (createDefaultFolders) {
            pastaManager.createDefaultFolders(processo);
        }
        return processInstance;
    }

    protected void createMetadadosProcesso(Processo processo, List<MetadadoProcesso> metadados) {
        if (metadados != null) {
            for (MetadadoProcesso metadadoProcesso : metadados) {
                metadadoProcesso.setProcesso(processo);
                metadadoProcessoManager.persist(metadadoProcesso);
            }
        }
    }

    protected ProcessInstance criarProcessInstance(Processo processo, Map<String, Object> variaveis) {
        String processDefinitionName = processo.getNaturezaCategoriaFluxo().getFluxo().getFluxo();
        JbpmContext jbpmContext = ManagedJbpmContext.instance();
        ProcessDefinition processDefinition = jbpmContext.getGraphSession().findLatestProcessDefinition(processDefinitionName);
        ProcessInstance processInstance = processDefinition.createProcessInstance(variaveis);
        jbpmContext.addAutoSaveProcessInstance(processInstance);
        return processInstance;
    }
    
    protected void adicionarVariaveisDefault(Processo processo, Map<String, Object> variaveis) {
        variaveis.put(VariaveisJbpmProcessosGerais.PROCESSO, processo.getIdProcesso());
        variaveis.put(VariaveisJbpmProcessosGerais.DATA_INICIO_PROCESSO, processo.getDataInicio());
        if (processo.getProcessoPai() == null) {
            variaveis.put(VariaveisJbpmProcessosGerais.NATUREZA, processo.getNaturezaCategoriaFluxo().getNatureza().getNatureza());
            variaveis.put(VariaveisJbpmProcessosGerais.CATEGORIA, processo.getNaturezaCategoriaFluxo().getCategoria().getCategoria());
            variaveis.put(VariaveisJbpmProcessosGerais.NUMERO_PROCESSO, processo.getNumeroProcesso());
        }
    }
    
    protected void movimentarProcesso(ProcessInstance processInstance, String transitionName) {
        if (StringUtil.isEmpty(transitionName)) {
            processInstance.signal();
        } else {
            processInstance.signal(transitionName);
        }
    }
    
// // Chamar popup com id do taskInstance
//    private void iniciaPrimeiraTarefa(ProcessInstance processInstance) {
//        Collection<TaskInstance> taskInstances = processInstance.getTaskMgmtInstance().getTaskInstances();
//        if (taskInstances != null && !taskInstances.isEmpty()) {
//            TaskInstance taskInstance = taskInstances.iterator().next();
//            long taskInstanceId = taskInstance.getId();
//        }
//    }
}
