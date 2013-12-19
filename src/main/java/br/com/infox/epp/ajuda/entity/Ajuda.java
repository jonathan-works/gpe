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

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.ajuda.query.AjudaQuery.AJUDA_BY_URL;
import static br.com.infox.epp.ajuda.query.AjudaQuery.AJUDA_BY_URL_QUERY;
import static br.com.infox.epp.ajuda.query.AjudaQuery.DATA_REGISTRO;
import static br.com.infox.epp.ajuda.query.AjudaQuery.ID_AJUDA;
import static br.com.infox.epp.ajuda.query.AjudaQuery.PAGINA;
import static br.com.infox.epp.ajuda.query.AjudaQuery.SEQUENCE_AJUDA;
import static br.com.infox.epp.ajuda.query.AjudaQuery.TABLE_AJUDA;
import static br.com.infox.epp.ajuda.query.AjudaQuery.TEXTO;
import static br.com.infox.epp.ajuda.query.AjudaQuery.USUARIO;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.search.Reindexer;

@Entity
@Table(name = TABLE_AJUDA, schema=PUBLIC)
@NamedQueries(value={
    @NamedQuery(name=AJUDA_BY_URL, query=AJUDA_BY_URL_QUERY)
})
@Analyzer(impl = BrazilianAnalyzer.class)
@Indexed
public class Ajuda implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Integer idAjuda;
	private Date dataRegistro;
	private String texto;
	private Pagina pagina;
	private UsuarioLogin usuario;

	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_AJUDA)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_AJUDA, unique = true, nullable = false)
	public Integer getIdAjuda() {
		return idAjuda;
	}

	public void setIdAjuda(Integer idAjuda) {
		this.idAjuda = idAjuda;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = DATA_REGISTRO, nullable = false, length = 0)
	@NotNull
	public Date getDataRegistro() {
		return dataRegistro;
	}
		
	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	@Column(name = TEXTO)
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = PAGINA, nullable = false)
	@NotNull
	public Pagina getPagina() {
		return pagina;
	}

	public void setPagina(Pagina pagina) {
		this.pagina = pagina;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = USUARIO, nullable = false)
	@NotNull
	public UsuarioLogin getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioLogin usuario) {
		this.usuario = usuario;
	}

	@Transient
	@Field(index=Index.YES, store=Store.NO, name="texto")	
	public String getTextoIndexavel() {
		return Reindexer.getTextoIndexavel(texto);
	}
	
}