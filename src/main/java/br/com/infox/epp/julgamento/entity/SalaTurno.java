package br.com.infox.epp.julgamento.entity;

import static br.com.infox.epp.julgamento.query.SalaTurnoQuery.DELETE_TURNOS_ANTERIORES;
import static br.com.infox.epp.julgamento.query.SalaTurnoQuery.DELETE_TURNOS_ANTERIORES_QUERY;
import static br.com.infox.epp.julgamento.query.SalaTurnoQuery.LIST_BY_SALA;
import static br.com.infox.epp.julgamento.query.SalaTurnoQuery.LIST_BY_SALA_QUERY;
import static java.text.MessageFormat.format;

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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.turno.type.DiaSemanaEnum;
import br.com.infox.util.time.DateRange;

@Entity
@Table(name = "tb_sala_turno")
@NamedQueries(value = {
        @NamedQuery(name = DELETE_TURNOS_ANTERIORES, query = DELETE_TURNOS_ANTERIORES_QUERY),
        @NamedQuery(name = LIST_BY_SALA, query = LIST_BY_SALA_QUERY) })
public class SalaTurno implements Serializable {

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "SalaTurnoGenerator", sequenceName = "sq_sala_turno")
    @GeneratedValue(generator = "SalaTurnoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_sala_turno", unique = true, nullable = false)
    private Long idSalaTurno;
    @Column(name = "cd_dia_semana", nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private DiaSemanaEnum diaSemana;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sala", nullable = false)
    private Sala sala;
    @Column(name = "dt_hora_inicio", nullable = false)
    @NotNull
    private Time horaInicio;
    @Column(name = "dt_hora_fim", nullable = false)
    @NotNull
    private Time horaFim;
    @Transient
    private DateRange periodo = new DateRange();

    public Long getIdSalaTurno() {
        return idSalaTurno;
    }

    public void setIdSalaTurno(Long idSalaTurno) {
        this.idSalaTurno = idSalaTurno;
    }

    public DiaSemanaEnum getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemanaEnum diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Time getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Time horaInicio) {
        this.periodo.setStart(this.horaInicio = horaInicio);
    }

    public Time getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(Time horaFim) {
        this.periodo.setEnd(this.horaFim = horaFim);
    }

    public DateRange getPeriodo() {
        return periodo;
    }

    public void setPeriodo(DateRange periodo) {
        this.periodo = periodo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((idSalaTurno == null) ? 0 : idSalaTurno.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SalaTurno)) {
            return false;
        }
        SalaTurno other = (SalaTurno) obj;
        if (idSalaTurno == null) {
            if (other.idSalaTurno != null) {
                return false;
            }
        } else if (!idSalaTurno.equals(other.idSalaTurno)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return format("{0} {1,time,short} - {2,time,short}", diaSemana.getLabel(),
                horaInicio, horaFim);
    }

    private static final long serialVersionUID = 1L;
}
