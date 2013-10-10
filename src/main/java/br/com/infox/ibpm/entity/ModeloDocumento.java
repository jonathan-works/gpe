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
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.query.ModeloDocumentoQuery;
import br.com.infox.util.constants.LengthConstants;
/**
 * ModeloDocumento generated by hbm2java
 */
@Entity
@Table(name = "tb_modelo_documento", schema="public")
@Inheritance(strategy=InheritanceType.JOINED)
@NamedQueries(value={
		@NamedQuery(name=ModeloDocumentoQuery.LIST_ATIVOS,
				    query=ModeloDocumentoQuery.LIST_ATIVOS_QUERY),
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

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_modelo_documento")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_modelo_documento", unique = true, nullable = false)
	public int getIdModeloDocumento() {
		return this.idModeloDocumento;
	}

	public void setIdModeloDocumento(int idModeloDocumento) {
		this.idModeloDocumento = idModeloDocumento;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_modelo_documento", nullable = false)
	@NotNull
	public TipoModeloDocumento getTipoModeloDocumento() {
		return this.tipoModeloDocumento;
	}

	public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
		this.tipoModeloDocumento = tipoModeloDocumento;
	}

	@Column(name = "ds_titulo_modelo_documento", nullable = false, length=LengthConstants.DESCRICAO_PADRAO_METADE)
	@Size(max=LengthConstants.DESCRICAO_PADRAO_METADE)
	@NotNull
	public String getTituloModeloDocumento() {
		return this.tituloModeloDocumento;
	}

	public void setTituloModeloDocumento(String tituloModeloDocumento) {
		this.tituloModeloDocumento = tituloModeloDocumento;
	}

	@Column(name = "ds_modelo_documento", nullable = false)
	@NotNull
	public String getModeloDocumento() {
		return this.modeloDocumento;
	}

	public void setModeloDocumento(String modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}

	@Column(name = "in_ativo", nullable = false)
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