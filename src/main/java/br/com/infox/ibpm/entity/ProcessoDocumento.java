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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import br.com.infox.access.entity.Papel;
import br.com.infox.access.entity.UsuarioLogin;

/**
 * ProcessoDocumento generated by hbm2java
 */
@Entity
@Table(name = "tb_processo_documento", schema="public")
@Inheritance(strategy=InheritanceType.JOINED)
public class ProcessoDocumento implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Long idJbpmTask;
	private int idProcessoDocumento;
	private TipoProcessoDocumento tipoProcessoDocumento;
	private ProcessoDocumentoBin processoDocumentoBin;
	private UsuarioLogin usuarioInclusao;
	private String nomeUsuarioInclusao;
	private Processo processo;
	private UsuarioLogin usuarioExclusao;
	private String nomeUsuarioExclusao;
	private String processoDocumento;
	private Date dataInclusao = new Date();
	private Date dataExclusao;
	private String motivoExclusao;
	private Integer numeroDocumento;
	private Boolean ativo = Boolean.TRUE;
	private String observacaoProcedimento;
	private Boolean documentoSigiloso = Boolean.FALSE;
	private Papel papel;
	private String nomePapel;
	private UsuarioLogin usuarioAlteracao;
	private String nomeUsuarioAlteracao;
	private Localizacao localizacao;
	private String nomeLocalizacao;
	
	public ProcessoDocumento() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_processo_documento")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_processo_documento", unique = true, nullable = false)
	@NotNull
	public int getIdProcessoDocumento() {
		return this.idProcessoDocumento;
	}

	public void setIdProcessoDocumento(int idProcessoDocumento) {
		this.idProcessoDocumento = idProcessoDocumento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_processo_documento", nullable = false)
	@NotNull
	public TipoProcessoDocumento getTipoProcessoDocumento() {
		return this.tipoProcessoDocumento;
	}

	public void setTipoProcessoDocumento(
			TipoProcessoDocumento tipoProcessoDocumento) {
		this.tipoProcessoDocumento = tipoProcessoDocumento;
	}
	
	@Column(name = "nr_documento", nullable = true)
	public Integer getNumeroDocumento() {
		return this.numeroDocumento;
	}

	public void setNumeroDocumento(Integer numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo_documento_bin", nullable = false)
	@NotNull
	public ProcessoDocumentoBin getProcessoDocumentoBin() {
		return this.processoDocumentoBin;
	}

	public void setProcessoDocumentoBin(
			ProcessoDocumentoBin processoDocumentoBin) {
		this.processoDocumentoBin = processoDocumentoBin;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_inclusao")
	public UsuarioLogin getUsuarioInclusao() {
		return this.usuarioInclusao;
	}
	
	public void setUsuarioInclusao(UsuarioLogin usuarioInclusao) {
		this.usuarioInclusao = usuarioInclusao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo", nullable = false)
	@NotNull
	public Processo getProcesso() {
		return this.processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_exclusao")
	public UsuarioLogin getUsuarioExclusao() {
		return this.usuarioExclusao;
	}

	public void setUsuarioExclusao(UsuarioLogin usuarioExclusao) {
		this.usuarioExclusao = usuarioExclusao;
	}

	@Column(name = "ds_processo_documento", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getProcessoDocumento() {
		return this.processoDocumento;
	}

	public void setProcessoDocumento(String processoDocumento) {
		this.processoDocumento = processoDocumento;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_inclusao", nullable = false)
	@NotNull
	public Date getDataInclusao() {
		return this.dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_exclusao")
	public Date getDataExclusao() {
		return this.dataExclusao;
	}

	public void setDataExclusao(Date dataExclusao) {
		this.dataExclusao = dataExclusao;
	}

	@Column(name = "ds_motivo_exclusao")
	public String getMotivoExclusao() {
		return this.motivoExclusao;
	}

	public void setMotivoExclusao(String motivoExclusao) {
		this.motivoExclusao = motivoExclusao;
	}

	@Column(name = "in_ativo", nullable = false)
	@NotNull
	public Boolean getAtivo() {
		return this.ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@Column(name = "ds_observacao_procedimento")
	public String getObservacaoProcedimento() {
		return this.observacaoProcedimento;
	}

	public void setObservacaoProcedimento(String observacaoProcedimento) {
		this.observacaoProcedimento = observacaoProcedimento;
	}
	
	@Column(name = "in_documento_sigiloso", nullable = false)
	@NotNull
	
	public Boolean getDocumentoSigiloso() {
		return this.documentoSigiloso;
	}

	public void setDocumentoSigiloso(Boolean documentoSigiloso) {
		this.documentoSigiloso = documentoSigiloso;
	}

	@Override
	public String toString() {
		return processoDocumento;
	}

	@Column(name = "id_jbpm_task")
	public Long getIdJbpmTask() {
		return idJbpmTask;
	}

	public void setIdJbpmTask(Long idJbpmTask) {
		this.idJbpmTask = idJbpmTask;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_papel")
	public Papel getPapel() {
		return this.papel;
	}
	
	public void setPapel(Papel papel) {
		this.papel = papel;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_alteracao")
	public UsuarioLogin getUsuarioAlteracao() {
		return this.usuarioAlteracao;
	}
	
	public void setUsuarioAlteracao(UsuarioLogin usuarioAlteracao) {
		this.usuarioAlteracao = usuarioAlteracao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localizacao")
	public Localizacao getLocalizacao() {
		return localizacao;
	}
	
	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}

	@Column(name = "ds_nome_usuario_inclusao", length = 100)
	@Length(max = 100)
	public String getNomeUsuarioInclusao() {
		return nomeUsuarioInclusao;
	}

	public void setNomeUsuarioInclusao(String nomeUsuarioInclusao) {
		this.nomeUsuarioInclusao = nomeUsuarioInclusao;
	}

	@Column(name = "ds_nome_usuario_exclusao", length = 100)
	@Length(max = 100)
	public String getNomeUsuarioExclusao() {
		return nomeUsuarioExclusao;
	}

	public void setNomeUsuarioExclusao(String nomeUsuarioExclusao) {
		this.nomeUsuarioExclusao = nomeUsuarioExclusao;
	}

	@Column(name = "ds_nome_papel", length = 100)
	@Length(max = 100)
	public String getNomePapel() {
		return nomePapel;
	}

	public void setNomePapel(String nomePapel) {
		this.nomePapel = nomePapel;
	}

	@Column(name = "ds_nome_usuario_alteracao", length = 100)
	@Length(max = 100)
	public String getNomeUsuarioAlteracao() {
		return nomeUsuarioAlteracao;
	}

	public void setNomeUsuarioAlteracao(String nomeUsuarioAlteracao) {
		this.nomeUsuarioAlteracao = nomeUsuarioAlteracao;
	}

	@Column(name = "ds_nome_localizacao", length = 100)
	@Length(max = 100)
	public String getNomeLocalizacao() {
		return nomeLocalizacao;
	}

	public void setNomeLocalizacao(String nomeLocalizacao) {
		this.nomeLocalizacao = nomeLocalizacao;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ProcessoDocumento)) {
			return false;
		}
		ProcessoDocumento other = (ProcessoDocumento) obj;
		if (getIdProcessoDocumento() != other.getIdProcessoDocumento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdProcessoDocumento();
		return result;
	}
}