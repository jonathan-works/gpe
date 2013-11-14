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
package br.com.infox.epp.ajuda.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.hibernate.search.annotations.Analyzer;

import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name = "tb_historico_ajuda", schema="public")
@Analyzer(impl = BrazilianAnalyzer.class)
public class HistoricoAjuda implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Integer idHistoricoAjuda;
	private Date dataRegistro;
	private String texto;
	private Pagina pagina;
	private UsuarioLogin usuario;

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_historico_ajuda")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_historico_ajuda", unique = true, nullable = false)
	public Integer getIdHistoricoAjuda() {
		return idHistoricoAjuda;
	}

	public void setIdHistoricoAjuda(Integer idHistoricoAjuda) {
		this.idHistoricoAjuda = idHistoricoAjuda;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_registro", nullable = false, length = 0)
	@NotNull
	public Date getDataRegistro() {
		return dataRegistro;
	}
		
	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	@Column(name = "ds_texto")
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pagina", nullable = false)
	@NotNull
	public Pagina getPagina() {
		return pagina;
	}

	public void setPagina(Pagina pagina) {
		this.pagina = pagina;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", nullable = false)
	@NotNull
	public UsuarioLogin getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioLogin usuario) {
		this.usuario = usuario;
	}
	
}