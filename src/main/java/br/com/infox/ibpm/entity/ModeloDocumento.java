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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
/**
 * ModeloDocumento generated by hbm2java
 */
@Entity
@Table(name = "tb_modelo_documento", schema="public")
@Inheritance(strategy=InheritanceType.JOINED)
public class ModeloDocumento implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idModeloDocumento;
	private TipoModeloDocumento tipoModeloDocumento;
	private String tituloModeloDocumento;
	private String modeloDocumento;
	private Boolean ativo = Boolean.TRUE;

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

	@Column(name = "ds_titulo_modelo_documento", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
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