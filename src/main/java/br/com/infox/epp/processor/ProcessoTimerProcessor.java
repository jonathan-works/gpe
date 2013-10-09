package br.com.infox.epp.processor;

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

import br.com.infox.epp.manager.ProcessoEpaManager;
import br.com.infox.epp.manager.ProcessoEpaTarefaManager;
import br.com.infox.epp.service.startup.ProcessoTimerStarter;
import br.com.infox.ibpm.type.PrazoEnum;
import br.com.infox.timer.TimerUtil;

/**
 * Processor que irá incrementar os tempos decorridos para cada tarefa aberta no
 * sistema, verificando a partir da localização do processo seus respectivos
 * turnos, calculando somente o horário útil gasto para cada tarefa em execução
 * do sistema.
 * 
 * @author Daniel
 */
@Name(ProcessoTimerProcessor.NAME)
@AutoCreate
public class ProcessoTimerProcessor {

	private static final LogProvider LOG = Logging
			.getLogProvider(ProcessoTimerProcessor.class);
	public static final String NAME = "processoTimerProcessor";

	@In
	private ProcessoEpaManager processoEpaManager;
	@In
	private ProcessoEpaTarefaManager processoEpaTarefaManager;

	public static ProcessoTimerProcessor instance() {
		return (ProcessoTimerProcessor) Component.getInstance(NAME);
	}

	/**
	 * Incrementa o tempo de cada tarefa, verificando seus turnos.
	 * 
	 * @param cron
	 *            - que está em execução
	 * @return null
	 */
	@Asynchronous
	@Transactional
	public QuartzTriggerHandle increaseProcessTimeSpent(
			@IntervalCron String cron) {
		String idTaskTimer = null;
		idTaskTimer = TimerUtil
				.getParametro(ProcessoTimerStarter.ID_INICIAR_PROCESSO_TIMER_PARAMETER);
		QuartzTriggerHandle handle = new QuartzTriggerHandle(idTaskTimer);
		Trigger trigger = null;
		try {
			trigger = handle.getTrigger();
		} catch (SchedulerException e) {
			LOG.error("ProcessoTimerProcessor.increaseProcessTimeSpent()", e);
		}
		if (trigger != null) {
			processoEpaManager.updateTempoGastoProcessoEpa();
			processoEpaTarefaManager.updateTarefasNaoFinalizadas(
					trigger.getPreviousFireTime(), PrazoEnum.D);
		}
		return null;
	}

}