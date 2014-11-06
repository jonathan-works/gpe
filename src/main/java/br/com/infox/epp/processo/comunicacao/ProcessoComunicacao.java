package br.com.infox.epp.processo.comunicacao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(name = "tb_processo_comunicacao")
@PrimaryKeyJoinColumn
@DiscriminatorValue("PC")
public class ProcessoComunicacao extends Processo {
	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name = "tp_meio_expedicao", nullable = false)
	private MeioExpedicao meioExpedicao;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_tipo_comunicacao", nullable = false)
	private TipoComunicacao tipoComunicacao;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_localizacao", nullable = false)
	private Localizacao localizacao;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "id_documento")
	private Documento comunicacao;
	
	@Temporal(TemporalType.DATE)
	@NotNull
	@Column(name = "dt_fim_prazo_ciencia", nullable = false)
	private Date dataFimPrazoCiencia;

	@Column(name = "nr_prazo")
	private Integer prazo;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "dt_fim_prazo")
	private Date dataFimPrazo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa_fisica")
	private PessoaFisica destinatario;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localizacao_destinataria")
	private Localizacao localizacaoDestinataria;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_modelo_comunicacao", nullable = false)
	private ModeloComunicacao modeloComunicacao;
	
	public MeioExpedicao getMeioExpedicao() {
		return meioExpedicao;
	}

	public void setMeioExpedicao(MeioExpedicao meioExpedicao) {
		this.meioExpedicao = meioExpedicao;
	}

	public TipoComunicacao getTipoComunicacao() {
		return tipoComunicacao;
	}

	public void setTipoComunicacao(TipoComunicacao tipoComunicacao) {
		this.tipoComunicacao = tipoComunicacao;
	}

	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}

	public Documento getComunicacao() {
		return comunicacao;
	}
	
	public void setComunicacao(Documento comunicacao) {
		this.comunicacao = comunicacao;
	}
	
	public Integer getPrazo() {
		return prazo;
	}

	public void setPrazo(Integer prazo) {
		this.prazo = prazo;
	}

	public PessoaFisica getDestinatario() {
		return destinatario;
	}
	
	public void setDestinatario(PessoaFisica destinatario) {
		this.destinatario = destinatario;
	}
	
	public Localizacao getLocalizacaoDestinataria() {
		return localizacaoDestinataria;
	}
	
	public void setLocalizacaoDestinataria(Localizacao localizacaoDestinataria) {
		this.localizacaoDestinataria = localizacaoDestinataria;
	}
	
	public Date getDataFimPrazoCiencia() {
		return dataFimPrazoCiencia;
	}
	
	public void setDataFimPrazoCiencia(Date dataFimPrazoCiencia) {
		this.dataFimPrazoCiencia = dataFimPrazoCiencia;
	}
	
	public ModeloComunicacao getModeloComunicacao() {
		return modeloComunicacao;
	}
	
	public void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		this.modeloComunicacao = modeloComunicacao;
	}
	
	public Date getDataFimPrazo() {
		return dataFimPrazo;
	}
	
	public void setDataFimPrazo(Date dataFimPrazo) {
		this.dataFimPrazo = dataFimPrazo;
	}
}
