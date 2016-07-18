package br.com.infox.epp.fluxo.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.fluxo.query.CategoriaItemQuery.COUNT_BY_CATEGORIA_ITEM;
import static br.com.infox.epp.fluxo.query.CategoriaItemQuery.COUNT_BY_CATEGORIA_ITEM_QUERY;
import static br.com.infox.epp.fluxo.query.CategoriaItemQuery.ID_CATEGORIA;
import static br.com.infox.epp.fluxo.query.CategoriaItemQuery.ID_CATEGORIA_ITEM;
import static br.com.infox.epp.fluxo.query.CategoriaItemQuery.ID_ITEM;
import static br.com.infox.epp.fluxo.query.CategoriaItemQuery.LIST_BY_CATEGORIA;
import static br.com.infox.epp.fluxo.query.CategoriaItemQuery.LIST_BY_CATEGORIA_QUERY;
import static br.com.infox.epp.fluxo.query.CategoriaItemQuery.SEQUENCE_CATEGORIA_ITEM;
import static br.com.infox.epp.fluxo.query.CategoriaItemQuery.TABLE_CATEGORIA_ITEM;

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

@Entity
@Table(name = TABLE_CATEGORIA_ITEM, uniqueConstraints = { @UniqueConstraint(columnNames = {
    ID_CATEGORIA, ID_ITEM }) })
@NamedQueries(value = {
    @NamedQuery(name = LIST_BY_CATEGORIA, query = LIST_BY_CATEGORIA_QUERY),
    @NamedQuery(name = COUNT_BY_CATEGORIA_ITEM, query = COUNT_BY_CATEGORIA_ITEM_QUERY) })
public class CategoriaItem implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_CATEGORIA_ITEM)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_CATEGORIA_ITEM, unique = true, nullable = false)
    private Integer idCategoriaItem;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_ITEM, nullable = false)
    private Categoria categoria;
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_CATEGORIA, nullable = false)
    private Item item;

    public CategoriaItem() {
    }

    public CategoriaItem(Categoria categoria, Item item) {
        this.categoria = categoria;
        this.item = item;
    }

    public Integer getIdCategoriaItem() {
        return idCategoriaItem;
    }

    public void setIdCategoriaItem(Integer idCategoriaItem) {
        this.idCategoriaItem = idCategoriaItem;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getIdCategoriaItem() == null) ? 0 : getIdCategoriaItem().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CategoriaItem))
			return false;
		CategoriaItem other = (CategoriaItem) obj;
		if (getIdCategoriaItem() == null) {
			if (other.getIdCategoriaItem() != null)
				return false;
		} else if (!getIdCategoriaItem().equals(other.getIdCategoriaItem()))
			return false;
		return true;
	}

}
