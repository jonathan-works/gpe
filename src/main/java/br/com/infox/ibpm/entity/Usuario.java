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
package br.com.infox.ibpm.entity;
// Generated 30/10/2008 07:40:27 by Hibernate Tools 3.2.0.CR1

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotNull;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.entity.log.EntityLog;

/**
 * Usuario generated by hbm2java
 */
@Entity
@Table(name = "tb_usuario", schema="public", uniqueConstraints={
		@UniqueConstraint(columnNames="nr_cpf")
})
@PrimaryKeyJoinColumn
public class Usuario extends UsuarioLogin implements java.io.Serializable  {

	private static final long serialVersionUID = 1L;

	private Boolean bloqueio;
	private Boolean provisorio;
	private String cpf;
	private Date dataExpiracao; //Data de previs�o para expirar o usu�rio provis�rio

	private List<ProcessoDocumentoBin> processoDocumentoBinList = new ArrayList<ProcessoDocumentoBin>(0);
	private List<Fluxo> fluxoList = new ArrayList<Fluxo>(0);
	private List<UsuarioLocalizacao> usuarioLocalizacaoList = new ArrayList<UsuarioLocalizacao>(0);
	private List<Processo> processoListForIdUsuarioCadastroProcesso = new ArrayList<Processo>(0);
	private List<BloqueioUsuario> bloqueioUsuarioList = new ArrayList<BloqueioUsuario>(0);
	private List<Endereco> enderecoList = new ArrayList<Endereco>(0);
	private List<ProcessoDocumento> processoDocumentoListForIdUsuarioInclusao = new ArrayList<ProcessoDocumento>(0);
	private List<ProcessoDocumento> processoDocumentoListForIdUsuarioExclusao = new ArrayList<ProcessoDocumento>(0);
	private List<EntityLog> entityLogList = new ArrayList<EntityLog>(0);

	public Usuario() {
		bloqueio = false;
		provisorio = false;
		dataExpiracao = null;
	}

	@Column(name = "in_bloqueio", nullable = false)
	@NotNull
	public Boolean getBloqueio() {
		return this.bloqueio;
	}

	public void setBloqueio(Boolean bloqueio) {
		this.bloqueio = bloqueio;
	}

	@Column(name = "in_provisorio")
	public Boolean getProvisorio() {
		return this.provisorio;
	}

	public void setProvisorio(Boolean provisorio) {
		this.provisorio = provisorio;
	}
	
	@Column(name = "dt_expiracao_usuario", nullable=true)
	public Date getDataExpiracao() {
		return dataExpiracao;
	}

	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuario")
	public List<ProcessoDocumentoBin> getProcessoDocumentoBinList() {
		return this.processoDocumentoBinList;
	}

	public void setProcessoDocumentoBinList(
			List<ProcessoDocumentoBin> processoDocumentoBinList) {
		this.processoDocumentoBinList = processoDocumentoBinList;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuarioPublicacao")
	public List<Fluxo> getFluxoList() {
		return this.fluxoList;
	}

	public void setFluxoList(List<Fluxo> fluxoList) {
		this.fluxoList = fluxoList;
	}
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuario")
	@OrderBy("idUsuarioLocalizacao")
	public List<UsuarioLocalizacao> getUsuarioLocalizacaoList() {
		return this.usuarioLocalizacaoList;
	}

	public void setUsuarioLocalizacaoList(
			List<UsuarioLocalizacao> usuarioLocalizacaoList) {
		this.usuarioLocalizacaoList = usuarioLocalizacaoList;
	}
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuarioCadastroProcesso")
	public List<Processo> getProcessoListForIdUsuarioCadastroProcesso() {
		return this.processoListForIdUsuarioCadastroProcesso;
	}

	public void setProcessoListForIdUsuarioCadastroProcesso(
			List<Processo> processoListForIdUsuarioCadastroProcesso) {
		this.processoListForIdUsuarioCadastroProcesso = processoListForIdUsuarioCadastroProcesso;
	}
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuario")
	public List<BloqueioUsuario> getBloqueioUsuarioList() {
		return this.bloqueioUsuarioList;
	}

	public void setBloqueioUsuarioList(List<BloqueioUsuario> bloqueioUsuarioList) {
		this.bloqueioUsuarioList = bloqueioUsuarioList;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuario")
	public List<Endereco> getEnderecoList() {
		return this.enderecoList;
	}

	public void setEnderecoList(List<Endereco> enderecoList) {
		this.enderecoList = enderecoList;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuarioInclusao")
	public List<ProcessoDocumento> getProcessoDocumentoListForIdUsuarioInclusao() {
		return this.processoDocumentoListForIdUsuarioInclusao;
	}

	public void setProcessoDocumentoListForIdUsuarioInclusao(
			List<ProcessoDocumento> processoDocumentoListForIdUsuarioInclusao) {
		this.processoDocumentoListForIdUsuarioInclusao = processoDocumentoListForIdUsuarioInclusao;
	}
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuarioExclusao")
	public List<ProcessoDocumento> getProcessoDocumentoListForIdUsuarioExclusao() {
		return this.processoDocumentoListForIdUsuarioExclusao;
	}

	public void setProcessoDocumentoListForIdUsuarioExclusao(
			List<ProcessoDocumento> processoDocumentoListForIdUsuarioExclusao) {
		this.processoDocumentoListForIdUsuarioExclusao = processoDocumentoListForIdUsuarioExclusao;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuario")	
	public List<EntityLog> getEntityLogList() {
		return entityLogList;
	}
	
	public void setEntityLogList(List<EntityLog> entityLogList) {
		this.entityLogList = entityLogList;
	}
	
	@Transient
	public Localizacao[] getLocalizacoes() {
		Localizacao[] locs = new Localizacao[usuarioLocalizacaoList.size()];
		int i = 0;
		for (UsuarioLocalizacao Uloc : usuarioLocalizacaoList) {
			locs[i] = Uloc.getLocalizacao();
			i++;
		}
		return locs;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getIdUsuario() == null) {
			return false;
		}
		if (!(obj instanceof Usuario)) {
			return false;
		}
		Usuario other = (Usuario) obj;
		return getIdUsuario().equals(other.getIdUsuario());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getIdUsuario() == null) ? 0 : getIdUsuario().hashCode());
		return result;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	@Column(name="nr_cpf", nullable=false, length=15)
	@NotNull
	public String getCpf() {
		return cpf;
	}
}