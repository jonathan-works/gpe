package br.com.infox.epp.fluxo.entity;

import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.CATEGORIA_ATTRIBUTE;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.DESCRICAO_CATEGORIA;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.ID_CATEGORIA;
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
@NamedQueries(value = { @NamedQuery(name = LIST_PROCESSO_EPP_BY_CATEGORIA, query = LIST_PROCESSO_EPA_BY_CATEGORIA_QUERY) })
public class Categoria implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idCategoria;
    private String categoria;
    private Boolean ativo;

    private List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList = new ArrayList<>(0);
    private List<CategoriaItem> categoriaItemList = new ArrayList<>();

    public Categoria() {
    }

    public Categoria(final String categoria, final Boolean ativo) {
        this.categoria = categoria;
        this.ativo = ativo;
    }

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_CATEGORIA)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_CATEGORIA, unique = true, nullable = false)
    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(final Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    @Column(name = DESCRICAO_CATEGORIA, length = LengthConstants.DESCRICAO_PEQUENA, nullable = false)
    @Size(min = LengthConstants.FLAG, max = LengthConstants.DESCRICAO_PEQUENA)
    @NotNull
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(final String categoria) {
        this.categoria = categoria;
    }

    @Column(name = ATIVO, nullable = false)
    @NotNull
    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(final Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return categoria;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = CATEGORIA_ATTRIBUTE, cascade = CascadeType.ALL)
    public List<CategoriaItem> getCategoriaItemList() {
        return categoriaItemList;
    }

    public void setCategoriaItemList(final List<CategoriaItem> categoriaItemList) {
        this.categoriaItemList = categoriaItemList;
    }

    public void setNaturezaCategoriaFluxoList(
            final List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList) {
        this.naturezaCategoriaFluxoList = naturezaCategoriaFluxoList;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = CATEGORIA_ATTRIBUTE)
    public List<NaturezaCategoriaFluxo> getNaturezaCategoriaFluxoList() {
        return naturezaCategoriaFluxoList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result)
                + ((idCategoria == null) ? 0 : idCategoria.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
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
        if (idCategoria != other.idCategoria) {
            return false;
        }
        return true;
    }

}
