package br.com.infox.epa.processor;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import br.com.infox.epa.entity.LocalizacaoTurno;
import br.com.infox.epa.entity.ProcessoEpaTarefa;
import br.com.infox.epa.manager.LocalizacaoTurnoManager;
import br.com.infox.epa.manager.ProcessoEpaTarefaManager;
import br.com.infox.epa.service.startup.TarefaTimerStarter;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.manager.ProcessoLocalizacaoIbpmManager;
import br.com.infox.timer.TimerUtil;

/**
 * Processor que irá incrementar os tempos decorridos
 * para cada tarefa aberta no sistema, verificando a 
 * partir da localização do processo seus respectivos 
 * turnos, calculando somente o horário útil gasto para 
 * cada tarefa em execução do sistema.
 * @author Daniel
 *
 */
@Name(TarefaTimerProcessor.NAME)
@AutoCreate
public class TarefaTimerProcessor {

	public static final String NAME = "tarefaTimerProcessor";
	
	@In
	private ProcessoEpaTarefaManager processoEpaTarefaManager;
	@In
	private LocalizacaoTurnoManager localizacaoTurnoManager;
	@In
	private ProcessoLocalizacaoIbpmManager processoLocalizacaoIbpmManager;
	
	public static TarefaTimerProcessor instance() {
		return (TarefaTimerProcessor) Component.getInstance(NAME);
	}	
	
	/**
	 * Incrementa o tempo de cada tarefa, verificando seus turnos.
	 * @param cron - que está em execução
	 * @return null
	 */
	@Asynchronous
	@Transactional
	public QuartzTriggerHandle increaseTaskTimeSpent(@IntervalCron String cron) {
		String idTaskTimer = null;
		idTaskTimer = TimerUtil.getParametro(TarefaTimerStarter.ID_INICIAR_TASK_TIMER_PARAMETER);
		QuartzTriggerHandle handle = new QuartzTriggerHandle(idTaskTimer);
		Trigger trigger = null;
		try {
			trigger = handle.getTrigger();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		if (trigger == null) {
			return null;
		}
		Date fireTime = trigger.getPreviousFireTime();
		for (ProcessoEpaTarefa pt : processoEpaTarefaManager.getAllNotEnded()) {
			List<Localizacao> byTaskInstance = processoLocalizacaoIbpmManager.
					listByTaskInstance(pt.getTaskInstance());
			int tempoGasto = 0;
			for(Localizacao localizacao : byTaskInstance) {
				for (LocalizacaoTurno lt : localizacao.getLocalizacaoTurnoList()) {
					tempoGasto = localizacaoTurnoManager.calcularMinutosGastos(fireTime, pt.getUltimoDisparo(), lt);
				}
			}
			pt.setTempoGasto(pt.getTempoGasto() + tempoGasto);
			pt.setUltimoDisparo(fireTime);
			if(pt.getTempoPrevisto() == 0) {	
				pt.setPorcentagem(-1);
			} else {
				pt.setPorcentagem((pt.getTempoGasto()*100)/pt.getTempoPrevisto());
			}
			processoEpaTarefaManager.update(pt);
		}
		return null;
	}
	
}