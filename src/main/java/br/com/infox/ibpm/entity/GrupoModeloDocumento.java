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
package br.com.infox.ibpm.entity;
// Generated 30/10/2008 07:40:27 by Hibernate Tools 3.2.0.CR1

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.util.constants.LengthConstants;
import br.com.itx.util.HibernateUtil;
/**
 * GrupoModeloDocumento generated by hbm2java
 */
@Entity
@Table(name = "tb_grupo_modelo_documento", schema="public")
public class GrupoModeloDocumento implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idGrupoModeloDocumento;
	private String grupoModeloDocumento;
	private Boolean ativo;
	private List<ItemTipoDocumento> itemTipoDocumentoList = new ArrayList<ItemTipoDocumento>(0);
	private List<TipoModeloDocumento> tipoModeloDocumentoList = new ArrayList<TipoModeloDocumento>(0);

	public GrupoModeloDocumento() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_grupo_modelo_documento")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_grupo_modelo_documento", unique = true, nullable = false)
	public int getIdGrupoModeloDocumento() {
		return this.idGrupoModeloDocumento;
	}

	public void setIdGrupoModeloDocumento(int idGrupoModeloDocumento) {
		this.idGrupoModeloDocumento = idGrupoModeloDocumento;
	}

	@Column(name = "ds_grupo_modelo_documento", nullable = false, length=LengthConstants.DESCRICAO_PEQUENA, unique=true)
	@Size(max=LengthConstants.DESCRICAO_PEQUENA)
	@NotNull
	public String getGrupoModeloDocumento() {
		return this.grupoModeloDocumento;
	}

	public void setGrupoModeloDocumento(String grupoModeloDocumento) {
		this.grupoModeloDocumento = grupoModeloDocumento;
	}
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "grupoModeloDocumento")
	public List<ItemTipoDocumento> getItemTipoDocumentoList() {
		return this.itemTipoDocumentoList;
	}

	public void setItemTipoDocumentoList(
			List<ItemTipoDocumento> itemTipoDocumentoList) {
		this.itemTipoDocumentoList = itemTipoDocumentoList;
	}
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "grupoModeloDocumento")
	public List<TipoModeloDocumento> getTipoModeloDocumentoList() {
		return this.tipoModeloDocumentoList;
	}

	public void setTipoModeloDocumentoList(
			List<TipoModeloDocumento> tipoModeloDocumentoList) {
		this.tipoModeloDocumentoList = tipoModeloDocumentoList;
	}

	
	@Column(name = "in_ativo", nullable = false)
	@NotNull
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public String toString() {
		return grupoModeloDocumento;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GrupoModeloDocumento)) {
			return false;
		}
		GrupoModeloDocumento other = (GrupoModeloDocumento) HibernateUtil.removeProxy(obj);
		if (getIdGrupoModeloDocumento() != other.getIdGrupoModeloDocumento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdGrupoModeloDocumento();
		return result;
	}
}