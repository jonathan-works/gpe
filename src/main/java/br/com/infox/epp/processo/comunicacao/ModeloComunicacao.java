package br.com.infox.epp.processo.comunicacao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.entity.ProcessoEpa;

@Entity
@Table(name = "tb_modelo_comunicacao")
public class ModeloComunicacao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "ModeloComunicacaoGenerator", initialValue = 1, allocationSize = 1, sequenceName = "sq_modelo_comunicacao")
	@GeneratedValue(generator = "ModeloComunicacaoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_modelo_comunicacao")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_tipo_comunicacao", nullable = false)
	private TipoComunicacao tipoComunicacao;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_documento_bin", nullable = false)
	private DocumentoBin comunicacao;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_classificacao_documento", nullable = false)
	private ClassificacaoDocumento classificacaoComunicacao;
	
	@NotNull
	@Column(name = "in_finalizada", nullable = false)
	private Boolean finalizada = false;
	
	@NotNull
	@Column(name = "in_expedida", nullable = false)
	private Boolean expedida = false;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_processo_epa", nullable = false)
	private ProcessoEpa processo;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_localizacao_resp_assinat", nullable = false)
	private Localizacao localizacaoResponsavelAssinatura;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_perfil_template_resp_assinat")
	private PerfilTemplate perfilResponsavelAssinatura;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "modeloComunicacao", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<DestinatarioModeloComunicacao> destinatarios = new ArrayList<>(0);
	
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "modeloComunicacao", orphanRemoval = true)
	private List<DocumentoModeloComunicacao> documentos = new ArrayList<>(0);
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TipoComunicacao getTipoComunicacao() {
		return tipoComunicacao;
	}

	public void setTipoComunicacao(TipoComunicacao tipoComunicacao) {
		this.tipoComunicacao = tipoComunicacao;
	}

	public DocumentoBin getComunicacao() {
		return comunicacao;
	}
	
	public void setComunicacao(DocumentoBin comunicacao) {
		this.comunicacao = comunicacao;
	}
	
	public ClassificacaoDocumento getClassificacaoComunicacao() {
		return classificacaoComunicacao;
	}
	
	public void setClassificacaoComunicacao(ClassificacaoDocumento classificacaoComunicacao) {
		this.classificacaoComunicacao = classificacaoComunicacao;
	}

	public Boolean getFinalizada() {
		return finalizada;
	}

	public void setFinalizada(Boolean finalizada) {
		this.finalizada = finalizada;
	}

	public Localizacao getLocalizacaoResponsavelAssinatura() {
		return localizacaoResponsavelAssinatura;
	}

	public void setLocalizacaoResponsavelAssinatura(Localizacao localizacaoResponsavelAssinatura) {
		this.localizacaoResponsavelAssinatura = localizacaoResponsavelAssinatura;
	}

	public PerfilTemplate getPerfilResponsavelAssinatura() {
		return perfilResponsavelAssinatura;
	}

	public void setPerfilResponsavelAssinatura(PerfilTemplate perfilResponsavelAssinatura) {
		this.perfilResponsavelAssinatura = perfilResponsavelAssinatura;
	}

	public List<DestinatarioModeloComunicacao> getDestinatarios() {
		return destinatarios;
	}

	public void setDestinatarios(List<DestinatarioModeloComunicacao> destinatarios) {
		this.destinatarios = destinatarios;
	}

	public List<DocumentoModeloComunicacao> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<DocumentoModeloComunicacao> documentos) {
		this.documentos = documentos;
	}
	
	public ProcessoEpa getProcesso() {
		return processo;
	}
	
	public void setProcesso(ProcessoEpa processo) {
		this.processo = processo;
	}
	
	public Boolean getExpedida() {
		return expedida;
	}
	
	public void setExpedida(Boolean expedida) {
		this.expedida = expedida;
	}
}