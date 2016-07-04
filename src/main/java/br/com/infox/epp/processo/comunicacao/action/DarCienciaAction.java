package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.google.common.base.Strings;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.exception.EppSystemException;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.assinador.AssinadorGroupService.StatusToken;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.DadosAssinatura;
import br.com.infox.epp.assinador.assinavel.AssinavelGenericoProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploader;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.error.DocumentoErrorCode;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.util.ComponentUtil;

@Named(DarCienciaAction.NAME)
@ViewScoped
public class DarCienciaAction implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(DarCienciaAction.class);
	private static final String COMPROVANTE_DE_CIENCIA = "Comprovante de Ciência";
	public static final String NAME = "darCienciaAction";
	
	@Inject
	private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
	@Inject
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@Inject
	private AssinadorService assinadorService;
	@Inject
	private ClassificacaoDocumentoPapelManager classificacaoDocumentoPapelManager = ComponentUtil.getComponent(ClassificacaoDocumentoPapelManager.NAME);
	@Inject
	private PrazoComunicacaoService prazoComunicacaoService;
	@Inject
	private ComunicacaoAction comunicacaoAction;
	@Inject
	private DocumentoUploader documentoUploader;
	@Inject
	protected InfoxMessages infoxMessages;
	@Inject
	protected ActionMessagesService actionMessagesService;
	@Inject
	private EntityManager entityManager;
	
	private AssinavelProvider assinavelProvider;

	private List<ClassificacaoDocumento> classificacoesDocumentoCiencia;
	private DestinatarioBean destinatario;
	private Date dataCiencia;
	private boolean ciencia;
	private String textoCiencia;
	private boolean editorCiencia = false; 
	private ClassificacaoDocumento classificacaoDocumentoCiencia;
	private boolean assinaDocumentoCiencia;
	private boolean enviaSemAssinarDocumentoCiencia;
	private String tokenAssinaturaDocumentoCiencia;
	private String signableDocumentoCiencia;
	
	public void setDestinatarioCiencia(DestinatarioBean destinatario) {
		clear();
		this.setDestinatario(destinatario);
		ciencia = true;
	}
	
	private void validaClassificacaoCiencia() {
		if (getClassificacaoDocumentoCiencia() != null) {
			enviaSemAssinarDocumentoCiencia = !assinaturaDocumentoService.precisaAssinatura(getClassificacaoDocumentoCiencia());
			assinaDocumentoCiencia = classificacaoDocumentoPapelManager.papelPodeTornarSuficientementeAssinado(Authenticator.getPapelAtual(), getClassificacaoDocumentoCiencia()); 
			if (!enviaSemAssinarDocumentoCiencia && !assinaDocumentoCiencia) {
				FacesMessages.instance().add("O papel atual não consegue completar as assinaturas dessa classificação de documento.");
			}
		}
	}

	public void darCiencia() {
		try {
			validarCiencia();
			Documento documento = criarDocumentoCiencia();
			darCiencia(documento);
			finalizaCiencia();
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		} catch (BusinessException e) {
			LOG.error("", e);
			FacesMessages.instance().add(e.getMessage());
		}
	}

	protected void darCiencia(Documento documento) {
		prazoComunicacaoService.darCienciaManual(getDestinatarioModeloComunicacao(getDestinatario()).getProcesso(), getDataCiencia(), documento);
	}

	public void assinarDarCiencia(){
		try {
			validarCiencia();
			List<DadosAssinatura> dadosAssinaturaList = getDadosAssinatura(tokenAssinaturaDocumentoCiencia);
			DadosAssinatura dadosAssinatura = dadosAssinaturaList.get(0);
			validaDocumentoAssinatura(dadosAssinaturaList);
			Documento documentoCiencia = criarDocumentoCiencia();
			darCienciaManualAssinar(dadosAssinatura, documentoCiencia);
			finalizaCiencia();
		} catch (CertificadoException | AssinaturaException e) {
			FacesMessages.instance().add(Severity.ERROR, e.getMessage());
		}catch (EppSystemException e) {
			FacesMessages.instance().add(Severity.ERROR, e.getMessage());
		} catch (Exception e) {
	        LOG.error("Erro ao assinar documentode ciência.", e);
	        FacesMessages.instance().add(Severity.ERROR, "Erro ao assinar documento de ciência, favor tente novamente.");
		}
	}

	protected void darCienciaManualAssinar(DadosAssinatura dadosAssinatura, Documento documentoCiencia)
			throws CertificadoException, AssinaturaException {
		prazoComunicacaoService.darCienciaManualAssinar(getDestinatarioModeloComunicacao(getDestinatario()).getProcesso(), getDataCiencia(), documentoCiencia, 
				dadosAssinatura, Authenticator.getUsuarioPerfilAtual());
	}
	
	protected List<DadosAssinatura> getDadosAssinatura(String token) throws CertificadoException {
		StatusToken status = assinadorService.getStatus(token);
	    if (status == StatusToken.EXPIRADO) {
	        throw new CertificadoException(infoxMessages.get("assinatura.error.hasExpired"));
	    } else if (StatusToken.ERRO.equals(status) || StatusToken.DESCONHECIDO.equals(status)) {
	        throw new CertificadoException("Erro de certificado " + token);
	    }
        return assinadorService.getDadosAssinatura(token);
    }
		    
	protected Documento criarDocumentoCiencia() {
		Documento documento = null;
		if (isEditorCiencia()) {	
			documento = new Documento();
			DocumentoBin bin = new DocumentoBin();
			documento.setDocumentoBin(bin);
			documento.setDescricao(COMPROVANTE_DE_CIENCIA);
			documento.setClassificacaoDocumento(getClassificacaoDocumentoCiencia());
			bin.setModeloDocumento(textoCiencia);
		} else {
			documento = documentoUploader.getDocumento();
			String nomeArquivo = documento.getDocumentoBin().getNomeArquivo();
			documento.setDescricao(nomeArquivo.length() > Documento.TAMANHO_MAX_DESCRICAO_DOCUMENTO ? nomeArquivo.substring(0, Documento.TAMANHO_MAX_DESCRICAO_DOCUMENTO -1) : nomeArquivo);
			documentoUploader.clear();
		}
		return documento;
	}
	
	protected void validaDocumentoAssinatura(List<DadosAssinatura> dadosAssinatura) throws CertificadoException {
		if(!assinadorService.validarDadosAssinados(dadosAssinatura, getAssinavelProvider())) {
			throw new CertificadoException("Documento recebido difere do documento enviado para assinatura.");			
		}
		if (!isEditorCiencia()) {
			if (!documentoUploader.isValido()) {
				throw new EppSystemException(DocumentoErrorCode.INVALID_DOCUMENT_TYPE);
			}
		}
	}

	protected void validarCiencia() {
		StringBuilder msg = new StringBuilder();
		if (getClassificacaoDocumentoCiencia() == null) {
			msg.append(infoxMessages.get("comunicacao.msg.erro.classificacao"));
			msg.append("\n");
		}
		if (getDataCiencia() == null) {
			msg.append(infoxMessages.get("comunicacao.msg.erro.cienciaData"));
			msg.append("\n");
		}
		if (documentoUploader.getDocumento() == null && Strings.isNullOrEmpty(getTextoCiencia())) {
			msg.append(infoxMessages.get("comunicacao.msg.erro.cienciaDocumento"));
		}
		if (msg.length() > 0) {
			FacesMessages.instance().add(msg.toString());
			return;
		}
	}

	protected void finalizaCiencia() {
		comunicacaoAction.getDadosCiencia().put(getDestinatario().getIdDestinatario(), true);
		clear();
		FacesMessages.instance().add(infoxMessages.get("comunicacao.msg.sucesso.ciencia"));
	}
	
	protected DestinatarioModeloComunicacao getDestinatarioModeloComunicacao(DestinatarioBean bean) {
		return entityManager.find(DestinatarioModeloComunicacao.class, bean.getIdDestinatario());
	}
	
	private void clear() {
		comunicacaoAction.clear();
		ciencia = false;
		dataCiencia = null;
		setDestinatario(null);
		documentoUploader.clear();
		setEditorCiencia(false);
		setSignableDocumentoCiencia(null);
		setTokenAssinaturaDocumentoCiencia(null);
	}
	
	public List<ClassificacaoDocumento> getClassificacoesDocumentoCiencia() {
		if (isCiencia() && classificacoesDocumentoCiencia == null) {
			boolean isModelo = isEditorCiencia();
			classificacoesDocumentoCiencia = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(isModelo);
		}
		return classificacoesDocumentoCiencia;
	}
	
	public DestinatarioBean getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(DestinatarioBean destinatario) {
		this.destinatario = destinatario;
	}

	public Date getStartDateCiencia(){
		if(getDestinatario() != null){
			DestinatarioModeloComunicacao destinatarioModeloComunicacao = getDestinatarioModeloComunicacao(getDestinatario());
			return destinatarioModeloComunicacao.getProcesso().getDataInicio();
		}
		return null;
	}
	
	public Date getDataCiencia() {
		return dataCiencia;
	}
	
	public void setDataCiencia(Date dataCiencia) {
		this.dataCiencia = dataCiencia;
	}
	
	public boolean isCiencia() {
		return ciencia;
	}
	
	public ClassificacaoDocumento getClassificacaoDocumentoCiencia() {
		return classificacaoDocumentoCiencia;
	}

	public void setClassificacaoDocumentoCiencia(
			ClassificacaoDocumento classificacaoDocumentoCiencia) {
		this.classificacaoDocumentoCiencia = classificacaoDocumentoCiencia;
		if (!isEditorCiencia()){ 
			documentoUploader.setClassificacaoDocumento(classificacaoDocumentoCiencia);
		}
		validaClassificacaoCiencia();
	}
	
	public String getTextoCiencia() {
		return textoCiencia;
	}

	public void setTextoCiencia(String textoCiencia) {
		this.textoCiencia = textoCiencia;
	}

	public boolean isEditorCiencia() {
		return editorCiencia;
	}

	public void setEditorCiencia(boolean editorCiencia) {
		this.editorCiencia = editorCiencia;
		setClassificacaoDocumentoCiencia(null);
		classificacoesDocumentoCiencia = null;
		if (!isEditorCiencia()){
			setTextoCiencia(null);
		}
	}

	public boolean getEnviaSemAssinarDocumentoCiencia(){
		return enviaSemAssinarDocumentoCiencia;
	}
	
	public boolean getAssinaDocumentoCiencia(){
		return assinaDocumentoCiencia;
	}
	
	public String getTokenAssinaturaDocumentoCiencia() {
		return tokenAssinaturaDocumentoCiencia;
	}

	public void setTokenAssinaturaDocumentoCiencia(String tokenAssinaturaDocumentoCiencia) {
		this.tokenAssinaturaDocumentoCiencia = tokenAssinaturaDocumentoCiencia;
	}

	public String getSignableDocumentoCiencia() {
		return signableDocumentoCiencia;
	}

	public void setSignableDocumentoCiencia(String signableDocumentoCiencia) {
		this.signableDocumentoCiencia = signableDocumentoCiencia;
	}

	public AssinavelProvider getAssinavelProvider() {
		if (signableDocumentoCiencia == null || signableDocumentoCiencia.isEmpty()) {
			if (isEditorCiencia() && getTextoCiencia() != null && !getTextoCiencia().isEmpty()) {
				assinavelProvider = new AssinavelGenericoProvider(getTextoCiencia());
			} else if (documentoUploader.getDocumento() != null) {
				byte[] data = documentoUploader.getDocumento().getDocumentoBin().getProcessoDocumento();
				documentoUploader.getDocumento().getDocumentoBin().setMd5Documento(MD5Encoder.encode(data));
				assinavelProvider = new AssinavelGenericoProvider(data);
			}
		}
		return assinavelProvider;
	}

	public void setAssinavelProvider(AssinavelProvider assinavelProvider) {
		this.assinavelProvider = assinavelProvider;
	}
	
}
