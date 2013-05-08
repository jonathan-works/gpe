package br.com.infox.epp.manager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.LocalizacaoTurnoDAO;
import br.com.infox.epp.dao.ProcessoEpaTarefaDAO;
import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.entity.LocalizacaoTurno;
import br.com.infox.epp.entity.ProcessoEpa;
import br.com.infox.epp.entity.ProcessoEpaTarefa;
import br.com.infox.epp.type.DiaSemanaEnum;
import br.com.infox.epp.type.SituacaoPrazoEnum;
import br.com.infox.ibpm.type.PrazoEnum;

@Name(ProcessoEpaTarefaManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoEpaTarefaManager extends GenericManager {

	private static final long serialVersionUID = 7702766272346991620L;

	public static final String NAME = "processoEpaTarefaManager";

	@In private ProcessoEpaTarefaDAO processoEpaTarefaDAO;
	@In private LocalizacaoTurnoDAO localizacaoTurnoDAO;
	
	public ProcessoEpaTarefa getByTaskInstance(Long taskInstance) {
		return processoEpaTarefaDAO.getByTaskInstance(taskInstance);
	}
	
	public List<ProcessoEpaTarefa> getTarefaNotEnded(PrazoEnum tipoPrazo) {
		return processoEpaTarefaDAO.getTarefaNotEnded(tipoPrazo);
	}
	
	public List<Object[]> listForaPrazoFluxo(Categoria c) {
		return processoEpaTarefaDAO.listForaPrazoFluxo(c);
	}
	
	public List<Object[]> listForaPrazoTarefa(Categoria c) {
		return processoEpaTarefaDAO.listForaPrazoTarefa(c);
	}
	
	public List<Object[]> listTarefaPertoLimite() {
		return processoEpaTarefaDAO.listTarefaPertoLimite();
	}
	
	/**
	 * Calcula o tempo a incrementar no {@link ProcessoEpaTarefa} de acordo 
	 * com a data em que ocorreu o disparo.
	 * @param horaDisparo
	 * @param processoEpaTarefa
	 * @return Incremento a ser adicionado ao tempo gasto de um {@link ProcessoEpaTarefa}
	 */
	private int getIncrementoTempoGasto(Date horaDisparo, ProcessoEpaTarefa processoEpaTarefa) {
		PrazoEnum tipoPrazo = processoEpaTarefa.getTarefa().getTipoPrazo();
		int result = 0;
		switch (tipoPrazo) {
		case H:
			result = calcularTempoGastoHoras(horaDisparo, processoEpaTarefa);
			break;
		case D:
			result = calcularTempoGastoDias(horaDisparo, processoEpaTarefa);
			break;
		}
		return result;
	}
	/**
	 * Atualiza os atributos referentes ao tempo gasto em
	 * uma tarefa caso exista incremento.
	 * @param fireTime
	 * @param tipoPrazo
	 */
	public void updateTempoGasto(Date fireTime, PrazoEnum tipoPrazo) {
		for (ProcessoEpaTarefa pt : getTarefaNotEnded(tipoPrazo)) {
			int incrementoTempoGasto = getIncrementoTempoGasto(fireTime, pt);
			if (incrementoTempoGasto > 0) {
				Integer prazo = pt.getTarefa().getPrazo();
				int porcentagem = 0;
				int tempoGasto = pt.getTempoGasto()+incrementoTempoGasto;
				if(prazo != null && prazo.compareTo(Integer.valueOf(0)) > 0) {
					porcentagem = (tempoGasto*100)/prazo;
				}
	            
	            ProcessoEpa processoEpa = pt.getProcessoEpa();
			    if (porcentagem > 100 
			    		&& processoEpa.getSituacaoPrazo() == SituacaoPrazoEnum.SAT) {
			        processoEpa.setSituacaoPrazo(SituacaoPrazoEnum.TAT);
			    }
			    
			    pt.setPorcentagem(porcentagem);
				pt.setTempoGasto(tempoGasto);
				pt.setUltimoDisparo(fireTime);
	            update(pt);
			}
		}
	}
	
	/**
	 * Pesquisa o turno da localizacao da tarefa em que o horário informado se encontra
	 * @param pt 
	 * @param horario 
	 * @return turno da localização da tarefa
	 */
	private LocalizacaoTurno getTurnoTarefa(ProcessoEpaTarefa pt, Date data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);
		return localizacaoTurnoDAO.getTurnoTarefa(pt, data, DiaSemanaEnum.values()[diaSemana-1]);
	}

	private int getMinutesOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MINUTE) + (calendar.get(Calendar.HOUR_OF_DAY)*60);
	}
	
	/**
	 * Verifica as possibilidades para os intervalos do turno de 
	 * uma determinada localização, para então calcular a diferença 
	 * de horas que deve ser acrescentada ao tempo gasto de uma 
	 * tarefa.
	 * @return minutos gastos dentro do intervalo informado
	 */
	private long calcularMinutosEmIntervalo(Date inicio, Date fim, Date inicioTurno, Date fimTurno) {
		int minutesBegin = Math.max(getMinutesOfDay(inicio), getMinutesOfDay(inicioTurno));
		int minutesEnd = Math.min(getMinutesOfDay(fim), getMinutesOfDay(fimTurno));
		
		if (minutesBegin < minutesEnd) {
			return minutesEnd - minutesBegin;
		}
		return 0;
	}
	
	private Date getDisparoIncrementado(Date ultimoDisparo, Date disparoAtual, int tipoIncremento, int incremento) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ultimoDisparo);
        calendar.add(tipoIncremento, incremento);
        Date proxDisparo = calendar.getTime();
        
        if (proxDisparo.before(disparoAtual)) {
            return proxDisparo;
        } else {
            return disparoAtual;
        }
	}
	
	private int calcularTempoGastoHoras(Date dataDisparo, ProcessoEpaTarefa processoEpaTarefa) {
		int result = 0;
		Date ultimaAtualizacao = processoEpaTarefa.getUltimoDisparo();
		
		while(ultimaAtualizacao.before(dataDisparo))	{
			Date disparoAtual = getDisparoIncrementado(ultimaAtualizacao, dataDisparo, Calendar.MINUTE, 30);
			LocalizacaoTurno localizacaoTurno = getTurnoTarefa(processoEpaTarefa, disparoAtual);
			if (localizacaoTurno != null) {
				result += calcularMinutosEmIntervalo(ultimaAtualizacao, disparoAtual, localizacaoTurno.getHoraInicio(), localizacaoTurno.getHoraFim());
			}
			ultimaAtualizacao = disparoAtual;
		}
		
		return result/60;
	}
	
	private int calcularDiasEmIntervalo(Date inicio, Date fim) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inicio);
		int diaInicio = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.setTime(fim);
		int diaFim = calendar.get(Calendar.DAY_OF_YEAR);
		return diaFim - diaInicio;
	}
	
	private int calcularTempoGastoDias(Date dataDisparo, ProcessoEpaTarefa processoEpaTarefa) {
		int result = 0;
		Date ultimaAtualizacao = processoEpaTarefa.getUltimoDisparo();
		
		while(ultimaAtualizacao.before(dataDisparo)) {
			Date disparoAtual = getDisparoIncrementado(ultimaAtualizacao, dataDisparo, Calendar.DAY_OF_MONTH, 1);
			if (contemTurnoTarefaDia(processoEpaTarefa, disparoAtual)) {
				result += calcularDiasEmIntervalo(ultimaAtualizacao, disparoAtual);
			}
			ultimaAtualizacao = disparoAtual;
		}
		
		return result;
	}
	
	/**
	 * Verifica se existe algum turno da localizacao da tarefa em que no dia informado
	 * @param pt 
	 * @param horario 
	 * @return turno da localização da tarefa
	 */
	public boolean contemTurnoTarefaDia(ProcessoEpaTarefa pt, Date data) {
		Calendar horarioCalendar = Calendar.getInstance();
		int diaSemana = horarioCalendar.get(Calendar.DAY_OF_WEEK);
		return localizacaoTurnoDAO.countTurnoTarefaDia(pt, data, DiaSemanaEnum.values()[diaSemana-1]) > 0;
	}
}