package br.com.infox.ibpm.task.home;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.SystemException;

import br.com.infox.epp.painel.PainelUsuarioController;
import br.com.infox.epp.painel.TaskBean;
import br.com.infox.epp.processo.entity.Processo;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ManagedJbpmContext;
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

import com.google.common.base.Strings;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.documento.domain.RegraAssinaturaService;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.manager.VariavelManager;
import br.com.infox.epp.documento.modelo.ModeloDocumentoSearch;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.documento.numeration.UltimoNumeroDocumentoManager;
import br.com.infox.epp.processo.handler.ProcessoHandler;
import br.com.infox.epp.processo.home.ProcessoEpaHome;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.action.TaskPageAction;
import br.com.infox.ibpm.task.dao.TaskConteudoDAO;
import br.com.infox.ibpm.task.entity.TaskConteudo;
import br.com.infox.ibpm.task.manager.TaskInstanceManager;
import br.com.infox.ibpm.task.view.Form;
import br.com.infox.ibpm.task.view.FormField;
import br.com.infox.ibpm.task.view.TaskInstanceForm;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.variable.VariableEditorModeloHandler;
import br.com.infox.ibpm.variable.VariableHandler;
import br.com.infox.ibpm.variable.entity.VariableInfo;
import br.com.infox.ibpm.variable.file.FileVariableHandler;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.context.ContextFacade;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.exception.BusinessRollbackException;
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
    private static final String TASK_INSTANCE_FORM_ID = "movimentarTabPanel:taskInstanceForm";
    private static final String VARIABLE_INSTANCE_RECUPERAR_PROCESSO = "recuperarProcesso";
    private static final String VARIABLE_INSTANCE_PERFIL_VISUALIZAR_RECUPERAR = "perfilVisualizarRecuperar";

    @Inject
    private ProcessoManager processoManager;
    @Inject
    private ProcessoTarefaManager processoTarefaManager;
    @Inject
    private TaskInstanceManager taskInstanceManager;
    @Inject
    private ModeloDocumentoManager modeloDocumentoManager;
    @Inject
    private AssinaturaDocumentoService assinaturaDocumentoService;
    @In
    private VariableTypeResolver variableTypeResolver;
    @Inject
    private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
    @Inject
    private DocumentoManager documentoManager;
	@Inject
	private UltimoNumeroDocumentoManager ultimoNumeroDocumentoManager;
	@Inject
	private VariavelManager variavelManager;
	@Inject
    private PastaManager pastaManager;
    @Inject
    private DocumentoBinManager documentoBinManager;
    @Inject
    private InfoxMessages infoxMessages;
    @Inject
    private AssinadorService assinadorService;
    @Inject
    private FluxoManager fluxoManager;
    @In
    private PathResolver pathResolver;
    @In
    private ProcessoEpaHome processoEpaHome;
    @In
    private ProcessoHandler processoHandler;

    @Inject
    private SituacaoProcessoDAO situacaoProcessoDAO;
    @Inject
    private FileVariableHandler fileVariableHandler;
    @Inject
    private ModeloDocumentoSearch modeloDocumentoSearch;
    @Inject
    private ActionMessagesService actionMessagesService;
    @Inject
    private RegraAssinaturaService regraAssinaturaService;

    private TaskInstance taskInstance;
    private Map<String, Object> mapaDeVariaveis;
    private Long taskId;
    private List<Transition> availableTransitions;
    private List<Transition> leavingTransitions;
    private String varName;
    private String name;
    private TaskInstance currentTaskInstance;
    private Map<String, Documento> variaveisDocumento;
    private Documento documentoToSign;
    private String tokenToSign;

    private Map<String, VariableAccess> mapVarAccess;

    private URL urlRetornoAcessoExterno;

    private boolean canClosePanelVal;
    private boolean taskCompleted;
    private boolean movimentarProcesso = false;

    @Inject
    private PainelUsuarioController painelUsuarioController;

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
        mapVarAccess = new HashMap<>();
        if (taskController != null) {
            List<VariableAccess> list = taskController.getVariableAccesses();
            for (VariableAccess variableAccess : list) {
                mapVarAccess.put(variableAccess.getVariableName(), variableAccess);
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
                loadClassificacaoDocumentoDefault(variableAccess.getVariableName(), documento);
                if (variableRetriever.isEditor()) {
                    DocumentoBin documentoBin = new DocumentoBin();
                    documento.setDocumentoBin(documentoBin);
                    if(variableAccess.getConfiguration() != null) {
                        List<String> codigosModelos = VariableEditorModeloHandler.fromJson(variableAccess.getConfiguration()).getCodigosModeloDocumento();
                        if (codigosModelos != null && codigosModelos.size() == 1) {
                            ModeloDocumento modelo = modeloDocumentoSearch.getModeloDocumentoByCodigo(codigosModelos.get(0));
                            documentoBin.setModeloDocumento(evaluateModeloDocumento(modelo, documento));
                            documento.setDescricao(modelo.getTituloModeloDocumento());
                        }
                    }
                }
            }
            if (variableAccess.isWritable())
                variaveisDocumento.put(getFieldName(variableRetriever.getName()), documento);
            else
                variaveisDocumento.put(variableRetriever.getName(), documento);
        }
    }

    public Integer getIdfluxo(TaskInstance taskInstance){
        Integer idFluxo = null;
        String nomeFluxo = taskInstance.getProcessInstance().getProcessDefinition().getName();
        Fluxo fluxoByDescricao = fluxoManager.getFluxoByDescricao(nomeFluxo);
        if(fluxoByDescricao != null)
            idFluxo = fluxoByDescricao.getIdFluxo();
        else
            idFluxo = processoEpaHome.getInstance().getNaturezaCategoriaFluxo().getFluxo().getIdFluxo();
        return idFluxo;
    }

    private void loadClassificacaoDocumentoDefault(String variableName, Documento documento) {
        List<ClassificacaoDocumento> classificacoes = getUseableClassificacaoDocumento(variableName);
        if (classificacoes != null && classificacoes.size() == 1) {
            documento.setClassificacaoDocumento(classificacoes.get(0));
        }
    }
    public void setModeloReadonly(FormField formField){
        setModeloReadonly(formField.getId().split("-")[0]);
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

    public Map<String, Object> getInstance() {
        createInstance();
        return mapaDeVariaveis;
    }

    public boolean update() {
        return update(false);
    }

    private boolean update(boolean validateForm) {
        prepareForUpdate();
        if (possuiTask()) {
            variableTypeResolver.setProcessInstance(taskInstance.getProcessInstance());
            TaskController taskController = taskInstance.getTask().getTaskController();
            TaskPageAction taskPageAction = Beans.getReference(TaskPageAction.class);
            if (taskController != null) {
                if (!taskPageAction.getHasTaskPage(getCurrentTaskInstance())) {
                    try {
                        if (validateForm && !validateRequiredVariables()) {
                            return false;
                        }
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

    private boolean validateRequiredVariables() {
        if (taskInstance.getTask().getTaskController() == null || taskInstance.getTask().getTaskController().getVariableAccesses() == null) {
            return true;
        }
        List<VariableAccess> variables = taskInstance.getTask().getTaskController().getVariableAccesses();
        List<String> failedInputIds = new ArrayList<>();
        for (VariableAccess variable : variables) {
            if (variable.isWritable() && variable.isRequired()) {
                Object value = mapaDeVariaveis.get(getFieldName(variable.getVariableName()));
                VariableInfo variableInfo = variableTypeResolver.getVariableInfoMap().get(variable.getVariableName());
                String fieldName = getFieldName(variable.getVariableName());

                if (variableInfo.getVariableType() == VariableType.EDITOR) {
                    Documento documento = variaveisDocumento.get(fieldName);
                    if (documento.getClassificacaoDocumento() == null) {
                        failedInputIds.add(String.format("%s:%sSubView:%stipoProcessoDocumentoDecoration:%stipoProcessoDocumento", TASK_INSTANCE_FORM_ID, fieldName, fieldName, fieldName));
                    }
                    if (documento.getDocumentoBin() == null || Strings.isNullOrEmpty(documento.getDocumentoBin().getModeloDocumento())) {
                        failedInputIds.add(String.format("%s:%sSubView:%sEditorDecoration:%sEditor", TASK_INSTANCE_FORM_ID, fieldName, fieldName, fieldName));
                    }
                } else if (variableInfo.getVariableType() == VariableType.FILE) {
                    Documento documento = variaveisDocumento.get(fieldName);
                    if (documento.getClassificacaoDocumento() == null) {
                        failedInputIds.add(String.format("%s:%sSubView:%stipoProcessoDocumentoDecoration:%stipoProcessoDocumento", TASK_INSTANCE_FORM_ID, fieldName, fieldName, fieldName));
                    }
                    if (documento.getDocumentoBin() == null) {
                        failedInputIds.add(String.format("%s:%sSubView:%sDecoration:%sHidden", TASK_INSTANCE_FORM_ID, fieldName, fieldName, fieldName));
                    }
                } else if (variableInfo.getVariableType() == VariableType.ENUMERATION_MULTIPLE && ((String[]) value).length == 0) {
                    failedInputIds.add(String.format("%s:%s:%sInput", TASK_INSTANCE_FORM_ID, fieldName, fieldName));
                } else if (variableInfo.getVariableType() == VariableType.FRAME || variableInfo.getVariableType() == VariableType.TASK_PAGE){
                    continue;
                } else if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                    if(variableInfo.getVariableType() == VariableType.MONETARY || variableInfo.getVariableType() == VariableType.DECIMAL) {
                        failedInputIds.add(String.format("%s:%sDecoration:%sInput", TASK_INSTANCE_FORM_ID, fieldName, fieldName));
                    } else {
                        failedInputIds.add(String.format("%s:%sDecoration:%s", TASK_INSTANCE_FORM_ID, fieldName, fieldName));
                    }
                }
            }
        }

        for (String failedInputId : failedInputIds) {
            FacesContext.getCurrentInstance().addMessage(
                    failedInputId,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, infoxMessages.get("beanValidation.notNull"), null));
        }

        return failedInputIds.isEmpty();
    }

    private void prepareForUpdate() {
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
                updateVariableEditor(documento, variableAccess);
                variableResolver.assignValueFromMapaDeVariaveis(mapaDeVariaveis);
                variableResolver.resolve();
            } else if (variableResolver.isFile()) {
                retrieveVariable(variableAccess);
            }
        }
    }

    private void updateVariableEditor(Documento documento, VariableAccess variableAccess) throws DAOException {
        if (documento.getId() != null && documento.getClassificacaoDocumento() != null) {
            documentoBinManager.update(documento.getDocumentoBin());
            documentoManager.update(documento);
        } else if (documento.getId() != null && documento.getClassificacaoDocumento() == null) {
            documentoManager.remove(documento);
            documentoBinManager.remove(documento.getDocumentoBin());
            documento = new Documento();
            documento.setDocumentoBin(new DocumentoBin());
        } else {
            if (documento.getClassificacaoDocumento() != null
                    && documento.getDocumentoBin() != null
                    && !StringUtil.isEmpty(documento.getDocumentoBin().getModeloDocumento())
            ){
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
        Pasta defaultFolder = pastaManager.getDefaultFolder(processoEpaHome.getInstance());
        if (defaultFolder == null) {
            throw new BusinessRollbackException(infoxMessages.get("documento.erro.processSemPasta"));
        }
        if (documento.getPasta() == null)
            documento.setPasta(defaultFolder);
        documento.setNumeroSequencialDocumento(documentoManager.getNextNumeracao(documento));
        documento.setIdJbpmTask(getCurrentTaskInstance().getId());
        if (StringUtils.isEmpty(documento.getDescricao())) {
            String descricao = variableAccess.getLabel();
            documento.setDescricao(descricao == null ? "-" : descricao);
        }
        documentoManager.persist(documento);
    }

    public Boolean checkAccessSemException() {
        Boolean hasAccess = false;
        try {
            hasAccess = checkAccess();
        } catch (ApplicationException e) {
            LOG.error(e);
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
        TaskInstance taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
        if (taskInstance == null){
            return false;
        }
        variableTypeResolver.setProcessInstance(taskInstance.getProcessInstance());
        VariableInfo variableInfo = variableTypeResolver.getVariableInfoMap().get(variableName);
        if (variableInfo == null){
            return false;
        }
        Integer idDocumento = (Integer) taskInstance.getVariable(variableInfo.getMappedName());
        if (idDocumento != null) {
            Documento documento = documentoManager.find(idDocumento);
            Papel papelAtual = Authenticator.getPapelAtual();
            if (documento != null) {
                PessoaFisica pessoaFisica = Authenticator.getUsuarioLogado().getPessoaFisica();
                return pessoaFisica != null
                        && regraAssinaturaService.permiteAssinaturaPor(documento, pessoaFisica, papelAtual);
            }
        }
        return false;
    }

    public boolean isPdf(String variableName){
        Documento documento = getDocumentoFromVariableName(variableName);
        return "pdf".equalsIgnoreCase(documento.getDocumentoBin().getExtensao()) || StringUtils.isEmpty(documento.getDocumentoBin().getExtensao());
    }

    public Documento getDocumentoFromVariableName(String variableName) {
        Integer idDocumento = (Integer) org.jboss.seam.bpm.TaskInstance.instance().getVariable("FILE:" + variableName);
        return documentoManager.find(idDocumento);
    }

    public void assinarDocumento() {
        if (documentoToSign == null) {
            FacesMessages.instance().add("Sem documento para assinar");
            return;
        }
        try {
            assinadorService.assinarToken(tokenToSign, Authenticator.getUsuarioPerfilAtual());
            for (String variavel : variaveisDocumento.keySet()) {
                if (documentoToSign.equals(variaveisDocumento.get(variavel))) {
                    setModeloReadonly(variavel.split("-")[0]);
                    break;
                }
            }
        } catch (AssinaturaException | DAOException e) {
            FacesMessages.instance().add(e.getMessage());
            LOG.error("assinarDocumento()", e);
        } finally {
            setDocumentoToSign(null);
            setVariavelDocumentoToSign(null);
            setTokenToSign(null);
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
            throw new ApplicationException(ApplicationException.createMessage(action + infoxMessages.get(ex.getLocalizedMessage()),
                    "setCurrentTaskInstance()", "TaskInstanceHome", "BPM"), ex);
        }
    }

    public String end(String transition) {
        if (checkAccess()) {
            checkCurrentTask();
            boolean validateForm = isTransitionValidateForm(transition);
            if (!update(validateForm)) {
                return null;
            }
            if (validateForm && (!validFileUpload() || !validEditor())) {
                return null;
            }
            if (validateForm && !validarAssinaturaDocumentosAoMovimentar()) {
                return null;
            }
            this.currentTaskInstance = null;
            try {
                finalizarTaskDoJbpm(transition);
                // Flush para que a consulta do canOpenTask consiga ver o pooled
                // actor que o jbpm criou
                // no TaskInstance#create, caso contrário, o epp achará que o
                // usuário não pode ver a tarefa seguinte,
                // mesmo que possa
                if (Transaction.instance().isActive()) {
                    JbpmUtil.getJbpmSession().flush();
                }
                mapaDeVariaveis = null;
                atualizarPaginaDeMovimentacao();
            } catch (HibernateException | SystemException | JbpmException | DAOException e) {
                LOG.error("", e);
                actionMessagesService.handleGenericException(e);
                rollbackActions();
                try {
                    if (Transaction.instance().isActive()) {
                        Transaction.instance().setRollbackOnly();
                    }
                } catch (IllegalStateException | SystemException e1) {
                    LOG.error("", e1);
                    actionMessagesService.handleGenericException(e1);
                }
            }
        }
        return null;
    }

    private void rollbackActions() {
        Session session = ManagedJbpmContext.instance().getSession();
        session.refresh(taskInstance.getToken());
        session.refresh(taskInstance.getProcessInstance().getTaskMgmtInstance());
        setCurrentTaskInstance(taskInstance);
    }

    private boolean validEditor() {
        if (possuiTask()) {
            TaskController taskController = taskInstance.getTask().getTaskController();
            if (taskController == null) {
                return true;
            }
            List<?> list = taskController.getVariableAccesses();
            for (Object object : list) {
                VariableAccess var = (VariableAccess) object;
                if (var.isRequired() && var.getMappedName().split(":")[0].equals("EDITOR")
                        && getInstance().get(getFieldName(var.getVariableName())) == null) {
                    String label = var.getLabel();
                    FacesMessages.instance().add("O editor do campo " + label + " é obrigatório");
                    return false;
                }
            }
        }
        return true;
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
                if ( documento == null ) continue;
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
                if (var.isRequired() && VariableType.FILE.equals(VariableType.valueOf(var.getType()))
                        && getInstance().get(getFieldName(var.getVariableName())) == null) {
                    String label = var.getLabel();
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

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("endTransisiton", true);
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
        BusinessProcess.instance().endTask(transition);
        atualizarBam();
    }

    private void atualizarBam() {
        ProcessoTarefa pt = processoTarefaManager.getByTaskInstance(taskInstance.getId());
        Date dtFinalizacao = taskInstance.getEnd();
        pt.setDataFim(dtFinalizacao);
        processoTarefaManager.update(pt);
        processoTarefaManager.updateTempoGasto(dtFinalizacao, pt);
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
                if (availableTransitions.contains(transition) && !transition.getConfiguration().isHidden()) {
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

    public boolean possuiModelo(String editorId) {
        VariableAccess varAccess = mapVarAccess.get(getVariableName(editorId));
        return varAccess.getConfiguration() != null && !varAccess.getConfiguration().isEmpty() &&
                VariableEditorModeloHandler.fromJson(varAccess.getConfiguration()).getCodigosModeloDocumento() != null &&
                !VariableEditorModeloHandler.fromJson(varAccess.getConfiguration()).getCodigosModeloDocumento().isEmpty();
    }

    public List<String> getCodigosModelos(String editorId) {
        String variableName = getVariableName(editorId);
        VariableAccess var = mapVarAccess.get(variableName);

        if(var.getConfiguration() == null) {
            return null;
        }

        return VariableEditorModeloHandler.fromJson(var.getConfiguration()).getCodigosModeloDocumento();
    }

    public List<ModeloDocumento> getModeloItems(String editorId) {
        return modeloDocumentoSearch.getModeloDocumentoListByListCodigos(getCodigosModelos(editorId));
    }

    private String evaluateModeloDocumento(ModeloDocumento modelo, Documento documento) {
		Map<String, String> variaveis = null;
        if (documento != null) {
        	if (documento.getClassificacaoDocumento().getTipoModeloDocumento() != null && 
        			documento.getClassificacaoDocumento().getTipoModeloDocumento().getNumeracaoAutomatica().equals(Boolean.TRUE) &&
        			documento.getNumeroDocumento() == null && 
        			this.movimentarProcesso) {
        		documento.setNumeroDocumento(ultimoNumeroDocumentoManager.getNextNumeroDocumento(documento.getClassificacaoDocumento().getTipoModeloDocumento(), LocalDate.now().getYear()));	
        	}
			if (documento.getNumeroDocumento() != null) {
	    		Variavel variavelNumDoc = variavelManager.getVariavelNumDocumento();
				if (variavelNumDoc != null) {
					variaveis = new HashMap<String, String>();
					variaveis.put(variavelNumDoc.getValorVariavel(), String.valueOf(documento.getNumeroDocumento()));
				}
    		}
		}
        ExpressionResolverChain chain = ExpressionResolverChainBuilder.defaultExpressionResolverChain(processoEpaHome.getInstance().getIdProcesso(), getCurrentTaskInstance());
        return modeloDocumentoManager.evaluateModeloDocumento(modelo, chain, variaveis);
    }

    private String evaluateModeloDocumento(ModeloDocumento modelo) {
        return evaluateModeloDocumento(modelo, null);
    }

    public void assignModeloDocumento(String id, ModeloDocumento modeloDocumento) {
        if (modeloDocumento != null) {
            String modelo = evaluateModeloDocumento(modeloDocumento, variaveisDocumento.get(id));
            variaveisDocumento.get(id).getDocumentoBin().setModeloDocumento(modelo);
            String descricao = modeloDocumento.getTituloModeloDocumento();
            variaveisDocumento.get(id).setDescricao(descricao);
        } else {
            variaveisDocumento.get(id).getDocumentoBin().setModeloDocumento("");
            variaveisDocumento.get(id).setDescricao("");

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
        TaskInstance taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
        TaskController taskController = taskInstance.getTask().getTaskController();
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

    public List<ClassificacaoDocumento> getUseableClassificacaoDocumento(String variableName) {
        VariableAccess var = mapVarAccess.get(variableName);
        boolean isModelo = VariableType.EDITOR.equals(VariableType.valueOf(var.getType()));
        String configuration = var.getConfiguration();
        List<String> listCodigos = null;
        if (configuration != null && !configuration.isEmpty()) {
            listCodigos = VariableEditorModeloHandler.fromJson(configuration).getCodigosClassificacaoDocumento();
        }
        return classificacaoDocumentoFacade.getUseableClassificacaoDocumentoVariavel(listCodigos, isModelo);
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

    private Transition getTransition(String name) {
        for (Transition transition : getTransitions()) {
            if (transition.getName().equals(name)) {
                return transition;
            }
        }
        return null;
    }

    public boolean isTransitionValidateForm(String transition) {
        return getTransition(transition).getConfiguration().isValidateForm();
    }

    public boolean estaMovimentandoProcesso(boolean param) {
    	movimentarProcesso = param;
        if(mapaDeVariaveis == null){
            createInstance();
        }else{
            retrieveVariables();
        }
    	return movimentarProcesso;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void recuperarProcesso(Object processo) {
        try {
            if(processo instanceof Processo){

            List<ProcessoTarefa> doisUltimosProcessosTarefa = processoTarefaManager.getDoisUltimosProcessosTarefa((Processo) processo);

            if (doisUltimosProcessosTarefa == null || doisUltimosProcessosTarefa.isEmpty() || doisUltimosProcessosTarefa.size() == 1)
                throw new RuntimeException("Processo Tarefa não encontrado nos parametros corretos para retornar tarefa");

            ProcessoTarefa processoTarefa = doisUltimosProcessosTarefa.get(1);

            TaskInstance taskInstanceOpen = taskInstanceManager.getTaskInstanceOpen((Processo) processo);
            setTaskId(taskInstanceOpen.getTask().getId());
            processoTarefaManager.finalizarInstanciaTarefa(taskInstanceOpen, processoTarefa.getTarefa().getTarefa());
            }

        } catch (Exception e) {
            throw new ApplicationException("Erro ao recuperar tarefa.");
        }
    }

    public boolean podeRecuperaProcesso(Object processo){
        try{
            if(processo instanceof Processo){
                TaskInstance taskInstanceOpen = taskInstanceManager.getTaskInstanceOpen((Processo) processo);
                Map<String, VariableInstance> variableMap = taskInstanceOpen.getVariableInstances();
                if (variableMap != null) {
                    VariableInstance recuperarProcesso = variableMap.get(VARIABLE_INSTANCE_RECUPERAR_PROCESSO);
                    VariableInstance perfilRecuperarProcesso = variableMap.get(VARIABLE_INSTANCE_PERFIL_VISUALIZAR_RECUPERAR);
                    
                    if((recuperarProcesso == null || perfilRecuperarProcesso == null)){
                        return false;
                    }

                    List<String> perfis = Arrays.asList(perfilRecuperarProcesso.getValue().toString().split(","));

                    String codigoLocalizacao = Authenticator.getLocalizacaoAtual().getCodigo();
                    String codigoPerfil = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getCodigo();
                    String concat = codigoLocalizacao.concat("/").concat(codigoPerfil);

                    return  Boolean.valueOf(recuperarProcesso.getValue().toString()).booleanValue() == true
                            && perfis.stream().anyMatch(p -> p.trim().equals(concat.trim()));
                }
            }
                return false;
        }catch (Exception e){
            return false;
        }
    }
}
