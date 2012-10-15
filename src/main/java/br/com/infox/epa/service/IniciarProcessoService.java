package br.com.infox.epa.service;

import java.util.Collection;
import java.util.Date;
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
import org.jbpm.taskmgmt.exe.SwimlaneInstance;

import br.com.infox.epa.manager.ProcessoManager;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.jbpm.assignment.LocalizacaoAssignment;

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

	public static final String ON_CREATE_PROCESS = 
		"br.com.infox.epa.IniciarProcessoService.ONCREATEPROCESS";
	public static final String NAME = "iniciarProcessoService";
	public static final String TYPE_MISMATCH_EXCEPTION = 
				"Tipo informado n�o � uma inst�ncia de " +
				"br.com.infox.ibpm.entity.Processo";
	
	/**
	 * 
	 * @param processo
	 * @param fluxo
	 */
	public void iniciarProcesso(Processo processo, Fluxo fluxo) {
		processo.setDataInicio(new Date());
		Long idJbpm = iniciarProcessoJbpm(processo, fluxo.getFluxo());
		processo.setIdJbpm(idJbpm);
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
		org.jbpm.graph.exe.ProcessInstance processInstance = 
					ProcessInstance.instance();
		processInstance.getContextInstance().setVariable("processo", processo.getIdProcesso());
		Collection<org.jbpm.taskmgmt.exe.TaskInstance> taskInstances = 
					processInstance.getTaskMgmtInstance().getTaskInstances();
		org.jbpm.taskmgmt.exe.TaskInstance taskInstance = null;
		if (taskInstances != null && !taskInstances.isEmpty()) {
			taskInstance = taskInstances.iterator().next();
			long taskId = taskInstance.getId();
			businessProcess.setTaskId(taskId);
			businessProcess.startTask();
		}
		SwimlaneInstance swimlaneInstance = TaskInstance.instance()
					.getSwimlaneInstance();
		String actorsExpression = swimlaneInstance.getSwimlane()
					.getPooledActorsExpression();
		Set<String> pooledActors = LocalizacaoAssignment.instance()
					.getPooledActors(actorsExpression);
		String[] actorIds = (String[]) pooledActors
					.toArray(new String[pooledActors.size()]);
		swimlaneInstance.setPooledActors(actorIds);
		Events.instance().raiseEvent(ON_CREATE_PROCESS, taskInstance, processo);
		return businessProcess.getProcessId();
	}
	
}