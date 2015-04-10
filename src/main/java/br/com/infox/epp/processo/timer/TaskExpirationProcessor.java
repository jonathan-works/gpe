package br.com.infox.epp.processo.timer;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.bpm.BusinessProcess;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.joda.time.DateTime;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.processo.timer.manager.TaskExpirationManager;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(TaskExpirationProcessor.NAME)
@AutoCreate
public class TaskExpirationProcessor {

    public static final String NAME = "taskExpirationProcessor";
    private static final LogProvider LOG = Logging.getLogProvider(TaskExpirationProcessor.class);

    @In
    private ProcessoTarefaManager processoTarefaManager;
    @In
    private TaskExpirationManager taskExpirationManager;

    @Asynchronous
    public QuartzTriggerHandle processTaskExpiration(@IntervalCron final String cron) {
        final List<ProcessoTarefa> processoTarefaList = this.processoTarefaManager.getWithTaskExpiration();
        for (final ProcessoTarefa processoTarefa : processoTarefaList) {
            final TaskExpiration taskExpiration = this.taskExpirationManager.getByFluxoAndTaskName(processoTarefa.getProcesso().getNaturezaCategoriaFluxo().getFluxo(), processoTarefa.getTarefa().getTarefa());
            if (taskExpiration != null) {
                final DateTime expirationDate = new DateTime(DateUtil.getEndOfDay(taskExpiration.getExpiration()));
                if (expirationDate.isBeforeNow()) {
                    BusinessProcess.instance().setProcessId(processoTarefa.getProcesso().getIdJbpm());
                    BusinessProcess.instance().setTaskId(processoTarefa.getTaskInstance());
                    final TaskInstance taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
                    try {
                        this.processoTarefaManager.finalizarInstanciaTarefa(taskInstance, taskExpiration.getTransition());
                    } catch (final DAOException e) {
                        LOG.error("taskExpirationProcessor.processTaskExpiration()", e);
                    }
                }
            }
        }
        return null;
    }
}
