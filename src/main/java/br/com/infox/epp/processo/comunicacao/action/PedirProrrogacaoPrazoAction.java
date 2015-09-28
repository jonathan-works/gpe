package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.RespostaComunicacaoService;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploader;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.util.ComponentUtil;

@Named(PedirProrrogacaoPrazoAction.NAME)
@ViewScoped
@Stateful
public class PedirProrrogacaoPrazoAction implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(PedirProrrogacaoPrazoAction.class);
	public static final String NAME = "pedirProrrogacaoPrazoAction";

	@Inject
	private DocumentoUploader documentoUploader;
	@Inject
	private PrazoComunicacaoService prazoComunicacaoService;
	@Inject
	private ActionMessagesService actionMessagesService;
	@Inject
	protected InfoxMessages infoxMessages;
	@Inject
	private EntityManager entityManager;
	
	private RespostaComunicacaoService respostaComunicacaoService = ComponentUtil.getComponent(RespostaComunicacaoService.NAME);
	
	private List<ClassificacaoDocumento> classificacoesDocumentoProrrogacaoPrazo;
	private DestinatarioBean destinatario;
	private ClassificacaoDocumento classificacaoDocumentoProrrogPrazo;
	private boolean prorrogacaoPrazo;
	
	
	public boolean podePedirProrrogacaoPrazo(DestinatarioBean bean) {
		DestinatarioModeloComunicacao destinatarioModeloComunicacao = getDestinatarioModeloComunicacao(bean);
	    return prazoComunicacaoService.canRequestProrrogacaoPrazo(destinatarioModeloComunicacao) && 
	                prazoComunicacaoService.getDataLimiteCumprimento(destinatarioModeloComunicacao.getProcesso()).after(new Date());
	}
	
	public void pedirProrrogacaoPrazo() {
		try {
			Processo comunicacao = getDestinatarioModeloComunicacao(destinatario).getProcesso();
			Documento documento = documentoUploader.getDocumento();
			documento.setDescricao(documentoUploader.getClassificacaoDocumento().getDescricao());
			respostaComunicacaoService.enviarProrrogacaoPrazo(documento, comunicacao);
			documentoUploader.clear();
			clear();
			FacesMessages.instance().add(infoxMessages.get("comunicacao.msg.sucesso.pedidoProrrogacao"));
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		} catch (BusinessException e) {
			LOG.error("", e);
			FacesMessages.instance().add(e.getMessage());
		}
	}
	
	public void clear(){
		destinatario = null;
		prorrogacaoPrazo = false;
		setClassificacaoDocumentoProrrogPrazo(null);
		documentoUploader.clear();
	}
	
	protected DestinatarioModeloComunicacao getDestinatarioModeloComunicacao(DestinatarioBean bean) {
		return entityManager.find(DestinatarioModeloComunicacao.class, bean.getIdDestinatario());
	}
	
	
	
	
	public boolean isProrrogacaoPrazo() {
		return prorrogacaoPrazo;
	}
	
	public void setDestinatarioProrrogacaoPrazo(DestinatarioBean destinatario) {
		clear();
		this.destinatario = destinatario;
		prorrogacaoPrazo = true;
		documentoUploader.setClassificacaoDocumento(null);
		classificacoesDocumentoProrrogacaoPrazo = null;
	}
	
	
	public List<ClassificacaoDocumento> getClassificacoesDocumentoProrrogacaoPrazo() {
		if (classificacoesDocumentoProrrogacaoPrazo == null) {
			if (isProrrogacaoPrazo()) {
				classificacoesDocumentoProrrogacaoPrazo = new ArrayList<>();
				classificacoesDocumentoProrrogacaoPrazo.add(prazoComunicacaoService.getClassificacaoProrrogacaoPrazo(getDestinatarioModeloComunicacao(destinatario)));
			}
		}
		return classificacoesDocumentoProrrogacaoPrazo;
	}
	
	public ClassificacaoDocumento getClassificacaoDocumentoProrrogPrazo() {
		return classificacaoDocumentoProrrogPrazo;
	}

	public void setClassificacaoDocumentoProrrogPrazo(ClassificacaoDocumento classificacaoDocumentoProrrogPrazo) {
		this.classificacaoDocumentoProrrogPrazo = classificacaoDocumentoProrrogPrazo;
		documentoUploader.setClassificacaoDocumento(classificacaoDocumentoProrrogPrazo);
	}

	public DestinatarioBean getDestinatario() {
		return destinatario;
	}
}
