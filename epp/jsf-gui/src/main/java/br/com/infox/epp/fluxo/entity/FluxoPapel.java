package br.com.infox.epp.fluxo.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.fluxo.query.FluxoPapelQuery.ID_FLUXO;
import static br.com.infox.epp.fluxo.query.FluxoPapelQuery.ID_FLUXO_PAPEL;
import static br.com.infox.epp.fluxo.query.FluxoPapelQuery.ID_PAPEL;
import static br.com.infox.epp.fluxo.query.FluxoPapelQuery.LIST_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.FluxoPapelQuery.LIST_BY_FLUXO_QUERY;
import static br.com.infox.epp.fluxo.query.FluxoPapelQuery.SEQUENCE_FLUXO_PAPEL;
import static br.com.infox.epp.fluxo.query.FluxoPapelQuery.TABLE_FLUXO_PAPEL;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Papel;

@Entity
@Table(name = TABLE_FLUXO_PAPEL, uniqueConstraints = { @UniqueConstraint(columnNames = {
    ID_FLUXO, ID_PAPEL }) })
@NamedQueries(value = { @NamedQuery(name = LIST_BY_FLUXO, query = LIST_BY_FLUXO_QUERY) })
public class FluxoPapel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idFluxoPapel;
    private Fluxo fluxo;
    private Papel papel;

    public FluxoPapel() {
    }

    public FluxoPapel(final Fluxo fluxo, final Papel papel) {
        this.fluxo = fluxo;
        this.papel = papel;
    }

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_FLUXO_PAPEL)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_FLUXO_PAPEL, unique = true, nullable = false)
    public Integer getIdFluxoPapel() {
        return idFluxoPapel;
    }

    public void setIdFluxoPapel(Integer idFluxoPapel) {
        this.idFluxoPapel = idFluxoPapel;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_FLUXO, nullable = false)
    @NotNull
    public Fluxo getFluxo() {
        return fluxo;
    }

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_PAPEL, nullable = false)
    @NotNull
    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getIdFluxoPapel() == null) ? 0 : getIdFluxoPapel().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FluxoPapel))
			return false;
		FluxoPapel other = (FluxoPapel) obj;
		if (getIdFluxoPapel() == null) {
			if (other.getIdFluxoPapel() != null)
				return false;
		} else if (!getIdFluxoPapel().equals(other.getIdFluxoPapel()))
			return false;
		return true;
	}

}
