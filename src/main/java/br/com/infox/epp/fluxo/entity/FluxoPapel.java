package br.com.infox.epp.fluxo.entity;

import static br.com.infox.core.persistence.ORConstants.*;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
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
import static br.com.infox.epp.fluxo.query.FluxoPapelQuery.*;

@Entity
@Table(name=TABLE_FLUXO_PAPEL, schema=PUBLIC, uniqueConstraints={
    @UniqueConstraint(columnNames={ID_FLUXO, ID_PAPEL})
})
@NamedQueries(value={
    @NamedQuery(name=LIST_BY_FLUXO, query=LIST_BY_FLUXO_QUERY)
})
public class FluxoPapel implements Serializable{

    private static final long serialVersionUID = 1L;

	private int idFluxoPapel;
	private Fluxo fluxo;	
	private Papel papel;
	
	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_FLUXO_PAPEL)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_FLUXO_PAPEL, unique = true, nullable = false)
	public int getIdFluxoPapel() {
		return idFluxoPapel;
	}
	
	public void setIdFluxoPapel(int idFluxoPapel) {
		this.idFluxoPapel = idFluxoPapel;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = ID_FLUXO, nullable=false)
	@NotNull
	public Fluxo getFluxo() {
		return fluxo;
	}

	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = ID_PAPEL, nullable=false)
	@NotNull
	public Papel getPapel() {
		return papel;
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}
	
}