package br.com.infox.epp.julgamento.entity;

import java.io.Serializable;
import java.text.MessageFormat;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.julgamento.query.SalaTurnoQuery;
import br.com.infox.epp.turno.type.DiaSemanaEnum;
import br.com.infox.util.time.DateRange;

@Entity
@Table(name = "tb_sala_turno")
@NamedQueries(value = {
        @NamedQuery(name = SalaTurnoQuery.DELETE_TURNOS_ANTERIORES, query = SalaTurnoQuery.DELETE_TURNOS_ANTERIORES_QUERY),
        @NamedQuery(name = SalaTurnoQuery.LIST_BY_SALA, query = SalaTurnoQuery.LIST_BY_SALA_QUERY) })
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date horaInicio;
    @Column(name = "dt_hora_fim", nullable = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date horaFim;
    @Transient
    private DateRange periodo = new DateRange();

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SalaTurno)) {
            return false;
        }
        final SalaTurno other = (SalaTurno) obj;
        if (this.idSalaTurno == null) {
            if (other.idSalaTurno != null) {
                return false;
            }
        } else if (!this.idSalaTurno.equals(other.idSalaTurno)) {
            return false;
        }
        return true;
    }

    public DiaSemanaEnum getDiaSemana() {
        return this.diaSemana;
    }

    public Date getHoraFim() {
        return this.horaFim;
    }

    public Date getHoraInicio() {
        return this.horaInicio;
    }

    public Long getIdSalaTurno() {
        return this.idSalaTurno;
    }

    public DateRange getPeriodo() {
        return this.periodo;
    }

    public Sala getSala() {
        return this.sala;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result)
                + ((this.idSalaTurno == null) ? 0 : this.idSalaTurno.hashCode());
        return result;
    }

    public void setDiaSemana(final DiaSemanaEnum diaSemana) {
        this.diaSemana = diaSemana;
    }

    public void setHoraFim(final Date horaFim) {
        this.periodo.setEnd(this.horaFim = horaFim);
    }

    public void setHoraInicio(final Date horaInicio) {
        this.periodo.setStart(this.horaInicio = horaInicio);
    }

    public void setIdSalaTurno(final Long idSalaTurno) {
        this.idSalaTurno = idSalaTurno;
    }

    public void setPeriodo(final DateRange periodo) {
        this.periodo = periodo;
    }

    public void setSala(final Sala sala) {
        this.sala = sala;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0} {1,time,short} - {2,time,short}",
                this.diaSemana.getLabel(), this.horaInicio, this.horaFim);
    }
}
