package br.com.infox.epa.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import javax.validation.constraints.NotNull;

import br.com.infox.epa.query.NaturezaCategoriaFluxoQuery;
import br.com.infox.ibpm.entity.Fluxo;

@Entity
@Table(name=NaturezaCategoriaFluxo.TABLE_NAME, schema="public",
	   uniqueConstraints={
			@UniqueConstraint(columnNames={"id_categoria", "id_natureza"})
		})
@NamedQueries(value={
				@NamedQuery(name=NaturezaCategoriaFluxoQuery.LIST_BY_NATUREZA,
						    query=NaturezaCategoriaFluxoQuery.LIST_BY_NATUREZA_QUERY)
			  })
public class NaturezaCategoriaFluxo implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "tb_natureza_categoria_fluxo";
	
	private int idNaturezaCategoriaFluxo;
	private Natureza natureza;
	private Categoria categoria;
	private Fluxo fluxo;
	
	private List<ProcessoEpa> processoEpaList = new ArrayList<ProcessoEpa>(0);
	
	private List<NatCatFluxoLocalizacao> natCatFluxoLocalizacaoList = 
		new ArrayList<NatCatFluxoLocalizacao>(0);
	
	public void setIdNaturezaCategoriaFluxo(int idNaturezaCategoriaFluxo) {
		this.idNaturezaCategoriaFluxo = idNaturezaCategoriaFluxo;
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_natureza_categoria_fluxo")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_natureza_categoria_fluxo", unique = true, nullable = false)
	public int getIdNaturezaCategoriaFluxo() {
		return idNaturezaCategoriaFluxo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_natureza", nullable=false)
	@NotNull
	public Natureza getNatureza() {
		return natureza;
	}
	
	public void setNatureza(Natureza natureza) {
		this.natureza = natureza;
	}

	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_fluxo", nullable=false)
	@NotNull
	public Fluxo getFluxo() {
		return fluxo;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria", nullable=false)
	@NotNull
	public Categoria getCategoria() {
		return categoria;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "naturezaCategoriaFluxo")
	public List<NatCatFluxoLocalizacao> getNatCatFluxoLocalizacaoList() {
		return natCatFluxoLocalizacaoList;
	}
	
	public void setNatCatFluxoLocalizacaoList(List<NatCatFluxoLocalizacao> naturezaLocalizacaoList) {
		this.natCatFluxoLocalizacaoList = naturezaLocalizacaoList;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(natureza)
		  .append(" - ")
		  .append(categoria)
		  .append(" - ")
		  .append(fluxo);
		return sb.toString();
	}

	public void setProcessoEpaList(List<ProcessoEpa> processoEpaList) {
		this.processoEpaList = processoEpaList;
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="naturezaCategoriaFluxo")
	public List<ProcessoEpa> getProcessoEpaList() {
		return processoEpaList;
	}
	
	@Transient
	public boolean isAtivo(){
		return (natureza.getAtivo() && categoria.getAtivo() && fluxo.getAtivo());
	}
	
}