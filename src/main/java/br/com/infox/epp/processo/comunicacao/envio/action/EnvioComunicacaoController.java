package br.com.infox.epp.processo.comunicacao.envio.action;

import java.io.Serializable;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.NonUniqueResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.TaskInstance;
import org.jboss.seam.faces.FacesMessages;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.Token;

import com.google.common.base.Strings;

import br.com.infox.certificado.CertificateSignatures;
import br.com.infox.certificado.bean.CertificateSignatureBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleStatus;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.component.tree.LocalizacaoSubTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.DocumentoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacaoManager;
import br.com.infox.epp.processo.documento.anexos.DocumentoDownloader;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Name(EnvioComunicacaoController.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
@Transactional
public class EnvioComunicacaoController implements Serializable {
	
	public static final String NAME = "envioComunicacaoController";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(EnvioComunicacaoController.class);
	
	@In
	private TipoComunicacaoManager tipoComunicacaoManager;
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private LocalizacaoSubTreeHandler localizacaoSubTree;
	@In
	private String raizLocalizacoesComunicacao;
	@In
	private LocalizacaoManager localizacaoManager;
	@In
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@In
	private ComunicacaoService comunicacaoService;
	@In
	private ProcessoManager processoManager;
	@In
	private DocumentoDownloader documentoDownloader;
	@In
	private CertificateSignatures certificateSignatures;
	@In
	private DocumentoComunicacaoAction documentoComunicacaoAction;
	@In
	private DestinatarioComunicacaoAction destinatarioComunicacaoAction;
	@In
	private DocumentoComunicacaoService documentoComunicacaoService;
	
	private ModeloComunicacao modeloComunicacao;
	private Long processInstanceId;
	
	private List<TipoComunicacao> tiposComunicacao;
	
	private boolean finalizada;
	private String token;
	private Boolean expedida;
	private DestinatarioModeloComunicacao destinatario;
	private boolean inTask = false;
	private boolean minuta = true;
	private String idModeloComunicacaoVariableName;
	private boolean isNew = true;
	
	@Create
	public void init() {
		String idJbpm = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jbpmProcessId");
		String idModelo = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("idModeloComunicacao");
		if (idJbpm != null) { // Nova comunicação fora da aba de saída
			processInstanceId = Long.valueOf(idJbpm);
		} else if (idModelo == null) { // Nova comunicação dentro da aba de saída
			processInstanceId = Long.valueOf(JbpmUtil.getProcesso().getIdJbpm());
			inTask = true;
		}
		org.jbpm.taskmgmt.exe.TaskInstance taskInstance = TaskInstance.instance();
		if (taskInstance != null) {
			idModeloComunicacaoVariableName = "idModeloComunicacao-" + taskInstance.getId();
		}
		initModelo(idModelo == null ? null : Long.valueOf(idModelo));
		initLocalizacaoRaiz();
		initDestinatarioComunicacaoAction();
		initDocumentoComunicacaoAction();
	}

	private void initDocumentoComunicacaoAction() {
		documentoComunicacaoAction.setModeloComunicacao(modeloComunicacao);
		documentoComunicacaoAction.init();		
	}
	
	private void initDestinatarioComunicacaoAction() {
		destinatarioComunicacaoAction.setModeloComunicacao(modeloComunicacao);
		destinatarioComunicacaoAction.init();		
	}

	private void initLocalizacaoRaiz() {
		try {
			Localizacao localizacaoRaiz = localizacaoManager.getLocalizacaoByNome(raizLocalizacoesComunicacao);
			if (localizacaoRaiz != null) {
				localizacaoSubTree.setIdLocalizacaoPai(localizacaoRaiz.getIdLocalizacao());
			} else {
				FacesMessages.instance().add("O parâmetro raizLocalizacoesComunicacao não foi definido.");
			}
		} catch (DAOException e) {
			LOG.error("", e);
			if (e.getCause() instanceof NonUniqueResultException) {
				FacesMessages.instance().add("Existe mais de uma localização com o nome definido no parâmetro raizLocalizacoesComunicacao: " + raizLocalizacoesComunicacao);
			} else {
				actionMessagesService.handleDAOException(e);
			}
		}
	}

	private void initModelo(Long idModelo) {
		if (idModelo == null) { // Nova comunicação
			org.jbpm.taskmgmt.exe.TaskInstance taskInstance = TaskInstance.instance();
			if (taskInstance != null) { // Nova comunicação na aba de saída
				ContextInstance context = taskInstance.getContextInstance();
				Token taskToken = taskInstance.getToken();
				idModelo = (Long) context.getVariable(idModeloComunicacaoVariableName, taskToken);
			}
		}
		if (idModelo == null) { // Nova Comunicação fora da aba de saída, pois o idModelo continua nulo
			this.modeloComunicacao = new ModeloComunicacao();
			this.modeloComunicacao.setProcesso(processoManager.getProcessoByNumero(processoManager.getNumeroProcessoByIdJbpm(processInstanceId)));
		} else { // Comunicação existente
			this.modeloComunicacao = modeloComunicacaoManager.find(idModelo);
			setFinalizada(modeloComunicacao.getFinalizada() != null ? modeloComunicacao.getFinalizada() : false);
			this.processInstanceId = this.modeloComunicacao.getProcesso().getIdJbpm();
			BusinessProcess.instance().setProcessId(processInstanceId);
			isNew = false;
		}
	}
	
	public void gravar() {
		try {
			validarGravacao();
			
			if (modeloComunicacao.getId() == null) {
				modeloComunicacaoManager.persist(modeloComunicacao);
			}

			destinatarioComunicacaoAction.persistDestinatarios();
			documentoComunicacaoAction.persistDocumentos();
			
			modeloComunicacaoManager.update(modeloComunicacao);
			setIdModeloVariable(modeloComunicacao.getId());
			if (isFinalizada()) {
				comunicacaoService.finalizarComunicacao(modeloComunicacao);
				if ((!modeloComunicacao.isDocumentoBinario() && !modeloComunicacao.isClassificacaoAssinavel()) 
					|| (modeloComunicacao.isDocumentoBinario() && documentoComunicacaoAction.isPossuiDocumentoInclusoPorUsuarioInterno())) {
					expedirComunicacao();
				}
			}
			FacesMessages.instance().add("Registro gravado com sucesso");
			isNew = false;
		} catch (Exception e) {
			LOG.error("Erro ao gravar comunicação ", e);
			if (e instanceof DAOException) {
				actionMessagesService.handleDAOException((DAOException) e);
			} else {
				FacesMessages.instance().add(e.getMessage());
			}
			resetEntityState();
		}
	}

	private void setIdModeloVariable(Long id) {
		org.jbpm.taskmgmt.exe.TaskInstance taskInstance = TaskInstance.instance();
		if (taskInstance != null) {
			ContextInstance context = taskInstance.getContextInstance();
			Token taskToken = taskInstance.getToken();
			context.setVariable(idModeloComunicacaoVariableName, id, taskToken);
		}
	}

	private void validarGravacao() {
		StringBuilder msg = criarMensagensValidacao();
		if (msg.length() > 0) {
			throw new BusinessException(msg.toString());
		}
	}

	protected StringBuilder criarMensagensValidacao() {
		StringBuilder msg = new StringBuilder();
		if (modeloComunicacao.getTipoComunicacao() == null) {
			msg.append("Escolha o tipo de comunicação.\n");
		}
		if (modeloComunicacao.getDestinatarios().isEmpty()) {
			msg.append("Nenhum destinatário foi selecionado.\n");
		}
		if (!modeloComunicacao.isMinuta() && Strings.isNullOrEmpty(modeloComunicacao.getTextoComunicacao())){
			msg.append("O documento do editor não é minuta mas não existe texto no editor.\n");
		}
		for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
			if (destinatario.getMeioExpedicao() == null) {
				msg.append("Existe destinatário sem meio de expedição selecionado.\n");
				break;
			}
			if (isPrazoComunicacaoRequired() && (destinatario.getPrazo() == null || destinatario.getPrazo() < 0)){
				msg.append("Não foi informado o prazo para o destinatário ");
				msg.append(destinatario.getNome());
				msg.append(" ou esse prazo é inválido.\n");
				break;
			}
		}
		return msg;
	}

	private void resetEntityState() {
		this.finalizada = false;
		modeloComunicacao.setFinalizada(false);
		this.minuta = true;
		modeloComunicacao.setMinuta(true);
		if (isNew) {
			modeloComunicacao.setId(null);
			setIdModeloVariable(null);
			documentoComunicacaoAction.resetEntityState();
			destinatarioComunicacaoAction.resetEntityState();
		}
	}

	public void expedirComunicacao() {
		try {
			if (destinatario != null) {
				CertificateSignatureBean signatureBean = getCertificateSignatureBean();
				assinaturaDocumentoService.assinarDocumento(destinatario.getDocumentoComunicacao(), Authenticator.getUsuarioPerfilAtual(), signatureBean.getCertChain(), signatureBean.getSignature());
				comunicacaoService.expedirComunicacao(destinatario);
			} else if ((!modeloComunicacao.isDocumentoBinario() && !modeloComunicacao.isClassificacaoAssinavel()) 
					|| documentoComunicacaoAction.isPossuiDocumentoInclusoPorUsuarioInterno()) {
				comunicacaoService.expedirComunicacao(modeloComunicacao);
			}
			expedida = null;
			FacesMessages.instance().add("Comunicação expedida com sucesso");
		} catch (DAOException e) {
			LOG.error("Erro ao expedir comunicação", e);
			actionMessagesService.handleDAOException(e);
		} catch (CertificadoException e) {
			LOG.error("Erro ao expedir comunicação", e);
			actionMessagesService.handleException("Erro ao expedir comunicação", e);
		} catch (AssinaturaException e) {
			LOG.error("Erro ao expedir comunicação", e);
			FacesMessages.instance().add(e.getMessage());
		}
	}

	private CertificateSignatureBean getCertificateSignatureBean() throws DAOException {
		CertificateSignatureBundleBean certificateSignatureBundleBean = certificateSignatures.get(token);
		if (certificateSignatureBundleBean.getStatus() != CertificateSignatureBundleStatus.SUCCESS) {
		    throw new DAOException(InfoxMessages.getInstance().get("comunicacao.assinar.erro"));
		}
		CertificateSignatureBean signatureBean = certificateSignatureBundleBean.getSignatureBeanList().get(0);
		return signatureBean;
	}

	public List<TipoComunicacao> getTiposComunicacao() {
		if (tiposComunicacao == null) {
			tiposComunicacao = tipoComunicacaoManager.listTiposComunicacaoAtivos();
		}
		return tiposComunicacao;
	}
	
	public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isFinalizada() {
		return finalizada;
	}
	
	public void setFinalizada(boolean finalizada) {
		this.finalizada = finalizada;
		if (!this.finalizada) {
			modeloComunicacao.setLocalizacaoResponsavelAssinatura(null);
			modeloComunicacao.setPerfilResponsavelAssinatura(null);
			localizacaoSubTree.clearTree();
		}
	}
	
	public ModeloComunicacao getModeloComunicacao() {
		return modeloComunicacao;
	}
	
	public boolean isExpedida() {
		if (expedida == null && modeloComunicacao.getFinalizada()) {
			expedida = modeloComunicacaoManager.isExpedida(modeloComunicacao);
		}
		return modeloComunicacao.getFinalizada() && expedida;
	}
	
	public boolean podeRenderizarApplet() {
		UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
		Papel papel = usuarioPerfil.getPerfilTemplate().getPapel();
		UsuarioLogin usuario = usuarioPerfil.getUsuarioLogin();
		DocumentoBin documento = null; 
		ClassificacaoDocumento classificacao = null;
		if (modeloComunicacao.isDocumentoBinario()) {
			Documento documentoComunicacao = modeloComunicacao.getDestinatarios().get(0).getDocumentoComunicacao();
			documento = documentoComunicacao.getDocumentoBin();
			classificacao = documentoComunicacao.getClassificacaoDocumento();
		} else {
			documento = destinatario.getDocumentoComunicacao().getDocumentoBin();
			classificacao = modeloComunicacao.getClassificacaoComunicacao();
		}
		return documento != null && assinaturaDocumentoService.podeRenderizarApplet(papel, classificacao, documento, usuario);
	}
	
	public DestinatarioModeloComunicacao getDestinatario() {
		return destinatario;
	}
	
	public void setDestinatario(DestinatarioModeloComunicacao destinatario) {
		this.destinatario = destinatario;
	}
	
	public boolean isInTask() {
		return inTask;
	}
	
	public TipoComunicacao getTipoComunicacao() {
		return modeloComunicacao.getTipoComunicacao();
	}
	
	public void setTipoComunicacao(TipoComunicacao tipoComunicacao) {
		modeloComunicacao.setTipoComunicacao(tipoComunicacao);
		documentoComunicacaoAction.initClassificacoes();
		documentoComunicacaoAction.setModelosDocumento(null);
		modeloComunicacao.setClassificacaoComunicacao(null);
		modeloComunicacao.setModeloDocumento(null);
	}
	
	public boolean podeExibirBotaoVisualizarComunicacoes() {
		return modeloComunicacao.getFinalizada() && isExpedida() && modeloComunicacao.isDocumentoBinario();
	}
	
	public boolean podeVisualizarComunicacaoNaoFinalizada(){
		return modeloComunicacao.isDocumentoBinario() && documentoComunicacaoAction.isPossuiDocumentoInclusoPorUsuarioInterno() && !modeloComunicacao.getFinalizada();
	}
	
	public boolean isUsuarioLogadoNaLocalizacaoPerfilResponsavel() {
		boolean usuarioLogadoNaLocalizacaoResponsavel = Authenticator.getLocalizacaoAtual().equals(modeloComunicacao.getLocalizacaoResponsavelAssinatura());
		PerfilTemplate perfilUsuarioLogado = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate();
		PerfilTemplate perfilResponsavelAssinatura = modeloComunicacao.getPerfilResponsavelAssinatura();
		boolean usuarioLogadoNoPerfilResponsavel = perfilResponsavelAssinatura == null || perfilUsuarioLogado.equals(perfilResponsavelAssinatura);
		return usuarioLogadoNaLocalizacaoResponsavel && usuarioLogadoNoPerfilResponsavel;
	}
	
	public boolean isMinuta() {
		return minuta;
	}
	
	public void setMinuta(boolean minuta) {
		this.minuta = minuta;
	}
	
	public boolean isPrazoComunicacaoRequired(){
		return false;
	}
	
	public Long getJbpmProcessId() {
		return JbpmUtil.getProcesso().getIdJbpm();
	}
}
