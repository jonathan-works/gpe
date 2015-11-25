package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.certificado.CertificateSignatures;
import br.com.infox.certificado.bean.CertificateSignatureBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleStatus;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DestinatarioModeloComunicacaoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacaoManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.transaction.TransactionService;

@Name(ExpedicaoComunicacaoAction.NAME)
@Scope(ScopeType.CONVERSATION)
@Transactional
@ContextDependency
public class ExpedicaoComunicacaoAction implements Serializable {
	
	private static final String TAB_SEARCH = "list";
	private static final long serialVersionUID = 1L;
	public static final String NAME = "expedicaoComunicacaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(ExpedicaoComunicacaoAction.class);
	
	@Inject
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@In
	private DestinatarioModeloComunicacaoList destinatarioModeloComunicacaoList;
	@In
	private ModeloDocumentoManager modeloDocumentoManager;
	@In
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@In
	private ComunicacaoService comunicacaoService;
	@Inject
	private ActionMessagesService actionMessagesService;
	@In
	private TipoComunicacaoManager tipoComunicacaoManager;
	@In
	private CertificateSignatures certificateSignatures;
	
	private String tab = TAB_SEARCH;
	private ModeloComunicacao modeloComunicacao;
	private DestinatarioModeloComunicacao destinatario;
	private String token;
	private List<TipoComunicacao> tiposComunicacao;
	
	public String getTab() {
		return tab;
	}
	
	public void setTab(String tab) {
		this.tab = tab;
	}
	
	public ModeloComunicacao getModeloComunicacao() {
		return modeloComunicacao;
	}
	
	public void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		this.modeloComunicacao = modeloComunicacao;
		destinatarioModeloComunicacaoList.setModeloComunicacao(modeloComunicacao);
		setDestinatario(null);
	}
	
	public void setId(Long id) {
		if (id == null) {
			setModeloComunicacao(null);
		} else if (modeloComunicacao == null || !modeloComunicacao.getId().equals(id)) {
			setModeloComunicacao(modeloComunicacaoManager.find(id));
		}
	}
	
	public Long getId() {
		return modeloComunicacao == null ? null : modeloComunicacao.getId();
	}
	
	public DestinatarioModeloComunicacao getDestinatario() {
		return destinatario;
	}
	
	public void setDestinatario(DestinatarioModeloComunicacao destinatario) {
		this.destinatario = destinatario;
	}
	
	public String getMd5Comunicacao() {
		return getDocumentoComunicacao().getDocumentoBin().getMd5Documento();
	}
	
	public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<TipoComunicacao> getTiposComunicacao() {
		if (tiposComunicacao == null) {
			tiposComunicacao = tipoComunicacaoManager.listTiposComunicacaoAtivos();
		}
		return tiposComunicacao;
	}
	
	public void setTiposComunicacao(List<TipoComunicacao> tiposComunicacao) {
		this.tiposComunicacao = tiposComunicacao;
	}
	
	public boolean podeRenderizarApplet() {
		UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
		UsuarioLogin usuario = usuarioPerfil.getUsuarioLogin();
		Papel papel = usuarioPerfil.getPerfilTemplate().getPapel();
		boolean expedicaoValida = !modeloComunicacao.isDocumentoBinario() && destinatario != null && !destinatario.getExpedido()
				&& !assinaturaDocumentoService.isDocumentoTotalmenteAssinado(destinatario.getDocumentoComunicacao());
		return expedicaoValida && 
				assinaturaDocumentoService.podeRenderizarApplet(papel, modeloComunicacao.getClassificacaoComunicacao(), 
						getDocumentoComunicacao().getDocumentoBin(), usuario);
	}
	
	public void expedirComunicacao() {
		try {
			if (modeloComunicacao.isDocumentoBinario()) {
				comunicacaoService.expedirComunicacao(modeloComunicacao);
				return;
			}
			Documento documentoComunicacao = getDocumentoComunicacao();
			if (!isComunicacaoSuficientementeAssinada()) {
				CertificateSignatureBundleBean certificateSignatureBundleBean = certificateSignatures.get(token);
				if (certificateSignatureBundleBean.getStatus() != CertificateSignatureBundleStatus.SUCCESS) {
				    throw new DAOException(InfoxMessages.getInstance().get("comunicacao.assinar.erro"));
				}
				CertificateSignatureBean signatureBean = certificateSignatureBundleBean.getSignatureBeanList().get(0);
				if (assinaturaDocumentoService.podeRenderizarApplet(Authenticator.getPapelAtual(), documentoComunicacao.getClassificacaoDocumento(), 
						documentoComunicacao.getDocumentoBin(), Authenticator.getUsuarioLogado())) {
					assinaturaDocumentoService.assinarDocumento(getDocumentoComunicacao(), Authenticator.getUsuarioPerfilAtual(), signatureBean.getCertChain(), signatureBean.getSignature());
				}
			}
			if (isComunicacaoSuficientementeAssinada()) {
				comunicacaoService.expedirComunicacao(destinatario);
				FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.sucesso.expedicao"));
			} else {
				FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.sucesso.assinatura"));
			}
		} catch (DAOException | CertificadoException | AssinaturaException e) {
			TransactionService.rollbackTransaction();
			handleException(e);
		}
	}
	
	public void reabrirComunicacao() {
		try {
			comunicacaoService.reabrirComunicacao(getModeloComunicacao());
			setTab(TAB_SEARCH);
			clear();
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.sucesso.reabertura"));
		} catch (DAOException | CloneNotSupportedException e) {
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.erro.reabertura"));
			LOG.error(e);
		}
	}

	private void clear() {
		destinatario = null;
	}
	
	public boolean isExpedida(ModeloComunicacao modeloComunicacao) {
		return modeloComunicacaoManager.isExpedida(modeloComunicacao);
	}
	
	public boolean isComunicacaoSuficientementeAssinada() {
		if (destinatario != null) {
			return assinaturaDocumentoService.isDocumentoTotalmenteAssinado(destinatario.getDocumentoComunicacao());
		}
		return false;
	}
	
	private Documento getDocumentoComunicacao() {
		if (destinatario != null) {
			return destinatario.getDocumentoComunicacao();
		} else {
			return modeloComunicacao.getDestinatarios().get(0).getDocumentoComunicacao();
		}
	}
	
	private void handleException(Exception e) {
		String mensagem = InfoxMessages.getInstance().get("comunicacao.msg.erro.expedicao") + modeloComunicacao.getId();
		if (destinatario != null) {
			mensagem += " para o destinatário " + destinatario.getId();
		}
		LOG.error(mensagem, e);
		
		if (e instanceof DAOException) {
			actionMessagesService.handleDAOException((DAOException) e);
		} else if (e instanceof CertificadoException) {
			actionMessagesService.handleException(InfoxMessages.getInstance().get("comunicacao.msg.erro.expedicao"), e);
		} else if (e instanceof AssinaturaException) {
			FacesMessages.instance().add(e.getMessage());
		}
	}
}
