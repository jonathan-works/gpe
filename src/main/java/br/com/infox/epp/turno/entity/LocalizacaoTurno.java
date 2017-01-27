package br.com.infox.epp.turno.entity;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.turno.query.LocalizacaoTurnoQuery;
import br.com.infox.epp.turno.type.DiaSemanaEnum;

@Entity
@Table(name = LocalizacaoTurno.TABLE_NAME)
@NamedQueries(value = {
        @NamedQuery(name = LocalizacaoTurnoQuery.LIST_BY_LOCALIZACAO, query = LocalizacaoTurnoQuery.LIST_BY_LOCALIZACAO_QUERY),
        @NamedQuery(name = LocalizacaoTurnoQuery.LIST_BY_HORA_INICIO_FIM, query = LocalizacaoTurnoQuery.LIST_BY_HORA_INICIO_FIM_QUERY),
        @NamedQuery(name = LocalizacaoTurnoQuery.COUNT_BY_HORA_INICIO_FIM, query = LocalizacaoTurnoQuery.COUNT_BY_HORA_INICIO_FIM_QUERY),
        @NamedQuery(name = LocalizacaoTurnoQuery.LOCALIZACAO_TURNO_BY_TAREFA_HORARIO, query = LocalizacaoTurnoQuery.LOCALIZACAO_TURNO_BY_TAREFA_HORARIO_QUERY,
                    hints = {@QueryHint(name="org.hibernate.cacheable", value="true"),
                            @QueryHint(name="org.hibernate.cacheRegion", value="br.com.infox.epp.turno.entity.LocalizacaoTurno")}),
        @NamedQuery(name = LocalizacaoTurnoQuery.COUNT_LOCALIZACAO_TURNO_BY_TAREFA_DIA, query = LocalizacaoTurnoQuery.COUNT_LOCALIZACAO_TURNO_BY_TAREFA_DIA_QUERY,
                    hints = {@QueryHint(name="org.hibernate.cacheable", value="true"),
                            @QueryHint(name="org.hibernate.cacheRegion", value="br.com.infox.epp.turno.entity.LocalizacaoTurno")}),
        @NamedQuery(name = LocalizacaoTurnoQuery.DELETE_TURNOS_ANTERIORES, query = LocalizacaoTurnoQuery.DELETE_TURNOS_ANTERIORES_QUERY),
        @NamedQuery(name = LocalizacaoTurnoQuery.LOCALIZACAO_TURNO_BY_TAREFA, query = LocalizacaoTurnoQuery.LOCALIZACAO_TURNO_BY_TAREFA_QUERY,
                    hints = {@QueryHint(name="org.hibernate.cacheable", value="true"),
                            @QueryHint(name="org.hibernate.cacheRegion", value="br.com.infox.epp.turno.entity.LocalizacaoTurno")}) 
        }
)
public class LocalizacaoTurno implements Serializable {

    private static final long serialVersionUID = -1258132003358073362L;

    public static final String TABLE_NAME = "tb_localizacao_turno";

    private int idLocalizacaoTurno;
    private Localizacao localizacao;
    private Date horaInicio;
    private Date horaFim;
    private Integer tempoTurno;
    private DiaSemanaEnum diaSemana;

    @Column(name = "cd_dia_semana", nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    public DiaSemanaEnum getDiaSemana() {
        return this.diaSemana;
    }

    @Column(name = "dt_hora_fim", nullable = false)
    @NotNull
    @Temporal(TemporalType.TIME)
    public Date getHoraFim() {
        return this.horaFim;
    }

    @Column(name = "dt_hora_inicio", nullable = false)
    @NotNull
    @Temporal(TemporalType.TIME)
    public Date getHoraInicio() {
        return this.horaInicio;
    }

    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "generator", sequenceName = "sq_tb_localizacao_turno")
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_localizacao_turno", unique = true, nullable = false)
    public int getIdLocalizacaoTurno() {
        return this.idLocalizacaoTurno;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_localizacao", nullable = false)
    @NotNull
    public Localizacao getLocalizacao() {
        return this.localizacao;
    }

    @Column(name = "nr_tempo_turno", nullable = false)
    @NotNull
    public Integer getTempoTurno() {
        return this.tempoTurno;
    }

    public void setDiaSemana(final DiaSemanaEnum diaSemana) {
        this.diaSemana = diaSemana;
    }

    public void setHoraFim(final Date horaFim) {
        this.horaFim = horaFim;
    }

    public void setHoraInicio(final Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setIdLocalizacaoTurno(final int idLocalizacaoTurno) {
        this.idLocalizacaoTurno = idLocalizacaoTurno;
    }

    public void setLocalizacao(final Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public void setTempoTurno(final Integer tempoTurno) {
        this.tempoTurno = tempoTurno;
    }

}
