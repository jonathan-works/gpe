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
package br.com.infox.ibpm.entity.help;

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
import javax.persistence.Transient;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.NotNull;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.search.Reindexer;

@Entity
@Table(name = "tb_ajuda", schema="public")
@Analyzer(impl = BrazilianAnalyzer.class)
@Indexed
public class Ajuda implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Integer idAjuda;
	private Date dataRegistro;
	private String texto;
	private Pagina pagina;
	private UsuarioLogin usuario;

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_ajuda")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_ajuda", unique = true, nullable = false)
	public Integer getIdAjuda() {
		return idAjuda;
	}

	public void setIdAjuda(Integer idAjuda) {
		this.idAjuda = idAjuda;
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

	@Transient
	@Field(index=Index.TOKENIZED, store=Store.NO, name="texto")	
	public String getTextoIndexavel() {
		return Reindexer.getTextoIndexavel(texto);
	}
	
}