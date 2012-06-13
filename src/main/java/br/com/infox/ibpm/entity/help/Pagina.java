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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

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
	
	@Column(name = "ds_descricao", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "ds_url", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}