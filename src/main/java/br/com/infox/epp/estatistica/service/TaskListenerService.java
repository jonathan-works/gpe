package br.com.infox.epp.estatistica.service;

import java.io.Serializable;
import java.util.Date;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.ApplicationException;

@Name(TaskListenerService.NAME)
@Transactional
public class TaskListenerService implements Serializable {

    private static final LogProvider LOG = Logging.getLogProvider(TaskListenerService.class);

    private static final long serialVersionUID = 1L;

    public static final String NAME = "taskListenerAction";

    @In
    private ProcessoTarefaManager processoTarefaManager;
    @In
    private TarefaManager tarefaManager;
    @In
    private ProcessoManager processoManager;

    @Observer(Event.EVENTTYPE_TASK_CREATE)
    public void onCreateJbpmTask(ExecutionContext context) {
        Processo processo = processoManager.getProcessoByIdJbpm(context.getProcessInstance().getRoot().getId());
        if (processo != null) {
            TaskInstance taskInstance = context.getTaskInstance();
            createProcessoTarefa(processo, taskInstance);
        }
    }
    
    private void createProcessoTarefa(Processo processo, TaskInstance taskInstance) {
        String taskName = taskInstance.getTask().getName();
        String procDefName = taskInstance.getProcessInstance().getProcessDefinition().getName();
        Tarefa tarefa = tarefaManager.getTarefa(taskName, procDefName);
        ManagedJbpmContext.instance().getSession().flush();
        ProcessoTarefa pTarefa = new ProcessoTarefa();
        pTarefa.setProcesso(processoManager.find(processo.getIdProcesso()));
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
            LOG.error(".createProcesso(processo, taskInstance)", e);
        }
    }

    @Observer(Event.EVENTTYPE_PROCESS_END)
    public void onEndProcess(ExecutionContext context) throws DAOException {
    	//Se for um subprocesso não faz nada
    	if(context.getProcessInstance().getSuperProcessToken() != null) 
    		return;
    	
        Processo processo = processoManager.getProcessoByIdJbpm(context.getProcessInstance().getRoot().getId());
        if (processo == null) {
            throw new ApplicationException("Erro ao criar o processo - Defição de fluxo imcompleta. Contate o administrador do sistema.");
        }
        processo.setDataFim(new Date());
        processoManager.update(processo);
    }

}
