package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.List;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.component.UICollapsiblePanel;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
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
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(ExpedicaoComunicacaoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ExpedicaoComunicacaoAction implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "expedicaoComunicacaoAction";
	private static final String PAINEL_COMUNICACAO_ID = ":comunicacaoTabPanel:comunicacaoForm:painelComunicacao";
	private static final LogProvider LOG = Logging.getLogProvider(ExpedicaoComunicacaoAction.class);
	
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@In
	private DestinatarioModeloComunicacaoList destinatarioModeloComunicacaoList;
	@In
	private ModeloDocumentoManager modeloDocumentoManager;
	@In
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@In
	private ComunicacaoService comunicacaoService;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private TipoComunicacaoManager tipoComunicacaoManager;
	
	private String tab = "list";
	private ModeloComunicacao modeloComunicacao;
	private DestinatarioModeloComunicacao destinatario;
	private String comunicacao;
	private String certChain;
	private String signature;
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
		this.comunicacao = null;
		UICollapsiblePanel panel = (UICollapsiblePanel) FacesContext.getCurrentInstance().getViewRoot().findComponent(PAINEL_COMUNICACAO_ID);
		if (destinatario != null) {
			panel.setExpanded(true);
		} else {
			panel.setExpanded(false);
		}
	}
	
	public String getComunicacao() {
		if (comunicacao == null) {
			String modeloDocumento = modeloComunicacao.getTextoComunicacao();
			if (modeloDocumento != null) {
				if (destinatario == null) {
					comunicacao = modeloDocumento;
				} else {
					comunicacao = comunicacaoService.evaluateComunicacao(destinatario);
				}
			}
		}
		return comunicacao;
	}
	
	public void setComunicacao(String comunicacao) {
		this.comunicacao = comunicacao;
	}
	
	public String getMd5Comunicacao() {
		return getDocumentoComunicacao().getMd5Documento();
	}
	
	public String getCertChain() {
		return certChain;
	}
	
	public void setCertChain(String certChain) {
		this.certChain = certChain;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public void setSignature(String signature) {
		this.signature = signature;
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
		boolean expedida = (getComunicacao() == null && isExpedida(modeloComunicacao)) || 
			(getComunicacao() != null && destinatario != null && destinatario.getExpedido());
		return !expedida && 
				assinaturaDocumentoService.podeRenderizarApplet(papel, modeloComunicacao.getClassificacaoComunicacao(), 
						getDocumentoComunicacao(), usuario);
	}
	
	public void expedirComunicacao() {
		try {
			DocumentoBin documentoComunicacao = getDocumentoComunicacao();
			if (documentoComunicacao.getAssinaturas().isEmpty()) {
				assinaturaDocumentoService.assinarDocumento(getDocumentoComunicacao(), Authenticator.getUsuarioPerfilAtual(), certChain, signature);
			}
			if (getComunicacao() != null) {
				comunicacaoService.expedirComunicacao(destinatario);
			} else {
				comunicacaoService.expedirComunicacao(modeloComunicacao);
			}
		} catch (DAOException | CertificadoException | AssinaturaException e) {
			handleException(e);
		}
	}
	
	public void downloadComunicacao(DestinatarioModeloComunicacao destinatario) {
		try {
			byte[] pdf = comunicacaoService.gerarPdfCompleto(modeloComunicacao, destinatario);
			FileDownloader.download(pdf, "application/pdf", "Comunicação.pdf");
		} catch (DAOException e) {
			LOG.error("", e);
		}
	}
	
	public boolean isExpedida(ModeloComunicacao modeloComunicacao) {
		return modeloComunicacaoManager.isExpedida(modeloComunicacao);
	}
	
	public boolean isDocumentoComunicacaoAssinado() {
		return getComunicacao() == null && !getDocumentoComunicacao().getAssinaturas().isEmpty() && !isExpedida(modeloComunicacao);
	}
	
	private DocumentoBin getDocumentoComunicacao() {
		return getComunicacao() != null ? this.destinatario.getComunicacao() : modeloComunicacao.getDestinatarios().get(0).getComunicacao();
	}
	
	private void handleException(Exception e) {
		String mensagem = "Erro ao expedir comunicação " + modeloComunicacao.getId();
		if (destinatario != null) {
			mensagem += " para o destinatário " + destinatario.getId();
		}
		LOG.error(mensagem, e);
		
		if (e instanceof DAOException) {
			actionMessagesService.handleDAOException((DAOException) e);
		} else if (e instanceof CertificadoException) {
			actionMessagesService.handleException("Erro ao expedir comunicação", e);
		} else if (e instanceof AssinaturaException) {
			FacesMessages.instance().add(e.getMessage());
		}
	}
}
