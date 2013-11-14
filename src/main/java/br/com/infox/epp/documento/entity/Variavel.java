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
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import br.com.infox.core.constants.LengthConstants;


/**
 * Variavel generated by hbm2java
 */
@Entity
@Table(name = "tb_variavel", schema="public")
public class Variavel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idVariavel;
	private String variavel;
	private String valorVariavel;
	private Boolean ativo;
	
	private List<VariavelTipoModelo> variavelTipoModeloList = new ArrayList<VariavelTipoModelo>(0);

	public Variavel() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_variavel")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_variavel", unique = true, nullable = false)
	public int getIdVariavel() {
		return this.idVariavel;
	}

	public void setIdVariavel(int idVariavel) {
		this.idVariavel = idVariavel;
	}

	@Column(name = "ds_variavel", nullable = false, length=LengthConstants.DESCRICAO_PADRAO, unique = true)
	@NotNull
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	public String getVariavel() {
		return this.variavel;
	}

	public void setVariavel(String variavel) {
		String var = "";
		if(variavel != null && variavel.trim().length() > 0){
			var = variavel.replace(" ", "_");
         }
		this.variavel = var;
	}

	@Column(name = "vl_variavel", nullable = false, length=LengthConstants.DESCRICAO_PADRAO_DOBRO)
	@NotNull
	@Size(max=LengthConstants.DESCRICAO_PADRAO_DOBRO)
	public String getValorVariavel() {
		return this.valorVariavel;
	}

	public void setValorVariavel(String valorVariavel) {
		this.valorVariavel = valorVariavel;
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
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "variavel")
	public List<VariavelTipoModelo> getVariavelTipoModeloList() {
		return variavelTipoModeloList;
	}

	public void setVariavelTipoModeloList(List<VariavelTipoModelo> variavelTipoModeloList) {
		this.variavelTipoModeloList = variavelTipoModeloList;
	}

	@Override
	public String toString() {
		return variavel;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Variavel)) {
			return false;
		}
		Variavel other = (Variavel) obj;
		return getIdVariavel() == other.getIdVariavel();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdVariavel();
		return result;
	}
}