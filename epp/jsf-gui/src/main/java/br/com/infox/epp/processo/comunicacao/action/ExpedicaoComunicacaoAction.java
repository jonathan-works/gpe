package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.documento.type.TipoMeioAssinaturaEnum;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
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
	private static final LogProvider LOG = Logging.getLogProvider(ExpedicaoComunicacaoAction.class);

	@Inject
	private ClassificacaoDocumentoPapelManager classificacaoDocumentoPapelManager;
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
	private Map<Long, List<Long>> documentosEnviadosAssinador;
	private List<DestinatarioModeloComunicacao> documentosPodeAssinar;
	private List<DestinatarioModeloComunicacao> documentosNaoPodeAssinar;


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


            if (modeloComunicacao.isModeloTotalmenteExpedido()) {
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

    public void verificaDocumentosAssinarLote() {
        documentosEnviadosAssinador = null;
        documentosPodeAssinar = null;
        documentosNaoPodeAssinar = null;

        UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
        UsuarioLogin usuario = usuarioPerfil.getUsuarioLogin();
        Papel papel = usuarioPerfil.getPerfilTemplate().getPapel();

        documentosEnviadosAssinador = new TreeMap<Long, List<Long>>();

        for(ModeloComunicacao modelo : getSelecionados()){
            for(DestinatarioModeloComunicacao destinatario : modelo.getDestinatarios()){

                DocumentoBin documentoBin = destinatario.getDocumentoComunicacao().getDocumentoBin();

                boolean podeAssinar =
                    !assinaturaDocumentoService.isDocumentoTotalmenteAssinado(destinatario.getDocumentoComunicacao()) &&
                    assinaturaDocumentoService.podeRenderizarApplet(papel, modelo.getClassificacaoComunicacao(), documentoBin, usuario);

                if(podeAssinar){
                    getDocumentosPodeAssinar().add(destinatario);

                    if(!documentosEnviadosAssinador.containsKey(modelo.getId())){ //guarda a referencia do que mandou para verificar no onSuccess
                        List<Long> list = new ArrayList<Long>();
                        list.add(destinatario.getId());
                        documentosEnviadosAssinador.put(modelo.getId(), list);
                    }else{
                        List<Long> list = documentosEnviadosAssinador.get(modelo.getId());
                        list.add(destinatario.getId());
                    }

                }else{
                    getDocumentosNaoPodeAssinar().add(destinatario);
                }
            }
        }
    }

    public AssinavelProvider getAssinavelProvider() {
        List<AssinavelDocumentoBinProvider.DocumentoComRegraAssinatura> datalist = new ArrayList<>();
        for (DestinatarioModeloComunicacao destinatario : documentosPodeAssinar) {
            TipoMeioAssinaturaEnum tipoMeioAssinatura = classificacaoDocumentoPapelManager.getTipoMeioAssinaturaUsuarioLogadoByClassificacaoDocumento(
                destinatario.getDocumentoComunicacao().getClassificacaoDocumento()
            );
            datalist.add(new AssinavelDocumentoBinProvider.DocumentoComRegraAssinatura(
                tipoMeioAssinatura,
                destinatario.getDocumentoComunicacao().getDocumentoBin()
            ));
        }


        return new AssinavelDocumentoBinProvider(datalist);
    }

    public void onSuccess(List<DadosAssinatura> dadosAssinatura) {
        int qtdAssinados = 0;
        int qtdErros = 0;
        try {
            for (ModeloComunicacao modelo : getSelecionados()) {
                try {
                    comunicacaoService.assinarExpedirComunicacao(dadosAssinatura, modelo, documentosEnviadosAssinador);
                    qtdAssinados++;
                } catch (Exception ex) {
                    LOG.error("Ocorreu um erro ao assinar o modelo " + modelo.getId() + ". Mensagem de erro: " + ex.getMessage(), ex);
                    qtdErros++;
                }
            }
            MessageFormat format = new MessageFormat(InfoxMessages.getInstance().get("comunicacao.msg.assinarQuantitativa"));
            Object[] objs = { qtdAssinados, qtdErros };
            FacesMessages.instance().add(format.format(objs));
            setSelecionados(null);
            lazyData.clear();
        } catch (Exception e) {
            FacesMessages.instance().add(Severity.ERROR, InfoxMessages.getInstance().get("anexarDocumentos.erroAssinarDocumentos"));
        }
    }

    public void onFail(StatusToken statusToken, List<DadosAssinatura> dadosAssinatura) {
        FacesMessages.instance().add(Severity.ERROR, InfoxMessages.getInstance().get("anexarDocumentos.erroAssinarDocumentos"));
    }

    public ConsultaComunicacaoLazyData getLazyData() {
        return lazyData;
    }

    public void limpaSelecionados() {
        selecionados = null;
        documentosEnviadosAssinador = null;
        documentosPodeAssinar = null;
        documentosNaoPodeAssinar = null;
    }

    public List<DestinatarioModeloComunicacao> getDocumentosNaoPodeAssinar() {
        if(documentosNaoPodeAssinar == null)
            documentosNaoPodeAssinar = new ArrayList<DestinatarioModeloComunicacao>();
        return documentosNaoPodeAssinar;
    }

    public void setDocumentosNaoPodeAssinar(List<DestinatarioModeloComunicacao> documentosNaoPodeAssinar) {
        this.documentosNaoPodeAssinar = documentosNaoPodeAssinar;
    }

    public List<DestinatarioModeloComunicacao> getDocumentosPodeAssinar() {
        if(documentosPodeAssinar == null)
            documentosPodeAssinar = new ArrayList<DestinatarioModeloComunicacao>();
        return documentosPodeAssinar;
    }

    public void setDocumentosPodeAssinar(List<DestinatarioModeloComunicacao> documentosPodeAssinar) {
        this.documentosPodeAssinar = documentosPodeAssinar;
    }
}
