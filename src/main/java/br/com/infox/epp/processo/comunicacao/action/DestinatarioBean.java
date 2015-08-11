package br.com.infox.epp.processo.comunicacao.action;

import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;

import com.google.common.base.Strings;

public class DestinatarioBean {
	private Long idDestinatario;
	private String nome;
	private TipoComunicacao tipoComunicacao;
	private String meioExpedicao;
	private String dataEnvio;
	private String dataConfirmacao;
	private String responsavelConfirmacao;
	private String prazoAtendimento;
	private String prazoFinal;
	private String prazoOriginal;
	private Processo comunicacao;
	private ModeloComunicacao modeloComunicacao;
	private Documento documentoComunicacao;
	private String statusProrrogacao;
	private DestinatarioModeloComunicacao destinatario;
	private String dataResposta;
	
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
	public TipoComunicacao getTipoComunicacao() {
		return tipoComunicacao;
	}
	public void setTipoComunicacao(TipoComunicacao tipoComunicacao) {
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
	public String getPrazoOriginal() {
        return prazoOriginal;
    }
    public void setPrazoOriginal(String prazoOriginal) {
        this.prazoOriginal = prazoOriginal;
    }
    public Processo getComunicacao() {
		return comunicacao;
	}
	public void setComunicacao(Processo comunicacao) {
		this.comunicacao = comunicacao;
	}
	public ModeloComunicacao getModeloComunicacao() {
		return modeloComunicacao;
	}
	public void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		this.modeloComunicacao = modeloComunicacao;
	}
	public Documento getDocumentoComunicacao() {
		return documentoComunicacao;
	}
	public void setDocumentoComunicacao(Documento documentoComunicacao) {
		this.documentoComunicacao = documentoComunicacao;
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
	public DestinatarioModeloComunicacao getDestinatario() {
		return destinatario;
	}
	public void setDestinatario(DestinatarioModeloComunicacao destinatario) {
		this.destinatario = destinatario;
	}
	public String getDataResposta() {
		return dataResposta;
	}
	public void setDataResposta(String dataResposta) {
		this.dataResposta = dataResposta;
	}
}