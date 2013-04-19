package br.com.infox.epp.processor;

import java.util.Calendar;
import java.util.Date;

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

import br.com.infox.epp.entity.LocalizacaoTurno;
import br.com.infox.epp.entity.ProcessoEpa;
import br.com.infox.epp.entity.ProcessoEpaTarefa;
import br.com.infox.epp.manager.LocalizacaoTurnoManager;
import br.com.infox.epp.manager.ProcessoEpaTarefaManager;
import br.com.infox.epp.service.startup.TarefaTimerStarter;
import br.com.infox.epp.type.SituacaoPrazoEnum;
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
@Name(TarefaTimerProcessor.NAME)
@AutoCreate
public class TarefaTimerProcessor {

	public static final String NAME = "tarefaTimerProcessor";
	
	@In
	private ProcessoEpaTarefaManager processoEpaTarefaManager;
	@In
	private LocalizacaoTurnoManager localizacaoTurnoManager;
	
	public static TarefaTimerProcessor instance() {
		return (TarefaTimerProcessor) Component.getInstance(NAME);
	}	
	
	/**
	 * Incrementa o tempo de cada tarefa, verificando se está dentro do turno da sua localização.
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
		for (ProcessoEpaTarefa pt : processoEpaTarefaManager.getTarefaNotEnded(PrazoEnum.H)) {
            while (pt.getUltimoDisparo().before(fireTime)) {
                LocalizacaoTurno lt = localizacaoTurnoManager.getTurnoTarefa(pt, fireTime);
                Date disparoAtual = getDisparoAtual(pt.getUltimoDisparo(), fireTime);
                if (lt != null) {
                    pt.setTempoGasto(pt.getTempoGasto()+localizacaoTurnoManager.calcularMinutosGastos(disparoAtual, pt.getUltimoDisparo(),lt));
                    if (pt.getTarefa().getPrazo() == 0) {
                        pt.setPorcentagem(-1);
                    } else {
                        pt.setTempoPrevisto(pt.getTarefa().getPrazo() * 60);
                        pt.setPorcentagem((pt.getTempoGasto() * 100)
                                / pt.getTempoPrevisto());
                    }

                    ProcessoEpa processoEpa = pt.getProcessoEpa();
                    if (pt.getPorcentagem() > 100
                            && processoEpa.getSituacaoPrazo() == SituacaoPrazoEnum.SAT) {
                        processoEpa.setSituacaoPrazo(SituacaoPrazoEnum.TAT);
                    }
                }

                pt.setUltimoDisparo(disparoAtual);
            }
            processoEpaTarefaManager.update(pt);
		}
		
		return null;
	}
	
	private Date getDisparoAtual(Date ultimoDisparo, Date fireTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ultimoDisparo);
        calendar.add(Calendar.MINUTE, 30);
        Date proxDisparo = calendar.getTime();
        
        if (proxDisparo.before(fireTime)) {
            return proxDisparo;
        } else {
            return fireTime;
        }
	}
	
}