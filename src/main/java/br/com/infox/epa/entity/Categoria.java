package br.com.infox.epa.entity;

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

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import br.com.infox.epa.query.CategoriaQuery;

@Entity
@Table(name=Categoria.TABLE_NAME, schema="public")
@NamedQueries(value={
				@NamedQuery(name=CategoriaQuery.LIST_PROCESSO_EPA_BY_CATEGORIA,
						    query=CategoriaQuery.LIST_PROCESSO_EPA_BY_CATEGORIA_QUERY)
			  })
public class Categoria {

	public static final String TABLE_NAME = "tb_categoria";

	private int idCategoria;
	private String categoria;
	private Boolean ativo;
	
	private List<CategoriaAssunto> categoriaAssuntolist = new ArrayList<CategoriaAssunto>(0);
	private List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList = new ArrayList<NaturezaCategoriaFluxo>(0);
	
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
	
	@Column(name="ds_categoria", length=30, nullable=false)
	@NotNull
	@Length(max=30)
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
	
	public void setCategoriaAssuntolist(List<CategoriaAssunto> categoriaAssuntolist) {
		this.categoriaAssuntolist = categoriaAssuntolist;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "categoria")
	public List<CategoriaAssunto> getCategoriaAssuntolist() {
		return categoriaAssuntolist;
	}

	public void setNaturezaCategoriaFluxoList(
			List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList) {
		this.naturezaCategoriaFluxoList = naturezaCategoriaFluxoList;
	}
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="categoria")
	public List<NaturezaCategoriaFluxo> getNaturezaCategoriaFluxoList() {
		return naturezaCategoriaFluxoList;
	}
	
}