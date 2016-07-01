package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.validation.ValidationException;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.exception.EppSystemException;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.assinador.AssinadorGroupService.DadosAssinatura;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.assinavel.AssinavelGenericoProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.RespostaComunicacaoService;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploader;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.error.DocumentoErrorCode;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Named(PedirProrrogacaoPrazoAction.NAME)
@ViewScoped
@Stateful
public class PedirProrrogacaoPrazoAction implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(PedirProrrogacaoPrazoAction.class);
	public static final String NAME = "pedirProrrogacaoPrazoAction";

	@Inject
	private ComunicacaoAction comunicacaoAction;
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
	@Inject
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@Inject
	private ClassificacaoDocumentoPapelManager classificacaoDocumentoPapelManager;
	@Inject
	private AssinadorService assinadorService;
	@Inject
	private RespostaComunicacaoService respostaComunicacaoService;
	
	protected List<ClassificacaoDocumento> classificacoesDocumentoProrrogacaoPrazo;
	private DestinatarioBean destinatario;
	private boolean prorrogacaoPrazo;
	private ClassificacaoDocumento classificacaoDocumentoProrrogPrazo;
	private boolean enviaSemAssinarPedidoProrrogacao;
	private boolean assinaPedidoProrrogacao;
	private String tokenAssinaturaDocumentoPedidoProrrogacao;
	
	public boolean podePedirProrrogacaoPrazo(DestinatarioBean bean) {
		DestinatarioModeloComunicacao destinatarioModeloComunicacao = getDestinatarioModeloComunicacao(bean);
		Date dataLimite = prazoComunicacaoService.getDataLimiteCumprimento(destinatarioModeloComunicacao.getProcesso());
			return prazoComunicacaoService.canRequestProrrogacaoPrazo(destinatarioModeloComunicacao) && 
					dataLimite != null && dataLimite.after(new Date());
	}
	
	public void pedirProrrogacaoPrazo() {
		try {
			Processo comunicacao = getDestinatarioModeloComunicacao(destinatario).getProcesso();
			respostaComunicacaoService.enviarProrrogacaoPrazo(createDocumentoPedidoProrrogacao(), comunicacao);
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

	private Documento createDocumentoPedidoProrrogacao() {
		Documento documento = documentoUploader.getDocumento();
		documento.setDescricao(documentoUploader.getClassificacaoDocumento().getDescricao());
		return documento;
	}
	
	public void assinarPedirProrrogacaoPrazo(){
		try {
			assinadorService.validarToken(tokenAssinaturaDocumentoPedidoProrrogacao);
			List<DadosAssinatura> dadosAssinaturaList = assinadorService.getDadosAssinatura(tokenAssinaturaDocumentoPedidoProrrogacao);
			validaDocumentoAssinatura(dadosAssinaturaList);
			Processo comunicacao = getDestinatarioModeloComunicacao(destinatario).getProcesso();
			respostaComunicacaoService.assinarEnviarProrrogacaoPrazo(createDocumentoPedidoProrrogacao(), comunicacao, dadosAssinaturaList, Authenticator.getUsuarioPerfilAtual());
			clear();
			FacesMessages.instance().add(infoxMessages.get("comunicacao.msg.sucesso.pedidoProrrogacao"));
		} catch (CertificadoException | AssinaturaException | ValidationException e) {
			FacesMessages.instance().add(Severity.ERROR, e.getMessage());
		}
		catch (EppSystemException e) {
			FacesMessages.instance().add(Severity.ERROR, e.getMessage());
		} catch (Exception e) {
	        LOG.error("Erro ao assinar documentode de Pedido de Prorrogação de Prazo.", e);
	        FacesMessages.instance().add(Severity.ERROR, "Erro ao assinar documentode de Pedido de Prorrogação de Prazo, favor tente novamente.");
		}
	}
	
	private void validaDocumentoAssinatura(List<DadosAssinatura> dadosAssinaturaList) throws CertificadoException {
		DocumentoBin bin = documentoUploader.getDocumento().getDocumentoBin();
		if(!assinadorService.validarDadosAssinadosByData(dadosAssinaturaList, Arrays.asList(bin.getProcessoDocumento()))) {
			throw new CertificadoException("Documento recebido difere do documento enviado para assinatura.");
		}
		if (!documentoUploader.isValido()) {
			throw new EppSystemException(DocumentoErrorCode.INVALID_DOCUMENT_TYPE);
		}		
	}

	private void validaClassificacao(){
		if (getClassificacaoDocumentoProrrogPrazo() != null) {
			enviaSemAssinarPedidoProrrogacao = !assinaturaDocumentoService.precisaAssinatura(getClassificacaoDocumentoProrrogPrazo());
			assinaPedidoProrrogacao = classificacaoDocumentoPapelManager.papelPodeTornarSuficientementeAssinado(Authenticator.getPapelAtual(), getClassificacaoDocumentoProrrogPrazo());
			if (!enviaSemAssinarPedidoProrrogacao && !assinaPedidoProrrogacao) {
				FacesMessages.instance().add("O papel atual não consegue completar as assinaturas dessa classificação de documento.");
			}
		}
	}
	
	public void clear(){
		comunicacaoAction.clear();
		destinatario = null;
		prorrogacaoPrazo = false;
		setClassificacaoDocumentoProrrogPrazo(null);
		documentoUploader.clear();
	}
	
	protected DestinatarioModeloComunicacao getDestinatarioModeloComunicacao(DestinatarioBean bean) {
		return entityManager.find(DestinatarioModeloComunicacao.class, bean.getIdDestinatario());
	}
	
	public void setDestinatarioProrrogacaoPrazo(DestinatarioBean destinatario) {
		clear();
		this.destinatario = destinatario;
		prorrogacaoPrazo = true;
		documentoUploader.setClassificacaoDocumento(null);
		classificacoesDocumentoProrrogacaoPrazo = null;
	}
	
	public boolean isProrrogacaoPrazo() {
		return prorrogacaoPrazo;
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

	public boolean isEnviaSemAssinarPedidoProrrogacao() {
		return enviaSemAssinarPedidoProrrogacao;
	}

	public boolean isAssinaPedidoProrrogacao() {
		return assinaPedidoProrrogacao;
	}

	public void setClassificacaoDocumentoProrrogPrazo(ClassificacaoDocumento classificacaoDocumentoProrrogPrazo) {
		this.classificacaoDocumentoProrrogPrazo = classificacaoDocumentoProrrogPrazo;
		if (documentoUploader.getDocumento() != null) {
			documentoUploader.setClassificacaoDocumento(classificacaoDocumentoProrrogPrazo);
		}
		validaClassificacao();
	}

	public DestinatarioBean getDestinatario() {
		return destinatario;
	}

	public String getTokenAssinaturaDocumentoPedidoProrrogacao() {
		return tokenAssinaturaDocumentoPedidoProrrogacao;
	}

	public void setTokenAssinaturaDocumentoPedidoProrrogacao(String tokenAssinaturaDocumentoPedidoProrrogacao) {
		this.tokenAssinaturaDocumentoPedidoProrrogacao = tokenAssinaturaDocumentoPedidoProrrogacao;
	}

	public AssinavelProvider getAssinavelProvider() {
		return new AssinavelGenericoProvider(documentoUploader.getDocumento().getDocumentoBin().getProcessoDocumento());
	}
	
}
