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
package br.com.infox.ibpm.entity.help;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import br.com.infox.util.constants.LengthConstants;

@Entity
@Table(name = "tb_pagina", schema="public")
public class Pagina implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Integer idPagina;
	private String descricao;
	private String url;

	public Pagina() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_pagina")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_pagina", unique = true, nullable = false)
	public Integer getIdPagina() {
		return this.idPagina;
	}

	public void setIdPagina(Integer idPagina) {
		this.idPagina = idPagina;
	}
	
	@Column(name = "ds_descricao", nullable = false, length=LengthConstants.DESCRICAO_PADRAO)
	@NotNull
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "ds_url", nullable = false, length=LengthConstants.DESCRICAO_PADRAO)
	@NotNull
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}