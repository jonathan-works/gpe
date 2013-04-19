package br.com.infox.epa.service;

import static br.com.infox.ibpm.jbpm.JbpmUtil.getTarefa;

import java.util.Date;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.epa.entity.ProcessoEpa;
import br.com.infox.epa.entity.ProcessoEpaTarefa;
import br.com.infox.epa.manager.LocalizacaoTurnoManager;
import br.com.infox.epa.manager.ProcessoEpaTarefaManager;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.Tarefa;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.manager.ProcessoLocalizacaoIbpmManager;
import br.com.infox.ibpm.type.PrazoEnum;

@Name(TaskListenerService.NAME)
public class TaskListenerService extends AbstractAction {

	public static final String NAME = "taskListenerAction";
	
	@In
	private ProcessoEpaTarefaManager processoEpaTarefaManager;
	@In
	private ProcessoLocalizacaoIbpmManager processoLocalizacaoIbpmManager;
	@In
	private LocalizacaoTurnoManager localizacaoTurnoManager;
	
	@Observer(IniciarProcessoService.ON_CREATE_PROCESS)
	public void onStartProcess(TaskInstance taskInstance, Processo processo) { 
		createProcessoEpa(processo, taskInstance);
	}
		
	@Observer(Event.EVENTTYPE_TASK_CREATE)
	public void onCreateJbpmTask(ExecutionContext context) {
		Processo processo = JbpmUtil.getProcesso();
		if(processo != null) {
			TaskInstance taskInstance = context.getTaskInstance();
			createProcessoEpa(processo, taskInstance);
		}
	}

	private void createProcessoEpa(Processo processo, TaskInstance taskInstance) {
		String taskName = taskInstance.getTask().getName();
		String procDefName = taskInstance.getProcessInstance()
                .getProcessDefinition().getName();
		Tarefa tarefa = getTarefa(taskName, procDefName);
		
		ProcessoEpaTarefa pEpaTarefa = new ProcessoEpaTarefa();
        pEpaTarefa.setProcessoEpa(find(ProcessoEpa.class, processo.getIdProcesso()));
		pEpaTarefa.setTarefa(tarefa);
		pEpaTarefa.setDataInicio(taskInstance.getCreate());
		pEpaTarefa.setTaskInstance(taskInstance.getId());
		pEpaTarefa.setUltimoDisparo(new Date());
		pEpaTarefa.setTempoGasto(0);
		pEpaTarefa.setPorcentagem(0);
		pEpaTarefa.setTempoPrevisto(tarefa.getPrazo());
		if (pEpaTarefa.getTempoPrevisto() == null) {
			pEpaTarefa.setTempoPrevisto(0);
		}
		if (tarefa.getTipoPrazo() == PrazoEnum.H) {
			pEpaTarefa.setTempoPrevisto(pEpaTarefa.getTempoPrevisto() * 60);
		}

		persist(pEpaTarefa);
	}

	@Observer(Event.EVENTTYPE_TASK_END)
	public void onEndJbpmTask(ExecutionContext context) {
		ProcessoEpaTarefa pt = processoEpaTarefaManager.getByTaskInstance
									(context.getTaskInstance().getId());
		pt.setDataFim(context.getTaskInstance().getEnd());
		update(pt);
	}
	
	@Observer(Event.EVENTTYPE_PROCESS_END)
	public void onEndProcess(ExecutionContext context) {
		Processo processo = JbpmUtil.getProcesso();
		processo.setDataFim(new Date());
		processoEpaTarefaManager.update(processo);
	}
	
}