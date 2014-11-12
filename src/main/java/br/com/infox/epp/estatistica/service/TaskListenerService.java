package br.com.infox.epp.estatistica.service;

import java.io.Serializable;
import java.util.Date;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.seam.exception.ApplicationException;

@Name(TaskListenerService.NAME)
public class TaskListenerService implements Serializable {

    private static final LogProvider LOG = Logging.getLogProvider(TaskListenerService.class);

    private static final long serialVersionUID = 1L;

    public static final String NAME = "taskListenerAction";

    @In
    private ProcessoTarefaManager processoTarefaManager;
    @In
    TarefaManager tarefaManager;
    @In
    private ProcessoEpaManager processoEpaManager;
    @In
    private ProcessoManager processoManager;

    @Observer(IniciarProcessoService.ON_CREATE_PROCESS)
    public void onStartProcess(TaskInstance taskInstance, Processo processo) {
        createProcessoTarefa(processo, taskInstance);
    }

    @Observer(Event.EVENTTYPE_TASK_CREATE)
    public void onCreateJbpmTask(ExecutionContext context) {
        Processo processo = JbpmUtil.getProcesso();
        if (processo != null) {
            TaskInstance taskInstance = context.getTaskInstance();
            createProcessoTarefa(processo, taskInstance);
        }
    }

    private void createProcessoTarefa(Processo processo, TaskInstance taskInstance) {
        String taskName = taskInstance.getTask().getName();
        String procDefName = taskInstance.getProcessInstance().getProcessDefinition().getName();
        Tarefa tarefa = tarefaManager.getTarefa(taskName, procDefName);

        ProcessoTarefa pTarefa = new ProcessoTarefa();
        pTarefa.setProcesso(processoEpaManager.find(processo.getIdProcesso()));
        pTarefa.setTarefa(tarefa);
        pTarefa.setDataInicio(taskInstance.getCreate());
        pTarefa.setUltimoDisparo(new Date());
        pTarefa.setTempoGasto(0);
        pTarefa.setTempoPrevisto(tarefa.getPrazo());
        if (pTarefa.getTempoPrevisto() == null) {
            pTarefa.setTempoPrevisto(0);
        }
        pTarefa.setTaskInstance(taskInstance.getId());

        try {
        	processoTarefaManager.persist(pTarefa);
        } catch (DAOException e) {
            LOG.error(".createProcessoEpa(processo, taskInstance)", e);
        }
    }

    @Observer(Event.EVENTTYPE_PROCESS_END)
    public void onEndProcess(ExecutionContext context) throws DAOException {
        Processo processo = JbpmUtil.getProcesso();
        if (processo == null) {
            throw new ApplicationException("Erro ao criar o processo - Defição de fluxo imcompleta. Contate o administrador do sistema.");
        }
        processo.setDataFim(new Date());
        processoManager.update(processo);
    }

}
