/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import br.com.infox.annotations.ChildList;
import br.com.infox.annotations.HierarchicalPath;
import br.com.infox.annotations.Parent;
import br.com.infox.annotations.PathDescriptor;
import br.com.infox.annotations.Recursive;

@Entity
@Table(name = Item.TABLE_NAME, schema="public")
@Recursive
public class Item implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_item";
	private static final long serialVersionUID = 1L;

	private int idItem;
	private Item itemPai;
	private String codigoItem;
	private String descricaoItem;
	private Boolean ativo;
	private String caminhoCompleto;
	private List<Item> itemList = new ArrayList<Item>(0);

	public Item() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_item")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_item", unique = true, nullable = false)
	public int getIdItem() {
		return this.idItem;
	}

	public void setIdItem(int idItem) {
		this.idItem = idItem;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_item_pai")
	@Parent
	public Item getItemPai() {
		return this.itemPai;
	}

	public void setItemPai(Item itemPai) {
		this.itemPai = itemPai;
	}

	@Column(name = "cd_item", length = 30)
	@Length(max = 30)
	public String getCodigoItem() {
		return this.codigoItem;
	}

	public void setCodigoItem(String codigoItem) {
		this.codigoItem = codigoItem;
	}

	@Column(name = "ds_item", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	@PathDescriptor
	public String getDescricaoItem() {
		return this.descricaoItem;
	}

	public void setDescricaoItem(String descricaoItem) {
		this.descricaoItem = descricaoItem;
	}

	@Column(name = "in_ativo", nullable = false)
	@NotNull
	public Boolean getAtivo() {
		return this.ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "itemPai")
	@ChildList
	public List<Item> getItemList() {
		return this.itemList;
	}

	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}

	@Column(name="ds_caminho_completo", unique=true)
	@HierarchicalPath
	public String getCaminhoCompleto() {
		return caminhoCompleto;
	}
	
	public void setCaminhoCompleto(String caminhoCompleto) {
		this.caminhoCompleto = caminhoCompleto;
	}
	
	public String caminhoCompletoToString()	{
		return caminhoCompleto.replace('|', '/').substring(0, caminhoCompleto.length()-1);
	}
	
	@Override
	public String toString() {
		return descricaoItem;
	}
	
	@Transient
	public List<Item> getListItemAtePai() {
		List<Item> list = new ArrayList<Item>();
		Item itemPai = getItemPai();
		while (itemPai != null) {
			list.add(itemPai);
			itemPai = itemPai.getItemPai();
		}
		return list;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Item)) {
			return false;
		}
		Item other = (Item) obj;
		if (getIdItem() != other.getIdItem()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdItem();
		return result;
	}
}