package br.com.infox.epp.entrega.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;

@Entity
@Table(name = "tb_modelo_entrega_item")
public class ModeloEntregaItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "ModeloEntregaItemGenerator", allocationSize = 1, initialValue = 1, sequenceName = "sq_modelo_entrega_item")
	@GeneratedValue(generator = "ModeloEntregaItemGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_modelo_entrega_item")
	private Long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_modelo_entrega", nullable = false)
	private ModeloEntrega modeloEntrega;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_categoria_entrega_item", nullable = false)
	private CategoriaEntregaItem item;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "id_categoria_entrega_item_pai", nullable = true)
	private CategoriaEntregaItem itemPai;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ModeloEntrega getModeloEntrega() {
		return modeloEntrega;
	}

	public void setModeloEntrega(ModeloEntrega modeloEntrega) {
		this.modeloEntrega = modeloEntrega;
	}

	public CategoriaEntregaItem getItem() {
		return item;
	}

	public void setItem(CategoriaEntregaItem item) {
		this.item = item;
	}

	public CategoriaEntregaItem getItemPai() {
		return itemPai;
	}

	public void setItemPai(CategoriaEntregaItem itemPai) {
		this.itemPai = itemPai;
	}
}
