package br.com.infox.epp.processo.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.bpm.ProcessInstance;
import org.jbpm.context.exe.ContextInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name(IniciarProcessoService.NAME)
@Stateless
@Transactional
@ContextDependency
public class IniciarProcessoService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ProcessoManager processoManager;
    @Inject
    private NaturezaManager naturezaManager;
    @Inject
    private PastaManager pastaManager;
    @Inject
    private MetadadoProcessoManager metadadoProcessoManager;

    public static final String NAME = "iniciarProcessoService";
    public static final String TYPE_MISMATCH_EXCEPTION = "Tipo informado não é uma instância de "
            + "br.com.infox.ibpm.entity.Processo";

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
    
    public void iniciarProcesso(Processo processo, Map<String, Object> variaveis, List<MetadadoProcesso> metadados, String transitionName, 
            boolean createDefaultFolders) throws DAOException {
        processo.setDataInicio(new Date());
        if (processo.getIdProcesso() == null) {
            processoManager.persist(processo);
        }
        createMetadadosProcesso(processo, metadados);
        org.jbpm.graph.exe.ProcessInstance processInstance = criarProcessoJbpm(processo, processo.getNaturezaCategoriaFluxo().getFluxo().getFluxo());
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
    }

    private void createMetadadosProcesso(Processo processo, List<MetadadoProcesso> metadados) {
        if (metadados == null) return;
        for (MetadadoProcesso metadadoProcesso : metadados) {
            metadadoProcesso.setProcesso(processo);
            metadadoProcessoManager.persist(metadadoProcesso);
        }
    }

    private org.jbpm.graph.exe.ProcessInstance criarProcessoJbpm(Processo processo, String fluxo) {
    	BusinessProcess businessProcess = BusinessProcess.instance();
        businessProcess.createProcess(fluxo, false);
        org.jbpm.graph.exe.ProcessInstance processInstance = ProcessInstance.instance();
        return processInstance;
    }
    
    private void inicializarProcessoJbpm(Processo processo, org.jbpm.graph.exe.ProcessInstance processoJbpm, Map<String, Object> variaveis, String transitionName) {
        iniciaVariaveisProcesso(processo, variaveis, processoJbpm);
        if (StringUtil.isEmpty(transitionName)) {
            processoJbpm.signal();
        } else {
            processoJbpm.signal(transitionName);
        }
        iniciaPrimeiraTarefa(processoJbpm);
    }

    private void iniciaVariaveisProcesso(Processo processo, Map<String, Object> variaveis, org.jbpm.graph.exe.ProcessInstance processInstance) {
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
    
    private void iniciaPrimeiraTarefa(org.jbpm.graph.exe.ProcessInstance processInstance) {
        Collection<org.jbpm.taskmgmt.exe.TaskInstance> taskInstances = processInstance.getTaskMgmtInstance().getTaskInstances();
        org.jbpm.taskmgmt.exe.TaskInstance taskInstance = null;
        BusinessProcess businessProcess = BusinessProcess.instance();
        if (taskInstances != null && !taskInstances.isEmpty()) {
            taskInstance = taskInstances.iterator().next();
            long taskInstanceId = taskInstance.getId();
            businessProcess.setTaskId(taskInstanceId);
            businessProcess.startTask();
        }
    }
}
