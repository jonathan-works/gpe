package br.com.infox.epp.processo.service;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
import br.com.infox.epp.access.assignment.LocalizacaoAssignment;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
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
        iniciarProcesso(processo, null);
    }
    
    public void iniciarProcesso(Processo processo, Map<String, Object> variaveis) throws DAOException {
    	processo.setDataInicio(new Date());
    	if (processo.getIdProcesso() == null) {
    		processoManager.persist(processo);
    	}
        Long idProcessoJbpm = iniciarProcessoJbpm(processo, processo.getNaturezaCategoriaFluxo().getFluxo().getFluxo(), variaveis);
        processo.setIdJbpm(idProcessoJbpm);
        processo.setNumeroProcesso(String.valueOf(processo.getIdProcesso()));
        if (processo.getProcessoPai() == null) {
        	ManagedJbpmContext.instance().getProcessInstanceForUpdate(idProcessoJbpm).getContextInstance().setVariable("numeroProcesso", processo.getNumeroProcesso());
        }
        naturezaManager.lockNatureza(processo.getNaturezaCategoriaFluxo().getNatureza());
        processoManager.update(processo);
        pastaManager.createDefaultFolders(processo);
    }

    private Long iniciarProcessoJbpm(Processo processo, String fluxo, Map<String, Object> variaveis) {
        
        BusinessProcess businessProcess = BusinessProcess.instance();
        businessProcess.createProcess(fluxo, false);
        org.jbpm.graph.exe.ProcessInstance processInstance = ProcessInstance.instance();
        
        iniciaVariaveisProcesso(processo, variaveis, processInstance);
        
        processInstance.signal();
        
        boolean iniciouTarefa = iniciaPrimeiraTarefa(businessProcess, processInstance);
        
        if (iniciouTarefa) {
        	atribuiSwimlaneTarefa();
        }
        
        return businessProcess.getProcessId();
    }

    private void atribuiSwimlaneTarefa() {
        SwimlaneInstance swimlaneInstance = TaskInstance.instance().getSwimlaneInstance();
        String actorsExpression = swimlaneInstance.getSwimlane().getPooledActorsExpression();
        Set<String> pooledActors = LocalizacaoAssignment.instance().getPooledActors(actorsExpression);
        String[] actorIds = pooledActors.toArray(new String[pooledActors.size()]);
        swimlaneInstance.setPooledActors(actorIds);
    }

    private void iniciaVariaveisProcesso(Processo processo, Map<String, Object> variaveis, org.jbpm.graph.exe.ProcessInstance processInstance) {
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("processo", processo.getIdProcesso());
        createJbpmVariables(processo, contextInstance);
        if (variaveis != null) {
            for (String variavel : variaveis.keySet()) {
                contextInstance.setVariable(variavel, variaveis.get(variavel));
            }
        }
        if (contextInstance.getVariable("naturezaProcesso") == null) {
        	contextInstance.setVariable("naturezaProcesso", processo.getNaturezaCategoriaFluxo().getNatureza().getNatureza());
        }
        if (contextInstance.getVariable("categoriaProcesso") == null) {
        	contextInstance.setVariable("categoriaProcesso", processo.getNaturezaCategoriaFluxo().getCategoria().getCategoria());
        }
        if (contextInstance.getVariable("dataInicioProcesso") == null) {
        	contextInstance.setVariable("dataInicioProcesso", processo.getDataInicio());
        }
    }

    private boolean iniciaPrimeiraTarefa(BusinessProcess businessProcess,
            org.jbpm.graph.exe.ProcessInstance processInstance) {
        @SuppressWarnings(UNCHECKED) Collection<org.jbpm.taskmgmt.exe.TaskInstance> taskInstances = processInstance.getTaskMgmtInstance().getTaskInstances();
        org.jbpm.taskmgmt.exe.TaskInstance taskInstance = null;
        if (taskInstances != null && !taskInstances.isEmpty()) {
            taskInstance = taskInstances.iterator().next();
            long taskInstanceId = taskInstance.getId();
            businessProcess.setTaskId(taskInstanceId);
            businessProcess.startTask();
            return true;
        }
        return false;
    }

    private void createJbpmVariables(Processo processo, ContextInstance contextInstance) {
    	DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager = BeanManager.INSTANCE.getReference(DefinicaoVariavelProcessoManager.class);
        List<DefinicaoVariavelProcesso> variaveis = definicaoVariavelProcessoManager.listVariaveisByFluxo(processo.getNaturezaCategoriaFluxo().getFluxo());
        for (DefinicaoVariavelProcesso variavelProcesso : variaveis) {
            contextInstance.setVariable(variavelProcesso.getNome(), null);
        }
    }
}
