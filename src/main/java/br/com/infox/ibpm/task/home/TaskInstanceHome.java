package br.com.infox.ibpm.task.home;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.SystemException;

import org.hibernate.HibernateException;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.transaction.Transaction;
import org.jbpm.JbpmException;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.context.exe.VariableInstance;
import org.jbpm.context.exe.variableinstance.NullInstance;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.certificado.CertificateSignatures;
import br.com.infox.certificado.bean.CertificateSignatureBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleStatus;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;
import br.com.infox.epp.menu.MenuMovimentar;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.handler.ProcessoHandler;
import br.com.infox.epp.processo.home.ProcessoEpaHome;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.task.action.TaskPageAction;
import br.com.infox.ibpm.task.dao.TaskConteudoDAO;
import br.com.infox.ibpm.task.entity.TaskConteudo;
import br.com.infox.ibpm.task.manager.TaskInstanceManager;
import br.com.infox.ibpm.task.view.Form;
import br.com.infox.ibpm.task.view.FormField;
import br.com.infox.ibpm.task.view.TaskInstanceForm;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.variable.VariableHandler;
import br.com.infox.ibpm.variable.entity.VariableInfo;
import br.com.infox.ibpm.variable.file.FileVariableHandler;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.context.ContextFacade;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.util.ComponentUtil;
import br.com.itx.component.AbstractHome;

@Name(TaskInstanceHome.NAME)
@Scope(ScopeType.CONVERSATION)
@Transactional
@ContextDependency
public class TaskInstanceHome implements Serializable {

	private static final String MSG_USUARIO_SEM_ACESSO = "Você não pode mais efetuar transações "
			+ "neste registro, verifique se ele não foi movimentado";
	private static final String UPDATED_VAR_NAME = "isTaskHomeUpdated";
	private static final LogProvider LOG = Logging.getLogProvider(TaskInstanceHome.class);
	private static final long serialVersionUID = 1L;
	public static final String NAME = "taskInstanceHome";
	private static final String URL_DOWNLOAD_BINARIO = "{0}/downloadDocumento.seam?id={1}";
	private static final String URL_DOWNLOAD_HTML = "{0}/Painel/documentoHTML.seam?id={1}";

	@In
	private ProcessoManager processoManager;
	@In
	private ProcessoTarefaManager processoTarefaManager;
	@In
	private TaskInstanceManager taskInstanceManager;
	@In
	private ModeloDocumentoManager modeloDocumentoManager;
	@In
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@In
	private VariableTypeResolver variableTypeResolver;
	@In(create = true)
	private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
	@In
	private DocumentoManager documentoManager;
	@In
	private PastaManager pastaManager; 
	@In
	private DocumentoBinManager documentoBinManager;
	@In
	private InfoxMessages infoxMessages;
	@In
	private CertificateSignatures certificateSignatures;
	@In
	private PathResolver pathResolver;
	@In
	private ProcessoEpaHome processoEpaHome;
	@In
	private TarefaManager tarefaManager;
	@In
	private ProcessoHandler processoHandler;
	
	@Inject
	private MenuMovimentar menuMovimentar;
	@Inject
	private SituacaoProcessoDAO situacaoProcessoDAO;
	@Inject
	private FileVariableHandler fileVariableHandler;
	@Inject
	private ActionMessagesService actionMessagesService;

	private TaskInstance taskInstance;
	private Map<String, Object> mapaDeVariaveis;
	private Long taskId;
	private List<Transition> availableTransitions;
	private List<Transition> leavingTransitions;
	private ModeloDocumento modeloDocumento;
	private String varName;
	private String name;
	private TaskInstance currentTaskInstance;
	private Map<String, Documento> variaveisDocumento;
	private Documento documentoToSign;
	private String tokenToSign;

	private URL urlRetornoAcessoExterno;

	private boolean canClosePanelVal;
	private boolean taskCompleted;

	public void createInstance() {
		taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
		if (mapaDeVariaveis == null && taskInstance != null) {
			variableTypeResolver.setProcessInstance(taskInstance.getProcessInstance());
			mapaDeVariaveis = new HashMap<String, Object>();
			variaveisDocumento = new HashMap<>();
			retrieveVariables();
		}
	}

	private void retrieveVariables() {
		TaskController taskController = taskInstance.getTask().getTaskController();
		if (taskController != null) {
			List<VariableAccess> list = taskController.getVariableAccesses();
			for (VariableAccess variableAccess : list) {
				retrieveVariable(variableAccess);
			}
			// Atualizar as transições possiveis. Isso é preciso, pois as
			// condições das transições são avaliadas antes deste metodo ser
			// executado.
			updateTransitions();
		}
	}

	private void retrieveVariable(VariableAccess variableAccess) {
		TaskVariableRetriever variableRetriever = new TaskVariableRetriever(variableAccess, taskInstance);
		variableRetriever.retrieveVariableContent();
        
		mapaDeVariaveis.put(getFieldName(variableRetriever.getName()), variableRetriever.getVariable());
		if (variableRetriever.isEditor() || variableRetriever.isFile()) {
			Integer idDocumento = (Integer) taskInstance.getVariable(variableRetriever.getMappedName());
			Documento documento = null;
			if (idDocumento != null) {
				documento = documentoManager.find(idDocumento);
				if (variableRetriever.isEditor() && documento.hasAssinatura()) {
					setModeloReadonly(variableRetriever.getName());
				}
			} else {
				documento = new Documento();
				loadClassificacaoDocumentoDefault(variableRetriever, documento);
				if (variableRetriever.isEditor()) {
					documento.setDocumentoBin(new DocumentoBin());
				}
			}
			variaveisDocumento.put(getFieldName(variableRetriever.getName()), documento);
			if (variableRetriever.isEditor() && documento.getId() == null) {
				setModeloWhenExists(variableRetriever, documento);
			}
		}
	}

	private void loadClassificacaoDocumentoDefault(TaskVariableRetriever variableRetriever, Documento documento) {
		Integer idFluxo = processoEpaHome.getInstance().getNaturezaCategoriaFluxo().getFluxo().getIdFluxo();
		List<ClassificacaoDocumento> classificacoes = getUseableClassificacaoDocumento(false, variableRetriever.getName(), idFluxo);
		if (classificacoes != null && classificacoes.size() == 1) {
			documento.setClassificacaoDocumento(classificacoes.get(0));
		}
	}

	private void setModeloWhenExists(TaskVariableRetriever variableRetriever, Documento documentoEditor) {
		String modelo = getModeloFromProcessInstance(variableRetriever.getName());
		if (modelo != null) {
			if (!variableRetriever.hasVariable()) {
				setModeloDocumento(getModeloDocumentoFromModelo(modelo));
				assignModeloDocumento(getFieldName(variableRetriever.getName()));
			}
		}
	}
	
	private void setModeloReadonly(String variavelEditor) {
		Form form = ComponentUtil.getComponent(TaskInstanceForm.NAME);
		String variavelComboModelo = variavelEditor + "Modelo";
		for (FormField formField : form.getFields()) {
			if (formField.getId().equals(variavelComboModelo)) {
				formField.getProperties().put("readonly", true);
				break;
			}
		}
	}

	private String getModeloFromProcessInstance(String variableName) {
		return (String) ProcessInstance.instance().getContextInstance().getVariable(variableName + "Modelo");
	}

	private ModeloDocumento getModeloDocumentoFromModelo(String modelo) {
		String s = modelo.split(",")[0].trim();
		return modeloDocumentoManager.find(Integer.parseInt(s));
	}

	@Factory("menuMovimentar")
	public MenuMovimentar getMenuMovimentar() {
		return menuMovimentar;
	}
	
	public Map<String, Object> getInstance() {
		createInstance();
		return mapaDeVariaveis;
	}

	public boolean update() {
		prepareForUpdate();
		if (possuiTask()) {
			TaskController taskController = taskInstance.getTask().getTaskController();
			TaskPageAction taskPageAction = ComponentUtil.getComponent(TaskPageAction.NAME);
			if (taskController != null) {
				if (!taskPageAction.getHasTaskPage(getCurrentTaskInstance())) {
					try {
						updateVariables(taskController);
					} catch (BusinessException e) {
						LOG.error("", e);
						FacesMessages.instance().clear();
						FacesMessages.instance().add(e.getMessage());
						return false;
					}
				}
				completeUpdate();
			}
		}
		return true;
	}

	private void prepareForUpdate() {
		modeloDocumento = null;
		taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
	}

	private void completeUpdate() {
		ContextFacade.setToEventContext(UPDATED_VAR_NAME, true);
		updateIndex();
		updateTransitions();
		// Necessário para gravar a prioridade do processo ao clicar no botão
		// Gravar
		// Não pode usar ProcessoEpaHome.instance().update() porque por algum
		// motivo dá um NullPointerException
		// ao finalizar a tarefa, algo relacionado às mensagens do Seam
		taskInstanceManager.flush();
	}

	private boolean possuiTask() {
		return (taskInstance != null) && (taskInstance.getTask() != null);
	}

	private void updateVariables(TaskController taskController) {
		updateVariablesEditorContent();
		List<VariableAccess> list = taskController.getVariableAccesses();
		for (VariableAccess variableAccess : list) {
			updateVariable(variableAccess);
		}
	}

	private void updateVariablesEditorContent() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		Enumeration<String> requestParamNames = request.getParameterNames();
		while (requestParamNames.hasMoreElements()) {
			String paramName = requestParamNames.nextElement();
			if (paramName.endsWith("Editor")){
				String paramValue = request.getParameter(paramName);
				int lastIndexNamedContainer = paramName.lastIndexOf(":") + 1;
				int lastIndexOfEditor = paramName.lastIndexOf("Editor");
				String variableFieldName = paramName.substring(lastIndexNamedContainer, lastIndexOfEditor);
				Documento documento = variaveisDocumento.get(variableFieldName);
				documento.getDocumentoBin().setModeloDocumento(paramValue);
			}
		}
	}

	private void updateVariable(VariableAccess variableAccess) {
		TaskVariableResolver variableResolver = new TaskVariableResolver(variableAccess, taskInstance);
		if (variableAccess.isWritable()) {
			variableResolver.assignValueFromMapaDeVariaveis(mapaDeVariaveis);
			variableResolver.resolve();
			if (variableResolver.isEditor()) {
				Documento documento = variaveisDocumento.get(getFieldName(variableResolver.getName()));
				try {
					updateVariableEditor(documento, variableAccess);
					variableResolver.assignValueFromMapaDeVariaveis(mapaDeVariaveis);
					variableResolver.resolve();
				} catch (DAOException e) {
					LOG.error("updateVariable(variableAccess)", e);
				}
			} else if (variableResolver.isFile()) {
				retrieveVariable(variableAccess);
			}
		}
	}

	private void updateVariableEditor(Documento documento, VariableAccess variableAccess) throws DAOException {
		if (documento.getId() != null) {
			documentoBinManager.update(documento.getDocumentoBin());
			documentoManager.update(documento);
		} else {
			if (documento.getClassificacaoDocumento() != null) {
				createVariableEditor(documento, variableAccess);
			}
		}
		taskInstance.setVariable(variableAccess.getMappedName(), documento.getId());
		mapaDeVariaveis.put(getFieldName(variableAccess.getVariableName()), documento.getDocumentoBin().getModeloDocumento());
		variaveisDocumento.put(getFieldName(variableAccess.getVariableName()), documento);
	}

	private void createVariableEditor(Documento documento, VariableAccess variableAccess) throws DAOException {
		DocumentoBin documentoBin = documento.getDocumentoBin();
		documentoBin.setDataInclusao(new Date());
		documentoBin.setMinuta(false);
		if (documentoBin.getModeloDocumento() == null) {
			documentoBin.setModeloDocumento("");
		}
		documentoBin.setMd5Documento(MD5Encoder.encode(documentoBin.getModeloDocumento()));
		documentoBinManager.persist(documentoBin);
		documento.setProcesso(processoEpaHome.getInstance());
		documento.setNumeroDocumento(documentoManager.getNextNumeracao(documento));
		documento.setIdJbpmTask(getCurrentTaskInstance().getId());
		documento.setPasta(pastaManager.getDefaultFolder(processoEpaHome.getInstance()));
		String descricao = JbpmUtil.instance().getMessages().get(processoEpaHome.getInstance().getNaturezaCategoriaFluxo().getFluxo().getFluxo() + ":" + variableAccess.getMappedName().split(":")[1]);
		documento.setDescricao(descricao == null ? "-" : descricao);
		documentoManager.persist(documento);
	}

	public Boolean checkAccessSemException() {
        Boolean hasAccess = false;
	    try {
            hasAccess = checkAccess();
        } catch (ApplicationException e) {
            FacesMessages.instance().add(e.getMessage());
        }
        return hasAccess;
	}
	
	private Boolean checkAccess() {
		int idProcesso = processoEpaHome.getInstance().getIdProcesso();
		Integer idUsuarioLogin = Authenticator.getUsuarioLogado().getIdUsuarioLogin();
		validateTaskId();
		if (processoManager.checkAccess(idProcesso, idUsuarioLogin, taskId)) {
			return Boolean.TRUE;
		} else {
			acusarUsuarioSemAcesso();
			return Boolean.FALSE;
		}
	}

	public void update(Object homeObject) {
		if (checkAccess()) {
			canDoOperation();
			if (homeObject instanceof AbstractHome<?>) {
				AbstractHome<?> home = (AbstractHome<?>) homeObject;
				home.update();
			}
			update();
		}
	}

	public void persist(Object homeObject) {
		if (checkAccess()) {
			canDoOperation();
			if (homeObject instanceof AbstractHome<?>) {
				AbstractHome<?> home = (AbstractHome<?>) homeObject;
				Object entity = home.getInstance();
				home.persist();
				Object idObject = EntityUtil.getEntityIdObject(entity);
				home.setId(idObject);
				if (varName != null) {
					mapaDeVariaveis.put(getFieldName(varName), idObject);
				}
				update();
			}
		}
	}

	private void canDoOperation() {
		if (getCurrentTaskInstance() != null) {
			if (canOpenTask()) {
				return;
			}
			acusarUsuarioSemAcesso();
		}
	}

	private void acusarUsuarioSemAcesso() {
		FacesMessages.instance().clear();
		throw new ApplicationException(MSG_USUARIO_SEM_ACESSO);
	}

	private boolean canOpenTask() {
		MetadadoProcesso metadadoProcesso = processoEpaHome.getInstance().getMetadado(EppMetadadoProvider.TIPO_PROCESSO);
		TipoProcesso tipoProcesso = (metadadoProcesso != null ? metadadoProcesso.<TipoProcesso> getValue() : null);
		return situacaoProcessoDAO.canOpenTask(currentTaskInstance.getId(), tipoProcesso, false);
	}

	public TaskInstance getCurrentTaskInstance() {
		if (currentTaskInstance == null) {
			currentTaskInstance = org.jboss.seam.bpm.TaskInstance.instance();
		}
		return currentTaskInstance;
	}

	public String getTaskNodeDescription() {
		if (currentTaskInstance == null) {
			currentTaskInstance = org.jboss.seam.bpm.TaskInstance.instance();
		}
		return currentTaskInstance.getTask().getTaskNode().getDescription();
	}

	public void updateIndex() {
		TaskConteudoDAO taskConteudoDAO = ComponentUtil.getComponent(TaskConteudoDAO.NAME);
		TaskConteudo taskConteudo = taskConteudoDAO.find(getTaskId());
		int idProcesso = processoEpaHome.getInstance().getIdProcesso();
		if (taskConteudo != null) {
			try {
				taskConteudoDAO.update(taskConteudo);
			} catch (DAOException e) {
				LOG.error("Não foi possível reindexar o conteúdo da TaskInstance " + getTaskId(), e);
			}
		} else {
			taskConteudo = new TaskConteudo();
			taskConteudo.setNumeroProcesso(idProcesso);
			taskConteudo.setIdTaskInstance(getTaskId());
			try {
				taskConteudoDAO.persist(taskConteudo);
			} catch (DAOException e) {
				LOG.error("Não foi possível indexar o conteúdo da TaskInstance " + getTaskId(), e);
			}
		}
	}

	public boolean podeAssinarDocumento(String variableName) {
		VariableInfo variableInfo = variableTypeResolver.getVariableInfoMap().get(variableName);
		TaskInstance taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
		if (taskInstance == null){
			return false;
		}
		if (variableInfo == null){
			return false;
		}
		Integer idDocumento = (Integer) taskInstance.getVariable(variableInfo.getMappedName());
		if (idDocumento != null) {
			Documento documento = documentoManager.find(idDocumento);
			return documento != null 
			        && documento.isDocumentoAssinavel(Authenticator.getPapelAtual())
			        && !documento.isDocumentoAssinado(Authenticator.getPapelAtual());
		}
		return false;
	}

	public String getViewUrlDownload(String variableName) {
		Integer idDocumento = (Integer) org.jboss.seam.bpm.TaskInstance.instance().getVariable("FILE:" + variableName);
		Documento documento = documentoManager.find(idDocumento);
		if (documento.getDocumentoBin().isBinario()) {
			return MessageFormat.format(URL_DOWNLOAD_BINARIO, pathResolver.getContextPath(), documento.getId().toString());
		}
		return MessageFormat.format(URL_DOWNLOAD_HTML, pathResolver.getContextPath(), documento.getId().toString());
	}

	public void assinarDocumento() {
		if (documentoToSign == null) {
			FacesMessages.instance().add("Sem documento para assinar");
		}
		CertificateSignatureBundleBean certificateSignatureBundle = certificateSignatures.get(tokenToSign);
		if (certificateSignatureBundle.getStatus() != CertificateSignatureBundleStatus.SUCCESS) {
			FacesMessages.instance().add("Erro ao assinar");
		} else {
			CertificateSignatureBean signatureBean = certificateSignatureBundle.getSignatureBeanList().get(0);
			try {
				assinaturaDocumentoService.assinarDocumento(getDocumentoToSign(), Authenticator.getUsuarioPerfilAtual(),
						signatureBean.getCertChain(), signatureBean.getSignature());
				for (String variavel : variaveisDocumento.keySet()) {
					if (documentoToSign.equals(variaveisDocumento.get(variavel))) {
						setModeloReadonly(variavel.split("-")[0]);
						break;
					}
				}
			} catch (CertificadoException | AssinaturaException | DAOException e) {
				FacesMessages.instance().add(e.getMessage());
				LOG.error("assinarDocumento()", e);
			} finally {
				setDocumentoToSign(null);
				setVariavelDocumentoToSign(null);
				setTokenToSign(null);
			}
		}
	}

	@Observer(Event.EVENTTYPE_TASK_CREATE)
	public void setCurrentTaskInstance(ExecutionContext context) {
		if (currentTaskInstance != null) {
			if (!currentTaskInstance.getProcessInstance().equals(context.getProcessInstance())) {
				return;
			}
			Token currentRootToken = currentTaskInstance.getProcessInstance().getRootToken();
			if (!currentRootToken.equals(context.getProcessInstance().getRootToken())) {
				return;
			}
		}
		setCurrentTaskInstance(context.getTaskInstance());
	}

	public void setCurrentTaskInstance(TaskInstance taskInstance) {
		try {
			this.currentTaskInstance = taskInstance;
		} catch (Exception ex) {
			String action = "atribuir a taskInstance corrente ao currentTaskInstance: ";
			LOG.warn(action, ex);
			throw new ApplicationException(ApplicationException.createMessage(action + ex.getLocalizedMessage(),
					"setCurrentTaskInstance()", "TaskInstanceHome", "BPM"), ex);
		}
	}

	public String end(String transition) {
		if (checkAccess()) {
			checkCurrentTask();
			if (!update()) {
				return null;
			}
			if (!validarAssinaturaDocumentosAoMovimentar()) {
				return null;
			}
			if (!validFileUpload()) {
				return null;
			}
			this.currentTaskInstance = null;
			finalizarTaskDoJbpm(transition);
			// Flush para que a consulta do canOpenTask consiga ver o pooled
			// actor que o jbpm criou
			// no TaskInstance#create, caso contrário, o epp achará que o
			// usuário não pode ver a tarefa seguinte,
			// mesmo que possa
			try {
				if (Transaction.instance().isActive()) {
					JbpmUtil.getJbpmSession().flush();
				}
			} catch (HibernateException | SystemException e) {
				LOG.error("", e);
			}
			mapaDeVariaveis = null;
			atualizarPaginaDeMovimentacao();
		}
		return null;
	}

	private boolean validarAssinaturaDocumentosAoMovimentar() {
		Map<String, VariableInstance> variableMap = org.jboss.seam.bpm.TaskInstance.instance().getVariableInstances();
		FacesMessages.instance().clear();
		boolean isAssinaturaOk = true;
		for (String key : variableMap.keySet()) {
			if (key.startsWith("FILE") || key.startsWith("EDITOR")) {
				VariableInstance variableInstance = variableMap.get(key);
				if (variableInstance instanceof NullInstance) {
					continue;
				}
				Documento documento = documentoManager.find(variableInstance.getValue());
				boolean assinaturaVariavelOk = validarAssinaturaDocumento(documento);
				if (!assinaturaVariavelOk) {
				    String label = VariableHandler.getLabel(format("{0}:{1}", taskInstance.getTask().getProcessDefinition().getName(), key.split(":")[1]));
					FacesMessages.instance().add(String.format(infoxMessages.get("assinaturaDocumento.faltaAssinatura"), label));
				}
				isAssinaturaOk = isAssinaturaOk && assinaturaVariavelOk;
			}
		}
		return isAssinaturaOk;
	}

    private boolean validarAssinaturaDocumento(Documento documento) {
        Papel papel = Authenticator.getPapelAtual();
        boolean isValid = assinaturaDocumentoService.isDocumentoTotalmenteAssinado(documento)
                || !documento.isAssinaturaObrigatoria(papel) || documento.isDocumentoAssinado(papel);
        return isValid;
    }

	private boolean validFileUpload() {
		// TODO verificar se é necessária a mesma validação do update para
		// quando não há taskPage
		if (possuiTask()) {
			TaskController taskController = taskInstance.getTask().getTaskController();
			if (taskController == null) {
				return true;
			}
			List<?> list = taskController.getVariableAccesses();
			for (Object object : list) {
				VariableAccess var = (VariableAccess) object;
				if (var.isRequired() && var.getMappedName().split(":")[0].equals("FILE")
						&& getInstance().get(getFieldName(var.getVariableName())) == null) {
					String label = JbpmUtil.instance().getMessages()
							.get(taskInstance.getProcessInstance().getProcessDefinition().getName() + ":" + var.getVariableName());
					FacesMessages.instance().add("O arquivo do campo " + label + " é obrigatório");
					return false;
				}
			}
		}
		return true;
	}

	private void atualizarPaginaDeMovimentacao() {
		setTaskCompleted(true);
		// TODO: remover os efeitos colaterais do canClosePanel()
		if (canClosePanel() && isUsuarioExterno()) {
			redirectToAcessoExterno();
		}
	}

	private boolean isUsuarioExterno() {
		Authenticator authenticator = ComponentUtil.getComponent("authenticator");
		return authenticator.isUsuarioExterno();
	}

	private void redirectToAcessoExterno() {
		Redirect red = Redirect.instance();
		red.setViewId("/AcessoExterno/externo.seam?urlRetorno=" + urlRetornoAcessoExterno.toString());
		red.setConversationPropagationEnabled(false);
		red.execute();
	}

	private boolean canClosePanel() {
		if (this.currentTaskInstance == null) {
			setCanClosePanelVal(true);
			return true;
		} else if (canOpenTask()) {
			setTaskId(currentTaskInstance.getId());
			FacesMessages.instance().clear();
			return false;
		} else {
			setCanClosePanelVal(true);
			return true;
		}
	}

	private void finalizarTaskDoJbpm(String transition) {
		try {
			BusinessProcess.instance().endTask(transition);
			atualizarBam();
		} catch (JbpmException e) {
			LOG.error(".end()", e);
		}
	}

	private void atualizarBam() {
		ProcessoTarefa pt = processoTarefaManager.getByTaskInstance(taskInstance.getId());
		Date dtFinalizacao = taskInstance.getEnd();
		pt.setDataFim(dtFinalizacao);
		try {
			processoTarefaManager.update(pt);
			processoTarefaManager.updateTempoGasto(dtFinalizacao, pt);
		} catch (DAOException e) {
			LOG.error(".atualizarBam()", e);
		}
	}

	private void checkCurrentTask() {
		TaskInstance tempTask = org.jboss.seam.bpm.TaskInstance.instance();
		if (currentTaskInstance != null && (tempTask == null || tempTask.getId() != currentTaskInstance.getId())) {
			acusarUsuarioSemAcesso();
		}
	}

	public void removeUsuario(TaskInstance taskInstance) {
	    if (taskInstance != null) {
            try {
                taskInstanceManager.removeUsuario(taskInstance.getId());
                afterLiberarTarefa();
            } catch (DAOException e) {
                LOG.error("TaskInstanceHome.removeUsuario(taskInstance)", e);
            }
            }
	    }
	public void removeUsuario(Long idTaskInstance) {
	    try {
            taskInstanceManager.removeUsuario(idTaskInstance);
            afterLiberarTarefa();
        } catch (Exception e) {
            LOG.error("TaskInstanceHome.removeUsuario(idTaskInstance)", e);
        }
        }
	public void removeUsuario() {
	    if (BusinessProcess.instance().hasCurrentTask()) {
            try {
                taskInstanceManager.removeUsuario(BusinessProcess.instance().getTaskId());
                afterLiberarTarefa();
            } catch (DAOException e) {
                LOG.error(".removeUsuario() - ", e);
            }
        } else {
            FacesMessages.instance().add(infoxMessages.get("org.jboss.seam.TaskNotFound"));
        }
	}
	
	private void afterLiberarTarefa() {
        processoHandler.clear();
        FacesMessages.instance().clear();
        FacesMessages.instance().add("Tarefa liberada com sucesso.");
	}

	public void start(long taskId) {
		setTaskId(taskId);
		BusinessProcess.instance().startTask();
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
		BusinessProcess bp = BusinessProcess.instance();
		bp.setTaskId(taskId);
		taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
		if (taskInstance != null) {
			long processId = taskInstance.getProcessInstance().getId();
			bp.setProcessId(processId);
			updateTransitions();
			createInstance();
		}
	}
	
	public List<Transition> getTransitions() {
		validateAndUpdateTransitions();
		return getAvailableTransitionsFromDefinicaoDoFluxo();
	}

	private List<Transition> getAvailableTransitionsFromDefinicaoDoFluxo() {
		List<Transition> list = new ArrayList<Transition>();
		if (availableTransitions != null) {
			for (Transition transition : leavingTransitions) {
				// POG temporario devido a falha no JBPM de avaliar as
				// avaliablesTransitions
				if (availableTransitions.contains(transition) && !transition.isHidden()) {
					list.add(transition);
				}
			}
		}
		return list;
	}

	private void validateAndUpdateTransitions() {
		validateTaskId();
		if (hasAvailableTransitions()) {
			updateTransitions();
		}
	}

	private boolean hasAvailableTransitions() {
		return availableTransitions != null && availableTransitions.isEmpty() && taskInstance != null;
	}

	private void validateTaskId() {
		if (taskId == null) {
			setTaskId(org.jboss.seam.bpm.TaskInstance.instance().getId());
		}
	}

	public List<ModeloDocumento> getModeloItems(String variavel) {
		if (!variavel.endsWith("Modelo")) {
			return null;
		}
		if (variavel.contains("-")) {
			variavel = variavel.split("-")[0];
		}
		String listaModelos = (String) taskInstance.getContextInstance().getVariable(variavel);
		return modeloDocumentoManager.getModelosDocumentoInListaModelo(listaModelos);
	}

	public void assignModeloDocumento(String id) {
		if (modeloDocumento != null) {
			ExpressionResolverChain chain = ExpressionResolverChainBuilder.defaultExpressionResolverChain(processoEpaHome.getInstance().getIdProcesso(), getCurrentTaskInstance());
			String modelo = modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento, chain);
			variaveisDocumento.get(id).getDocumentoBin().setModeloDocumento(modelo);
		} else {
			variaveisDocumento.get(id).getDocumentoBin().setModeloDocumento("");
		}
	}

	public void updateTransitions() {
		availableTransitions = taskInstance.getAvailableTransitions();
		leavingTransitions = taskInstance.getTask().getTaskNode().getLeavingTransitions();
	}

	/**
	 * Refeita a combobox com as transições utilizando um f:selectItem pois o
	 * componente do Seam (s:convertEntity) estava dando problemas com as
	 * entidades do JBPM.
	 * 
	 * @return Lista das transições.
	 */
	public List<SelectItem> getTranstionsSelectItems() {
		List<SelectItem> selectList = new ArrayList<SelectItem>();
		for (Transition t : getTransitions()) {
			selectList.add(new SelectItem(t.getName(), t.getName()));
		}
		return selectList;
	}

	public void clear() {
		this.mapaDeVariaveis = null;
		this.taskInstance = null;
	}

	public ModeloDocumento getModeloDocumento() {
		createInstance();
		return modeloDocumento;
	}

	public void setModeloDocumento(ModeloDocumento modelo) {
		this.modeloDocumento = modelo;
	}

	public String getHomeName() {
		return NAME;
	}

	public String getName() {
		return name;
	}

	public void setName(String transition) {
		this.name = transition;
	}

	public static TaskInstanceHome instance() {
		return (TaskInstanceHome) Component.getInstance(TaskInstanceHome.NAME);
	}

	private String getFieldName(String name) {
		return name + "-" + taskInstance.getId();
	}

	public String getVariableName(String fieldName) {
		return fieldName.split("-")[0];
	}

	public void setUrlRetornoAcessoExterno(URL urlRetornoAcessoExterno) {
		this.urlRetornoAcessoExterno = urlRetornoAcessoExterno;
	}

	public boolean isCanClosePanelVal() {
		return canClosePanelVal;
	}

	public void setCanClosePanelVal(boolean canClosePanelVal) {
		this.canClosePanelVal = canClosePanelVal;
	}

	public boolean isTaskCompleted() {
		return taskCompleted;
	}

	public void setTaskCompleted(boolean taskCompleted) {
		this.taskCompleted = taskCompleted;
	}

	public Object getValueOfVariableFromTaskInstance(String variableName) {
		TaskController taskController = getCurrentTaskInstance().getTask().getTaskController();
		if (taskController != null) {
			List<VariableAccess> variables = taskController.getVariableAccesses();
			for (VariableAccess variable : variables) {
				if (variable.getVariableName().equals(variableName)) {
					return taskInstance.getVariable(variable.getMappedName());
				}
			}
		}
		return null;
	}

	public TipoDocumentoEnum[] getTipoDocumentoEnumValues() {
		return classificacaoDocumentoFacade.getTipoDocumentoEnumValues();
	}

	public TipoNumeracaoEnum[] getTipoNumeracaoEnumValues() {
		return classificacaoDocumentoFacade.getTipoNumeracaoEnumValues();
	}

	public VisibilidadeEnum[] getVisibilidadeEnumValues() {
		return classificacaoDocumentoFacade.getVisibilidadeEnumValues();
	}

	public TipoAssinaturaEnum[] getTipoAssinaturaEnumValues() {
		return classificacaoDocumentoFacade.getTipoAssinaturaEnumValues();
	}

	public List<ClassificacaoDocumento> getUseableClassificacaoDocumento(boolean isModelo, String nomeVariavel, Integer idFluxo) {
		return classificacaoDocumentoFacade.getUseableClassificacaoDocumento(isModelo, nomeVariavel, idFluxo);
	}

	public void setVariavelDocumentoToSign(String variavelDocumentoToSign) {
		if (variavelDocumentoToSign != null) {
			updateVariablesEditorContent();
			setDocumentoToSign(variaveisDocumento.get(variavelDocumentoToSign));
		}
	}

	public Documento getDocumentoToSign() {
		return documentoToSign;
	}

	public void setDocumentoToSign(Documento documentoToSign) {
		this.documentoToSign = documentoToSign;
	}

	public String getTokenToSign() {
		return tokenToSign;
	}

	public void setTokenToSign(String tokenToSign) {
		this.tokenToSign = tokenToSign;
	}

	public Map<String, Documento> getVariaveisDocumento() {
		return variaveisDocumento;
	}

	public void setVariaveisDocumento(Map<String, Documento> variaveisDocumento) {
		this.variaveisDocumento = variaveisDocumento;
	}

	public void removerDocumento(String variableFieldName) {
		Documento documento = getVariaveisDocumento().get(variableFieldName);
		if (documento != null) {
			if (documento.getId() != null) {
				try {
					fileVariableHandler.removeDocumento(documento, variableFieldName);
					variaveisDocumento.put(variableFieldName, new Documento());
					variaveisDocumento.get(variableFieldName).setClassificacaoDocumento(documento.getClassificacaoDocumento());
				} catch (DAOException e) {
					LOG.error("", e);
					actionMessagesService.handleDAOException(e);
					documentoManager.refresh(documento);
				}
			} else {
				documento.setDocumentoBin(null);
			}
		}
	}
}
