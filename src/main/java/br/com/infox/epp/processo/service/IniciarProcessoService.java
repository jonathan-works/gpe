package br.com.infox.epp.processo.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ProcessInstance;

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
        processo.setDataInicio(new Date());
        if (processo.getIdProcesso() == null) {
            processoManager.persist(processo);
        }
        createMetadadosProcesso(processo, metadados);
        ProcessInstance processInstance = criarProcessoJbpm(processo, processo.getNaturezaCategoriaFluxo().getFluxo().getFluxo());
        processo.setIdJbpm(processInstance.getId());
        processoManager.flush();
        inicializarProcessoJbpm(processo, processInstance, variaveis, transitionName);
        processo.setNumeroProcesso(String.valueOf(processo.getIdProcesso()));
        if (processo.getProcessoPai() == null) {
            processInstance.getContextInstance().setVariable(VariaveisJbpmProcessosGerais.NUMERO_PROCESSO, processo.getNumeroProcesso());
        }
        naturezaManager.lockNatureza(processo.getNaturezaCategoriaFluxo().getNatureza());
        processoManager.update(processo);
        if (createDefaultFolders) {
            pastaManager.createDefaultFolders(processo);
        }
        return processInstance;
    }

    private void createMetadadosProcesso(Processo processo, List<MetadadoProcesso> metadados) {
        if (metadados == null) return;
        for (MetadadoProcesso metadadoProcesso : metadados) {
            metadadoProcesso.setProcesso(processo);
            metadadoProcessoManager.persist(metadadoProcesso);
        }
    }

    private ProcessInstance criarProcessoJbpm(Processo processo, String fluxo) {
        return ManagedJbpmContext.instance().newProcessInstanceForUpdate(fluxo);
    }
    
    private void inicializarProcessoJbpm(Processo processo, ProcessInstance processoJbpm, Map<String, Object> variaveis, String transitionName) {
        iniciaVariaveisProcesso(processo, variaveis, processoJbpm);
        if (StringUtil.isEmpty(transitionName)) {
            processoJbpm.signal();
        } else {
            processoJbpm.signal(transitionName);
        }
//        iniciaPrimeiraTarefa(processoJbpm);
    }

    private void iniciaVariaveisProcesso(Processo processo, Map<String, Object> variaveis, ProcessInstance processInstance) {
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable(VariaveisJbpmProcessosGerais.PROCESSO, processo.getIdProcesso());
        if (variaveis != null) {
            for (String variavel : variaveis.keySet()) {
                contextInstance.setVariable(variavel, variaveis.get(variavel));
            }
        }
        if (processo.getProcessoPai() == null) {
        	contextInstance.setVariable(VariaveisJbpmProcessosGerais.NATUREZA, processo.getNaturezaCategoriaFluxo().getNatureza().getNatureza());
        	contextInstance.setVariable(VariaveisJbpmProcessosGerais.CATEGORIA, processo.getNaturezaCategoriaFluxo().getCategoria().getCategoria());
        }
    	contextInstance.setVariable(VariaveisJbpmProcessosGerais.DATA_INICIO_PROCESSO, processo.getDataInicio());
        ManagedJbpmContext.instance().getSession().flush();
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
