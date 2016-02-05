package br.com.infox.epp.quartz.ws.impl;

import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.contexts.Lifecycle;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.estatistica.manager.BamTimerManager;
import br.com.infox.epp.quartz.ws.BamResource;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.epp.tarefa.type.PrazoEnum;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class BamResourceImpl implements BamResource {
    
    private static final LogProvider LOG = Logging.getLogProvider(BamResourceImpl.class);
    
    private BamTimerManager bamTimerManager;
    private ProcessoTarefaManager processoTarefaManager;
    
    public BamResourceImpl(BamTimerManager bamTimerManager, ProcessoTarefaManager processoTarefaManager) {
        this.bamTimerManager = bamTimerManager;
        this.processoTarefaManager = processoTarefaManager;
    }

    @Override
    public void tarefaTimerProcessor(String parameterName, PrazoEnum prazo) {
        Lifecycle.beginCall();
        try {
            updateTarefasNaoFinalizadas(prazo, parameterName);
        } finally {
            Lifecycle.endCall();
        }
    }

    @Override
    public void processoTimerProcessor(String parameterName, PrazoEnum prazo) {
        Lifecycle.beginCall();
        try {
            updateTarefasNaoFinalizadas(prazo, parameterName);
        } finally {
            Lifecycle.endCall();
        }
    }
    
    private void updateTarefasNaoFinalizadas(PrazoEnum d, String parameterName) {
        String idTaskTimer = bamTimerManager.getParametro(parameterName);
        QuartzTriggerHandle handle = new QuartzTriggerHandle(idTaskTimer);
        Trigger trigger = null;
        try {
            trigger = handle.getTrigger();
        } catch (SchedulerException e) {
            LOG.error("Não foi possivel obter a trigger do Quartz", e);
        }
        if (trigger != null) {
            try {
                processoTarefaManager.updateTarefasNaoFinalizadas(trigger.getPreviousFireTime(), d);
            } catch (DAOException e) {
                LOG.error(".updateTarefasNaoFinalizadas(d)", e);
            }
        }
    }

}
