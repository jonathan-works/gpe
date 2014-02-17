package br.com.infox.epp.processo.service;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.bpm.TaskInstance;
import org.jboss.seam.core.Events;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.assignment.LocalizacaoAssignment;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;

@Name(IniciarProcessoService.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class IniciarProcessoService {

    @In
    private DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;

    @In
    private ProcessoEpaManager processoEpaManager;
    
    @In
    private NaturezaManager naturezaManager;

    public static final String ON_CREATE_PROCESS = "br.com.infox.epp.IniciarProcessoService.ONCREATEPROCESS";
    public static final String NAME = "iniciarProcessoService";
    public static final String TYPE_MISMATCH_EXCEPTION = "Tipo informado não é uma instância de "
            + "br.com.infox.ibpm.entity.Processo";

    /**
     * 
     * @param processoEpa
     * @throws DAOException
     */
    public void iniciarProcesso(ProcessoEpa processoEpa) throws DAOException {
        processoEpaManager.persist(processoEpa);
        processoEpa.setDataInicio(new Date());
        Long idProcessoJbpm = iniciarProcessoJbpm(processoEpa, processoEpa.getNaturezaCategoriaFluxo().getFluxo().getFluxo());
        processoEpa.setIdJbpm(idProcessoJbpm);
        processoEpa.setNumeroProcesso(String.valueOf(processoEpa.getIdProcesso()));
        naturezaManager.lockNatureza(processoEpa.getNaturezaCategoriaFluxo().getNatureza());
        processoEpaManager.update(processoEpa);
    }

    private Long iniciarProcessoJbpm(ProcessoEpa processoEpa, String fluxo) {
        BusinessProcess businessProcess = BusinessProcess.instance();
        businessProcess.createProcess(fluxo);
        org.jbpm.graph.exe.ProcessInstance processInstance = ProcessInstance.instance();
        processInstance.getContextInstance().setVariable("processo", processoEpa.getIdProcesso());
        createJbpmVariables(processoEpa, processInstance.getContextInstance());
        @SuppressWarnings(UNCHECKED) Collection<org.jbpm.taskmgmt.exe.TaskInstance> taskInstances = processInstance.getTaskMgmtInstance().getTaskInstances();
        org.jbpm.taskmgmt.exe.TaskInstance taskInstance = null;
        if (taskInstances != null && !taskInstances.isEmpty()) {
            taskInstance = taskInstances.iterator().next();
            long taskInstanceId = taskInstance.getId();
            businessProcess.setTaskId(taskInstanceId);
            businessProcess.startTask();
        }
        SwimlaneInstance swimlaneInstance = TaskInstance.instance().getSwimlaneInstance();
        String actorsExpression = swimlaneInstance.getSwimlane().getPooledActorsExpression();
        Set<String> pooledActors = LocalizacaoAssignment.instance().getPooledActors(actorsExpression);
        String[] actorIds = pooledActors.toArray(new String[pooledActors.size()]);
        swimlaneInstance.setPooledActors(actorIds);
        Events.instance().raiseEvent(ON_CREATE_PROCESS, taskInstance, processoEpa);
        return businessProcess.getProcessId();
    }

    private void createJbpmVariables(ProcessoEpa processoEpa, ContextInstance contextInstance) {
        List<DefinicaoVariavelProcesso> variaveis = definicaoVariavelProcessoManager.listVariaveisByFluxo(processoEpa.getNaturezaCategoriaFluxo().getFluxo());
        for (DefinicaoVariavelProcesso variavelProcesso : variaveis) {
            contextInstance.setVariable(variavelProcesso.getNome(), null);
        }
    }
}
