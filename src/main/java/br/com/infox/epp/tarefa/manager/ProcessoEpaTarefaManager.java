package br.com.infox.epp.tarefa.manager;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateRange;
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

    private static final int PORCENTAGEM_MAXIMA = 100;

    private static final long serialVersionUID = 7702766272346991620L;

	public static final String NAME = "processoEpaTarefaManager";

	@In private ProcessoEpaTarefaDAO processoEpaTarefaDAO;
	@In private LocalizacaoTurnoDAO localizacaoTurnoDAO;
	
	public ProcessoEpaTarefa getByTaskInstance(Long taskInstance) {
		return processoEpaTarefaDAO.getByTaskInstance(taskInstance);
	}
	
	public List<ProcessoEpaTarefa> getTarefaEnded() {
	    return processoEpaTarefaDAO.getTarefaEnded();
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
	
	private float calcularTempoGastoHoras(final Date dataDisparo, final long idTaskInstance, final Date ultimoDisparo) {
	    float result = 0;
	    
	    final Calendar ultimaAtualizacao = new GregorianCalendar();
	    final Calendar disparoAtual = new GregorianCalendar();
	    disparoAtual.setTime(dataDisparo);
	    ultimaAtualizacao.setTime(ultimoDisparo);
	    while (ultimaAtualizacao.before(disparoAtual)) {
	        
	        final List<LocalizacaoTurno> localizacoes = localizacaoTurnoDAO.getTurnosTarefa(idTaskInstance, DiaSemanaEnum.values()[ultimaAtualizacao.get(Calendar.DAY_OF_WEEK)-1], ultimaAtualizacao.getTime());
	        for (int i = 0,l=localizacoes.size(); i < l; i++) {
                LocalizacaoTurno localizacaoTurno = localizacoes.get(i);
	            final Calendar inicioTurno = new GregorianCalendar();
                inicioTurno.setTime(localizacaoTurno.getHoraInicio());
	            
	            final Calendar fimTurno = new GregorianCalendar();
	            fimTurno.setTime(localizacaoTurno.getHoraFim());
	            
	            adjustCalendar(ultimaAtualizacao, inicioTurno, fimTurno, Calendar.DAY_OF_MONTH);
	            adjustCalendar(ultimaAtualizacao, inicioTurno, fimTurno, Calendar.MONTH);
	            adjustCalendar(ultimaAtualizacao, inicioTurno, fimTurno, Calendar.YEAR);
	            DateRange range = getIncrementoLocalizacaoTurno(disparoAtual.getTime(), ultimaAtualizacao, localizacaoTurno, inicioTurno, fimTurno); 
                result = result + range.get(DateRange.MINUTES);
                
                ultimaAtualizacao.setTime(range.getEnd());
                
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
	
    private DateRange getIncrementoLocalizacaoTurno(final Date dataDisparo,final Calendar ultimaAtualizacao,
            final LocalizacaoTurno localizacaoTurno,final Calendar inicioTurno,final Calendar fimTurno) {
        final Date beginning = inicioTurno.after(dataDisparo) ? inicioTurno.getTime() : ultimaAtualizacao.getTime(); 
        final Date end = fimTurno.before(dataDisparo) ? fimTurno.getTime() : dataDisparo;
        //final float result = calcularMinutosEmIntervalo(beginning, end);
        //ultimaAtualizacao.setTime(end);
        return new DateRange(beginning, end);
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