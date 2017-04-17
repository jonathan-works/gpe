package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.DadosAssinatura;
import br.com.infox.epp.assinador.assinavel.AssinavelDocumentoBinProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.assinador.view.AssinaturaCallback;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.ConsultaComunicacaoLazyData;
import br.com.infox.epp.processo.comunicacao.list.DestinatarioModeloComunicacaoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacaoManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.ibpm.sinal.SignalService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.transaction.TransactionService;

@Named
@ViewScoped
public class ExpedicaoComunicacaoAction implements Serializable, AssinaturaCallback {
	
	private static final String TAB_SEARCH = "list";
	private static final long serialVersionUID = 1L;
	public static final String NAME = "expedicaoComunicacaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(ExpedicaoComunicacaoAction.class);
	
	@Inject
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@Inject
	private DestinatarioModeloComunicacaoList destinatarioModeloComunicacaoList;
	@Inject
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@Inject
	private ComunicacaoService comunicacaoService;
	@Inject
	private ActionMessagesService actionMessagesService;
	@Inject
	private TipoComunicacaoManager tipoComunicacaoManager;
	@Inject
	private AssinadorService assinadorService;
	@Inject
	private SignalService signalService;
	@Inject
    private ConsultaComunicacaoLazyData lazyData;
	
	private String tab = TAB_SEARCH;
	private ModeloComunicacao modeloComunicacao;
	private DestinatarioModeloComunicacao destinatario;
	private String token;
	private List<TipoComunicacao> tiposComunicacao;
	private List<ModeloComunicacao> selecionados;
	
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
		destinatarioModeloComunicacaoList.refresh();
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
	
	public DocumentoBin getDocumentoBinComunicacao() {
		return getDocumentoComunicacao().getDocumentoBin();
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
			if (!isComunicacaoSuficientementeAssinada()) {
				try {
					assinadorService.assinarToken(token, Authenticator.getUsuarioPerfilAtual());
				}
				catch(AssinaturaException e) {
				    throw new DAOException(InfoxMessages.getInstance().get("comunicacao.assinar.erro"));
				}
			}
			if (isComunicacaoSuficientementeAssinada()) {
				comunicacaoService.expedirComunicacao(destinatario);
				FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.sucesso.expedicao"));
			} else {
				FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.sucesso.assinatura"));
			}
			
            boolean ultimaComunicacao = true;
            for (DestinatarioModeloComunicacao destino : modeloComunicacao.getDestinatarios()) {
                if (!destino.getExpedido().booleanValue())
                    ultimaComunicacao = false;
            }
            if (ultimaComunicacao) {
                signalService.dispatch(modeloComunicacao.getProcesso().getIdProcesso(), ComunicacaoService.SINAL_COMUNICACAO_EXPEDIDA);
            }
			
		} catch (DAOException e) {
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
			actionMessagesService.handleException(InfoxMessages.getInstance().get("comunicacao.msg.erro.expedicaoCompleta") + e.getMessage(), e);
		} else if (e instanceof AssinaturaException) {
			FacesMessages.instance().add(e.getMessage());
		}
	}

    public List<ModeloComunicacao> getSelecionados() {
        return selecionados;
    }

    public void setSelecionados(List<ModeloComunicacao> selecionados) {
        this.selecionados = selecionados;
    }
    
    public AssinavelProvider getAssinavelProvider(){
        List<DocumentoBin> datalist = new ArrayList<>();
        for(ModeloComunicacao modelo : getSelecionados()){
            for(DocumentoModeloComunicacao documentos : modelo.getDocumentos()){
                datalist.add(documentos.getDocumento().getDocumentoBin());
            }
        }
        return new AssinavelDocumentoBinProvider(datalist);
    }

    public void onSuccess(List<DadosAssinatura> dadosAssinatura) {
        try {
            for (ModeloComunicacao modelo : getSelecionados()) {
                comunicacaoService.expedirComunicacao(modelo);
                signalService.dispatch(modelo.getProcesso().getIdProcesso(), ComunicacaoService.SINAL_COMUNICACAO_EXPEDIDA);
            }
            FacesMessages.instance().add(InfoxMessages.getInstance().get("anexarDocumentos.sucessoAssinatura"));
            setSelecionados(null);
        }catch (Exception e) {
            FacesMessages.instance().add(Severity.ERROR, InfoxMessages.getInstance().get("anexarDocumentos.erroAssinarDocumentos"));
        }
    }
    public void onFail(StatusToken statusToken, List<DadosAssinatura> dadosAssinatura) {
        FacesMessages.instance().add(Severity.ERROR, InfoxMessages.getInstance().get("anexarDocumentos.erroAssinarDocumentos"));
    }

    public ConsultaComunicacaoLazyData getLazyData() {
        return lazyData;
    }
}
