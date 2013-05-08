package br.com.infox.epp.processor;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import br.com.infox.epp.manager.ProcessoEpaManager;
import br.com.infox.epp.manager.ProcessoEpaTarefaManager;
import br.com.infox.epp.service.startup.ProcessoTimerStarter;
import br.com.infox.ibpm.type.PrazoEnum;
import br.com.infox.timer.TimerUtil;

/**
 * Processor que ir� incrementar os tempos decorridos
 * para cada tarefa aberta no sistema, verificando a 
 * partir da localiza��o do processo seus respectivos 
 * turnos, calculando somente o hor�rio �til gasto para 
 * cada tarefa em execu��o do sistema.
 * @author Daniel
 *
 */
@Name(ProcessoTimerProcessor.NAME)
@AutoCreate
public class ProcessoTimerProcessor {

	public static final String NAME = "processoTimerProcessor";
	
	@In private ProcessoEpaManager processoEpaManager;
	@In private ProcessoEpaTarefaManager processoEpaTarefaManager;
	
	public static ProcessoTimerProcessor instance() {
		return (ProcessoTimerProcessor) Component.getInstance(NAME);
	}	
	
	/**
	 * Incrementa o tempo de cada tarefa, verificando seus turnos.
	 * @param cron - que est� em execu��o
	 * @return null
	 */
	@Asynchronous
	@Transactional
	public QuartzTriggerHandle increaseProcessTimeSpent(@IntervalCron String cron) {
		String idTaskTimer = null;
		idTaskTimer = TimerUtil.getParametro(ProcessoTimerStarter.ID_INICIAR_PROCESSO_TIMER_PARAMETER);
		QuartzTriggerHandle handle = new QuartzTriggerHandle(idTaskTimer);
		Trigger trigger = null;
		try {
			trigger = handle.getTrigger();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		if (trigger != null) {
			processoEpaManager.updateTempoGastoProcessoEpa();
			processoEpaTarefaManager.updateTempoGasto(trigger.getPreviousFireTime(), PrazoEnum.D);
		}
		return null;
	}

}