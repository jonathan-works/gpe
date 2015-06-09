package br.com.infox.certificado;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.faces.event.AbortProcessingException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.certificado.bean.CertificateSignatureBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.exception.ValidaDocumentoException;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.AssinaturaDocumentoManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.service.ProcessoAnaliseDocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Scope(ScopeType.CONVERSATION)
@Name(ValidaDocumentoAction.NAME)
@Transactional
public class ValidaDocumentoAction implements Serializable {

	private static final String RECURSO_ANEXAR_DOCUMENTO_SEM_ANALISE = "anexarDocumentoSemAnalise";
	private static final long serialVersionUID = 1L;
	public static final String NAME = "validaDocumentoAction";
	private static final LogProvider LOG = Logging.getLogProvider(ValidaDocumentoAction.class);

	private Documento documento;
	private DocumentoBin documentoBin;
	private Boolean valido;
	private Certificado dadosCertificado;
	private List<AssinaturaDocumento> listAssinaturaDocumento;
	private Integer idDocumento;
	private String externalCallback;
	private String token;

	@In
	public DocumentoManager documentoManager;
	@In
	private DocumentoBinarioManager documentoBinarioManager;
	@In
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@In
	private AssinaturaDocumentoManager assinaturaDocumentoManager;
	@In
	private CertificateSignatures certificateSignatures;
	@In
	private InfoxMessages infoxMessages;
	@In
	private ProcessoAnaliseDocumentoService processoAnaliseDocumentoService;
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	@In
	private ProcessoDAO processoDAO;
	@In
	private ActionMessagesService actionMessagesService;

	private Boolean podeIniciarFluxoAnaliseDocumentos;

	/**
	 * @deprecated
	 * */
	@Deprecated
	public void validaDocumento(Documento documento) {
		this.documento = documento;
		DocumentoBin bin = documento.getDocumentoBin();
		// TODO ASSINATURA
		// validaDocumento(bin, bin.getCertChain(), bin.getSignature());
	}

	/**
	 * Valida a assinatura de um ProcessoDocumento. Quando o documento é do tipo
	 * modelo as quebras de linha são retiradas.
	 * 
	 * @param bin
	 * @param certChain
	 * @param signature
	 */
	public void validaDocumento(DocumentoBin bin, String certChain, String signature) {
		documentoBin = bin;
		setValido(false);
		setDadosCertificado(null);
		try {
			ValidaDocumento validaDocumento = assinaturaDocumentoService.validaDocumento(bin, certChain, signature);
			setValido(validaDocumento.verificaAssinaturaDocumento());
			setDadosCertificado(validaDocumento.getDadosCertificado());
		} catch (ValidaDocumentoException | CertificadoException | IllegalArgumentException e) {
			LOG.error(".validaDocumento(bin, certChain, signature)", e);
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, e.getMessage());
		}
	}

	public boolean podeAssinar() {
		UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
		if (documento == null || !usuarioPerfil.getAtivo())
			return false;

		boolean isAssinavel = documento.isDocumentoAssinavel(usuarioPerfil.getPerfilTemplate().getPapel());
		if (!isAssinavel)
			return false;

		boolean assinadoPor = isAssinadoPor(usuarioPerfil);

		return isAssinavel && !assinadoPor;
	}

	public boolean isAssinadoPor(UsuarioPerfil usuarioPerfil) {
		boolean result = false;
		final List<AssinaturaDocumento> assinaturas = getListAssinaturaDocumento();
		if (assinaturas != null) {
			for (AssinaturaDocumento assinatura : assinaturas) {
				if (result = assinatura.getUsuarioPerfil().equals(usuarioPerfil)) {
					break;
				}
			}
		}
		return result;
	}

	public boolean isAssinadoPor(final UsuarioLogin usuarioLogin) {
		boolean result = false;
		final List<AssinaturaDocumento> assinaturas = getListAssinaturaDocumento();
		if (assinaturas != null) {
			for (final AssinaturaDocumento assinatura : assinaturas) {
				if (result = assinatura.getUsuario().equals(usuarioLogin)) {
					break;
				}
			}
		}
		return result;
	}

	public void assinaDocumento(UsuarioPerfil usuarioPerfil) {
		if (this.documentoBin != null && usuarioPerfil.getAtivo() && !isAssinadoPor(usuarioPerfil)) {
			try {
				CertificateSignatureBundleBean bundle = getSignature();
				for (CertificateSignatureBean certificateSignatureBean : bundle.getSignatureBeanList()) {
					if (certificateSignatureBean.getDocumentMD5().equals(documentoBin.getMd5Documento())) {
						assinaturaDocumentoService.assinarDocumento(documentoBin, usuarioPerfil, certificateSignatureBean.getCertChain(),
								certificateSignatureBean.getSignature());
						setPodeIniciarFluxoAnaliseDocumentos(assinaturaDocumentoService.isDocumentoTotalmenteAssinado(getDocumento()));
						break;
					}
				}
				listAssinaturaDocumento = null;
				setPodeIniciarFluxoAnaliseDocumentos(validaPodeIniciarFluxoAnalise());
			} catch (CertificadoException | AssinaturaException | DAOException e) {
				LOG.error("assinaDocumento(String, String, UsuarioPerfil)", e);
				FacesMessages.instance().add(Severity.ERROR, e.getMessage());
				throw new AbortProcessingException();
			}
		}
	}

	public void validaDocumentoId(Integer idDocumento) {
		try {
			this.documento = assinaturaDocumentoService.validaDocumentoId(idDocumento);
			this.documentoBin = this.documento.getDocumentoBin();
			refresh();
		} catch (IllegalArgumentException e) {
			FacesMessages.instance().add(Severity.ERROR, e.getMessage());
		}
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	public void setValido(Boolean valido) {
		this.valido = valido;
	}

	public Boolean getValido() {
		return valido;
	}

	public List<AssinaturaDocumento> getListAssinaturaDocumento() {
		if (listAssinaturaDocumento == null) {
			refresh();
		}
		return listAssinaturaDocumento;
	}

	private void refresh() {
		listAssinaturaDocumento = assinaturaDocumentoManager.listAssinaturaDocumentoByDocumento(documento);
	}

	public void setDadosCertificado(Certificado dadosCertificado) {
		this.dadosCertificado = dadosCertificado;
	}

	public Certificado getDadosCertificado() {
		return dadosCertificado;
	}

	public DocumentoBin getDocumentoBin() {
		return documentoBin;
	}

	public String getNomeCertificadora() {
		return dadosCertificado == null ? null : dadosCertificado.getAutoridadeCertificadora();
	}

	public String getNome() {
		return dadosCertificado == null ? null : dadosCertificado.getNome();
	}

	public BigInteger getSerialNumber() {
		return dadosCertificado == null ? null : dadosCertificado.getSerialNumber();
	}

	public static ValidaDocumentoAction instance() {
		return ComponentUtil.getComponent(NAME);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	private CertificateSignatureBundleBean getSignature() throws CertificadoException {
		CertificateSignatureBundleBean bundle = certificateSignatures.get(getToken());
		if (bundle == null) {
			throw new CertificadoException(infoxMessages.get("assinatura.error.hashExpired"));
		} else {
			switch (bundle.getStatus()) {
			case ERROR:
			case UNKNOWN:
				throw new CertificadoException(infoxMessages.get("assinatura.error.unknown"));
			default:
				break;
			}
		}
		return bundle;
	}

	public Integer getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(Integer idDocumento) {
		validaDocumentoId(idDocumento);
		this.idDocumento = idDocumento;
		setPodeIniciarFluxoAnaliseDocumentos(validaPodeIniciarFluxoAnalise());
	}

	public String getExternalCallback() {
		return externalCallback;
	}

	public void setExternalCallback(String externalCallback) {
		this.externalCallback = externalCallback;
	}

	public void inicarAnaliseDocumento() {
		Processo processo = getDocumento().getProcesso();
		if (processo != null) {
			try {
				Processo processoAnalise = processoAnaliseDocumentoService.criarProcessoAnaliseDocumentos(processo, getDocumento());
				processoAnaliseDocumentoService.inicializarFluxoDocumento(processoAnalise, null);
				FacesMessages.instance().add("Processo de Análise Iniciado com sucesso");
				setPodeIniciarFluxoAnaliseDocumentos(Boolean.FALSE);
			} catch (DAOException e) {
				LOG.error(e);
				actionMessagesService.handleDAOException(e);
			}

		}
	}

	public Boolean getpodeIniciarFluxoAnaliseDocumentos() {
		return this.podeIniciarFluxoAnaliseDocumentos;
	}

	private Boolean validaPodeIniciarFluxoAnalise() {
		Documento documento = getDocumento();
		boolean podeIniciarAnaliseDoc;
		if (documento == null) {
			podeIniciarAnaliseDoc = false;
		} else {
			podeIniciarAnaliseDoc = !papelInclusaoPossuiRecursoAnexar(documento) && assinaturaDocumentoService.isDocumentoTotalmenteAssinado(documento);
			if (podeIniciarAnaliseDoc){
				podeIniciarAnaliseDoc = !existeProcessoAnaliseByDocumento(documento);
			}			
		}
		setPodeIniciarFluxoAnaliseDocumentos(podeIniciarAnaliseDoc);
		return podeIniciarAnaliseDoc;
	}

	private boolean existeProcessoAnaliseByDocumento(Documento documento) {
		List<Processo> listProcesso = processoDAO.getProcessosFilhosByTipo(documento.getProcesso(),
				TipoProcesso.DOCUMENTO.toString());
		for (Processo processo : listProcesso) {
			MetadadoProcesso metadadoProcesso = processo.getMetadado(EppMetadadoProvider.DOCUMENTO_EM_ANALISE);
			if (metadadoProcesso.getValor().equals(documento.getId().toString())) {
				return true;
			}
		}
		return false;
	}

	private boolean papelInclusaoPossuiRecursoAnexar(Documento documento) {
		Papel papelInclusao = documento.getPerfilTemplate().getPapel();
		if(papelInclusao.getRecursos().contains(RECURSO_ANEXAR_DOCUMENTO_SEM_ANALISE))
			return true;
		
		Queue<Papel> papeisPais = new LinkedList<Papel>(papelInclusao.getGrupos());
		while(papeisPais.peek() != null){
			Papel papelPai = papeisPais.element();
			papeisPais.remove();
			if(papelPai.getRecursos().contains(RECURSO_ANEXAR_DOCUMENTO_SEM_ANALISE))
				return true;
			for(Papel papel : papelPai.getGrupos()){
				papeisPais.add(papel);
			}
		}
		return false;
	}

	public void setPodeIniciarFluxoAnaliseDocumentos(Boolean isDocumentoTotalmenteAssinado) {
		this.podeIniciarFluxoAnaliseDocumentos = isDocumentoTotalmenteAssinado;
	}
}
