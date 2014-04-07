package br.com.infox.epp.turno.entity;

import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.COUNT_BY_HORA_INICIO_FIM;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.COUNT_BY_HORA_INICIO_FIM_QUERY;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.COUNT_LOCALIZACAO_TURNO_BY_TAREFA_DIA;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.COUNT_LOCALIZACAO_TURNO_BY_TAREFA_DIA_QUERY;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.DELETE_TURNOS_ANTERIORES;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.DELETE_TURNOS_ANTERIORES_QUERY;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.LIST_BY_HORA_INICIO_FIM;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.LIST_BY_HORA_INICIO_FIM_QUERY;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.LIST_BY_LOCALIZACAO;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.LIST_BY_LOCALIZACAO_QUERY;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.LOCALIZACAO_TURNO_BY_TAREFA;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.LOCALIZACAO_TURNO_BY_TAREFA_HORARIO;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.LOCALIZACAO_TURNO_BY_TAREFA_HORARIO_QUERY;
import static br.com.infox.epp.turno.query.LocalizacaoTurnoQuery.LOCALIZACAO_TURNO_BY_TAREFA_QUERY;

import java.io.Serializable;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.turno.type.DiaSemanaEnum;

@Entity
@Table(name = LocalizacaoTurno.TABLE_NAME)
@NamedQueries(value = {
    @NamedQuery(name = LIST_BY_LOCALIZACAO, query = LIST_BY_LOCALIZACAO_QUERY),
    @NamedQuery(name = LIST_BY_HORA_INICIO_FIM, query = LIST_BY_HORA_INICIO_FIM_QUERY),
    @NamedQuery(name = COUNT_BY_HORA_INICIO_FIM, query = COUNT_BY_HORA_INICIO_FIM_QUERY),
    @NamedQuery(name = LOCALIZACAO_TURNO_BY_TAREFA_HORARIO, query = LOCALIZACAO_TURNO_BY_TAREFA_HORARIO_QUERY),
    @NamedQuery(name = COUNT_LOCALIZACAO_TURNO_BY_TAREFA_DIA, query = COUNT_LOCALIZACAO_TURNO_BY_TAREFA_DIA_QUERY),
    @NamedQuery(name = DELETE_TURNOS_ANTERIORES, query = DELETE_TURNOS_ANTERIORES_QUERY),
    @NamedQuery(name = LOCALIZACAO_TURNO_BY_TAREFA, query = LOCALIZACAO_TURNO_BY_TAREFA_QUERY) })
public class LocalizacaoTurno implements Serializable {

    private static final long serialVersionUID = -1258132003358073362L;

    public static final String TABLE_NAME = "tb_localizacao_turno";

    private int idLocalizacaoTurno;
    private Localizacao localizacao;
    private Time horaInicio;
    private Time horaFim;
    private Integer tempoTurno;
    private DiaSemanaEnum diaSemana;

    @SequenceGenerator(name = "generator", sequenceName = "sq_tb_localizacao_turno")
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_localizacao_turno", unique = true, nullable = false)
    public int getIdLocalizacaoTurno() {
        return idLocalizacaoTurno;
    }

    public void setIdLocalizacaoTurno(int idLocalizacaoTurno) {
        this.idLocalizacaoTurno = idLocalizacaoTurno;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_localizacao", nullable = false)
    @NotNull
    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public void setHoraInicio(Time horaInicio) {
        this.horaInicio = horaInicio;
    }

    @Column(name = "dt_hora_inicio", nullable = false)
    @NotNull
    public Time getHoraInicio() {
        return horaInicio;
    }

    public void setHoraFim(Time horaFim) {
        this.horaFim = horaFim;
    }

    @Column(name = "dt_hora_fim", nullable = false)
    @NotNull
    public Time getHoraFim() {
        return horaFim;
    }

    public void setTempoTurno(Integer tempoTurno) {
        this.tempoTurno = tempoTurno;
    }

    @Column(name = "nr_tempo_turno", nullable = false)
    @NotNull
    public Integer getTempoTurno() {
        return tempoTurno;
    }

    @Column(name = "cd_dia_semana", nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    public DiaSemanaEnum getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemanaEnum diaSemana) {
        this.diaSemana = diaSemana;
    }

}
