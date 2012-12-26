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
package br.com.infox.ibpm.entity;
// Feito dia 17/11/2008, por Hiran

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

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import br.com.infox.access.entity.UsuarioLogin;


@Entity
@Table(name = "tb_parametro", schema="public")
public class Parametro implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idParametro;
	private String nomeVariavel;
	private String descricaoVariavel;
	private String valorVariavel;
	private Date dataAtualizacao = new Date();
	private Boolean sistema;	
	private UsuarioLogin usuarioModificacao;
	private Boolean ativo;
	private String esquemaTabelaId;

	public Parametro() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_parametro")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_parametro", unique = true, nullable = false)
	public int getIdParametro() {
		return this.idParametro;
	}

	public void setIdParametro(int idParametro) {
		this.idParametro = idParametro;
	}
	
	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario_modificacao", nullable = true)
	public UsuarioLogin getUsuarioModificacao() {
		return this.usuarioModificacao;
	}

	public void setUsuarioModificacao(UsuarioLogin usuarioModificacao) {
		this.usuarioModificacao = usuarioModificacao;
	}

	@Column(name = "nm_variavel", nullable = false, length = 100, unique = true)
	@NotNull
	@Length(max = 100)
	public String getNomeVariavel() {
		return this.nomeVariavel;
	}

	public void setNomeVariavel(String nomeVariavel) {
		this.nomeVariavel = nomeVariavel;
	}

	@Column(name = "ds_variavel", nullable = false, length = 200)
	@NotNull
	@Length(max = 100)
	public String getDescricaoVariavel() {
		return this.descricaoVariavel;
	}

	public void setDescricaoVariavel(String descricaoVariavel) {
		this.descricaoVariavel = descricaoVariavel;
	}

	@Column(name = "vl_variavel", nullable = false)
	@NotNull
	public String getValorVariavel() {
		return this.valorVariavel;
	}

	public void setValorVariavel(String valorVariavel) {
		this.valorVariavel = valorVariavel;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_atualizacao")
	public Date getDataAtualizacao() {
		return this.dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao= dataAtualizacao;
	}
	
	@Column(name = "in_sistema", nullable = false)
	@NotNull
	
	public Boolean getSistema() {
		return this.sistema;
	}

	public void setSistema(Boolean sistema) {
		this.sistema = sistema;
	}
	
	@Column(name = "in_ativo", nullable = false)
	@NotNull
	public Boolean getAtivo() {
		return this.ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@Column(name = "ds_esquema_tabela_id", length = 200)
	@Length(max = 200)
	public String getEsquemaTabelaId() {
		return this.esquemaTabelaId;
	}

	public void setEsquemaTabelaId(String esquemaTabelaId) {
		this.esquemaTabelaId = esquemaTabelaId;
	}

	@Override
	public String toString() {
		return nomeVariavel;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Parametro)) {
			return false;
		}
		Parametro other = (Parametro) obj;
		if (getIdParametro() != other.getIdParametro()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdParametro();
		return result;
	}
}