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
package br.com.infox.epp.endereco.entity;
// Generated 30/10/2008 07:40:27 by Hibernate Tools 3.2.0.CR1

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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.ParamDef;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.endereco.filter.CepFilter;

/**
 * Cep generated by hbm2java
 */

@Entity
@Table(name = Cep.TABLE_NAME, schema="public", uniqueConstraints={
		@UniqueConstraint(columnNames={"nr_cep"})
		})
@FilterDefs(value={
		@FilterDef(name=CepFilter.FILTER_CEP_ESTADO, parameters={
				@ParamDef(type="string", name=CepFilter.FILTER_PARAM_NUMERO_CEP)})
		})
@Filter(name=CepFilter.FILTER_CEP_ESTADO, condition= CepFilter.CONDITION_CEP_ESTADO)
public class Cep implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_cep";
	private static final long serialVersionUID = 1L;
	
	private int idCep;
	private String numeroCep;
	private String nomeLogradouro;
	private String nomeBairro;
	private Municipio municipio;
	private String complemento;
	private Boolean ativo;
	
	public Cep() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_cep")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_cep", unique = true, nullable = false)
	public int getIdCep() {
		return this.idCep;
	}

	public void setIdCep(int idCep) {
		this.idCep = idCep;
	}

	
	@Column(name = "nr_cep", nullable = false, length=LengthConstants.CEP, unique = true)
	@Size(max=LengthConstants.CEP)
	@NotNull
	public String getNumeroCep() {
		return this.numeroCep;
	}

	public void setNumeroCep(String numeroCep) {
		this.numeroCep = numeroCep;
	}

	@Column(name = "nm_logradouro", length=LengthConstants.NOME_LOGRADOURO)
	@Size(max=LengthConstants.NOME_LOGRADOURO)
	public String getNomeLogradouro() {
		return this.nomeLogradouro;
	}

	public void setNomeLogradouro(String nomeLogradouro) {
		this.nomeLogradouro = nomeLogradouro;
	}

	@Column(name = "nm_bairro", length=LengthConstants.NOME_BAIRRO)
	@Size(max=LengthConstants.NOME_BAIRRO)
	public String getNomeBairro() {
		return this.nomeBairro;
	}

	public void setNomeBairro(String nomeBairro) {
		this.nomeBairro = nomeBairro;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_municipio")
	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	@Column(name = "ds_complemento", length=LengthConstants.DESCRICAO_PADRAO)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
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
		return numeroCep;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Cep)) {
			return false;
		}
		Cep other = (Cep) obj;
		if (getIdCep() != other.getIdCep()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdCep();
		return result;
	}
	
}