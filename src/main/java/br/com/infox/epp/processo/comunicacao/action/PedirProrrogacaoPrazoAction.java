package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.validation.ValidationException;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import br.com.infox.certificado.CertificateSignatures;
import br.com.infox.certificado.bean.CertificateSignatureBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleStatus;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.exception.EppSystemException;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.RespostaComunicacaoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.error.DocumentoErrorCode;
import br.com.infox.epp.processo.documento.service.DocumentoUploaderService;
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
	private CertificateSignatures certificateSignatures;
	@Inject
	private RespostaComunicacaoService respostaComunicacaoService;
	@Inject
	private DocumentoUploaderService documentoUploaderService;
	
	protected List<ClassificacaoDocumento> classificacoesDocumentoProrrogacaoPrazo;
	private DestinatarioBean destinatario;
	private boolean prorrogacaoPrazo;
	private ClassificacaoDocumento classificacaoDocumentoProrrogPrazo;
	private boolean enviaSemAssinarPedidoProrrogacao;
	private boolean assinaPedidoProrrogacao;
	private String tokenAssinaturaDocumentoPedidoProrrogacao;
	private String signableDocumentoPedidoProrrogacao;
	
	private Documento documento;
	private boolean isValido;
	
	
	
	public boolean podePedirProrrogacaoPrazo(DestinatarioBean bean) {
		DestinatarioModeloComunicacao destinatarioModeloComunicacao = getDestinatarioModeloComunicacao(bean);
		Date dataLimite = prazoComunicacaoService.getDataLimiteCumprimento(destinatarioModeloComunicacao.getProcesso());
			return prazoComunicacaoService.canRequestProrrogacaoPrazo(destinatarioModeloComunicacao) && 
					dataLimite != null && dataLimite.after(new Date());
	}
	
	public void pedirProrrogacaoPrazo() {
		try {
			Processo comunicacao = getDestinatarioModeloComunicacao(destinatario).getProcesso();
			respostaComunicacaoService.enviarProrrogacaoPrazo(documento, comunicacao);
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

	public void processFileUpload(FileUploadEvent fileUploadEvent) {
        final UploadedFile ui = fileUploadEvent.getUploadedFile();
        try {
            documentoUploaderService.validaDocumento(ui, classificacaoDocumentoProrrogPrazo);
            documento.setDocumentoBin(documentoUploaderService.createProcessoDocumentoBin(ui));
            setValido(true);
            FacesMessages.instance().add(infoxMessages.get("processoDocumento.uploadCompleted"));
        } catch (EJBException e) {
            setValido(false);
            actionMessagesService.handleGenericException(e);
            if (! (e.getCause() instanceof ValidationException)) {
                LOG.error("", e);
            }
        }
    }
	
	public void updateSignablePedidoProrrogacao(){
		if (documento != null) {
			String md5 = MD5Encoder.encode(documento.getDocumentoBin().getProcessoDocumento());
			documento.getDocumentoBin().setMd5Documento(md5);
			setSignableDocumentoPedidoProrrogacao(md5);
		}
	}
	
	public void assinarPedirProrrogacaoPrazo(){
		try {
			CertificateSignatureBundleBean bundle = getSignatureBundle(tokenAssinaturaDocumentoPedidoProrrogacao);
			CertificateSignatureBean signatureBean = bundle.getSignatureBeanList().get(0);
			validaDocumentoAssinatura(signatureBean);
			Processo comunicacao = getDestinatarioModeloComunicacao(destinatario).getProcesso();
			respostaComunicacaoService.assinarEnviarProrrogacaoPrazo(documento, comunicacao, signatureBean, Authenticator.getUsuarioPerfilAtual());
			clear();
			FacesMessages.instance().add(infoxMessages.get("comunicacao.msg.sucesso.pedidoProrrogacao"));
		} catch (CertificadoException | AssinaturaException e) {
			FacesMessages.instance().add(Severity.ERROR, e.getMessage());
		}
		catch (EppSystemException e) {
			FacesMessages.instance().add(Severity.ERROR, e.getMessage());
		} catch (Exception e) {
	        LOG.error("Erro ao assinar documentode de Pedido de Prorrogação de Prazo.", e);
	        FacesMessages.instance().add(Severity.ERROR, "Erro ao assinar documentode de Pedido de Prorrogação de Prazo, favor tente novamente.");
		}
	}
	
	private void validaDocumentoAssinatura(CertificateSignatureBean signatureBean) throws CertificadoException {
		DocumentoBin bin = documento.getDocumentoBin();
		if (!bin.getMd5Documento().equals(signatureBean.getDocumentMD5())){
			throw new CertificadoException("Documento recebido difere do documento enviado para assinatura.");
		} 
		if (!isValido()) {
			throw new EppSystemException(DocumentoErrorCode.INVALID_DOCUMENT_TYPE);
		}
		
	}

	private CertificateSignatureBundleBean getSignatureBundle(String token) throws CertificadoException {
	    CertificateSignatureBundleBean bundle = certificateSignatures.get(token);
	    if (bundle == null) {
	        throw new CertificadoException(infoxMessages.get("assinatura.error.hasExpired"));
	    } else if (CertificateSignatureBundleStatus.ERROR.equals(bundle.getStatus()) || CertificateSignatureBundleStatus.UNKNOWN.equals(bundle.getStatus())) {
	        throw new CertificadoException("Erro de certificado " + bundle);
	    }
        return bundle;
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
	
	public void clearDocumento() {
	    documento = new Documento();
        documento.setDocumentoBin(new DocumentoBin());   
        documento.setClassificacaoDocumento(classificacaoDocumentoProrrogPrazo);
	}
	
	public void clear(){
		comunicacaoAction.clear();
		destinatario = null;
		prorrogacaoPrazo = false;
		setClassificacaoDocumentoProrrogPrazo(null);
	}
	
	protected DestinatarioModeloComunicacao getDestinatarioModeloComunicacao(DestinatarioBean bean) {
		return entityManager.find(DestinatarioModeloComunicacao.class, bean.getIdDestinatario());
	}
	
	public void setDestinatarioProrrogacaoPrazo(DestinatarioBean destinatario) {
		clear();
		this.destinatario = destinatario;
		prorrogacaoPrazo = true;
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
		clearDocumento();
		if (classificacaoDocumentoProrrogPrazo != null) {
		    documento.setDescricao(classificacaoDocumentoProrrogPrazo.getDescricao());
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

	public String getSignableDocumentoPedidoProrrogacao() {
		return signableDocumentoPedidoProrrogacao;
	}

	public void setSignableDocumentoPedidoProrrogacao(String signableDocumentoPedidoProrrogacao) {
		this.signableDocumentoPedidoProrrogacao = signableDocumentoPedidoProrrogacao;
	}

    public boolean isValido() {
        return isValido;
    }

    public void setValido(boolean isValido) {
        this.isValido = isValido;
    }

}
