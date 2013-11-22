package br.com.infox.epp.estatistica.processor;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.estatistica.startup.TarefaTimerStarter;
import br.com.infox.epp.estatistica.timer.TimerUtil;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.tarefa.manager.ProcessoEpaTarefaManager;
import br.com.infox.ibpm.type.PrazoEnum;

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
public class TarefaTimerProcessor {

	private static final LogProvider LOG = Logging
			.getLogProvider(TarefaTimerProcessor.class);
	public static final String NAME = "tarefaTimerProcessor";

	@In
	private ProcessoEpaTarefaManager processoEpaTarefaManager;
	@In
    private ProcessoEpaManager processoEpaManager;
	
	public static TarefaTimerProcessor instance() {
		return (TarefaTimerProcessor) Component.getInstance(NAME);
	}

	/**
	 * Incrementa o tempo de cada tarefa, verificando se está dentro do turno da
	 * sua localização.
	 * 
	 * @param cron
	 *            - que está em execução
	 * @return null
	 */
	@Asynchronous
	@Transactional
	public QuartzTriggerHandle increaseTaskTimeSpent(@IntervalCron String cron) {
		String idTaskTimer = null;
		idTaskTimer = TimerUtil
				.getParametro(TarefaTimerStarter.ID_INICIAR_TASK_TIMER_PARAMETER);
		QuartzTriggerHandle handle = new QuartzTriggerHandle(idTaskTimer);
		Trigger trigger = null;
		try {
			trigger = handle.getTrigger();
		} catch (SchedulerException e) {
			LOG.error("TarefaTimerProcessor.increaseTaskTimeSpent()", e);
		}
		if (trigger != null) {
			try {
				processoEpaTarefaManager.updateTarefasNaoFinalizadas(
						trigger.getPreviousFireTime(), PrazoEnum.H);
				processoEpaManager.updateTempoGastoProcessoEpa();
			} catch (DAOException e) {
				LOG.error(".increaseTaskTimeSpent()", e);
			}
		}
		return null;
	}

}