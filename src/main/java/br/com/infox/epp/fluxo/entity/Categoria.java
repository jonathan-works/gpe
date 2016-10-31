package br.com.infox.epp.fluxo.entity;

import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.CATEGORIA_ATTRIBUTE;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.DESCRICAO_CATEGORIA;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.ID_CATEGORIA;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.LIST_CATEGORIAS_BY_NATUREZA;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.LIST_CATEGORIAS_BY_NATUREZA_QUERY;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.LIST_PROCESSO_EPA_BY_CATEGORIA_QUERY;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.LIST_PROCESSO_EPP_BY_CATEGORIA;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.SEQUENCE_CATEGORIA;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.TABLE_CATEGORIA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.hibernate.util.HibernateUtil;

@Entity
@Table(name = TABLE_CATEGORIA)
@NamedQueries(value = { @NamedQuery(name = LIST_PROCESSO_EPP_BY_CATEGORIA, query = LIST_PROCESSO_EPA_BY_CATEGORIA_QUERY), 
@NamedQuery(name = LIST_CATEGORIAS_BY_NATUREZA, query = LIST_CATEGORIAS_BY_NATUREZA_QUERY) })
public class Categoria implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_CATEGORIA)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_CATEGORIA, unique = true, nullable = false)
    private Integer idCategoria;

    @NotNull
    @Column(name = "cd_categoria")    
    @Size(max = LengthConstants.CODIGO_DOCUMENTO)
    private String codigo;
    
    @NotNull
    @Size(min = LengthConstants.FLAG, max = LengthConstants.DESCRICAO_PEQUENA)
    @Column(name = DESCRICAO_CATEGORIA, length = LengthConstants.DESCRICAO_PEQUENA, nullable = false)
    private String categoria;
    
    @NotNull
    @Column(name = ATIVO, nullable = false)
    private Boolean ativo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = CATEGORIA_ATTRIBUTE)
    private List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList = new ArrayList<>(0);
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = CATEGORIA_ATTRIBUTE, cascade = CascadeType.ALL)
    private List<CategoriaItem> categoriaItemList = new ArrayList<>();

    public Categoria() {
    }

    public Categoria(String categoria, Boolean ativo) {
        this.categoria = categoria;
        this.ativo = ativo;
    }
    
    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public List<CategoriaItem> getCategoriaItemList() {
        return categoriaItemList;
    }

    public void setCategoriaItemList(List<CategoriaItem> categoriaItemList) {
        this.categoriaItemList = categoriaItemList;
    }

    public void setNaturezaCategoriaFluxoList(List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList) {
        this.naturezaCategoriaFluxoList = naturezaCategoriaFluxoList;
    }

    public List<NaturezaCategoriaFluxo> getNaturezaCategoriaFluxoList() {
        return naturezaCategoriaFluxoList;
    }
    
    @Override
    public String toString() {
        return categoria;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result)
                + ((getIdCategoria() == null) ? 0 : getIdCategoria().hashCode());
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
        if (!(obj instanceof Categoria)) {
            return false;
        }
        final Categoria other = (Categoria) HibernateUtil.removeProxy(obj);
        if (getIdCategoria() != other.getIdCategoria()) {
            return false;
        }
        return true;
    }

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
