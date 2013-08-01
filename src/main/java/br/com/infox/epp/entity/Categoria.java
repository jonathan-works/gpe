package br.com.infox.epp.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.query.CategoriaQuery;
import br.com.infox.util.constants.LengthConstants;
import br.com.itx.util.HibernateUtil;

@Entity
@Table(name=Categoria.TABLE_NAME, schema="public")
@NamedQueries(value={
				@NamedQuery(name=CategoriaQuery.LIST_PROCESSO_EPP_BY_CATEGORIA,
						    query=CategoriaQuery.LIST_PROCESSO_EPA_BY_CATEGORIA_QUERY)
			  })
public class Categoria implements Serializable{
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "tb_categoria";

	private int idCategoria;
	private String categoria;
	private Boolean ativo;
	
	private List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList = new ArrayList<NaturezaCategoriaFluxo>(0);
	private List<CategoriaItem> categoriaItemList = new ArrayList<CategoriaItem>();
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_categoria")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_categoria", unique = true, nullable = false)
	public int getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(int idCategoria) {
		this.idCategoria = idCategoria;
	}
	
	@Column(name="ds_categoria", length=LengthConstants.DESCRICAO_PEQUENA, nullable=false)
	@Size(max=LengthConstants.DESCRICAO_PEQUENA)
	@NotNull
	public String getCategoria() {
		return categoria;
	}
	
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	@Column(name="in_ativo", nullable=false)
	@NotNull
	public Boolean getAtivo() {
		return ativo;
	}
	
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@Override
	public String toString() {
		return categoria;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "categoria")
	public List<CategoriaItem> getCategoriaItemList() {
		return categoriaItemList;
	}
	public void setCategoriaItemList(List<CategoriaItem> categoriaItemList) {
		this.categoriaItemList = categoriaItemList;
	}
	
	public void setNaturezaCategoriaFluxoList(
			List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList) {
		this.naturezaCategoriaFluxoList = naturezaCategoriaFluxoList;
	}
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="categoria")
	public List<NaturezaCategoriaFluxo> getNaturezaCategoriaFluxoList() {
		return naturezaCategoriaFluxoList;
	}
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idCategoria;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Categoria))
            return false;
        Categoria other = (Categoria) HibernateUtil.removeProxy(obj);
        if (idCategoria != other.idCategoria)
            return false;
        return true;
    }
	
}