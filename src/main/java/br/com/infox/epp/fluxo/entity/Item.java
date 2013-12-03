/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.epp.fluxo.entity;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.core.persistence.Recursive;

@Entity
@Table(name = Item.TABLE_NAME, schema="public")
public class Item implements java.io.Serializable, Recursive<Item> {

	public static final String TABLE_NAME = "tb_item";
	private static final long serialVersionUID = 1L;

	private int idItem;
	private Item itemPai;
	private String codigoItem;
	private String descricaoItem;
	private Boolean ativo;
	private String caminhoCompleto;
	private List<Item> itemList;
	
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
	public Item getItemPai() {
		return this.itemPai;
	}

	public void setItemPai(Item itemPai) {
		this.itemPai = itemPai;
	}

	@Column(name = "cd_item", length=LengthConstants.DESCRICAO_PEQUENA)
	public String getCodigoItem() {
		return this.codigoItem;
	}

	public void setCodigoItem(String codigoItem) {
		this.codigoItem = codigoItem;
	}

	@Column(name = "ds_item", nullable = false, length=LengthConstants.DESCRICAO_PADRAO)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	@NotNull
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
	public List<Item> getItemList() {
		return this.itemList;
	}

	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}

	@Column(name="ds_caminho_completo", unique=true)
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
		Item pai = getItemPai();
		while (pai != null) {
			list.add(pai);
			pai = pai.getItemPai();
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

    @Override
    @Transient
    public Item getParent() {
        return this.getItemPai();
    }

    @Override
    public void setParent(Item parent) {
        this.setItemPai(parent);
    }

    @Override
    @Transient
    public String getHierarchicalPath() {
        return this.getCaminhoCompleto();
    }

    @Override
    public void setHierarchicalPath(String path) {
        this.setCaminhoCompleto(path);
    }

    @Override
    @Transient
    public String getPathDescriptor() {
        return this.getDescricaoItem();
    }

    @Override
    public void setPathDescriptor(String pathDescriptor) {
        this.setDescricaoItem(pathDescriptor);
    }

    @Override
    @Transient
    public List<Item> getChildList() {
    	List<Item> ret = getItemList();
    	if (ret != null){
    		return ret;
    	}
    	else return new ArrayList<>();
//        return this.getItemList();
    }

    @Override
    public void setChildList(List<Item> childList) {
        this.setItemList(childList);
    }

}