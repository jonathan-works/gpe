package br.com.infox.epp.processo.documento.action;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoComunicacaoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.service.ProcessoAnaliseDocumentoService;
import br.com.infox.epp.processo.entity.Processo;

@Named
@ViewScoped
public class AnaliseDocumentoAction implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@Inject 
	private DocumentoComunicacaoList documentoComunicacaoList;
	@Inject
	private InfoxMessages infoxMessages;
	@Inject
	private ProcessoAnaliseDocumentoService processoAnaliseDocumentoService;
	
	private List<Documento> documentosAnalise;
	private Processo processo;
	private DestinatarioModeloComunicacao destinatarioComunicacao;
	private Processo comunicacao;
	private List<Documento> documentosComunicacao;
	
	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo){
		this.processo = processo;
		if(isRespostaComunicacao() || isPedidoProrrogacaoPrazo()){
			comunicacao = processo.getProcessoPai();
			setDestinatarioComunicacao(comunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO).<DestinatarioModeloComunicacao>getValue());
			documentoComunicacaoList.setModeloComunicacao(getDestinatarioComunicacao().getModeloComunicacao());
		}
	}
	
	public List<Documento> getDocumentosAnalise(){
		if (documentosAnalise == null){
			documentosAnalise = processoAnaliseDocumentoService.getDocumentosAnalise(getProcesso());
		}
		return documentosAnalise;
	}

	public boolean isRespostaComunicacao(){
		if(getProcesso() != null){
			return processoAnaliseDocumentoService.isRespostaComunicacao(getProcesso())
					&& !isPedidoProrrogacaoPrazo();
		}
		return false;
	}
	
	public boolean isPedidoProrrogacaoPrazo(){
		if(getProcesso() != null){
			return processoAnaliseDocumentoService.isPedidoProrrogacaoPrazo(getProcesso());
		}
		return false;
	}
	
	public String getTituloGridDocumentosAnalise(){
		if (isRespostaComunicacao()){
			return infoxMessages.get("comunicacao.analisarRespostaComunicacao");
		}else if (isPedidoProrrogacaoPrazo()){
			return infoxMessages.get("comunicacao.analisarProrrogacaoPrazo");
		}
		return infoxMessages.get("comunicacao.analisarDocumento");
	}
	
	public String getTituloDocumentosAnaliseList(){
		if (isRespostaComunicacao()){
			return infoxMessages.get("comunicacao.documentosResposta");
		}
		return infoxMessages.get("comunicacao.documentosAnalise");
	}
	
	public String getDestinatario(){
		if(destinatarioComunicacao.getNome() != null){
			return destinatarioComunicacao.getNome();
		}
		return "";
		
	}
	
	public Long getDestinatarioId(){
		return destinatarioComunicacao.getId();
	}
	
	public Date getDataEnvio(){
		return comunicacao.getDataInicio();
	}
	
	public Date getDataResposta(){
		return processo.getDataInicio();
	}
	
	public String getTipoComunicacao(){
		return destinatarioComunicacao.getModeloComunicacao().getTipoComunicacao().getDescricao();
	}
	
	public List<Documento> getDocumentosComunicacao(){
		if(documentosComunicacao == null){
			documentosComunicacao = modeloComunicacaoManager.getDocumentosByModeloComunicacao(destinatarioComunicacao.getModeloComunicacao());
		}
		return documentosComunicacao;
	}

	public DestinatarioModeloComunicacao getDestinatarioComunicacao() {
		return destinatarioComunicacao;
	}

	public void setDestinatarioComunicacao(DestinatarioModeloComunicacao destinatarioComunicacao) {
		this.destinatarioComunicacao = destinatarioComunicacao;
	}

}
