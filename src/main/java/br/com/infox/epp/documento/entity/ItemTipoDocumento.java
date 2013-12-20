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
package br.com.infox.epp.documento.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.documento.query.ItemTipoDocumentoQuery.ID_GRUPO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ItemTipoDocumentoQuery.ID_ITEM_TIPO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ItemTipoDocumentoQuery.ID_LOCALIZACAO;
import static br.com.infox.epp.documento.query.ItemTipoDocumentoQuery.SEQUENCE_ITEM_TIPO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ItemTipoDocumentoQuery.TABLE_ITEM_TIPO_DOCUMENTO;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;

@Entity
@Table(name = TABLE_ITEM_TIPO_DOCUMENTO, schema=PUBLIC)
public class ItemTipoDocumento implements Serializable {

	private static final long serialVersionUID = 1L;

	private int idItemTipoDocumento;
	private Localizacao localizacao;
	private GrupoModeloDocumento grupoModeloDocumento;

	public ItemTipoDocumento() {
	}

	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_ITEM_TIPO_DOCUMENTO)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_ITEM_TIPO_DOCUMENTO, unique = true, nullable = false)
	public int getIdItemTipoDocumento() {
		return this.idItemTipoDocumento;
	}

	public void setIdItemTipoDocumento(int idItemTipoDocumento) {
		this.idItemTipoDocumento = idItemTipoDocumento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = ID_LOCALIZACAO)
	public Localizacao getLocalizacao() {
		return this.localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = ID_GRUPO_MODELO_DOCUMENTO, nullable = false)
	@NotNull
	public GrupoModeloDocumento getGrupoModeloDocumento() {
		return this.grupoModeloDocumento;
	}

	public void setGrupoModeloDocumento(
			GrupoModeloDocumento grupoModeloDocumento) {
		this.grupoModeloDocumento = grupoModeloDocumento;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ItemTipoDocumento)) {
			return false;
		}
		ItemTipoDocumento other = (ItemTipoDocumento) obj;
		if (getIdItemTipoDocumento() != other.getIdItemTipoDocumento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdItemTipoDocumento();
		return result;
	}
}