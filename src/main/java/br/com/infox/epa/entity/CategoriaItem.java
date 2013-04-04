package br.com.infox.epa.entity;

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

import org.hibernate.validator.NotNull;

import br.com.infox.epa.query.CategoriaItemQuery;
import br.com.infox.ibpm.entity.Item;

@Entity
@Table(name=CategoriaItem.TABLE_NAME, schema="public",
	   uniqueConstraints={
			@UniqueConstraint(columnNames={"id_categoria", "id_item"})
		})
@NamedQueries(value={
        @NamedQuery(name=CategoriaItemQuery.LIST_BY_CATEGORIA,
                query=CategoriaItemQuery.LIST_BY_CATEGORIA_QUERY),
        @NamedQuery(name=CategoriaItemQuery.COUNT_BY_CATEGORIA_ITEM,
                        query=CategoriaItemQuery.COUNT_BY_CATEGORIA_ITEM_QUERY)
	})
public class CategoriaItem implements Serializable{
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "tb_categoria_item";
	
	private int idCategoriaItem;
	private Categoria categoria;
	private Item item;
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_categoria_item")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_categoria_item", unique = true, nullable = false)
	public int getIdCategoriaItem() {
		return idCategoriaItem;
	}
	
	public void setIdCategoriaItem(int idCategoriaItem) {
		this.idCategoriaItem = idCategoriaItem;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_item", nullable=false)
	@NotNull
	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria", nullable=false)
	public Categoria getCategoria() {
		return categoria;
	}
	
}