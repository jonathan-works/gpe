package br.com.infox.epp.processo.comunicacao.action;

import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.entity.Processo;

public class DestinatarioBean {
	private Long idDestinatario;
	private String nome;
	private String tipoComunicacao;
	private String meioExpedicao;
	private String dataEnvio;
	private String dataConfirmacao;
	private String responsavelConfirmacao;
	private String prazoAtendimento;
	private String prazoFinal;
	private Processo comunicacao;
	private Long idModeloComunicacao;
	private DocumentoBin documentoComunicacao;
	
	public Long getIdDestinatario() {
		return idDestinatario;
	}
	public void setIdDestinatario(Long idDestinatario) {
		this.idDestinatario = idDestinatario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getTipoComunicacao() {
		return tipoComunicacao;
	}
	public void setTipoComunicacao(String tipoComunicacao) {
		this.tipoComunicacao = tipoComunicacao;
	}
	public String getMeioExpedicao() {
		return meioExpedicao;
	}
	public void setMeioExpedicao(String meioExpedicao) {
		this.meioExpedicao = meioExpedicao;
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
	public Processo getComunicacao() {
		return comunicacao;
	}
	public void setComunicacao(Processo comunicacao) {
		this.comunicacao = comunicacao;
	}
	public Long getIdModeloComunicacao() {
		return idModeloComunicacao;
	}
	public void setIdModeloComunicacao(Long idModeloComunicacao) {
		this.idModeloComunicacao = idModeloComunicacao;
	}
	public DocumentoBin getDocumentoComunicacao() {
		return documentoComunicacao;
	}
	public void setDocumentoComunicacao(DocumentoBin documentoComunicacao) {
		this.documentoComunicacao = documentoComunicacao;
	}
}