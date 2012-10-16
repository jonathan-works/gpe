package br.com.infox.epa.manager;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epa.dao.LocalizacaoTurnoDAO;
import br.com.infox.epa.entity.LocalizacaoTurno;
import br.com.infox.epa.entity.ProcessoEpaTarefa;
import br.com.infox.epa.type.DiaSemanaEnum;
import br.com.infox.ibpm.entity.Localizacao;

@Name(LocalizacaoTurnoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class LocalizacaoTurnoManager extends GenericManager {

	private static final long serialVersionUID = -7441171561119813498L;

	public static final String NAME = "localizacaoTurnoManager";

	@In
	private LocalizacaoTurnoDAO localizacaoTurnoDAO;
	
	/**
	 * Pesquisa o turno da localizacao da tarefa em que o hor�rio informado se encontra
	 * @param pt 
	 * @param horario 
	 * @return turno da localiza��o da tarefa
	 */
	public LocalizacaoTurno getTurnoTarefa(ProcessoEpaTarefa pt, Date horario) {
		Calendar horarioCalendar = Calendar.getInstance();
		horarioCalendar.setTime(horario);
		int diaSemana = horarioCalendar.get(Calendar.DAY_OF_WEEK);
		return localizacaoTurnoDAO.getTurnoTarefa(pt, horario, DiaSemanaEnum.values()[diaSemana]);
	}
	
	/**
	 * Lista todos os turnos da localiza��o
	 * @param localizacao
	 * @return lista de turnos
	 */
	public List<LocalizacaoTurno> listByLocalizacao(Localizacao localizacao) {
		return localizacaoTurnoDAO.listByLocalizacao(localizacao);
	}
	
	/**
	 * Contabiliza o tempo �til, em minutos,  dos turnos das localiza��es durante um dia
	 * @param localizacaoList
	 * @return
	 */
	public int contarTempoUtilDiaByLocalizacaoList(List<Localizacao> localizacaoList) {
		List<LocalizacaoTurno> localizacaoTurnoList = new ArrayList<LocalizacaoTurno>();
		for (Localizacao l : localizacaoList) {
			for (LocalizacaoTurno lt : l.getLocalizacaoTurnoList()) {
				localizacaoTurnoList.add(lt);
			}
		}
		return contarTempoUtilTurnos(localizacaoTurnoList);
	}
	
	/**
	 * Calcula o tempo �til total dos turnos, em minutos 
	 * @param localizacaoTurnoList
	 * @return
	 */
	public int contarTempoUtilTurnos(List<LocalizacaoTurno> localizacaoTurnoList) {
		int minutos = 0;
		for (LocalizacaoTurno lt : localizacaoTurnoList) {
			minutos += minutesBetween(lt.getHoraInicio(), lt.getHoraFim());
		}
		return minutos;
	}
	
	private int minutesBetween(Date inicio, Date fim) {
		DateTime dtInicio = new DateTime(inicio);
		DateTime dtFim = new DateTime(fim);
		return Minutes.minutesBetween(dtInicio, dtFim).getMinutes();
	}
	
	/**
	 * verifica se existe choque de hor�rio entre os turnos da localiza��o e o turno 
	 * definido pelos parametros inicio e fim
	 * 
	 * @param l
	 * @param inicio
	 * @param fim
	 * @return true se existir choque de horario
	 */
	public boolean verificarTurnos(Localizacao l, Time inicio, Time fim) {
		return localizacaoTurnoDAO.	countByHoraInicioFim(l, inicio, fim) > 0;
	}

	/**
	 * Verifica as possibilidades para os intervalos do turno de 
	 * uma determinada localiza��o, para ent�o calcular a diferen�a 
	 * de horas que deve ser acrescentada ao tempo gasto de uma 
	 * tarefa.
	 * @param fireTime - Hora de disparo da trigger
	 * @param pt - ProcessoEpaTarefa a ser verificado o tempo gasto
	 * @param lt - LocalizacaoTurno da tarefa em verifica��o.
	 * @return minutos gastos dentro do turno informado
	 */
	public int calcularMinutosGastos(Date fireTime, Date lastFire, LocalizacaoTurno lt) {
		long millisBegin = Math.max(lastFire.getTime(), lt.getHoraInicio().getTime());
		long millisEnd = Math.min(fireTime.getTime(), lt.getHoraFim().getTime());
		
		if (millisBegin < millisEnd) {
			return (int) (millisEnd - millisBegin)/(1000*60);
		}
		return 0;
	}
	
}