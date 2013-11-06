package br.com.infox.ibpm.entity;

import java.io.Serializable;

import javax.persistence.*;

import javax.validation.constraints.Size;

import br.com.infox.epp.documento.query.DocumentoFisicoQuery;
import br.com.infox.util.constants.LengthConstants;

@Entity
@Table(schema="public", name=DocumentoFisico.TABLE_NAME)
@NamedQueries(value={
		@NamedQuery(name=DocumentoFisicoQuery.LIST_BY_PROCESSO,
				    query=DocumentoFisicoQuery.LIST_BY_PROCESSO_QUERY)
	  })
public class DocumentoFisico implements Serializable {

	public static final String TABLE_NAME = "tb_documento_fisico";
	private static final long serialVersionUID = 1L;

	private Integer idDocumentoFisico;
	private LocalizacaoFisica localizacaoFisica;
	private Processo processo;
	private String descricaoDocumentoFisico;
	private Boolean ativo=true;
	
	@SequenceGenerator(name="generator", sequenceName="sq_tb_documento_fisico")
	@Id
	@GeneratedValue(generator="generator")
	@Column(name="id_documento_fisico", unique=true, nullable=false)
	public Integer getIdDocumentoFisico() {
		return idDocumentoFisico;
	}
	public void setIdDocumentoFisico(Integer idDocumentoFisico) {
		this.idDocumentoFisico = idDocumentoFisico;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localizacao_fisica")
	public LocalizacaoFisica getLocalizacaoFisica() {
		return localizacaoFisica;
	}
	public void setLocalizacaoFisica(LocalizacaoFisica localizacaoFisica) {
		this.localizacaoFisica = localizacaoFisica;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo")
	public Processo getProcesso() {
		return processo;
	}
	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
	
	@Column(name="ds_documento_fisico", nullable=false, length=LengthConstants.DESCRICAO_PADRAO)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	public String getDescricaoDocumentoFisico() {
		return descricaoDocumentoFisico;
	}
	public void setDescricaoDocumentoFisico(String descricaoDocumentoFisico) {
		this.descricaoDocumentoFisico = descricaoDocumentoFisico;
	}
	
	@Column(name="in_ativo", nullable=false)
	public Boolean getAtivo() {
		return this.ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}
