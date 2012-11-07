package br.com.infox.epa.processor;

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

import br.com.infox.epa.entity.ProcessoEpa;
import br.com.infox.epa.entity.ProcessoEpaTarefa;
import br.com.infox.epa.manager.LocalizacaoTurnoManager;
import br.com.infox.epa.manager.ProcessoEpaManager;
import br.com.infox.epa.manager.ProcessoEpaTarefaManager;
import br.com.infox.epa.service.startup.ProcessoTimerStarter;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.entity.Tarefa;
import br.com.infox.ibpm.type.PrazoEnum;
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
@Name(ProcessoTimerProcessor.NAME)
@AutoCreate
public class ProcessoTimerProcessor {

	public static final String NAME = "processoTimerProcessor";
	
	@In
	private ProcessoEpaManager processoEpaManager;
	@In
	private ProcessoEpaTarefaManager processoEpaTarefaManager;
	@In
	private LocalizacaoTurnoManager localizacaoTurnoManager;
	
	public static ProcessoTimerProcessor instance() {
		return (ProcessoTimerProcessor) Component.getInstance(NAME);
	}	
	
	/**
	 * Incrementa o tempo de cada tarefa, verificando seus turnos.
	 * @param cron - que está em execução
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
		if (trigger == null) {
			return null;
		}
		updateTempoGastoProcessoEpa();
		updateTempoGastoProcessoEpaTarefa(trigger.getPreviousFireTime());
		return null;
	}

	private void updateTempoGastoProcessoEpa() {
		List<ProcessoEpa> listAllNotEnded = processoEpaManager.listAllNotEnded();
		for (ProcessoEpa processoEpa : listAllNotEnded) {
			Fluxo f = processoEpa.getNaturezaCategoriaFluxo().getFluxo();
			
			Integer tempoGasto = processoEpa.getTempoGasto();
			if (tempoGasto == null) {
				tempoGasto = 0;
			}
			processoEpa.setTempoGasto(tempoGasto + 1);
			if(f.getQtPrazo() != null && f.getQtPrazo() != 0) {
				processoEpa.setPorcentagem((processoEpa.getTempoGasto()*100)/
						f.getQtPrazo());
			}
			processoEpaManager.update(processoEpa);
		}
	}
	
	private void updateTempoGastoProcessoEpaTarefa(Date fireTime) {
		for (ProcessoEpaTarefa pt : processoEpaTarefaManager.getTarefaNotEnded(PrazoEnum.D)) {
			if (localizacaoTurnoManager.contemTurnoTarefaDia(pt, fireTime)) {
				Tarefa tarefa = pt.getTarefa();
				
				Integer tempoGasto = pt.getTempoGasto();
				if (tempoGasto == null) {
					tempoGasto = 0;
				}
				pt.setTempoGasto(tempoGasto + 1);
				if(tarefa.getPrazo() != null && tarefa.getPrazo() != 0) {
					pt.setPorcentagem((pt.getTempoGasto()*100)/
							tarefa.getPrazo());
				}
				processoEpaTarefaManager.update(pt);
			}
		}
	}
}