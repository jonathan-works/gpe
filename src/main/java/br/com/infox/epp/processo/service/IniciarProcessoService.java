package br.com.infox.epp.processo.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.bpm.TaskInstance;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.assignment.LocalizacaoAssignment;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name(IniciarProcessoService.NAME)
@Transactional
public class IniciarProcessoService implements Serializable {

	private static final long serialVersionUID = 1L;

	@In
    private VariavelProcessoService variavelProcessoService;
	@In
	private ProcessoManager processoManager;
    @In
    private NaturezaManager naturezaManager;
    @In
    private PastaManager pastaManager;

    public static final String ON_CREATE_PROCESS = "br.com.infox.epp.IniciarProcessoService.ONCREATEPROCESS";
    public static final String NAME = "iniciarProcessoService";
    public static final String TYPE_MISMATCH_EXCEPTION = "Tipo informado não é uma instância de "
            + "br.com.infox.ibpm.entity.Processo";

    public void iniciarProcesso(Processo processo) throws DAOException {
        iniciarProcesso(processo, null, null);
    }
    
    public void iniciarProcesso(Processo processo, String transitionName) throws DAOException {
        iniciarProcesso(processo, null, transitionName);
    }
    
    public void iniciarProcesso(Processo processo, Map<String, Object> variaveis) throws DAOException {
        iniciarProcesso(processo, variaveis, null);
    }
    
    public void iniciarProcesso(Processo processo, Map<String, Object> variaveis, String transitionName) throws DAOException {
        processo.setDataInicio(new Date());
        if (processo.getIdProcesso() == null) {
            processoManager.persist(processo);
        }
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
        pastaManager.createDefaultFolders(processo);
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
        boolean iniciouTarefa = iniciaPrimeiraTarefa(processoJbpm);
        if (iniciouTarefa) {
        	atribuiSwimlaneTarefa(processoJbpm);
        }
    }

    private void atribuiSwimlaneTarefa(org.jbpm.graph.exe.ProcessInstance processoJbpm) {
        SwimlaneInstance swimlaneInstance = TaskInstance.instance().getSwimlaneInstance();
        String actorsExpression = swimlaneInstance.getSwimlane().getPooledActorsExpression();
        Set<String> pooledActors = LocalizacaoAssignment.instance().updatePooledActors(actorsExpression, TaskInstance.instance(), processoJbpm);
        String[] actorIds = pooledActors.toArray(new String[pooledActors.size()]);
        swimlaneInstance.setPooledActors(actorIds);
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
    
    private boolean iniciaPrimeiraTarefa(org.jbpm.graph.exe.ProcessInstance processInstance) {
        Collection<org.jbpm.taskmgmt.exe.TaskInstance> taskInstances = processInstance.getTaskMgmtInstance().getTaskInstances();
        org.jbpm.taskmgmt.exe.TaskInstance taskInstance = null;
        BusinessProcess businessProcess = BusinessProcess.instance();
        if (taskInstances != null && !taskInstances.isEmpty()) {
            taskInstance = taskInstances.iterator().next();
            long taskInstanceId = taskInstance.getId();
            businessProcess.setTaskId(taskInstanceId);
            businessProcess.startTask();
            return true;
        }
        return false;
    }
}
