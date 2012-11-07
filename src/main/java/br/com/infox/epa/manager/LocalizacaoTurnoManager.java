package br.com.infox.epa.manager;

import java.sql.Time;
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
	 * Pesquisa o turno da localizacao da tarefa em que o horário informado se encontra
	 * @param pt 
	 * @param horario 
	 * @return turno da localização da tarefa
	 */
	public LocalizacaoTurno getTurnoTarefa(ProcessoEpaTarefa pt, Date data, Time horario) {
		Calendar horarioCalendar = Calendar.getInstance();
		horarioCalendar.setTime(horario);
		int diaSemana = horarioCalendar.get(Calendar.DAY_OF_WEEK);
		return localizacaoTurnoDAO.getTurnoTarefa(pt, data, horario, DiaSemanaEnum.values()[diaSemana-1]);
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
		return localizacaoTurnoDAO.contemTurnoTarefaDia(pt, data, DiaSemanaEnum.values()[diaSemana-1]);
	}
	
	/**
	 * Lista todos os turnos da localização
	 * @param localizacao
	 * @return lista de turnos
	 */
	public List<LocalizacaoTurno> listByLocalizacao(Localizacao localizacao) {
		return localizacaoTurnoDAO.listByLocalizacao(localizacao);
	}
	
	/**
	 * Calcula o tempo útil total dos turnos, em minutos 
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
	 * verifica se existe choque de horário entre os turnos da localização e o turno 
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
	 * uma determinada localização, para então calcular a diferença 
	 * de horas que deve ser acrescentada ao tempo gasto de uma 
	 * tarefa.
	 * @param fireTime - Hora de disparo da trigger
	 * @param pt - ProcessoEpaTarefa a ser verificado o tempo gasto
	 * @param lt - LocalizacaoTurno da tarefa em verificação.
	 * @return minutos gastos dentro do turno informado
	 */
	public int calcularMinutosGastos(Date fireTime, Date lastFire, LocalizacaoTurno lt) {
		int minutesBegin = Math.max(getMinutesOfDay(lastFire), getMinutesOfDay(lt.getHoraInicio()));
		int minutesEnd = Math.min(getMinutesOfDay(fireTime), getMinutesOfDay(lt.getHoraFim()));
		
		if (minutesBegin < minutesEnd) {
			return minutesEnd - minutesBegin;
		}
		return 0;
	}

	private int getMinutesOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MINUTE) + (calendar.get(Calendar.HOUR)*60);
	}
	
	public static void main(String[] args) {
		
		Time t = new Time(new Date().getTime());
		System.out.println(t);
		System.out.println(t.getTime());
		
	}
	
}