package br.com.infox.epp.fluxo.entity;

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

import br.com.infox.epp.entity.ProcessoEpa;
import br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery;
import br.com.itx.util.HibernateUtil;

@Entity
@Table(name=NaturezaCategoriaFluxo.TABLE_NAME, schema="public",
	   uniqueConstraints={
			@UniqueConstraint(columnNames={"id_categoria", "id_fluxo"})
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
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idNaturezaCategoriaFluxo;
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
        if (!(obj instanceof NaturezaCategoriaFluxo)) {
            return false;
        }
        NaturezaCategoriaFluxo other = (NaturezaCategoriaFluxo) HibernateUtil.removeProxy(obj);
        if (idNaturezaCategoriaFluxo != other.idNaturezaCategoriaFluxo) {
            return false;
        }
        return true;
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
		return true;
	}
	
}