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
// Generated 30/10/2008 07:40:27 by Hibernate Tools 3.2.0.CR1

import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.CONTEUDO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.ID_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.ID_TIPO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.LIST_ATIVOS;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.LIST_ATIVOS_QUERY;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.MODELO_BY_TITULO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.MODELO_BY_TITULO_QUERY;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.SEQUENCE_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.TABLE_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ModeloDocumentoQuery.TITULO_MODELO_DOCUMENTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;

@Entity
@Table(name = TABLE_MODELO_DOCUMENTO, schema=PUBLIC)
@Inheritance(strategy=InheritanceType.JOINED)
@NamedQueries(value={
    @NamedQuery(name=LIST_ATIVOS, query=LIST_ATIVOS_QUERY),
    @NamedQuery(name=MODELO_BY_TITULO, query=MODELO_BY_TITULO_QUERY)
})
public class ModeloDocumento implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idModeloDocumento;
	private TipoModeloDocumento tipoModeloDocumento;
	private String tituloModeloDocumento;
	private String modeloDocumento;
	private Boolean ativo;

	public ModeloDocumento() {
	}

	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_MODELO_DOCUMENTO)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_MODELO_DOCUMENTO, unique = true, nullable = false)
	public int getIdModeloDocumento() {
		return this.idModeloDocumento;
	}

	public void setIdModeloDocumento(int idModeloDocumento) {
		this.idModeloDocumento = idModeloDocumento;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = ID_TIPO_MODELO_DOCUMENTO, nullable = false)
	@NotNull
	public TipoModeloDocumento getTipoModeloDocumento() {
		return this.tipoModeloDocumento;
	}

	public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
		this.tipoModeloDocumento = tipoModeloDocumento;
	}

	@Column(name = TITULO_MODELO_DOCUMENTO, nullable = false, length=LengthConstants.DESCRICAO_PADRAO_METADE)
	@Size(max=LengthConstants.DESCRICAO_PADRAO_METADE)
	@NotNull
	public String getTituloModeloDocumento() {
		return this.tituloModeloDocumento;
	}

	public void setTituloModeloDocumento(String tituloModeloDocumento) {
		this.tituloModeloDocumento = tituloModeloDocumento;
	}

	@Column(name = CONTEUDO_MODELO_DOCUMENTO, nullable = false)
	@NotNull
	public String getModeloDocumento() {
		return this.modeloDocumento;
	}

	public void setModeloDocumento(String modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}

	@Column(name = ATIVO, nullable = false)
	@NotNull
	public Boolean getAtivo() {
		return this.ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public String toString() {
		return tituloModeloDocumento;
	}

	public boolean hasChanges(ModeloDocumento modelo)	{
		if (modelo == null) {
			return true;
		}
		return !modelo.modeloDocumento.equals(this.modeloDocumento) 
		     || !modelo.tipoModeloDocumento.equals(this.tipoModeloDocumento)
		     || !modelo.tituloModeloDocumento.equals(this.tituloModeloDocumento)
		     || !modelo.ativo.equals(this.ativo);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ModeloDocumento)) {
			return false;
		}
		ModeloDocumento other = (ModeloDocumento) obj;
		if (getIdModeloDocumento() != other.getIdModeloDocumento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdModeloDocumento();
		return result;
	}
}