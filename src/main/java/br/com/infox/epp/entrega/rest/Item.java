package br.com.infox.epp.entrega.rest;

import java.io.Serializable;

public class Item implements Serializable, Comparable<Item> {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String codigo;	
	private String descricao;
	private Integer idItemPai;
	private Integer idCategoria;
	
	public Item() {
	}
	
	public Item(String codigo, String descricao) {
		super();
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
		Item other = (Item) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	@Override
	public int compareTo(Item o) {
		int retorno = getDescricao().compareTo(o.getDescricao());
		if(retorno == 0) {
			retorno = getCodigo().compareTo(o.getCodigo());
		}
		return retorno;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdItemPai() {
		return idItemPai;
	}

	public void setIdItemPai(Integer idItemPai) {
		this.idItemPai = idItemPai;
	}

	public Integer getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}
}
