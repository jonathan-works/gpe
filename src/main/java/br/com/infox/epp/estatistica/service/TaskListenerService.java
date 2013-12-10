package br.com.infox.epp.estatistica.service;

import static br.com.infox.ibpm.util.JbpmUtil.getTarefa;

import java.util.Date;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.exception.ApplicationException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.manager.ProcessoEpaTarefaManager;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(TaskListenerService.NAME)
public class TaskListenerService extends GenericManager {
	
	private static final LogProvider LOG = Logging.getLogProvider(TaskListenerService.class);

	private static final long serialVersionUID = 1L;

	public static final String NAME = "taskListenerAction";
	
	@In
	private ProcessoEpaTarefaManager processoEpaTarefaManager;
	
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
		pEpaTarefa.setUltimoDisparo(new Date());
		pEpaTarefa.setTempoGasto(0);
		pEpaTarefa.setPorcentagem(0);
		pEpaTarefa.setTempoPrevisto(tarefa.getPrazo());
		if (pEpaTarefa.getTempoPrevisto() == null) {
			pEpaTarefa.setTempoPrevisto(0);
		}
		pEpaTarefa.setTaskInstance(taskInstance.getId());
		
		UsuarioLocalizacao usrLoc = Authenticator.getUsuarioLocalizacaoAtual();
		UsuarioTaskInstance uti = new UsuarioTaskInstance(taskInstance.getId(), usrLoc.getUsuario(), usrLoc.getLocalizacao(), usrLoc.getPapel());

		try {
			persist(pEpaTarefa);
			persist(uti);
		} catch (DAOException e) {
			LOG.error(".createProcessoEpa(processo, taskInstance)", e);
		}
	}

	@Observer(Event.EVENTTYPE_TASK_END)
	public void onEndJbpmTask(ExecutionContext context) throws DAOException {
		ProcessoEpaTarefa pt = processoEpaTarefaManager.getByTaskInstance
									(context.getTaskInstance().getId());
		Date dtFinalizacao = context.getTaskInstance().getEnd();
		pt.setDataFim(dtFinalizacao);
		pt.setUltimoDisparo(dtFinalizacao);
		update(pt);
		processoEpaTarefaManager.updateTempoGasto(dtFinalizacao, pt);
	}
	
	@Observer(Event.EVENTTYPE_PROCESS_END)
	public void onEndProcess(ExecutionContext context) throws DAOException {
		Processo processo = JbpmUtil.getProcesso();
		if (processo == null) {
			throw new ApplicationException("Erro ao criar o processo. Verifique a configuração das raias na definição do fluxo.");
		}
		processo.setDataFim(new Date());
		processoEpaTarefaManager.update(processo);
	}
	
}