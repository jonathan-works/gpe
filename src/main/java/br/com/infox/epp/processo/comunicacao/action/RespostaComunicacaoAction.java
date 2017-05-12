package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import com.google.common.base.Strings;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.dao.PerfilTemplateDAO;
import br.com.infox.epp.access.dao.UsuarioPerfilDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.dao.ModeloDocumentoDAO;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoRespostaComunicacao;
import br.com.infox.epp.processo.comunicacao.PessoaRespostaComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoComunicacaoList;
import br.com.infox.epp.processo.comunicacao.list.RespostaComunicacaoList;
import br.com.infox.epp.processo.comunicacao.meioexpedicao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.service.DocumentoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.PessoaRespostaComunicacaoSearch;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.RespostaComunicacaoService;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.anexos.DocumentoEditor;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploader;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.variable.components.Taskpage;
import br.com.infox.ibpm.variable.components.TaskpageController;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Taskpage(id="responderComunicacao", xhtmlPath="/WEB-INF/taskpages/responderComunicacao.xhtml", name="Responder Comunicação")
@Named
@Stateful
@LocalBean
@ViewScoped
public class RespostaComunicacaoAction implements Serializable, TaskpageController {
	
	public static final String NAME = "respostaComunicacaoAction";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(RespostaComunicacaoAction.class);
	
	@Inject
	private DocumentoComunicacaoService documentoComunicacaoService;
	@Inject
	private PrazoComunicacaoService prazoComunicacaoService;
	@Inject
	private DocumentoUploader documentoUploader;
	@Inject
	private DocumentoEditor documentoEditor;
	@Inject
	protected InfoxMessages infoxMessages;
	@Inject
	private ActionMessagesService actionMessagesService;
	@Inject
	private RespostaComunicacaoList respostaComunicacaoList;
	@Inject
	private DocumentoComunicacaoList documentoComunicacaoList;
	@Inject
	protected ModeloDocumentoManager modeloDocumentoManager;
	@Inject
	private DocumentoManager documentoManager;
	@Inject
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@Inject
	private RespostaComunicacaoService respostaComunicacaoService;
	@Inject
	private ModeloDocumentoDAO modeloDocumentoDAO;
	@Inject
	private PerfilTemplateDAO perfilTemplateDAO;
	@Inject 
	private UsuarioPerfilDAO usuarioPerfilDAO;
	@Inject
	private PessoaRespostaComunicacaoSearch pessoaRespostaComunicacaoSearch; 
	
	private DestinatarioModeloComunicacao destinatario;

	protected Processo processoComunicacao;
	protected Processo processoRaiz;
	protected Date prazoResposta;
	protected String statusProrrogacao;
	protected PerfilTemplate perfilResponderFilter;
	protected PessoaFisica pessoaResponder;
	
	private List<ClassificacaoDocumento> classificacoesEditor;
	private List<ClassificacaoDocumento> classificacoesAnexo;
	private List<ModeloDocumento> modeloDocumentoList;
	private List<PessoaRespostaComunicacao> pessoaResponderComunicacaoList;
	private List<PerfilTemplate> perfilTemplateList;
	private List<PessoaFisica> pessoaResponderList;
	
	private ModeloDocumento modeloDocumento;
	
	private boolean possivelMostrarBotaoEnvio;
	private boolean possivelLiberarResponder;
	
	
	@PostConstruct
	public void init() {
		Processo processoComunicacao = JbpmUtil.getProcesso();
		if (processoComunicacao != null) {
		    init(processoComunicacao);
		}
	}

    public void init(Processo processoComunicacao) {
        this.processoComunicacao = processoComunicacao;
        respostaComunicacaoList.setProcesso(processoComunicacao);
		prazoResposta = prazoComunicacaoService.getDataLimiteCumprimento(processoComunicacao);
		MetadadoProcesso metadadoDestinatario = processoComunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO);
		if(metadadoDestinatario != null){
			destinatario = metadadoDestinatario.getValue();
			documentoComunicacaoList.setModeloComunicacao(destinatario.getModeloComunicacao());
		}
		
		processoRaiz = processoComunicacao.getProcessoRoot();
		documentoUploader.newInstance();
		documentoUploader.clear();
		documentoUploader.setProcesso(processoRaiz);
		documentoEditor.setProcesso(processoRaiz);
		
		documentoComunicacaoList.setModeloComunicacao(destinatario.getModeloComunicacao());

		possivelLiberarResponder = isDestinatarioComunicacao(destinatario);
		
		if (possivelLiberarResponder) {
		    perfilTemplateList = perfilTemplateDAO.listByEstrutura(Authenticator.getLocalizacaoAtual().getEstruturaFilho().getId());
		    loadPessoasResponderList();
		    loadPessoaResponderComunicacaoList();
		}
		
		newDocumentoEdicao();
        initClassificacoes();
        verificarPossibilidadeEnvioResposta();
    }
    
    public void adicionarPessoaResponder() {
        try {
            respostaComunicacaoService.adicionarPessoaResponderComunicacao(processoComunicacao, pessoaResponder);
            pessoaResponder = null;
            perfilResponderFilter = null;
            loadPessoaResponderComunicacaoList();
            FacesMessages.instance().add(infoxMessages.get("entity_created"));
        } catch (BusinessException | DAOException e) {
            FacesMessages.instance().add(e.getMessage());
        } catch (Exception e1) {
            FacesMessages.instance().add(e1.getMessage());
            LOG.error("", e1);
        }
    }
    
    public void removerPessoaResponder(PessoaRespostaComunicacao pessoaResponderComunicacao) {
        try {
            respostaComunicacaoService.removerPessoaResponderComunicacao(pessoaResponderComunicacao);
            loadPessoaResponderComunicacaoList();
            FacesMessages.instance().add(infoxMessages.get("entity_deleted"));
        } catch (BusinessException | DAOException e) {
            FacesMessages.instance().add(e.getMessage());
            LOG.info(e);
        } catch (Exception e1) {
            FacesMessages.instance().add(e1.getMessage());
            LOG.error("", e1);
        }
    }

	private void loadPessoasResponderList() {
	    Localizacao localizacaoAtual = Authenticator.getLocalizacaoAtual();
	    if ( perfilResponderFilter == null ) {
	        pessoaResponderList = usuarioPerfilDAO.listByLocalizacaoAtivo(localizacaoAtual);
	    } else {
	        pessoaResponderList = usuarioPerfilDAO.getPessoasPermitidos(localizacaoAtual, perfilResponderFilter);
	    }
    }
	
	private void loadPessoaResponderComunicacaoList() {
	    pessoaResponderComunicacaoList = pessoaRespostaComunicacaoSearch.listByComunicacao(processoComunicacao.getIdProcesso());
	}
	
	public void onChangePerfilResponder() {
	    loadPessoasResponderList();
	}

    private boolean isDestinatarioComunicacao(DestinatarioModeloComunicacao destinatarioComunicacao) {
	    PessoaFisica pessoaDestinatario = destinatarioComunicacao.getDestinatario();
	    if ( pessoaDestinatario != null ) {
	        PessoaFisica pessoaFisica = Authenticator.getUsuarioLogado().getPessoaFisica();
	        return pessoaDestinatario.equals(pessoaFisica);
	    } else {
	        Localizacao localizacaoAtual = Authenticator.getLocalizacaoAtual();
	        PerfilTemplate perfilDestino = destinatarioComunicacao.getPerfilDestino();
	        PerfilTemplate perfilAtual = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate();
	        return localizacaoAtual.equals(destinatarioComunicacao.getDestino())
	                   && (perfilDestino == null || perfilDestino.equals(perfilAtual));
	    }
    }

    public Long getIdDestinatario(){
		return destinatario.getId();
	}
	
	public DestinatarioModeloComunicacao getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(DestinatarioModeloComunicacao destinatario) {
		this.destinatario = destinatario;
	}
	
	public void assignModeloDocumento() {
		if (modeloDocumento == null) {
			documentoEditor.getDocumento().getDocumentoBin().setModeloDocumento("");
		} else {
			documentoEditor.getDocumento().getDocumentoBin().setModeloDocumento(modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento));
		}
	}
	
	public boolean isPossivelMostrarBotaoEnvio() {
		return possivelMostrarBotaoEnvio;
	}
	
	public boolean isPossivelLiberarResponder() {
        return possivelLiberarResponder;
    } 
	
	public void gravarResposta() {
		if (Strings.isNullOrEmpty(documentoEditor.getDocumento().getDocumentoBin().getModeloDocumento())) {
			FacesMessages.instance().add("Insira texto no editor");
			return;
		}
		try {
			if (!documentoManager.contains(documentoEditor.getDocumento())) {
				Documento documentoEdicao = getDocumentoEdicao();
				documentoEditor.persist();
				if (documentoEditor.getDocumentosDaSessao().isEmpty()) {
					return;
				}
				documentoComunicacaoService.vincularDocumentoRespostaComunicacao(documentoEdicao, processoComunicacao);
			} else {
				documentoManager.update(documentoEditor.getDocumento());
			}
			newDocumentoEdicao();
			respostaComunicacaoList.refresh();
			JsfUtil.instance().execute("PF('panelPessoaResponderEditor').toggle();");
			FacesMessages.instance().add(infoxMessages.get("comunicacao.resposta.gravadoSucesso"));
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
		verificarPossibilidadeEnvioResposta();
	}
	
	public void newDocumentoEdicao() {
		documentoEditor.clear();
		documentoEditor.newInstance();
		documentoEditor.getDocumento().setPerfilTemplate(Authenticator.getUsuarioPerfilAtual().getPerfilTemplate());
		documentoEditor.getDocumento().setAnexo(false);
		modeloDocumento = null;
		modeloDocumentoList = null;
	}
	
	//TODO ver como colocar esse método no service
	public void gravarAnexoResposta() {
		try {
			documentoUploader.persist();
		} catch (BusinessException e){
			LOG.error("", e);
			FacesMessages.instance().add(e.getMessage());
			return;
		}
		if (documentoUploader.getDocumentosDaSessao().isEmpty()) {
			return;
		}
		Documento resposta = documentoUploader.getDocumentosDaSessao().get(documentoUploader.getDocumentosDaSessao().size() - 1);
		try {
			documentoComunicacaoService.vincularDocumentoRespostaComunicacao(resposta, processoComunicacao);
			respostaComunicacaoList.refresh();
			FacesMessages.instance().add(infoxMessages.get("comunicacao.resposta.gravadoSucesso"));
			JsfUtil.instance().execute("PF('panelPessoaResponderUpload').toggle();");
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		} 
		documentoUploader.clear();
		verificarPossibilidadeEnvioResposta();
	}
	
	//TODO ver como colocar esse método no service
	public void enviarRespostaComunicacao(){
		List<Documento> documentosRespostaComunicacao = getDocumentoRespostaList();
		try {
			if(!documentosRespostaComunicacao.isEmpty()){
			    respostaComunicacaoService.enviarResposta(documentosRespostaComunicacao);
				initClassificacoes();
				FacesMessages.instance().add(infoxMessages.get("comunicacao.resposta.enviadaSucesso"));
				modeloDocumentoList = null;
				newDocumentoEdicao();
				initClassificacoes();
				respostaComunicacaoList.refresh();
			} else {
			    FacesMessages.instance().add(infoxMessages.get("comunicacao.responder.error.semDocumento"));
			}
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
		verificarPossibilidadeEnvioResposta();
	}
	
	//TODO ver como colocar esse método no service
	public void removerDocumento(Documento documento) {
		boolean isDocumentoEdicao = documentoEditor.getDocumento() != null && documentoEditor.getDocumento().equals(documento);
		try {
			documentoComunicacaoService.desvincularDocumentoRespostaComunicacao(documento);
			documentoManager.remove(documento);
			if (isDocumentoEdicao) {
				newDocumentoEdicao();
			}
			respostaComunicacaoList.refresh();
			FacesMessages.instance().add("Documento removido com sucesso");
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
		verificarPossibilidadeEnvioResposta();
	}
	
	public boolean podeRemoverDocumento(Documento documento) {
		return documento.getDocumentoBin().isMinuta() || !assinaturaDocumentoService.isDocumentoTotalmenteAssinado(documento);
	}

	public List<ClassificacaoDocumento> getClassificacoesEditor() {
		return classificacoesEditor;
	}
	
	public List<ClassificacaoDocumento> getClassificacoesAnexo() {
		return classificacoesAnexo;
	}
	
	public List<ModeloDocumento> getModeloDocumentoList() {
		if (modeloDocumentoList == null && getDocumentoEdicao() != null && getDocumentoEdicao().getClassificacaoDocumento() != null) {
			modeloDocumentoList = modeloDocumentoDAO.getModelosDocumentoLitsByClassificacaoEPapel(getDocumentoEdicao().getClassificacaoDocumento(), Authenticator.getPapelAtual());
		}
		return modeloDocumentoList;
	}
	
	public ModeloDocumento getModeloDocumento() {
		return modeloDocumento;
	}
	
	public void setModeloDocumento(ModeloDocumento modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}
	
	public ClassificacaoDocumento getClassificacaoAnexo() {
		return documentoUploader.getClassificacaoDocumento();
	}

	public void setClassificacaoAnexo(ClassificacaoDocumento classificacaoDocumento) {
		documentoUploader.setClassificacaoDocumento(classificacaoDocumento);
	}
	
	public String getDescricaoAnexo() {
		return documentoUploader.getDocumento().getDescricao();
	}
	
	public void setDescricaoAnexo(String descricao) {
		documentoUploader.getDocumento().setDescricao(descricao);
	}
	
	public boolean isAnexoValido() {
		return documentoUploader.isValido();
	}
	
	public Documento getDocumentoEdicao() {
		return documentoEditor.getDocumento();
	}
	
	public void setDocumentoEdicao(Documento documentoEdicao) {
		documentoEditor.setDocumento(documentoEdicao);
	}
	
	public ClassificacaoDocumento getClassificacaoDocumentoEditor() {
		return getDocumentoEdicao().getClassificacaoDocumento();
	}
	
	public void setClassificacaoDocumentoEditor(ClassificacaoDocumento classificacaoDocumento) {
		getDocumentoEdicao().setClassificacaoDocumento(classificacaoDocumento);
		modeloDocumentoList = null;
	}
	
	public MeioExpedicao getMeioExpedicao() {
		return destinatario.getMeioExpedicao();
	}
	
	public TipoComunicacao getTipoComunicacao() {
		return destinatario.getModeloComunicacao().getTipoComunicacao();
	}

	public Date getPrazoResposta() {
		return prazoResposta;
	}

	public String getStatusProrrogacao() {
		setStatusProrrogacao(prazoComunicacaoService.getStatusProrrogacaoFormatado(processoComunicacao));
		if(Strings.isNullOrEmpty(statusProrrogacao)){
			if (prazoComunicacaoService.canTipoComunicacaoRequestProrrogacaoPrazo(destinatario.getModeloComunicacao().getTipoComunicacao())){
				setStatusProrrogacao("Não solicitada");
			}else{
				setStatusProrrogacao("Indisponível");
			}
		}
		return statusProrrogacao;
	}

	public void setStatusProrrogacao(String statusProrrogacao) {
		this.statusProrrogacao = statusProrrogacao;
	}

	private void initClassificacoes() {
		classificacoesEditor = documentoComunicacaoService.getClassificacoesDocumentoDisponiveisRespostaComunicacao(destinatario, true);
		classificacoesAnexo = documentoComunicacaoService.getClassificacoesDocumentoDisponiveisRespostaComunicacao(destinatario, false);
	}
	
	public void verificarPossibilidadeEnvioResposta() {
		possivelMostrarBotaoEnvio = true;
		if ( !possivelLiberarResponder ) {
		    possivelMostrarBotaoEnvio = false;
		} else {
		    List<Documento> documentosResposta = getDocumentoRespostaList();
	        if (documentosResposta == null || documentosResposta.isEmpty()) {
	            possivelMostrarBotaoEnvio = false;
	        } else {
	            for (Documento documento : documentosResposta) {
	                if(!assinaturaDocumentoService.isDocumentoTotalmenteAssinado(documento) || documento.getDocumentoBin().isMinuta()) {
	                    possivelMostrarBotaoEnvio = false;
	                    break;
	                }
	            }
	        }
		}
	}
	
	protected List<Documento> getDocumentoRespostaList(){
	    respostaComunicacaoList.refresh();
	    List<DocumentoRespostaComunicacao> documentosRespostaComunicacao = new ArrayList<DocumentoRespostaComunicacao>(respostaComunicacaoList.list());
	    List<Documento> documentosResposta = new ArrayList<Documento>();
	    for (DocumentoRespostaComunicacao documentoRespostaComunicacao : documentosRespostaComunicacao) {
            documentosResposta.add(documentoRespostaComunicacao.getDocumento());
        }
	    return documentosResposta;
	}
	
	public List<PerfilTemplate> getPerfilTemplateList() {
        return perfilTemplateList;
    }
	
	public List<PessoaFisica> getPessoaResponderList() {
        return pessoaResponderList;
    }
	
	public List<PessoaRespostaComunicacao> getPessoaResponderComunicacaoList() {
        return pessoaResponderComunicacaoList;
    }

    public PerfilTemplate getPerfilResponderFilter() {
        return perfilResponderFilter;
    }

    public void setPerfilResponderFilter(PerfilTemplate perfilResponderFilter) {
        this.perfilResponderFilter = perfilResponderFilter;
    }

    public PessoaFisica getPessoaResponder() {
        return pessoaResponder;
    }

    public void setPessoaResponder(PessoaFisica pessoaResponder) {
        this.pessoaResponder = pessoaResponder;
    }
	
}
