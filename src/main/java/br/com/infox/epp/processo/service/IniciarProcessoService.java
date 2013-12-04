package br.com.infox.epp.processo.service;

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

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.assignment.LocalizacaoAssignment;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoManager;

/**
 * 
 * @author Daniel
 *
 */
@Name(IniciarProcessoService.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class IniciarProcessoService {
	
	@In
	private ProcessoManager processoManager;

	@In
	private DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;
	
	public static final String ON_CREATE_PROCESS = 
		"br.com.infox.epp.IniciarProcessoService.ONCREATEPROCESS";
	public static final String NAME = "iniciarProcessoService";
	public static final String TYPE_MISMATCH_EXCEPTION = 
				"Tipo informado não é uma instância de " +
				"br.com.infox.ibpm.entity.Processo";
	
	/**
	 * 
	 * @param processo
	 * @param fluxo
	 * @throws DAOException 
	 */
	public void iniciarProcesso(Processo processo, Fluxo fluxo) throws DAOException {
		processo.setDataInicio(new Date());
		Long idProcessoJbpm = iniciarProcessoJbpm(processo, fluxo.getFluxo());
		processo.setIdJbpm(idProcessoJbpm);
		processo.setNumeroProcesso(String.valueOf(processo.getIdProcesso()));
		
		processoManager.update(processo);
	}
	
	/**
	 * 
	 * @param id
	 * @param fluxo
	 */
    public Long iniciarProcessoJbpm(Processo processo, String fluxo) {
        BusinessProcess businessProcess = BusinessProcess.instance();
        businessProcess.createProcess(fluxo);
        org.jbpm.graph.exe.ProcessInstance processInstance = ProcessInstance
                .instance();
        processInstance.getContextInstance().setVariable("processo",
                processo.getIdProcesso());
        createJbpmVariables(processo, processInstance.getContextInstance());
        @SuppressWarnings(WarningConstants.UNCHECKED)
		Collection<org.jbpm.taskmgmt.exe.TaskInstance> taskInstances = processInstance
                .getTaskMgmtInstance().getTaskInstances();
        org.jbpm.taskmgmt.exe.TaskInstance taskInstance = null;
        if (taskInstances != null && !taskInstances.isEmpty()) {
            taskInstance = taskInstances.iterator().next();
            long taskInstanceId = taskInstance.getId();
            businessProcess.setTaskId(taskInstanceId);
            businessProcess.startTask();
        }
        SwimlaneInstance swimlaneInstance = TaskInstance.instance()
                .getSwimlaneInstance();
        String actorsExpression = swimlaneInstance.getSwimlane()
                .getPooledActorsExpression();
        Set<String> pooledActors = LocalizacaoAssignment.instance()
                .getPooledActors(actorsExpression);
        String[] actorIds = pooledActors
                .toArray(new String[pooledActors.size()]);
        swimlaneInstance.setPooledActors(actorIds);
        Events.instance().raiseEvent(ON_CREATE_PROCESS, taskInstance, processo);
        return businessProcess.getProcessId();
    }

	private void createJbpmVariables(Processo processo, ContextInstance contextInstance) {
		ProcessoEpa processoEpa = definicaoVariavelProcessoManager.find(ProcessoEpa.class, processo.getIdProcesso());
		List<DefinicaoVariavelProcesso> variaveis = definicaoVariavelProcessoManager.listVariaveisByFluxo(processoEpa.getNaturezaCategoriaFluxo().getFluxo());
		for (DefinicaoVariavelProcesso variavelProcesso : variaveis) {
			contextInstance.setVariable(variavelProcesso.getNome(), null);
		}
	}
}