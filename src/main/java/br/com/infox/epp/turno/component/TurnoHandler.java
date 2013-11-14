package br.com.infox.epp.turno.component;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.infox.epp.turno.type.DiaSemanaEnum;
import br.com.infox.util.DateUtil;

/**
 * Classe que gerencia o componenete de criação de turnos.
 * 
 * @author tassio
 *
 */
public class TurnoHandler {
	
	private Map<DiaSemanaEnum, List<HorarioBean>> horarioBeanMap;
	
	private Map<Time, Integer> horarioMap;
	private List<Time> horarios;
	
	public TurnoHandler(Integer interval) {
		horarios = createHorarios(interval);
		horarioMap = createHorarioMap();
		horarioBeanMap = createHorarioBeanMap();
	}
	
	private List<Time> createHorarios(Integer intervalInMinutes) {
		List<Time> horarioList = new ArrayList<Time>();
		Calendar calendar = DateUtil.getBeginningOfDay();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		while (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
			horarioList.add(new Time(calendar.getTimeInMillis()));
			
			calendar.add(Calendar.MINUTE, intervalInMinutes);
		}
		return horarioList;
	}
	
	private Map<Time, Integer> createHorarioMap() {
		Map<Time, Integer> map = new HashMap<Time, Integer>();
		int i = 0;
		for (Time horario: horarios) {
			map.put(horario, i);
			i++;
		}
		return map;
	}
	
	private Map<DiaSemanaEnum, List<HorarioBean>> createHorarioBeanMap() {
		Map<DiaSemanaEnum, List<HorarioBean>> map = new HashMap<DiaSemanaEnum, List<HorarioBean>>();
		for (DiaSemanaEnum dia: DiaSemanaEnum.values()) {
			map.put(dia, createHorarioBeanList());
		}
		return map;
	}

	private List<HorarioBean> createHorarioBeanList() {
		List<HorarioBean> horarioBeanList = new ArrayList<HorarioBean>();
		for (Time horario: getHorarios()) {
			HorarioBean bean = new HorarioBean();
			bean.setHora(horario);
			bean.setSelected(false);
			horarioBeanList.add(bean);
		}
		return horarioBeanList;
	}

	public DiaSemanaEnum[] getDiasSemana() {
		return DiaSemanaEnum.values();
	}
	
	/**
	 * Retorna uma lista de horários contendo a informação se eles estão 
	 * dentro de algum turno selecionado.
	 * 
	 * @param diaSemana 
	 * @return lista de HorarioBean
	 */
	public List<HorarioBean> getHorarioBeanList(DiaSemanaEnum diaSemana) {
		return horarioBeanMap.get(diaSemana);
	}
	
	public HorarioBean getHorarioBean(DiaSemanaEnum diaSemana, Time horario) {
		int index = horarioMap.get(horario);
		return horarioBeanMap.get(diaSemana).get(index);
	}
	
	/**
	 * Retorna uma lista de todos os horários gerados das 00:00 até 23:59
	 * com o passo igual ao intervalo informado no construtor da classe
	 * 
	 * @return lista de horários
	 */
	public final List<Time> getHorarios() {
		return horarios;
	}
	
	/**
	 * Retorna um set com todos os turnos selecionados pelo usuário
	 * 
	 * @return
	 */
	public List<TurnoBean> getTurnosSelecionados() {
		List<TurnoBean> turnos = new ArrayList<TurnoBean>();
		for (DiaSemanaEnum diaSemana: getDiasSemana()) {
			turnos.addAll(getTurnosSelecionados(diaSemana));
		}
		return turnos;
	}
	
	/**
	 * Retorna um set com os turnos selecionados pelo usuário em um determinado
	 * dia da semana
	 * 
	 * @return
	 */
	private List<TurnoBean> getTurnosSelecionados(DiaSemanaEnum diaSemana) {
		List<TurnoBean> turnos = new ArrayList<TurnoBean>();
		Time begin = null;
		for (HorarioBean horarioBean: getHorarioBeanList(diaSemana)) {
			if (horarioBean.getSelected()) {
				 if (begin == null) {
					 begin = horarioBean.getHora();
				 }
			} else if (begin != null) {
				turnos.add(new TurnoBean(diaSemana, begin, horarioBean.getHora()));
				begin = null;
			}
		}
		if (begin != null) {
			turnos.add(new TurnoBean(diaSemana, begin, new Time(DateUtil.getEndOfDay().getTimeInMillis())));
		}
		return turnos;
	}
	
	/**
	 * Adiciona um intervalo de <i>horaInicio</i> ate <i>horaFim</i> no dia da semana informado 
	 * @param diaSemana
	 * @param horaInicio
	 * @param horaFim
	 */
	public void addIntervalo(DiaSemanaEnum diaSemana, Time horaInicio, Time horaFim) {
		List<HorarioBean> horarioBeanList = horarioBeanMap.get(diaSemana);
		HorarioBean horarioBean;
		int i = 0;
		do {
			horarioBean = horarioBeanList.get(i);
			if (horarioBean.getHora().equals(horaInicio) 
			        || (horarioBean.getHora().after(horaInicio) 
			                && (horarioBean.getHora().before(horaFim)))) {
				horarioBean.setSelected(true);
			}
			i++;
		} while (horarioBean.getHora().before(horaFim) && i < horarioBeanList.size());
	}

	/**
	 * Descarta todos os turnos criados
	 */
	public void clearIntervalos() {
		horarioBeanMap = createHorarioBeanMap();
	}
	
}
