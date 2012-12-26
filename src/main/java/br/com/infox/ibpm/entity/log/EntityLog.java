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
package br.com.infox.ibpm.entity.log;

import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.type.TipoOperacaoLogEnum;


/**
 * Endereco generated by Rodrigo ;p
 */
@Ignore
@Entity
@Table(name = "tb_log", schema="public")
public class EntityLog implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idLog;
	private UsuarioLogin usuario;
	private String urlRequisicao;
	private String ip;
	private String nomeEntidade;
	private String nomePackage;
	private String idEntidade;
	private TipoOperacaoLogEnum tipoOperacao;
	private Date dataLog;
	private List<EntityLogDetail> entityLogDetailList = new ArrayList<EntityLogDetail>(0);

	public EntityLog() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_log")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_log", unique = true, nullable = false)
	public int getIdLog() {
		return idLog;
	}
	
	public void setIdLog(int idLog) {
		this.idLog = idLog;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	public UsuarioLogin getUsuario() {
		return this.usuario;
	}

	public void setUsuario(UsuarioLogin usuario) {
		this.usuario = usuario;
	}

	@Column(name = "id_pagina", length = 200)
	@Length(max = 200)
	public String getUrlRequisicao() {
		return urlRequisicao;
	}
	
	public void setUrlRequisicao(String urlRequisicao) {
		this.urlRequisicao = urlRequisicao;
	}

	@Column(name = "ds_ip", length = 15)
	@Length(max = 15)
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "ds_entidade", length = 50)
	@Length(max = 50)
	public String getNomeEntidade() {
		return nomeEntidade;
	}
	
	public void setNomeEntidade(String nomeEntidade) {
		this.nomeEntidade = nomeEntidade;
	}
	
	@Column(name = "ds_package", length = 150)
	@Length(max = 150)
	public String getNomePackage() {
		return nomePackage;
	}
	
	public void setNomePackage(String nomePackage) {
		this.nomePackage = nomePackage;
	}
	
	@Column(name = "ds_id_entidade", length = 200)
	@Length(max = 200)
	public String getIdEntidade() {
		return idEntidade;
	}
	
	public void setIdEntidade(String idEntidade) {
		this.idEntidade = idEntidade;
	}
	
	@Column(name = "tp_operacao")
	@Type(type = "br.com.infox.type.TipoOperacaoLogType")
	public TipoOperacaoLogEnum getTipoOperacao() {
		return tipoOperacao;
	}
	
	public void setTipoOperacao(TipoOperacaoLogEnum tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_log", nullable = false)
	@NotNull	
	public Date getDataLog() {
		return dataLog;
	}
	
	public void setDataLog(Date dataLog) {
		this.dataLog = dataLog;
	}

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "entityLog")
	public List<EntityLogDetail> getLogDetalheList() {
		return entityLogDetailList;
	}
	
	public void setLogDetalheList(List<EntityLogDetail> logDetalheList) {
		this.entityLogDetailList = logDetalheList;
	}

	@Override
	public String toString() {
		return nomeEntidade;
	}
	
}