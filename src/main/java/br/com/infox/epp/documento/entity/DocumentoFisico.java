package br.com.infox.epp.documento.entity;

import static br.com.infox.core.persistence.ORConstants.*;
import static br.com.infox.epp.documento.query.DocumentoFisicoQuery.*;
import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(schema=PUBLIC, name=TABLE_DOCUMENTO_FISICO)
@NamedQueries(value={
    @NamedQuery(name=LIST_BY_PROCESSO, query=LIST_BY_PROCESSO_QUERY)
})
public class DocumentoFisico implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer idDocumentoFisico;
	private LocalizacaoFisica localizacaoFisica;
	private Processo processo;
	private String descricaoDocumentoFisico;
	private Boolean ativo=true;
	
	@SequenceGenerator(name=GENERATOR, sequenceName=SEQUENCE_DOCUMENTO_FISICO)
	@Id
	@GeneratedValue(generator=GENERATOR)
	@Column(name=ID_DOCUMENTO_FISICO, unique=true, nullable=false)
	public Integer getIdDocumentoFisico() {
		return idDocumentoFisico;
	}
	public void setIdDocumentoFisico(Integer idDocumentoFisico) {
		this.idDocumentoFisico = idDocumentoFisico;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = ID_LOCALIZACAO_FISICA)
	public LocalizacaoFisica getLocalizacaoFisica() {
		return localizacaoFisica;
	}
	public void setLocalizacaoFisica(LocalizacaoFisica localizacaoFisica) {
		this.localizacaoFisica = localizacaoFisica;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = ID_PROCESSO)
	public Processo getProcesso() {
		return processo;
	}
	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
	
	@Column(name=DOCUMENTO_FISICO, nullable=false, length=LengthConstants.DESCRICAO_PADRAO)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	public String getDescricaoDocumentoFisico() {
		return descricaoDocumentoFisico;
	}
	public void setDescricaoDocumentoFisico(String descricaoDocumentoFisico) {
		this.descricaoDocumentoFisico = descricaoDocumentoFisico;
	}
	
	@Column(name=ATIVO, nullable=false)
	public Boolean getAtivo() {
		return this.ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}
