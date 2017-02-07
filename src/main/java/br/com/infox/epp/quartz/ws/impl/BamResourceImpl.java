package br.com.infox.epp.quartz.ws.impl;

import java.util.List;

import javax.inject.Inject;

import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.contexts.Lifecycle;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import br.com.infox.epp.estatistica.manager.BamTimerManager;
import br.com.infox.epp.quartz.ws.BamResource;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.epp.tarefa.type.PrazoEnum;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class BamResourceImpl implements BamResource {
    
    private static final LogProvider LOG = Logging.getLogProvider(BamResourceImpl.class);
    
    @Inject
    private BamTimerManager bamTimerManager;
    @Inject
    private ProcessoTarefaManager processoTarefaManager;
    
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
    
    private void updateTarefasNaoFinalizadas(PrazoEnum tipoPrazo, String parameterName) {
        String idTaskTimer = bamTimerManager.getParametro(parameterName);
        QuartzTriggerHandle handle = new QuartzTriggerHandle(idTaskTimer);
        Trigger trigger = null;
        try {
            trigger = handle.getTrigger();
        } catch (SchedulerException e) {
            LOG.error("Não foi possivel obter a trigger do Quartz", e);
        }
        if (trigger != null) {
            List<ProcessoTarefa> processoTarefaNotEndedList = processoTarefaManager.getTarefaNotEnded(tipoPrazo);
            for (ProcessoTarefa processoTarefaNotEnded : processoTarefaNotEndedList) {
                try {
                    processoTarefaManager.updateTempoGasto(trigger.getPreviousFireTime(), processoTarefaNotEnded);
                } catch (Exception exception) {
                    LOG.error(".updateTarefasNaoFinalizadas(d)", exception);
                }
            }
        }
    }

}
