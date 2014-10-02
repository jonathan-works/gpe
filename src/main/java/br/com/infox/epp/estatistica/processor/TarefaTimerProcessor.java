package br.com.infox.epp.estatistica.processor;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.epp.estatistica.abstracts.BamTimerProcessor;
import br.com.infox.epp.estatistica.manager.BamTimerManager;
import br.com.infox.epp.estatistica.startup.BamTimerStarter;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.epp.tarefa.type.PrazoEnum;

/**
 * Processor que irá incrementar os tempos decorridos para cada tarefa aberta no
 * sistema, verificando a partir da localização do processo seus respectivos
 * turnos, calculando somente o horário útil gasto para cada tarefa em execução
 * do sistema.
 * 
 * @author Daniel
 */
@Name(TarefaTimerProcessor.NAME)
@AutoCreate
public class TarefaTimerProcessor extends BamTimerProcessor {
    public static final String NAME = "tarefaTimerProcessor";

    @In
    private ProcessoTarefaManager processoEpaTarefaManager;
    @In
    private BamTimerManager bamTimerManager;

    /**
     * Incrementa o tempo de cada tarefa, verificando se está dentro do turno da
     * sua localização.
     * 
     * @param cron - que está em execução
     * @return null
     */
    @Asynchronous
    @Transactional
    public QuartzTriggerHandle increaseTimeSpent(@IntervalCron String cron) {
        return updateTarefasNaoFinalizadas(PrazoEnum.H);
    }

    @Override
    protected ProcessoTarefaManager getProcessoEpaTarefamanager() {
        return processoEpaTarefaManager;
    }

    @Override
    protected BamTimerManager getBamTimerManager() {
        return bamTimerManager;
    }

    @Override
    protected String getParameterName() {
        return BamTimerStarter.ID_INICIAR_TASK_TIMER_PARAMETER;
    }

}
