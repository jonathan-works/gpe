package br.com.infox.epp.processo.comunicacao.envio.action;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OptimisticLockException;

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
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.localizacao.LocalizacaoSearch;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacaoManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.usuario.UsuarioLoginSearch;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.util.ComponentUtil;

@Named(EnvioComunicacaoController.NAME)
@Stateful
@ViewScoped
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EnvioComunicacaoController implements Serializable {
	
	public static final String NAME = "envioComunicacaoController";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(EnvioComunicacaoController.class);
	public static final int MAX_RESULTS = 15;
	
	private TipoComunicacaoManager tipoComunicacaoManager = ComponentUtil.getComponent(TipoComunicacaoManager.NAME);
	private AssinaturaDocumentoService assinaturaDocumentoService = ComponentUtil.getComponent(AssinaturaDocumentoService.NAME);
	private CertificateSignatures certificateSignatures = ComponentUtil.getComponent(CertificateSignatures.NAME);
	
	@Inject
	private DocumentoComunicacaoAction documentoComunicacaoAction;
	@Inject
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@Inject
	private DestinatarioComunicacaoAction destinatarioComunicacaoAction;
	@Inject
	private ProcessoManager processoManager;
	@Inject
	private ComunicacaoService comunicacaoService;
	@Inject
	protected LocalizacaoManager localizacaoManager;
	@Inject
	private ActionMessagesService actionMessagesService;
	@Inject
	private LocalizacaoSearch localizacaoSearch;
	@Inject
	private UsuarioLoginSearch usuarioLoginSearch;
	
	private String raizLocalizacoesComunicacao = Parametros.RAIZ_LOCALIZACOES_COMUNICACAO.getValue();
	private Localizacao localizacaoRaizComunicacao;
	
	private ModeloComunicacao modeloComunicacao;
	private Long processInstanceId;
	
	private List<TipoComunicacao> tiposComunicacao;
	
	private boolean finalizada;
	private String token;
	private Boolean expedida;
	private Boolean comunicacaoSuficientementeAssinada;
	private DestinatarioModeloComunicacao destinatario;
	private boolean inTask = false;
	private boolean minuta;
	private String idModeloComunicacaoVariableName;
	private boolean isNew = true;
	private boolean existeUsuarioLocalizacaoAssinatura = true;
	
	@PostConstruct
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
		clear();
	}
	
	@Remove
	public void destroy(){
		
	}

	private void initDocumentoComunicacaoAction() {
		documentoComunicacaoAction.setModeloComunicacao(modeloComunicacao);
		documentoComunicacaoAction.init();		
	}
	
	private void initDestinatarioComunicacaoAction() {
		destinatarioComunicacaoAction.setModeloComunicacao(modeloComunicacao);
		destinatarioComunicacaoAction.init(getLocalizacaoRaizComunicacao());		
	}
	
	private void initLocalizacaoRaiz() {
		try {
			localizacaoRaizComunicacao = localizacaoManager.getLocalizacaoByNome(raizLocalizacoesComunicacao);
			if (localizacaoRaizComunicacao == null) {
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
	    org.jbpm.taskmgmt.exe.TaskInstance taskInstance = TaskInstance.instance();
		if (idModelo == null) { // Nova comunicação
			if (taskInstance != null) { // Nova comunicação na aba de saída
				ContextInstance context = taskInstance.getContextInstance();
				Token taskToken = taskInstance.getToken();
				idModelo = (Long) context.getVariable(idModeloComunicacaoVariableName, taskToken);
			}
		}
		if (idModelo == null) {
			this.modeloComunicacao = new ModeloComunicacao();
			this.modeloComunicacao.setProcesso(processoManager.getProcessoByIdJbpm(processInstanceId));
			if (taskInstance != null && inTask) {
			    this.modeloComunicacao.setTaskKey(taskInstance.getTask().getKey());
			}
		} else { // Comunicação existente
			this.modeloComunicacao = modeloComunicacaoManager.find(idModelo);
			setFinalizada(modeloComunicacao.getFinalizada() != null ? modeloComunicacao.getFinalizada() : false);
			this.processInstanceId = this.modeloComunicacao.getProcesso().getIdJbpm();
			BusinessProcess.instance().setProcessId(processInstanceId);
			isNew = false;
		}
		minuta = modeloComunicacao.isMinuta();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
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
			clear();
			FacesMessages.instance().add("Registro gravado com sucesso");
			isNew = false;
			minuta = modeloComunicacao.isMinuta();
		} catch (Exception e) {
			LOG.error("Erro ao gravar comunicação ", e);
			if (e instanceof DAOException) {
				if (e.getCause() instanceof OptimisticLockException) {
					actionMessagesService.handleGenericException(e, "Erro ao gravar: A comunicação foi alterada por outro usuário");
				} else {
					actionMessagesService.handleDAOException((DAOException) e);
				}
			} else {
				FacesMessages.instance().add(e.getMessage());
			}
			resetEntityState();
		}
	}

	private void clear() {
		destinatario = null;
		initLocalizacaoRaiz();
		initDestinatarioComunicacaoAction();
		initDocumentoComunicacaoAction();
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
		if (!modeloComunicacao.isMinuta() && modeloComunicacao.getClassificacaoComunicacao() == null){
			msg.append("Escolha a classificação de documento.\n");
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
		if (isNew) {
			minuta = true;
			modeloComunicacao.setId(null);
			setIdModeloVariable(null);
			documentoComunicacaoAction.resetEntityState();
			destinatarioComunicacaoAction.resetEntityState();
			destinatarioComunicacaoAction.setLocalizacao(null);
			destinatarioComunicacaoAction.setPerfilDestino(null);
		}
		modeloComunicacao.setMinuta(minuta);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void expedirComunicacao() {
		try {
			if (destinatario != null) {
				if (!isComunicacaoSuficientementeAssinada()) {
					CertificateSignatureBean signatureBean = getCertificateSignatureBean();
					assinaturaDocumentoService.assinarDocumento(destinatario.getDocumentoComunicacao(), Authenticator.getUsuarioPerfilAtual(), signatureBean.getCertChain(), signatureBean.getSignature());
					clearAssinaturas();
				}
				if (isComunicacaoSuficientementeAssinada()) {
					comunicacaoService.expedirComunicacao(destinatario);
				}
			} else if ((!modeloComunicacao.isDocumentoBinario() && !modeloComunicacao.isClassificacaoAssinavel()) 
					|| documentoComunicacaoAction.isPossuiDocumentoInclusoPorUsuarioInterno()) {
				comunicacaoService.expedirComunicacao(modeloComunicacao);
			}
			clearAssinaturas();
			clear();
			expedida = null;
			if ((destinatario!= null && destinatario.getExpedido()) || (destinatario == null && isExpedida())) {
				FacesMessages.instance().add("Comunicação expedida com sucesso");
			} 
		} catch (DAOException e) {
			LOG.error("Erro ao expedir comunicação", e);
			actionMessagesService.handleDAOException(e);
		} catch (CertificadoException e) {
			LOG.error("Erro ao expedir comunicação", e);
			actionMessagesService.handleException("Erro ao expedir comunicação. " + e.getMessage(), e);
		} catch (AssinaturaException e) {
			LOG.error("Erro ao expedir comunicação", e);
			FacesMessages.instance().add(e.getMessage());
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void reabrirComunicacao() {
		try {
			modeloComunicacao = comunicacaoService.reabrirComunicacao(getModeloComunicacao());
			isNew = false;
			minuta = true;
			resetEntityState();
			clear();
			destinatarioComunicacaoAction.init(getLocalizacaoRaizComunicacao());
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.sucesso.reabertura"));
		} catch (DAOException | CloneNotSupportedException e) {
			LOG.error("Erro ao rebarir comunicação", e);
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.erro.reabertura"));
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void excluirDestinatarioComunicacao(DestinatarioModeloComunicacao destinatarioModeloComunicacao) {
		try {
			destinatarioComunicacaoAction.excluirDestinatario(destinatarioModeloComunicacao);
			if (modeloComunicacao.getDestinatarios().isEmpty()) {
				modeloComunicacao = comunicacaoService.reabrirComunicacao(getModeloComunicacao());
				isNew = true;
				resetEntityState();
			}
			clear();
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.sucesso.exclusaoDestinatario"));
		} catch (DAOException e) {
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.erro.exclusaoDestinatario"));
		} catch (CloneNotSupportedException e) {
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.erro.recuperaModelo"));
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
	
	public List<Localizacao> getLocalizacoesDisponiveisAssinatura(String query) {
		if (localizacaoRaizComunicacao != null && query != null && !query.isEmpty()) {
			return localizacaoSearch.getLocalizacoesByRaizWithDescricaoLike(Authenticator.getLocalizacaoAtual(), query, MAX_RESULTS);
		}
		return Collections.emptyList();
	}

	public List<TipoComunicacao> getTiposComunicacao() {
		if (tiposComunicacao == null) {
			tiposComunicacao = tipoComunicacaoManager.listTiposComunicacaoAtivos();
		}
		return tiposComunicacao;
	}
	
	protected Localizacao getLocalizacaoRaizComunicacao() {
		return localizacaoRaizComunicacao;
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
	
	public boolean isComunicacaoSuficientementeAssinada() {
		if (destinatario != null && comunicacaoSuficientementeAssinada == null) {
			comunicacaoSuficientementeAssinada = assinaturaDocumentoService.isDocumentoTotalmenteAssinado(destinatario.getDocumentoComunicacao());
		}
		return comunicacaoSuficientementeAssinada;
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
		clearAssinaturas();
	}

	private void clearAssinaturas() {
		this.comunicacaoSuficientementeAssinada = null;
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
		modeloComunicacaoManager.refresh(modeloComunicacao);
		boolean usuarioLogadoNaLocalizacaoResponsavel = Authenticator.getLocalizacaoAtual().equals(modeloComunicacao.getLocalizacaoResponsavelAssinatura());
		PerfilTemplate perfilUsuarioLogado = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate();
		PerfilTemplate perfilResponsavelAssinatura = modeloComunicacao.getPerfilResponsavelAssinatura();
		boolean usuarioLogadoNoPerfilResponsavel = perfilResponsavelAssinatura == null || perfilUsuarioLogado.equals(perfilResponsavelAssinatura);
		return usuarioLogadoNaLocalizacaoResponsavel && usuarioLogadoNoPerfilResponsavel;
	}
	
	public boolean isPrazoComunicacaoRequired(){
		return false;
	}
	
	public Long getJbpmProcessId() {
		return JbpmUtil.getProcesso().getIdJbpm();
	}
	
	public boolean existeUsuarioLocalizacaoAssinatura() {
		return existeUsuarioLocalizacaoAssinatura;
	}
	
	public void verificaExistenciaUsuario() {
		if (getModeloComunicacao().getLocalizacaoResponsavelAssinatura() != null) {
			existeUsuarioLocalizacaoAssinatura = usuarioLoginSearch.existsUsuarioWithLocalizacaoPerfil(getModeloComunicacao().getLocalizacaoResponsavelAssinatura(),
					getModeloComunicacao().getPerfilResponsavelAssinatura());
		}
	}
}
