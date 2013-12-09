package br.com.infox.epp.tarefa.manager;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Conversation;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.tarefa.dao.ProcessoEpaTarefaDAO;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;
import br.com.infox.epp.tarefa.type.PrazoEnum;
import br.com.infox.epp.turno.dao.LocalizacaoTurnoDAO;
import br.com.infox.epp.turno.entity.LocalizacaoTurno;
import br.com.infox.epp.turno.type.DiaSemanaEnum;

@Name(ProcessoEpaTarefaManager.NAME)
@AutoCreate
public class ProcessoEpaTarefaManager extends GenericManager {

	private static final int MEIA_HORA = 30;

    private static final int MINUTES_OF_HOUR = 60;

    private static final int PORCENTAGEM_MAXIMA = 100;

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
	
	public Map<String, Object> findProcessoEpaTarefaByIdProcessoAndIdTarefa(final Integer idProcesso, final Integer idTarefa) {
	    return processoEpaTarefaDAO.findProcessoEpaTarefaByIdProcessoAndIdTarefa(idProcesso, idTarefa);
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
	
	/**
	 * Atualiza os atributos referentes ao tempo gasto em
	 * uma tarefa caso exista incremento.
	 * @param fireTime
	 * @param tipoPrazo
	 * @throws DAOException 
	 */
	public void updateTarefasNaoFinalizadas(Date fireTime, PrazoEnum tipoPrazo) throws DAOException {
		for (ProcessoEpaTarefa pt : getTarefaNotEnded(tipoPrazo)) {
			updateTempoGasto(fireTime, pt);
		}
	}
	
	@Begin(nested=true, flushMode=FlushModeType.AUTO)
	public void updateTarefasFinalizadas() throws DAOException {
		for (ProcessoEpaTarefa pt : processoEpaTarefaDAO.getTarefaEnded()) {
			pt.setUltimoDisparo(pt.getDataInicio());
			pt.setTempoGasto(0);
			pt.setPorcentagem(0);
			updateTempoGasto(pt.getDataFim(), pt);
		}
		Conversation.instance().end();
	}
	
	public void forceUpdateTarefasNaoFinalizadas() {
	    Date fireTime = new Date();
	    for (ProcessoEpaTarefa pt : getTarefaNotEnded(PrazoEnum.H)) {
	        pt.setUltimoDisparo(pt.getDataInicio());
            try {
                updateTempoGasto(fireTime, pt);
            } catch (DAOException e) {
                e.printStackTrace();
            }
        }
	}
	
	public void updateTempoGasto(Date fireTime, ProcessoEpaTarefa processoEpaTarefa) throws DAOException {
		if (processoEpaTarefa.getTarefa().getTipoPrazo() == null) {
			return;
		}
		if (processoEpaTarefa.getUltimoDisparo().before(fireTime)) {
		    float incrementoTempoGasto = getIncrementoTempoGasto(fireTime, processoEpaTarefa);
			Integer prazo = processoEpaTarefa.getTarefa().getPrazo();
			int porcentagem = 0;
			int tempoGasto = (int)(processoEpaTarefa.getTempoGasto()+incrementoTempoGasto);
			if(prazo != null && prazo.compareTo(Integer.valueOf(0)) > 0) {
				porcentagem = (tempoGasto*PORCENTAGEM_MAXIMA)/(prazo* 60);
			}
            
            ProcessoEpa processoEpa = processoEpaTarefa.getProcessoEpa();
		    if (porcentagem > PORCENTAGEM_MAXIMA 
		    		&& processoEpa.getSituacaoPrazo() == SituacaoPrazoEnum.SAT) {
		        processoEpa.setSituacaoPrazo(SituacaoPrazoEnum.TAT);
		    }
		    
		    processoEpaTarefa.setPorcentagem(porcentagem);
			processoEpaTarefa.setTempoGasto(tempoGasto);
			processoEpaTarefa.setUltimoDisparo(fireTime);
            update(processoEpaTarefa);
		}
	}
	
	/**
	 * Calcula o tempo a incrementar no {@link ProcessoEpaTarefa} de acordo 
	 * com a data em que ocorreu o disparo.
	 * @param horaDisparo
	 * @param processoEpaTarefa
	 * @return Incremento a ser adicionado ao tempo gasto de um {@link ProcessoEpaTarefa}
	 */
	private float getIncrementoTempoGasto(Date horaDisparo, ProcessoEpaTarefa processoEpaTarefa) {
		PrazoEnum tipoPrazo = processoEpaTarefa.getTarefa().getTipoPrazo();
		float result = 0;
		if (tipoPrazo == null) {
			return 0;
		}
		switch (tipoPrazo) {
		case H:
			result = calcularTempoGastoHoras(horaDisparo, processoEpaTarefa.getTaskInstance(), processoEpaTarefa.getUltimoDisparo());
			break;
		case D:
			result = calcularTempoGastoDias(horaDisparo, processoEpaTarefa);
			break;
		}
		return result;
	}
	
	/**
	 * Pesquisa o turno da localizacao da tarefa em que o horário informado se encontra
	 * @param pt 
	 * @param horario 
	 * @return turno da localização da tarefa
	 */
	private LocalizacaoTurno getTurnoTarefa(Integer idProcesso,Date dataAnterior, Date dataAtual) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataAtual);
		int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);
		return localizacaoTurnoDAO.getTurnoTarefa(idProcesso,dataAnterior, dataAtual, DiaSemanaEnum.values()[diaSemana-1]);
	}

	private int getMinutesOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MINUTE) + (calendar.get(Calendar.HOUR_OF_DAY)*MINUTES_OF_HOUR);
	}
	
	/**
	 * Verifica as possibilidades para os intervalos do turno de 
	 * uma determinada localização, para então calcular a diferença 
	 * de horas que deve ser acrescentada ao tempo gasto de uma 
	 * tarefa.
	 * @return minutos gastos dentro do intervalo informado
	 */
	private float calcularMinutosEmIntervalo(Date inicio, Date fim, Date inicioTurno, Date fimTurno) {
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
	
	private void adjustCalendar(Calendar toGet, Calendar toSet1, Calendar toSet2, int field) {
	    int value = toGet.get(field);
	    toSet1.set(field, value);
	    toSet2.set(field, value);
	}
	
	private float calcularMinutosEmIntervalo(Date d1, Date d2) {
	    return (d2.getTime()-d1.getTime())/60000;
	}
	
	private float calcularTempoGastoHoras(final Date dataDisparo, final long idTaskInstance, final Date ultimoDisparo) {
	    float result = 0;
	    
	    final Calendar ultimaAtualizacao = new GregorianCalendar();
	    final Calendar disparoAtual = new GregorianCalendar();
	    disparoAtual.setTime(dataDisparo);
	    ultimaAtualizacao.setTime(ultimoDisparo);
	    while (ultimaAtualizacao.before(disparoAtual)) {
	        
	        final List<LocalizacaoTurno> localizacoes = localizacaoTurnoDAO.getTurnosTarefa(idTaskInstance, DiaSemanaEnum.values()[ultimaAtualizacao.get(Calendar.DAY_OF_WEEK)-1], ultimaAtualizacao.getTime());
	        for (int i = 0,l=localizacoes.size(); i < l; i++) {
	            final Calendar inicioTurno = new GregorianCalendar();
	            inicioTurno.setTime(localizacoes.get(i).getHoraInicio());
	            
	            final Calendar fimTurno = new GregorianCalendar();
	            fimTurno.setTime(localizacoes.get(i).getHoraFim());
	            
	            adjustCalendar(ultimaAtualizacao, inicioTurno, fimTurno, Calendar.DAY_OF_MONTH);
	            adjustCalendar(ultimaAtualizacao, inicioTurno, fimTurno, Calendar.MONTH);
	            adjustCalendar(ultimaAtualizacao, inicioTurno, fimTurno, Calendar.YEAR);
	            
                result += getIncrementoLocalizacaoTurno(disparoAtual.getTime(), ultimaAtualizacao, localizacoes.get(i), inicioTurno, fimTurno);
                if (!ultimaAtualizacao.before(disparoAtual)) {
                    break;
                }
            }
	        if (ultimaAtualizacao.before(disparoAtual)) {
	            ultimaAtualizacao.set(Calendar.HOUR_OF_DAY, 0);
	            ultimaAtualizacao.set(Calendar.MINUTE, 0);
                ultimaAtualizacao.set(Calendar.SECOND, 0);
                ultimaAtualizacao.set(Calendar.MILLISECOND, 0);
                ultimaAtualizacao.add(Calendar.DAY_OF_MONTH, 1);
            } 
	    }
	    
	    return result;
	}

    private float getIncrementoLocalizacaoTurno(final Date dataDisparo,final Calendar ultimaAtualizacao,
            final LocalizacaoTurno localizacaoTurno,final Calendar inicioTurno,final Calendar fimTurno) {
        float result = 0f;
        if (inicioTurno.after(dataDisparo)) {
            
            if (fimTurno.before(dataDisparo)) {
                result += localizacaoTurno.getTempoTurno();
                ultimaAtualizacao.setTime(fimTurno.getTime());
            } else {
                result += calcularMinutosEmIntervalo(inicioTurno.getTime(), dataDisparo);
                ultimaAtualizacao.setTime(dataDisparo);
            }
            
        } else {
            
            if (fimTurno.before(dataDisparo)) {
                result += calcularMinutosEmIntervalo(ultimaAtualizacao.getTime(), fimTurno.getTime());
                ultimaAtualizacao.setTime(fimTurno.getTime());
            } else {
                result += calcularMinutosEmIntervalo(ultimaAtualizacao.getTime(), dataDisparo);
                ultimaAtualizacao.setTime(dataDisparo);
            }
            
        }
        return result;
    }
	
	private float calcularTempoGastoHoras(Date dataDisparo, ProcessoEpaTarefa processoEpaTarefa) {
		int result = 0;
		Date ultimaAtualizacao = processoEpaTarefa.getUltimoDisparo();
		while(ultimaAtualizacao.before(dataDisparo))	{
			Date disparoAtual = getDisparoIncrementado(ultimaAtualizacao, dataDisparo, Calendar.MINUTE, MEIA_HORA);
			LocalizacaoTurno localizacaoTurno = getTurnoTarefa(processoEpaTarefa.getProcessoEpa().getIdProcesso(), ultimaAtualizacao, disparoAtual);
			if (localizacaoTurno != null) {
				result += calcularMinutosEmIntervalo(ultimaAtualizacao, disparoAtual, localizacaoTurno.getHoraInicio(), localizacaoTurno.getHoraFim());
			}
			ultimaAtualizacao = disparoAtual;
		}
		return result;
	}
	
	private int calcularTempoGastoDias(Date dataDisparo, ProcessoEpaTarefa processoEpaTarefa) {
		int result = 0;
		Date ultimaAtualizacao = processoEpaTarefa.getUltimoDisparo();
		
		while(ultimaAtualizacao.before(dataDisparo)) {
			Date disparoAtual = getDisparoIncrementado(ultimaAtualizacao, dataDisparo, Calendar.DAY_OF_MONTH, 1);
			if (contemTurnoTarefaDia(processoEpaTarefa, disparoAtual)) {
				result += DateUtil.diferencaDias(disparoAtual, ultimaAtualizacao);
			}
			ultimaAtualizacao = disparoAtual;
		}
		
		return result;
	}
}