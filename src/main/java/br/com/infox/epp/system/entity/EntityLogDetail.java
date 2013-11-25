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
package br.com.infox.epp.system.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.system.annotation.Ignore;

@Ignore
@Entity
@Table(name = "tb_log_detalhe", schema="public")
public class EntityLogDetail implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idLogDetalhe;
	private EntityLog entityLog;
	private String nomeAtributo;
	private String valorAnterior;
	private String valorAtual;

	public EntityLogDetail() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_log_detalhe")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_log_detalhe", unique = true, nullable = false)
	public int getIdLogDetalhe() {
		return idLogDetalhe;
	}
	
	public void setIdLogDetalhe(int idLogDetalhe) {
		this.idLogDetalhe = idLogDetalhe;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_log", nullable = false)
	@NotNull
	public EntityLog getEntityLog() {
		return entityLog;
	}
	
	public void setEntityLog(EntityLog entityLog) {
		this.entityLog = entityLog;
	}

	@Column(name = "nm_atributo", length=LengthConstants.NOME_ATRIBUTO)
	@Size(max=LengthConstants.NOME_ATRIBUTO)
	public String getNomeAtributo() {
		return nomeAtributo;
	}
	
	public void setNomeAtributo(String nomeAtributo) {
		this.nomeAtributo = nomeAtributo;
	}

	@Column(name = "ds_valor_anterior")
	public String getValorAnterior() {
		return valorAnterior;
	}
	
	public void setValorAnterior(String valorAnterior) {
		this.valorAnterior = valorAnterior;
	}

	@Column(name = "ds_valor_atual")
	public String getValorAtual() {
		return valorAtual;
	}
	
	public void setValorAtual(String valorAtual) {
		this.valorAtual = valorAtual;
	}
	
	@Override
	public String toString() {
		return nomeAtributo;
	}
	
}