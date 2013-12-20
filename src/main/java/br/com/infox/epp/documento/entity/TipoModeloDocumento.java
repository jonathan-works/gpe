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

import static br.com.infox.core.persistence.ORConstants.*;
import static br.com.infox.epp.documento.query.TipoModeloDocumentoQuery.*;

import java.io.Serializable;
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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import br.com.infox.core.constants.LengthConstants;
import br.com.itx.util.HibernateUtil;

@Entity
@Table(name = TABLE_TIPO_MODELO_DOCUMENTO, schema=PUBLIC, uniqueConstraints={
		@UniqueConstraint(columnNames={TIPO_MODELO_DOCUMENTO}), 
		@UniqueConstraint(columnNames={ABREVIACAO})
})
public class TipoModeloDocumento implements Serializable {

	private static final long serialVersionUID = 1L;

	private int idTipoModeloDocumento;
	private GrupoModeloDocumento grupoModeloDocumento;
	private String tipoModeloDocumento;
	private String abreviacao;
	private Boolean ativo;
	private TipoModeloDocumento tipoModeloDocumentoCombo;
	
	private List<ModeloDocumento> modeloDocumentoList = new ArrayList<ModeloDocumento>(0);
	
	private List<VariavelTipoModelo> variavelTipoModeloList = new ArrayList<VariavelTipoModelo>(0);


	public TipoModeloDocumento() {
	}

	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_TIPO_MODELO_DOCUMENTO)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_TIPO_MODELO_DOCUMENTO, unique = true, nullable = false)
	public int getIdTipoModeloDocumento() {
		return this.idTipoModeloDocumento;
	}

	public void setIdTipoModeloDocumento(int idTipoModeloDocumento) {
		this.idTipoModeloDocumento = idTipoModeloDocumento;
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

	@Column(name = TIPO_MODELO_DOCUMENTO, nullable = false, length=LengthConstants.DESCRICAO_PADRAO_METADE)
	@NotNull
	@Size(max=LengthConstants.DESCRICAO_PADRAO_METADE)
	public String getTipoModeloDocumento() {
		return this.tipoModeloDocumento;
	}

	public void setTipoModeloDocumento(String tipoModeloDocumento) {
		this.tipoModeloDocumento = tipoModeloDocumento;
	}

	@Column(name = ABREVIACAO, nullable = false, length=LengthConstants.DESCRICAO_ABREVIADA, unique=true)
	@NotNull
	@Size(max=LengthConstants.DESCRICAO_ABREVIADA)
	public String getAbreviacao() {
		return this.abreviacao;
	}

	public void setAbreviacao(String abreviacao) {
		this.abreviacao = abreviacao;
	}
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = TIPO_MODELO_DOCUMENTO_ATTRIBUTE)
	public List<ModeloDocumento> getModeloDocumentoList() {
		return this.modeloDocumentoList;
	}

	public void setModeloDocumentoList(List<ModeloDocumento> modeloDocumentoList) {
		this.modeloDocumentoList = modeloDocumentoList;
	}
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = TIPO_MODELO_DOCUMENTO_ATTRIBUTE)
	public List<VariavelTipoModelo> getVariavelTipoModeloList() {
		return variavelTipoModeloList;
	}

	public void setVariavelTipoModeloList(List<VariavelTipoModelo> variavelTipoModeloList) {
		this.variavelTipoModeloList = variavelTipoModeloList;
	}
	
	@Column(name = ATIVO, nullable = false)
	@NotNull
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@Transient
	public TipoModeloDocumento getTipoModeloDocumentoCombo(){
		return tipoModeloDocumentoCombo;
	}
	
	public void setTipoModeloDocumentoCombo(TipoModeloDocumento tipoModeloDocumentoCombo){
		this.tipoModeloDocumentoCombo = tipoModeloDocumentoCombo;
	}
	
	@Override
	public String toString() {
		return tipoModeloDocumento;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TipoModeloDocumento)) {
			return false;
		}
		TipoModeloDocumento other = (TipoModeloDocumento) HibernateUtil.removeProxy(obj);
		if (getIdTipoModeloDocumento() != other.getIdTipoModeloDocumento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdTipoModeloDocumento();
		return result;
	}
}