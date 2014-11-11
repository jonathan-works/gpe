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
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.bpm.TaskInstance;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.assignment.LocalizacaoAssignment;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name(IniciarProcessoService.NAME)
public class IniciarProcessoService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@In
    private DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;
    @In
    private ProcessoEpaManager processoEpaManager;
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
        processoEpaManager.persist(processo);
        processo.setDataInicio(new Date());
        Long idProcessoJbpm = iniciarProcessoJbpm(processo, processo.getNaturezaCategoriaFluxo().getFluxo().getFluxo(), variaveis);
        processo.setIdJbpm(idProcessoJbpm);
        processo.setNumeroProcesso(String.valueOf(processo.getIdProcesso()));
        naturezaManager.lockNatureza(processo.getNaturezaCategoriaFluxo().getNatureza());
        processoEpaManager.update(processo);
        pastaManager.createDefaultFolders(processo);
    }

    private Long iniciarProcessoJbpm(Processo processo, String fluxo, Map<String, Object> variaveis) {
        
        BusinessProcess businessProcess = BusinessProcess.instance();
        businessProcess.createProcess(fluxo, false);
        org.jbpm.graph.exe.ProcessInstance processInstance = ProcessInstance.instance();
        
        iniciaVariaveisProcesso(processo, variaveis, processInstance);
        
        processInstance.signal();
        
        iniciaPrimeiraTarefa(businessProcess, processInstance);
        
        atribuiSwimlaneTarefa();
        
        return businessProcess.getProcessId();
    }

    private void atribuiSwimlaneTarefa() {
        SwimlaneInstance swimlaneInstance = TaskInstance.instance().getSwimlaneInstance();
        String actorsExpression = swimlaneInstance.getSwimlane().getPooledActorsExpression();
        Set<String> pooledActors = LocalizacaoAssignment.instance().getPooledActors(actorsExpression);
        String[] actorIds = pooledActors.toArray(new String[pooledActors.size()]);
        swimlaneInstance.setPooledActors(actorIds);
    }

    private void iniciaVariaveisProcesso(Processo processo,
            Map<String, Object> variaveis,
            org.jbpm.graph.exe.ProcessInstance processInstance) {
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("processo", processo.getIdProcesso());
        createJbpmVariables(processo, contextInstance);
        if (variaveis != null) {
            for (String variavel : variaveis.keySet()) {
                contextInstance.setVariable(variavel, variaveis.get(variavel));
            }
        }
    }

    private void iniciaPrimeiraTarefa(BusinessProcess businessProcess,
            org.jbpm.graph.exe.ProcessInstance processInstance) {
        @SuppressWarnings(UNCHECKED) Collection<org.jbpm.taskmgmt.exe.TaskInstance> taskInstances = processInstance.getTaskMgmtInstance().getTaskInstances();
        org.jbpm.taskmgmt.exe.TaskInstance taskInstance = null;
        if (taskInstances != null && !taskInstances.isEmpty()) {
            taskInstance = taskInstances.iterator().next();
            long taskInstanceId = taskInstance.getId();
            businessProcess.setTaskId(taskInstanceId);
            businessProcess.startTask();
        }
    }

    private void createJbpmVariables(Processo processo,
            ContextInstance contextInstance) {
        List<DefinicaoVariavelProcesso> variaveis = definicaoVariavelProcessoManager.listVariaveisByFluxo(processo.getNaturezaCategoriaFluxo().getFluxo());
        for (DefinicaoVariavelProcesso variavelProcesso : variaveis) {
            contextInstance.setVariable(variavelProcesso.getNome(), null);
        }
    }
}
