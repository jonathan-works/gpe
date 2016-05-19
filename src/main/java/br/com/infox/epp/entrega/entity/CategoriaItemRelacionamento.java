package br.com.infox.epp.entrega.entity;

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

@Entity
@Table(name="tb_categoria_item_relac")
public class CategoriaItemRelacionamento implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "GeneratorCategoriaItemRelacionamento", sequenceName = "sq_categoria_item_relac")
    @GeneratedValue(generator = "GeneratorCategoriaItemRelacionamento", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_categoria_item_relac", unique = true, nullable = false)
	private Integer id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_categoria_item_filho")
	private CategoriaEntregaItem itemFilho;	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_categoria_item_pai")
	private CategoriaEntregaItem itemPai;
	
	public Integer getId() {
		return id;
	}

	public CategoriaEntregaItem getItemFilho() {
		return itemFilho;
	}



	public void setItemFilho(CategoriaEntregaItem itemFilho) {
		this.itemFilho = itemFilho;
	}



	public CategoriaEntregaItem getItemPai() {
		return itemPai;
	}



	public void setItemPai(CategoriaEntregaItem itemPai) {
		this.itemPai = itemPai;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemFilho == null) ? 0 : itemFilho.hashCode());
		result = prime * result + ((itemPai == null) ? 0 : itemPai.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoriaItemRelacionamento other = (CategoriaItemRelacionamento) obj;
		if (itemFilho == null) {
			if (other.itemFilho != null)
				return false;
		} else if (!itemFilho.equals(other.itemFilho))
			return false;
		if (itemPai == null) {
			if (other.itemPai != null)
				return false;
		} else if (!itemPai.equals(other.itemPai))
			return false;
		return true;
	}
}
