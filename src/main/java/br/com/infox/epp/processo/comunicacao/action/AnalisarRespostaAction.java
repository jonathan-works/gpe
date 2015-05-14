package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(AnalisarRespostaAction.NAME)
@Scope(ScopeType.CONVERSATION)//TODO ver isso ai
public class AnalisarRespostaAction implements Serializable{

	private static final long serialVersionUID = 1L;
	static final String NAME = "analisarRespostaAction";
	private static final LogProvider LOG = Logging.getLogProvider(AnalisarRespostaAction.class);

	private String destinatario = "Isaac de Oliveira Seabra";
	private Date dataResposta = new Date();
	private String tipoComunicacao = "Notificação Defesa Prévia";
	private List<DummyDocumento> documentosComunicacao;
	private List<DummyDocumento> documentosResposta;
	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	public Date getDataResposta() {
		return dataResposta;
	}

	public void setDataResposta(Date dataResposta) {
		this.dataResposta = dataResposta;
	}

	public String getTipoComunicacao() {
		return tipoComunicacao;
	}

	public void setTipoComunicacao(String tipoComunicacao) {
		this.tipoComunicacao = tipoComunicacao;
	}

	public List<DummyDocumento> getDocumentosComunicacao() {
		documentosComunicacao = new ArrayList<AnalisarRespostaAction.DummyDocumento>();
		for (int i = 0; i < 2; i++) {
			documentosComunicacao.add(new DummyDocumento("Documento "+i, "Classificacao "+i, (40*i)+"k", Integer.toString(i+200)));
		}
		return documentosComunicacao;
	}

	public void setDocumentosComunicacao(List<DummyDocumento> documentosComunicacao) {
		this.documentosComunicacao = documentosComunicacao;
	}
	
	public List<DummyDocumento> getDocumentosResposta() {
		documentosResposta = new ArrayList<AnalisarRespostaAction.DummyDocumento>();
		for (int i = 0; i < 5; i++) {
			documentosResposta.add(new DummyDocumento("Documento "+i, "Classificacao "+i, (25*i)+"k", Integer.toString(i+200)));
		}
		return documentosResposta;
	}

	public void setDocumentosResposta(List<DummyDocumento> documentosResposta) {
		this.documentosResposta = documentosResposta;
	}

	public static class DummyDocumento{
		private String descricao;
		private String classificacaoDocumento;
		private String tamanho;
		private String numeroDocumento;
		
		public DummyDocumento(String descricao, String classificacaoDocumento, String tamanho, String numeroDocumento){
			setDescricao(descricao);
			setClassificacaoDocumento(classificacaoDocumento);
			setTamanho(tamanho);
			setNumeroDocumento(numeroDocumento);
		}
		public String getDescricao() {
			return descricao;
		}
		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}
		public String getClassificacaoDocumento() {
			return classificacaoDocumento;
		}
		public void setClassificacaoDocumento(String classificacaoDocumento) {
			this.classificacaoDocumento = classificacaoDocumento;
		}
		public String getTamanho() {
			return tamanho;
		}
		public void setTamanho(String tamanho) {
			this.tamanho = tamanho;
		}
		public String getNumeroDocumento() {
			return numeroDocumento;
		}
		public void setNumeroDocumento(String numeroDocumento) {
			this.numeroDocumento = numeroDocumento;
		}
	}
}
