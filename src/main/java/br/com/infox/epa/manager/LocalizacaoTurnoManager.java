package br.com.infox.epa.manager;

import static br.com.infox.util.DateUtil.calculateMinutesBetweenTimes;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epa.dao.LocalizacaoTurnoDAO;
import br.com.infox.epa.entity.LocalizacaoTurno;
import br.com.infox.epa.entity.ProcessoEpaTarefa;
import br.com.infox.ibpm.entity.Localizacao;

@Name(LocalizacaoTurnoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class LocalizacaoTurnoManager extends GenericManager {

	public static final String NAME = "localizacaoTurnoManager";

	@In
	private LocalizacaoTurnoDAO localizacaoTurnoDAO;
	
	public List<LocalizacaoTurno> listByLocalizacao(Localizacao localizacao) {
		return localizacaoTurnoDAO.listByLocalizacao(localizacao);
	}
	
	public int contarTempoUtilDiaByLocalizacaoList(List<Localizacao> localizacaoList) {
		List<LocalizacaoTurno> localizacaoTurnoList = new ArrayList<LocalizacaoTurno>();
		for (Localizacao l : localizacaoList) {
			for (LocalizacaoTurno lt : l.getLocalizacaoTurnoList()) {
				localizacaoTurnoList.add(lt);
			}
		}
		return contarTempoUtilDia(localizacaoTurnoList);
	}
	
	public int contarTempoUtilDia(List<LocalizacaoTurno> localizacaoTurnoList) {
		boolean[] minutosDia = new boolean[1440];
		for (LocalizacaoTurno lt : localizacaoTurnoList) {
			Calendar inicio = Calendar.getInstance();
			Calendar fim = Calendar.getInstance();
			inicio.setTime(lt.getHoraInicio());
			fim.setTime(lt.getHoraFim());
			int minutoInicioDia = (inicio.get(Calendar.HOUR_OF_DAY)*60)+
								   inicio.get(Calendar.MINUTE) - 1;
			int minutoFimDia = (fim.get(Calendar.HOUR_OF_DAY)*60)+
								fim.get(Calendar.MINUTE) - 1;
			for(;minutoInicioDia<minutoFimDia;minutoInicioDia++) {
				minutosDia[minutoInicioDia] = true;
			}
		}
		int minutoUtil = 0;
		for (boolean b : minutosDia) {
			if(b) {
				minutoUtil++;
			}
		}
		return minutoUtil;
	}
	
	/**
	 * 
	 * @param l
	 * @param inicio
	 * @param fim
	 * @return true se existir choque de horario
	 */
	public boolean verificarTurnos(Localizacao l, Time inicio, Time fim) {
		boolean b = false;
		List<LocalizacaoTurno> ltList = localizacaoTurnoDAO.
			listByHoraInicioFim(l, inicio, fim);
		if(ltList != null && ltList.size() > 0) {
			b = true;
		}
		return b;
	}

	/**
	 * Verifica as possibilidades para os intervalos do turno de 
	 * uma determinada localização, para então calcular a diferença 
	 * de horas que deve ser acrescentada ao tempo gasto de uma 
	 * tarefa.
	 * @param fireTime - Hora de disparo da trigger
	 * @param pt - ProcessoEpaTarefa a ser verificado o tempo gasto
	 * @param lastFire - Hora da ultima verificação da trigger
	 * @param lt - LocalizacaoTurno da tarefa em verificação.
	 */
	public void verifyToCalculate(Calendar fireTime, ProcessoEpaTarefa pt, 
								  LocalizacaoTurno lt) {
		Calendar lastFire = Calendar.getInstance();
		Calendar horaInicio = Calendar.getInstance();
		Calendar horaFim = Calendar.getInstance();
		horaInicio.setTime(lt.getHoraInicio());
		horaFim.setTime(lt.getHoraFim());
		lastFire.setTime(pt.getUltimoDisparo());
		int hourDayFire = fireTime.get(Calendar.HOUR_OF_DAY);
		int minuteFire = fireTime.get(Calendar.MINUTE);
		int hourDayLastFire = lastFire.get(Calendar.HOUR_OF_DAY);
		int minuteLastFire = lastFire.get(Calendar.MINUTE);
		int hourDayInicio = horaInicio.get(Calendar.HOUR_OF_DAY);
		int minuteInicio = horaInicio.get(Calendar.MINUTE);
		int hourDayFim = horaFim.get(Calendar.HOUR_OF_DAY);
		int minuteFim = horaFim.get(Calendar.MINUTE);
		if(hourDayFire == 0 && hourDayLastFire == 23) {
			if(hourDayFim == 23) {
				hourDayFire = 24;
			} else {
				hourDayLastFire = -1;
			}
		}
		if((hourDayLastFire > hourDayInicio && hourDayLastFire < hourDayFim) &&
				   (hourDayFire > hourDayInicio && hourDayFire < hourDayFim)) {
			//increase fireTime - lastFire
			int difference = calculateMinutesBetweenTimes(lastFire, fireTime);
			pt.setTempoGasto(pt.getTempoGasto() + difference);
			pt.setUltimoDisparo(fireTime.getTime());
		} else if (hourDayLastFire > hourDayInicio && hourDayLastFire < hourDayFim
				&& hourDayFire > hourDayFim) {
			//increase horaFim - lastFire
			Calendar dataFim = createDataWithEspecificHours(
					lastFire, hourDayFim, minuteFim);
			int difference = calculateMinutesBetweenTimes(lastFire, dataFim);
			pt.setTempoGasto(pt.getTempoGasto() + difference);
			pt.setUltimoDisparo(dataFim.getTime());
		} else if(hourDayLastFire < hourDayInicio && hourDayFire > hourDayFim) {
			//increase horaFim - horaInicio
			int difference = calculateMinutesBetweenTimes(horaInicio, horaFim);
			pt.setTempoGasto(pt.getTempoGasto() + difference);
			Calendar dataFim = createDataWithEspecificHours(
					lastFire, hourDayFim, minuteFim);
			pt.setUltimoDisparo(dataFim.getTime());
		} else if(hourDayLastFire < hourDayInicio && hourDayFire > hourDayInicio
				&& hourDayFire < hourDayFim) {
			Calendar dataInicio = createDataWithEspecificHours(
					fireTime, hourDayInicio, minuteInicio);
			int difference = calculateMinutesBetweenTimes(dataInicio, fireTime);
			pt.setTempoGasto(pt.getTempoGasto() + difference);
			pt.setUltimoDisparo(fireTime.getTime());
		} else if(hourDayLastFire == hourDayInicio && hourDayFire != hourDayFim) {
			if(minuteLastFire >= minuteInicio) {
				if(hourDayFire > hourDayFim) {
					Calendar dataFim = createDataWithEspecificHours(
							lastFire, hourDayFim, minuteFim);
					int difference = calculateMinutesBetweenTimes(lastFire, dataFim);
					pt.setTempoGasto(pt.getTempoGasto() + difference);
					pt.setUltimoDisparo(dataFim.getTime());
				} else {
					//increase fireTime - lastFire
					int difference = calculateMinutesBetweenTimes(lastFire, fireTime);
					pt.setTempoGasto(pt.getTempoGasto() + difference);
					pt.setUltimoDisparo(fireTime.getTime());
				}
			} else {
				if(hourDayFire > hourDayFim) {
					//increase horaFim - horaInicio
					int difference = calculateMinutesBetweenTimes(horaInicio, horaFim);
					pt.setTempoGasto(pt.getTempoGasto() + difference);
					Calendar dataFim = createDataWithEspecificHours(
							lastFire, hourDayFim, minuteFim);
					pt.setUltimoDisparo(dataFim.getTime());
				} else {
					Calendar dataInicio = createDataWithEspecificHours(
							fireTime, hourDayInicio, minuteInicio);
					int difference = calculateMinutesBetweenTimes(dataInicio, fireTime);
					pt.setTempoGasto(pt.getTempoGasto() + difference);
					pt.setUltimoDisparo(fireTime.getTime());
				}
			}
		} else if(hourDayFire == hourDayFim && hourDayLastFire != hourDayInicio) {
			if(minuteFire > minuteFim) {
				if(hourDayLastFire > hourDayInicio) {
					Calendar dataFim = createDataWithEspecificHours(
							lastFire, hourDayFim, minuteFim);
					int difference = calculateMinutesBetweenTimes(lastFire, dataFim);
					pt.setTempoGasto(pt.getTempoGasto() + difference);
					pt.setUltimoDisparo(dataFim.getTime());
				} else {
					//increase horaFim - horaInicio 
					int difference = calculateMinutesBetweenTimes(horaInicio, horaFim);
					pt.setTempoGasto(pt.getTempoGasto() + difference);
					Calendar dataFim = createDataWithEspecificHours(
							lastFire, hourDayFim, minuteFim);
					pt.setUltimoDisparo(dataFim.getTime());
				}
			} else {
				if(hourDayLastFire > hourDayInicio) {
					//increase fireTime - lastFire
					int difference = calculateMinutesBetweenTimes(lastFire, fireTime);
					pt.setTempoGasto(pt.getTempoGasto() + difference);
					
				} else {
					Calendar dataInicio = createDataWithEspecificHours(
							fireTime, hourDayInicio, minuteInicio);
					int difference = calculateMinutesBetweenTimes(dataInicio, fireTime);
					pt.setTempoGasto(pt.getTempoGasto() + difference);
					pt.setUltimoDisparo(fireTime.getTime());
				}
			}
		} else if(hourDayFire == hourDayFim && hourDayLastFire == hourDayInicio){
			if(minuteLastFire > minuteInicio) {
				if(minuteFire > minuteFim) {
					Calendar dataFim = createDataWithEspecificHours(
							lastFire, hourDayFim, minuteFim);
					int difference = calculateMinutesBetweenTimes(lastFire, dataFim);
					pt.setTempoGasto(pt.getTempoGasto() + difference);
					pt.setUltimoDisparo(dataFim.getTime());
				} else {
					//increase fireTime - lastFire
					int difference = calculateMinutesBetweenTimes(lastFire, fireTime);
					pt.setTempoGasto(pt.getTempoGasto() + difference);
					pt.setUltimoDisparo(fireTime.getTime());
				}
			} else {
				if(minuteFire > minuteFim) {
					//increase horaFim - horaInicio
					int difference = calculateMinutesBetweenTimes(horaInicio, horaFim);
					pt.setTempoGasto(pt.getTempoGasto() + difference);
					Calendar dataFim = createDataWithEspecificHours(
							lastFire, hourDayFim, minuteFim);
					pt.setUltimoDisparo(dataFim.getTime());
				} else {
					Calendar dataInicio = createDataWithEspecificHours(
							fireTime, hourDayInicio, minuteInicio);
					int difference = calculateMinutesBetweenTimes(dataInicio, fireTime);
					pt.setTempoGasto(pt.getTempoGasto() + difference);
					pt.setUltimoDisparo(fireTime.getTime());
				}
			}
		}
	}
	
	/**
	 * 
	 * @param inicio
	 * @param fim
	 * @return true se o intervalo não for valido
	 */
	public boolean verificarIntervalo(Time inicio, Time fim) {
		Calendar dataInicio = Calendar.getInstance();
		Calendar dataFim = Calendar.getInstance();
		dataInicio.setTime(inicio);
		dataFim.setTime(fim);
		int hourDayInicio = dataInicio.get(Calendar.HOUR_OF_DAY);
		int hourDayFim = dataFim.get(Calendar.HOUR_OF_DAY);
		if(hourDayInicio > hourDayFim) {
			return true;
		} else if(hourDayFim == hourDayInicio) {
			int minutoInicio = dataInicio.get(Calendar.MINUTE);
			int minutoFim = dataFim.get(Calendar.MINUTE);
			if(minutoInicio > minutoFim) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Cria uma data igual a data informada, porém substitui o 
	 * horario da data criada pelo horario informado nos parametros.
	 * @param dayFire - data que se deseja copiar.
	 * @param hourDayFim - hora que será definida na data criada.
	 * @param minuteFim - minuto que será definido na data criada.
	 * @return Data copiada com os horários novos especificados.
	 */
	private Calendar createDataWithEspecificHours(Calendar dayFire,
			int hourDayFim, int minuteFim) {
		Calendar dataFim = Calendar.getInstance();
		dataFim.setTimeInMillis(dayFire.getTimeInMillis());
		dataFim.set(Calendar.HOUR_OF_DAY, hourDayFim);
		dataFim.set(Calendar.MINUTE, minuteFim);
		return dataFim;
	}
	
}