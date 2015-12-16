package br.com.infox.epp.cliente.entity;

import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_BY_SERIE;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_BY_SERIE_AFTER_DATE;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_BY_SERIE_AFTER_DATE_QUERY;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_BY_SERIE_QUERY;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_CALENDARIO_BY;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_CALENDARIO_BY_QUERY;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_ORPHAN_SERIES;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_ORPHAN_SERIES_QUERY;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_PERIODICOS_NAO_ATUALIZADOS;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_PERIODICOS_NAO_ATUALIZADOS_QUERY;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_ULTIMO_EVENTO_SERIE;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_ULTIMO_EVENTO_SERIE_QUERY;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Cacheable;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.joda.time.LocalDate;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.calendario.TipoEvento;
import br.com.infox.epp.calendario.entity.SerieEventos;
import br.com.infox.epp.cliente.query.CalendarioEventosQuery;
import br.com.infox.util.time.DateRange;

@Entity
@Table(name = CalendarioEventos.TABLE_NAME)
@NamedQueries({
        @NamedQuery(name = CalendarioEventosQuery.GET_BY_DATA, query = CalendarioEventosQuery.GET_BY_DATA_QUERY, hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true"),
                @QueryHint(name = "org.hibernate.cacheRegion", value = "br.com.infox.epp.cliente.entity.CalendarioEventos") }),
        @NamedQuery(name = CalendarioEventosQuery.GET_BY_DATA_RANGE, query = CalendarioEventosQuery.GET_BY_DATA_RANGE_QUERY, hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true"),
                @QueryHint(name = "org.hibernate.cacheRegion", value = "br.com.infox.epp.cliente.entity.CalendarioEventos") }),
        @NamedQuery(name = GET_CALENDARIO_BY, query = GET_CALENDARIO_BY_QUERY),
        @NamedQuery(name = GET_PERIODICOS_NAO_ATUALIZADOS, query = GET_PERIODICOS_NAO_ATUALIZADOS_QUERY),
        @NamedQuery(name = GET_BY_SERIE, query = GET_BY_SERIE_QUERY),
        @NamedQuery(name = GET_ULTIMO_EVENTO_SERIE, query = GET_ULTIMO_EVENTO_SERIE_QUERY),
        @NamedQuery(name = GET_BY_SERIE_AFTER_DATE, query = GET_BY_SERIE_AFTER_DATE_QUERY),
        @NamedQuery(name = GET_ORPHAN_SERIES, query = GET_ORPHAN_SERIES_QUERY) })
@Cacheable
public class CalendarioEventos implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_calendario_eventos";

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "CalendarioEventosGenerator", sequenceName = "sq_tb_calendario_eventos")
    @GeneratedValue(generator = "CalendarioEventosGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_calendario_evento", unique = true, nullable = false)
    private Integer idCalendarioEvento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_localizacao", nullable = false)
    private Localizacao localizacao;

    @NotNull
    @Column(name = "ds_evento", length = LengthConstants.DESCRICAO_PADRAO, nullable = false)
    @Size(max = LengthConstants.DESCRICAO_PADRAO)
    private String descricaoEvento;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_inicio", nullable = false)
    private Date dataInicio;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_fim", nullable = false)
    private Date dataFim;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tp_evento", nullable = false)
    private TipoEvento tipoEvento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_serie_eventos", nullable = true)
    private SerieEventos serie;

    public Integer getIdCalendarioEvento() {
        return idCalendarioEvento;
    }

    public void setIdCalendarioEvento(Integer idCalendarioEvento) {
        this.idCalendarioEvento = idCalendarioEvento;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public String getDescricaoEvento() {
        return descricaoEvento;
    }

    public void setDescricaoEvento(String descricaoEvento) {
        this.descricaoEvento = descricaoEvento;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public SerieEventos getSerie() {
        return serie;
    }

    public void setSerie(SerieEventos serie) {
        this.serie = serie;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    @PrePersist
    private void beforePersist() {
        setDefaultValues();
    }

    @PreUpdate
    private void beforeUpdate() {
        setDefaultValues();
    }

    private void setDefaultValues() {
        if (getTipoEvento() == null) {
            setTipoEvento(TipoEvento.F);
        }
        if (getDataFim() == null) {
            setDataFim(getDataInicio());
        }
    }

    public CalendarioEventos plusYears(int ammount) {
        CalendarioEventos newCalendario = new CalendarioEventos();
        newCalendario.setDataInicio(new br.com.infox.util.time.Date(getDataInicio()).plusYears(ammount).toDate());
        newCalendario.setDataFim(new br.com.infox.util.time.Date(getDataFim() != null ? getDataFim() : getDataInicio()).plusYears(ammount).toDate());
        newCalendario.setDescricaoEvento(getDescricaoEvento());
        newCalendario.setLocalizacao(getLocalizacao());
        newCalendario.setSerie(getSerie());
        newCalendario.setTipoEvento(getTipoEvento());
        return newCalendario;
    }

    public CalendarioEventos withYear(int year) {
        CalendarioEventos newCalendario = new CalendarioEventos();
        newCalendario.setDataInicio(LocalDate.fromDateFields(getDataInicio()).withYear(year).toDate());
        newCalendario.setDataFim(LocalDate.fromDateFields(getDataFim()).withYear(year).toDate());
        newCalendario.setDescricaoEvento(getDescricaoEvento());
        newCalendario.setLocalizacao(getLocalizacao());
        newCalendario.setSerie(getSerie());
        newCalendario.setTipoEvento(getTipoEvento());
        return newCalendario;
    }

    public String getProximoPeriodo() {
        CalendarioEventos proximo = getProximoEvento();
        return proximo == null ? "-" : proximo.getPeriodo();
    }

    public CalendarioEventos getProximoEvento() {
        if (getSerie() != null) {
            switch (getSerie().getTipo()) {
            case A:
                return plusYears(1);
            default:
                return null;
            }
        }
        return null;
    }

    public boolean isBefore(Date date) {
        return date.after(getDataInicio()) && date.after(getDataFim());
    }

    public boolean isAfter(Date date) {
    	return date.before(getDataInicio()) && date.before(getDataFim());
    }

    public String getPeriodo() {
        return getInterval().toString();
    }

    public boolean isPastEventWithSerie() {
        return isBefore(new Date()) && getSerie() != null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idCalendarioEvento == null) ? 0 : idCalendarioEvento.hashCode());
        return result;
    }

    public boolean isSimilar(CalendarioEventos other) {
        if (other == null){
            return false;
        }
        if (!Objects.equals(getDataInicio(), other.getDataInicio())) {
            return false;
        }
        if (!Objects.equals(getDataFim(), other.getDataFim())) {
            return false;
        }
        if (!Objects.equals(getTipoEvento(), other.getTipoEvento())) {
            return false;
        }
        if (!Objects.equals(getLocalizacao(), other.getLocalizacao())) {
            return false;
        }
        if (!Objects.equals(getDescricaoEvento(), other.getDescricaoEvento())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CalendarioEventos)) {
            return false;
        }
        CalendarioEventos other = (CalendarioEventos) obj;
        if (idCalendarioEvento == null) {
            if (other.idCalendarioEvento != null) {
                return false;
            }
        } else if (!idCalendarioEvento.equals(other.idCalendarioEvento)) {
            return false;
        }
        return true;
    }

    public void setInterval(DateRange periodo){
        this.dataInicio = periodo.getStart().toDate();
        this.dataFim = periodo.getEnd().toDate();
    }
    
    public DateRange getInterval(){
        Date start = new br.com.infox.util.time.Date(getDataInicio()).withTimeAtStartOfDay().toDate();
		Date end = new br.com.infox.util.time.Date(getDataFim() == null ? getDataInicio() : getDataFim()).withTimeAtStartOfDay().toDate();
		return new DateRange(start, end);
    }
    
    @Override
    public String toString() {
        return MessageFormat.format("[{0}-{2}] {1}: [{3}]", getLocalizacao(), getDescricaoEvento(), getTipoEvento(),
                getPeriodo());
    }

    public boolean isSuspensaoPrazo() {
        return TipoEvento.S.equals(getTipoEvento());
    }

}
