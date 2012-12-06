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
	public void onStartProcess(TaskInstance ti, Processo processo) { 
		createProcessoEpa(processo, ti);
	}
		
	@Observer(Event.EVENTTYPE_TASK_CREATE)
	public void onCreateJbpmTask(ExecutionContext context) {
		Processo p = JbpmUtil.getProcesso();
		if(p != null) {
			TaskInstance taskInstance = context.getTaskInstance();
			createProcessoEpa(p, taskInstance);
		}
	}

	private void createProcessoEpa(Processo p, TaskInstance taskInstance) {
		String task = taskInstance.getTask().getName();
		String procDefName = taskInstance.getProcessInstance()
							   .getProcessDefinition().getName();
		ProcessoEpaTarefa pt = new ProcessoEpaTarefa();
		pt.setProcessoEpa(find(ProcessoEpa.class, p.getIdProcesso()));
		Tarefa tarefa = getTarefa(task, procDefName);
		pt.setTarefa(tarefa);
		pt.setDataInicio(taskInstance.getCreate());
		pt.setTaskInstance(taskInstance.getId());
		pt.setUltimoDisparo(new Date());
		pt.setTempoGasto(0);
		pt.setPorcentagem(0);
		
		pt.setTempoPrevisto(tarefa.getPrazo());
		if (pt.getTempoPrevisto() == null) {
			pt.setTempoPrevisto(0);
		}
		if (tarefa.getTipoPrazo() == PrazoEnum.H) {
			pt.setTempoPrevisto(pt.getTempoPrevisto() * 60);
		}

		persist(pt);
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