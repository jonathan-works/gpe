package br.com.infox.epp.turno.component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.turno.type.DiaSemanaEnum;
import br.com.infox.util.time.DateRange;

/**
 * Classe que gerencia o componenete de criação de turnos.
 *
 * @author tassio
 *
 */
public class TurnoHandler {

    private Map<DiaSemanaEnum, List<HorarioBean>> horarioBeanMap;

    private final Map<Date, Integer> horarioMap;
    private final List<Date> horarios;

    public TurnoHandler(final Integer interval) {
        this.horarios = createHorarios(interval);
        this.horarioMap = createHorarioMap();
        this.horarioBeanMap = createHorarioBeanMap();
    }

    /**
     * Adiciona um intervalo de <i>horaInicio</i> ate <i>horaFim</i> no dia da
     * semana informado
     *
     * @param diaSemana
     * @param horaInicio
     * @param horaFim
     */
    public void addIntervalo(final DiaSemanaEnum diaSemana,
            final Date horaInicio, final Date horaFim) {
        final List<HorarioBean> horarioBeanList = this.horarioBeanMap
                .get(diaSemana);
        HorarioBean horarioBean;
        int i = 0;
        do {
            horarioBean = horarioBeanList.get(i);
            if (horarioBean.getHora().equals(horaInicio)
                    || (horarioBean.getHora().after(horaInicio) && (horarioBean
                            .getHora().before(horaFim)))) {
                horarioBean.setSelected(true);
            }
            i++;
        } while (horarioBean.getHora().before(horaFim)
                && (i < horarioBeanList.size()));
    }

    public void addIntervalo(final DiaSemanaEnum diaSemana,
            final DateRange intervalo) {
        final List<HorarioBean> horarioBeanList = this.horarioBeanMap
                .get(diaSemana);
        HorarioBean horarioBean;
        int i = 0;
        final Date start = intervalo.getStart().toDate();
        final Date end = intervalo.getEnd().toDate();
        Date hora;
        do {
            horarioBean = horarioBeanList.get(i);
            hora = horarioBean.getHora();
            if (hora.equals(start) || (hora.after(start) && hora.before(end))) {
                horarioBean.setSelected(true);
            }
            i++;
        } while (hora.before(end) && (i < horarioBeanList.size()));
    }

    /**
     * Descarta todos os turnos criados
     */
    public void clearIntervalos() {
        this.horarioBeanMap = createHorarioBeanMap();
    }

    private List<HorarioBean> createHorarioBeanList() {
        final List<HorarioBean> horarioBeanList = new ArrayList<HorarioBean>();
        for (final Date horario : getHorarios()) {
            final HorarioBean bean = new HorarioBean();
            bean.setHora(horario);
            bean.setSelected(false);
            horarioBeanList.add(bean);
        }
        return horarioBeanList;
    }

    private Map<DiaSemanaEnum, List<HorarioBean>> createHorarioBeanMap() {
        final Map<DiaSemanaEnum, List<HorarioBean>> map = new HashMap<>();
        for (final DiaSemanaEnum dia : DiaSemanaEnum.values()) {
            map.put(dia, createHorarioBeanList());
        }
        return map;
    }

    private Map<Date, Integer> createHorarioMap() {
        final Map<Date, Integer> map = new HashMap<>();
        int i = 0;
        for (final Date horario : this.horarios) {
            map.put(horario, i);
            i++;
        }
        return map;
    }

    private List<Date> createHorarios(final Integer intervalInMinutes) {
        final List<Date> horarioList = new ArrayList<>();
        final Calendar calendar = DateUtil.getBeginningOfDay();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        while (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            horarioList.add(new Date(calendar.getTimeInMillis()));

            calendar.add(Calendar.MINUTE, intervalInMinutes);
        }
        return horarioList;
    }

    public DiaSemanaEnum[] getDiasSemana() {
        return DiaSemanaEnum.values();
    }

    public HorarioBean getHorarioBean(final DiaSemanaEnum diaSemana,
            final Date horario) {
        final int index = this.horarioMap.get(horario);
        return this.horarioBeanMap.get(diaSemana).get(index);
    }

    /**
     * Retorna uma lista de horários contendo a informação se eles estão dentro
     * de algum turno selecionado.
     *
     * @param diaSemana
     * @return lista de HorarioBean
     */
    public List<HorarioBean> getHorarioBeanList(final DiaSemanaEnum diaSemana) {
        return this.horarioBeanMap.get(diaSemana);
    }

    /**
     * Retorna uma lista de todos os horários gerados das 00:00 até 23:59 com o
     * passo igual ao intervalo informado no construtor da classe
     *
     * @return lista de horários
     */
    public final List<Date> getHorarios() {
        return this.horarios;
    }

    /**
     * Retorna um set com todos os turnos selecionados pelo usuário
     *
     * @return
     */
    public List<TurnoBean> getTurnosSelecionados() {
        final List<TurnoBean> turnos = new ArrayList<TurnoBean>();
        for (final DiaSemanaEnum diaSemana : getDiasSemana()) {
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
    private List<TurnoBean> getTurnosSelecionados(final DiaSemanaEnum diaSemana) {
        final List<TurnoBean> turnos = new ArrayList<>();
        Date begin = null;
        for (final HorarioBean horarioBean : getHorarioBeanList(diaSemana)) {
            if (horarioBean.getSelected()) {
                if (begin == null) {
                    begin = horarioBean.getHora();
                }
            } else if (begin != null) {
                turnos.add(new TurnoBean(diaSemana, begin, horarioBean
                        .getHora()));
                begin = null;
            }
        }
        if (begin != null) {

            turnos.add(new TurnoBean(diaSemana, begin, new Date(DateUtil
                    .getEndOfDay().getTimeInMillis())));
        }
        return turnos;
    }

}
