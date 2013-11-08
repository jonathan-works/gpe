package br.com.infox.epp.fluxo.entity;

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

import br.com.infox.access.entity.Papel;
import br.com.infox.ibpm.query.FluxoPapelQuery;

@Entity
@Table(name=FluxoPapel.TABLE_NAME, schema="public",
	   uniqueConstraints={
			@UniqueConstraint(columnNames={"id_fluxo", "id_papel"})
	   })
@NamedQueries(value={
				@NamedQuery(name=FluxoPapelQuery.LIST_BY_FLUXO,
						    query=FluxoPapelQuery.LIST_BY_FLUXO_QUERY)
			  })
public class FluxoPapel {

	public static final String TABLE_NAME = "tb_fluxo_papel";

	private int idFluxoPapel;
	private Fluxo fluxo;	
	private Papel papel;
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_fluxo_papel")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_fluxo_papel", unique = true, nullable = false)
	public int getIdFluxoPapel() {
		return idFluxoPapel;
	}
	
	public void setIdFluxoPapel(int idFluxoPapel) {
		this.idFluxoPapel = idFluxoPapel;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_fluxo", nullable=false)
	@NotNull
	public Fluxo getFluxo() {
		return fluxo;
	}

	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_papel", nullable=false)
	@NotNull
	public Papel getPapel() {
		return papel;
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}
	
}