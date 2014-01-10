package br.com.infox.epp.estatistica.abstracts;

import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.estatistica.manager.BamTimerManager;
import br.com.infox.epp.tarefa.manager.ProcessoEpaTarefaManager;
import br.com.infox.epp.tarefa.type.PrazoEnum;

public abstract class BamTimerProcessor {
    private static final LogProvider LOG = Logging.getLogProvider(BamTimerProcessor.class);
    
    public abstract QuartzTriggerHandle increaseTimeSpent(@IntervalCron String cron);
    protected abstract String getParameterName();
    protected abstract ProcessoEpaTarefaManager getProcessoEpaTarefamanager();
    protected abstract BamTimerManager getBamTimerManager();

    protected final QuartzTriggerHandle updateTarefasNaoFinalizadas(PrazoEnum d) {
        String idTaskTimer = getBamTimerManager().getParametro(getParameterName());
        QuartzTriggerHandle handle = new QuartzTriggerHandle(idTaskTimer);
        
        Trigger trigger = null;
        try {
            trigger = handle.getTrigger();
        } catch (SchedulerException e) {
            LOG.error(e);
        }
        if (trigger != null) {
            try {
                getProcessoEpaTarefamanager().updateTarefasNaoFinalizadas(trigger.getPreviousFireTime(), d);
            } catch (DAOException e) {
                LOG.error(e);
            }
        }
        return null;
    }
}
