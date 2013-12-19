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
import static br.com.infox.epp.ajuda.query.PaginaQuery.DESCRICAO;
import static br.com.infox.epp.ajuda.query.PaginaQuery.ID_PAGINA;
import static br.com.infox.epp.ajuda.query.PaginaQuery.PAGINA_BY_URL;
import static br.com.infox.epp.ajuda.query.PaginaQuery.PAGINA_BY_URL_QUERY;
import static br.com.infox.epp.ajuda.query.PaginaQuery.SEQUENCE_PAGINA;
import static br.com.infox.epp.ajuda.query.PaginaQuery.TABLE_PAGINA;
import static br.com.infox.epp.ajuda.query.PaginaQuery.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;

@Entity
@Table(name = TABLE_PAGINA, schema=PUBLIC)
@NamedQueries(value={
    @NamedQuery(name=PAGINA_BY_URL, query=PAGINA_BY_URL_QUERY)
})
public class Pagina implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Integer idPagina;
	private String descricao;
	private String url;

	public Pagina() {
	}

	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_PAGINA)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_PAGINA, unique = true, nullable = false)
	public Integer getIdPagina() {
		return this.idPagina;
	}

	public void setIdPagina(Integer idPagina) {
		this.idPagina = idPagina;
	}
	
	@Column(name = DESCRICAO, nullable = false, length=LengthConstants.DESCRICAO_PADRAO)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	@NotNull
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = URL, nullable = false, length=LengthConstants.DESCRICAO_PADRAO)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	@NotNull
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}