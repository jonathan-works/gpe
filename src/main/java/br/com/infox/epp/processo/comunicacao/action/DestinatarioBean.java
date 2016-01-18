package br.com.infox.epp.processo.comunicacao.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.base.Strings;

import br.com.infox.epp.processo.comunicacao.MeioExpedicao;

public class DestinatarioBean {
	
	private String tipoComunicacao;
	private String dataEnvio;
	private String dataConfirmacao;
	private String responsavelConfirmacao;
	private String prazoAtendimento;
	private String prazoFinal;
	private String prazoOriginal;
	private String statusProrrogacao;
	private String nome;
	private String dataResposta;
	private Long idDestinatario;
	private String numeroComunicacao;
	private String numeroModeloComunicacao;
	private MeioExpedicao meioExpedicao;
	
	public DestinatarioBean(String tipoComunicacao, Date dataEnvio, Long idDestinatario, String numeroComunicacao, String numeroModeloComunicacao,
			MeioExpedicao meioExpedicao) {
		this.tipoComunicacao = tipoComunicacao;
		this.dataEnvio = new SimpleDateFormat("dd/MM/yyyy").format(dataEnvio);
		this.idDestinatario = idDestinatario;
		this.numeroComunicacao = numeroComunicacao;
		this.numeroModeloComunicacao = numeroModeloComunicacao;
		this.meioExpedicao = meioExpedicao;
	}
	
	public String getTipoComunicacao() {
		return tipoComunicacao;
	}
	public void setTipoComunicacao(String tipoComunicacao) {
		this.tipoComunicacao = tipoComunicacao;
	}
	public String getDataEnvio() {
		return dataEnvio;
	}
	public void setDataEnvio(String dataEnvio) {
		this.dataEnvio = dataEnvio;
	}
	public String getDataConfirmacao() {
		return dataConfirmacao;
	}
	public void setDataConfirmacao(String dataConfirmacao) {
		this.dataConfirmacao = dataConfirmacao;
	}
	public String getResponsavelConfirmacao() {
		return responsavelConfirmacao;
	}
	public void setResponsavelConfirmacao(String responsavelConfirmacao) {
		this.responsavelConfirmacao = responsavelConfirmacao;
	}
	public String getPrazoAtendimento() {
		return prazoAtendimento;
	}
	public void setPrazoAtendimento(String prazoAtendimento) {
		this.prazoAtendimento = prazoAtendimento;
	}
	public String getPrazoFinal() {
		return prazoFinal;
	}
	public void setPrazoFinal(String prazoFinal) {
		this.prazoFinal = prazoFinal;
	}
	public String getPrazoOriginal() {
        return prazoOriginal;
    }
    public void setPrazoOriginal(String prazoOriginal) {
        this.prazoOriginal = prazoOriginal;
    }
	public String getStatusProrrogacao() {
		if(Strings.isNullOrEmpty(statusProrrogacao)){
			return "-";
		}
		return statusProrrogacao;
	}
	public void setStatusProrrogacao(String statusProrrogacao) {
		this.statusProrrogacao = statusProrrogacao;
	}
	public String getDataResposta() {
		return dataResposta;
	}
	public void setDataResposta(String dataResposta) {
		this.dataResposta = dataResposta;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(Long idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	public String getNumeroComunicacao() {
		return numeroComunicacao;
	}

	public void setNumeroComunicacao(String numeroComunicacao) {
		this.numeroComunicacao = numeroComunicacao;
	}

	public String getNumeroModeloComunicacao() {
		return numeroModeloComunicacao;
	}

	public void setNumeroModeloComunicacao(String numeroModeloComunicacao) {
		this.numeroModeloComunicacao = numeroModeloComunicacao;
	}

	public MeioExpedicao getMeioExpedicao() {
		return meioExpedicao;
	}

	public void setMeioExpedicao(MeioExpedicao meioExpedicao) {
		this.meioExpedicao = meioExpedicao;
	}
}