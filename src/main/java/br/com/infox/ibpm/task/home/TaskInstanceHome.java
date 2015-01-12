package br.com.infox.ibpm.task.home;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jbpm.JbpmException;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.messages.Messages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.documento.type.JbpmExpressionResolver;
import br.com.infox.epp.documento.type.SeamExpressionResolver;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.DadosDocumentoAssinavel;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
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
import br.com.infox.ibpm.transition.TransitionHandler;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.util.UserHandler;
import br.com.infox.jsf.function.ElFunctions;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.context.ContextFacade;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.util.ComponentUtil;
import br.com.itx.component.AbstractHome;

@Name(TaskInstanceHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class TaskInstanceHome implements Serializable {

    private static final String MSG_USUARIO_SEM_ACESSO = "Você não pode mais efetuar transações "
            + "neste registro, verifique se ele não foi movimentado";
    private static final String UPDATED_VAR_NAME = "isTaskHomeUpdated";
    private static final LogProvider LOG = Logging.getLogProvider(TaskInstanceHome.class);
    private static final long serialVersionUID = 1L;
    public static final String NAME = "taskInstanceHome";

    @In
    private SituacaoProcessoDAO situacaoProcessoDAO;
    @In
    private ProcessoManager processoManager;
    @In
    private ProcessoTarefaManager processoTarefaManager;
    @In
    private TaskInstanceManager taskInstanceManager;
    @In
    private ModeloDocumentoManager modeloDocumentoManager;
    @In
    private UserHandler userHandler;
    @In
    private AssinaturaDocumentoService assinaturaDocumentoService;
    @In
    private VariableTypeResolver variableTypeResolver;
    @In(create=true)
    private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
    @In
    private DocumentoManager documentoManager;
    
    private TaskInstance taskInstance;
    private Map<String, Object> mapaDeVariaveis;
    private String variavelDocumento;
    private Long taskId;
    private List<Transition> availableTransitions;
    private List<Transition> leavingTransitions;
    private ModeloDocumento modeloDocumento;
    private String varName;
    private String name;
    private Boolean assinado = Boolean.FALSE;
    private TaskInstance currentTaskInstance;
    private Map<String, DadosDocumentoAssinavel> documentosAssinaveis;
    private Map<String, ClassificacaoDocumento> classificacoesVariaveisUpload;

    private URL urlRetornoAcessoExterno;
    private String documentoAAssinar;

    private boolean canClosePanelVal;
    private boolean taskCompleted;

    public void createInstance() {
        taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
        if (mapaDeVariaveis == null && taskInstance != null) {
            variableTypeResolver.setProcessInstance(taskInstance.getProcessInstance());
            mapaDeVariaveis = new HashMap<String, Object>();
            documentosAssinaveis = new HashMap<>();
            classificacoesVariaveisUpload = new HashMap<>();
            retrieveVariables();
        }
    }

    @SuppressWarnings(UNCHECKED)
    private void retrieveVariables() {
        TaskController taskController = taskInstance.getTask()
                .getTaskController();
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
        final TaskVariableRetriever variableRetriever = new TaskVariableRetriever(variableAccess, taskInstance);
        variableRetriever.retrieveVariableContent();
        mapaDeVariaveis.put(getFieldName(variableRetriever.getName()), variableRetriever.getVariable());
        if (variableRetriever.isEditor()) {
            DadosDocumentoAssinavel dados = new DadosDocumentoAssinavel();
            Integer id = (Integer) taskInstance.getVariable(variableRetriever.getMappedName());
            if (id != null) {
                DocumentoManager documentoManager = ComponentUtil.getComponent(DocumentoManager.NAME);
                Documento pd = documentoManager.find(id);
                if (pd != null) {
                    dados.setIdDocumento(id);
                    dados.setClassificacao(pd.getClassificacaoDocumento());
                    dados.setMinuta(pd.getDocumentoBin().isMinuta());
                }
            }
            List<ClassificacaoDocumento> useableTipoProcessoDocumento = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(true, getVariableName(variableRetriever.getName()), ((Processo)ProcessoEpaHome.instance().getInstance()).getNaturezaCategoriaFluxo().getFluxo().getIdFluxo());
            if (useableTipoProcessoDocumento != null && useableTipoProcessoDocumento.size()>0 && dados.getClassificacao() == null){
                dados.setClassificacao(useableTipoProcessoDocumento.get(0));
            }
            documentosAssinaveis.put(getFieldName(variableRetriever.getName()), dados);
        } else if (variableRetriever.isVariableType(VariableType.FILE)) {
            Integer id = (Integer) taskInstance.getVariable(variableRetriever.getMappedName());
            if (id != null) {
                DocumentoManager documentoManger = ComponentUtil.getComponent(DocumentoManager.NAME);
                Documento pd = documentoManger.find(id);
                classificacoesVariaveisUpload.put(getFieldName(variableRetriever.getName()), pd.getClassificacaoDocumento());
            } else {
                classificacoesVariaveisUpload.put(getFieldName(variableRetriever.getName()), null);
            }
        }
        setModeloWhenExists(variableRetriever);
    }

    private void setModeloWhenExists(TaskVariableRetriever variableRetriever) {
        String modelo = getModeloFromProcessInstance(variableRetriever.getName());
        if (modelo != null) {
            variavelDocumento = variableRetriever.getName();
            if (!variableRetriever.hasVariable()) {
                setModeloDocumento(getModeloDocumentoFromModelo(modelo));
            }
        }
    }

    private String getModeloFromProcessInstance(String variableName) {
        return (String) ProcessInstance.instance().getContextInstance()
                .getVariable(variableName + "Modelo");
    }

    private ModeloDocumento getModeloDocumentoFromModelo(String modelo) {
        String s = modelo.split(",")[0].trim();
        return modeloDocumentoManager.find(Integer.parseInt(s));
    }

    public Map<String, Object> getInstance() {
        createInstance();
        return mapaDeVariaveis;
    }

    // Método que será chamado pelo botão "Assinar Digitalmente"
    public void assinarDocumento(String idEditor) {
        documentoAAssinar = idEditor;
        this.update();
    }

    public boolean update() {
        prepareForUpdate();
        if (possuiTask()) {
            TaskController taskController = taskInstance.getTask()
                    .getTaskController();
            TaskPageAction taskPageAction = ComponentUtil
                    .getComponent(TaskPageAction.NAME);
            if (taskController != null) {
                if (!taskPageAction.getHasTaskPage()) {
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
        Contexts.getBusinessProcessContext().flush();
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

    @SuppressWarnings(UNCHECKED)
    private void updateVariables(TaskController taskController) {
        List<VariableAccess> list = taskController.getVariableAccesses();
        for (VariableAccess variableAccess : list) {
            updateVariable(variableAccess);
        }
    }

    private void updateVariable(VariableAccess variableAccess) {
        boolean documentoCorreto = false;
        String fieldName = getFieldName(variableAccess.getMappedName().split(":")[1]);
        if (documentoAAssinar != null && fieldName.equals(documentoAAssinar)) {
            documentoCorreto = true;
        }
        TaskVariableResolver variableResolver = new TaskVariableResolver(
                variableAccess, taskInstance, documentoCorreto);

        if (variableAccess.isWritable()) {
            if (variableResolver.isEditor() && variableAccess.isReadable()) {
            	DadosDocumentoAssinavel dados = documentosAssinaveis.get(fieldName);
                ProcessoEpaHome processoEpaHome = ProcessoEpaHome.instance();
                processoEpaHome.setClassificacaoDocumento(dados.getClassificacao());
                processoEpaHome.setSignature(dados.getSignature());
                processoEpaHome.setCertChain(dados.getCertChain());
            }
            variableResolver.assignValueFromMapaDeVariaveis(mapaDeVariaveis);
            variableResolver.resolve();
            if (variableResolver.isEditor()) {
            	DadosDocumentoAssinavel dados = documentosAssinaveis.get(fieldName);
            	Documento documento = documentoManager.find(variableResolver.getValue());
            	documento.getDocumentoBin().setMinuta(dados.isMinuta());
            	try {
					documentoManager.update(documento);
				} catch (DAOException e) {
					throw new BusinessException("Erro ao atualizar documento", e);
				}
                if (documentoCorreto) {
                    if (!variableResolver.isEditorAssinado()) {
                        assinado = false;
                    } else {
                        assinado = assinado || documentoAAssinar != null;
                        dados.setIdDocumento(documento.getId());
                    }
                    documentoAAssinar = null;
                } else {
                    assinado = assinado || documentoAAssinar != null;
                }
            } else if (variableResolver.getType() == VariableType.FILE) {
                Contexts.getBusinessProcessContext().flush();
                retrieveVariable(variableAccess);
            }
        }
    }

    private Boolean checkAccess() {
        int idProcesso = ProcessoEpaHome.instance().getInstance().getIdProcesso();
        Integer idUsuarioLogin = Authenticator.getUsuarioLogado().getIdUsuarioLogin();
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
    	MetadadoProcesso metadadoProcesso = ProcessoEpaHome.instance().getInstance().getMetadado(EppMetadadoProvider.TIPO_PROCESSO);
    	TipoProcesso tipoProcesso = (metadadoProcesso != null ? metadadoProcesso.<TipoProcesso>getValue() : null);
        return situacaoProcessoDAO.canOpenTask(currentTaskInstance.getId(), tipoProcesso);
    }

    private TaskInstance getCurrentTaskInstance() {
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
        TaskConteudoDAO taskConteudoDAO = ComponentUtil
                .getComponent(TaskConteudoDAO.NAME);
        TaskConteudo taskConteudo = taskConteudoDAO.find(getTaskId());
        int idProcesso = ProcessoEpaHome.instance().getInstance().getIdProcesso();
        if (taskConteudo != null) {
            try {
                taskConteudoDAO.update(taskConteudo);
            } catch (DAOException e) {
                LOG.error(
                        "Não foi possível reindexar o conteúdo da TaskInstance "
                                + getTaskId(), e);
            }
        } else {
            taskConteudo = new TaskConteudo();
            taskConteudo.setNumeroProcesso(idProcesso);
            taskConteudo.setIdTaskInstance(getTaskId());
            try {
                taskConteudoDAO.persist(taskConteudo);
            } catch (DAOException e) {
                LOG.error(
                        "Não foi possível indexar o conteúdo da TaskInstance "
                                + getTaskId(), e);
            }
        }
    }

    @Observer(Event.EVENTTYPE_TASK_CREATE)
    public void setCurrentTaskInstance(ExecutionContext context) {
    	setCurrentTaskInstance(context.getTaskInstance());
    }
    
    public void setCurrentTaskInstance(TaskInstance taskInstance) {
        try {
            this.currentTaskInstance = taskInstance;
        } catch (Exception ex) {
            String action = "atribuir a taskInstance corrente ao currentTaskInstance: ";
            LOG.warn(action, ex);
            throw new ApplicationException(ApplicationException.createMessage(
                    action + ex.getLocalizedMessage(),
                    "setCurrentTaskInstance()", "TaskInstanceHome", "BPM"), ex);
        }
    }

    public String end(String transition) {
        if (checkAccess()) {
            checkCurrentTask();
            ProcessoEpaHome processoHome = ComponentUtil
                    .getComponent(ProcessoEpaHome.NAME);

            if (!update()) {
                return null;
            }
            if (!validFileUpload()) {
                return null;
            }
            limparEstado(processoHome);
            finalizarTaskDoJbpm(transition);
            atualizarPaginaDeMovimentacao();
        }
        return null;
    }

    private boolean validFileUpload() {
        // TODO verificar se é necessária a mesma validação do update para
        // quando não há taskPage
        if (possuiTask()) {
            TaskController taskController = taskInstance.getTask()
                    .getTaskController();
            if (taskController == null) {
                return true;
            }
            List<?> list = taskController.getVariableAccesses();
            for (Object object : list) {
                VariableAccess var = (VariableAccess) object;
                if (var.isRequired()
                        && var.getMappedName().split(":")[0].equals("FILE")
                        && getInstance().get(
                                getFieldName(var.getVariableName())) == null) {
                    String label = JbpmUtil
                            .instance()
                            .getMessages()
                            .get(taskInstance.getProcessInstance()
                                    .getProcessDefinition().getName()
                                    + ":" + var.getVariableName());
                    FacesMessages.instance().add(
                            "O arquivo do campo " + label + " é obrigatório");
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
        Authenticator authenticator = ComponentUtil
                .getComponent("authenticator");
        return authenticator.isUsuarioExterno();
    }

    private void redirectToAcessoExterno() {
        Redirect red = Redirect.instance();
        red.setViewId("/AcessoExterno/externo.seam?urlRetorno="
                + urlRetornoAcessoExterno.toString());
        red.setConversationPropagationEnabled(false);
        red.execute();
    }

    private boolean canClosePanel() {
        if (this.currentTaskInstance == null) {
            setCanClosePanelVal(true);
            return true;
        } else if ( canOpenTask() ) {
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

    private void limparEstado(ProcessoEpaHome processoEpaHome) {
        this.currentTaskInstance = null;
        processoEpaHome.setIdDocumento(null);
        processoEpaHome.setCertChain(null);
        processoEpaHome.setSignature(null);
        processoEpaHome.setClassificacaoDocumento(null);
    }

    private void checkCurrentTask() {
        TaskInstance tempTask = org.jboss.seam.bpm.TaskInstance.instance();
        if (currentTaskInstance != null
                && (tempTask == null || tempTask.getId() != currentTaskInstance
                        .getId())) {
            acusarUsuarioSemAcesso();
        }
    }

    public void removeUsuario(final TaskInstance taskInstance) {
        if (taskInstance != null) {
            try {
                taskInstanceManager.removeUsuario(taskInstance.getId());
                afterLiberarTarefa();
            } catch (DAOException e) {
                LOG.error("TaskInstanceHome.removeUsuario(taskInstance)", e);
            }
        }
    }

    public void removeUsuario(final Long idTaskInstance) {
        try {
            taskInstanceManager.removeUsuario(idTaskInstance);
            afterLiberarTarefa();
        } catch (Exception e) {
            LOG.error("TaskInstanceHome.removeUsuario(idTaskInstance)", e);
        }
    }

    public void removeUsuario(final Integer idProcesso, final Integer idTarefa) {
        try {
            final Map<String, Object> result = processoTarefaManager
                    .findProcessoTarefaByIdProcessoAndIdTarefa(idProcesso, idTarefa);
            taskInstanceManager.removeUsuario((Long) result.get("idTaskInstance"));
            afterLiberarTarefa();
        } catch (NoResultException e) {
            LOG.error(".removeUsuario(idProcesso, idTarefa) - Sem resultado", e);
        } catch (NonUniqueResultException e) {
            LOG.error(
                    ".removeUsuario(idProcesso, idTarefa) - Mais de um resultado",
                    e);
        } catch (IllegalStateException e) {
            LOG.error(".removeUsuario(idProcesso, idTarefa) - Estado ilegal", e);
        } catch (DAOException e) {
            LOG.error(".removeUsuario(idProcesso, idTarefa) - ", e);
        }
    }

    private void afterLiberarTarefa() {
        userHandler.clear();
        FacesMessages.instance().clear();
        FacesMessages.instance().add("Tarefa liberada com sucesso.");
    }

    public void removeUsuario() {
        if (BusinessProcess.instance().hasCurrentTask()) {
            try {
                taskInstanceManager.removeUsuario(BusinessProcess.instance()
                        .getTaskId());
                afterLiberarTarefa();
            } catch (DAOException e) {
                LOG.error(".removeUsuario() - ", e);
            }
        } else {
            FacesMessages.instance().add(
                    Messages.resolveMessage("org.jboss.seam.TaskNotFound"));
        }
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
                if (availableTransitions.contains(transition)
                        && !hasOcculTransition(transition)) {
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
        return availableTransitions != null && availableTransitions.isEmpty()
                && taskInstance != null;
    }

    private void validateTaskId() {
        if (taskId == null) {
            setTaskId(org.jboss.seam.bpm.TaskInstance.instance().getId());
        }
    }

    public List<ModeloDocumento> getModeloItems(String variavel) {
        ElFunctions elFunctions = (ElFunctions) Component
                .getInstance(ElFunctions.NAME);
        String listaModelos = elFunctions.evaluateExpression(variavel);
        return modeloDocumentoManager
                .getModelosDocumentoInListaModelo(listaModelos);
    }

    public void assignModeloDocumento(final String id) {
        String modelo = "";
        if (modeloDocumento != null) {
        	ExpressionResolverChain chain = ExpressionResolverChainBuilder.with(new JbpmExpressionResolver(variableTypeResolver.getVariableTypeMap(), ProcessInstance.instance().getContextInstance()))
                	.and(new SeamExpressionResolver()).build();
            modelo = modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento, chain);
        }
        mapaDeVariaveis.put(id, modelo);
    }

    public static boolean hasOcculTransition(Transition transition) {
        return transition.getDescription() != null
                && transition.getDescription().contains(
                        TransitionHandler.OCCULT_TRANSITION);
        // return OCCULT_TRANSITION.equals(transition.getCondition());
    }

    @SuppressWarnings(UNCHECKED)
    public void updateTransitions() {
        availableTransitions = taskInstance.getAvailableTransitions();
        leavingTransitions = taskInstance.getTask().getTaskNode()
                .getLeavingTransitions();
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
        this.documentoAAssinar = null;
        this.documentosAssinaveis = null;
    }

    public ModeloDocumento getModeloDocumento() {
        createInstance();
        return modeloDocumento;
    }

    public void setModeloDocumento(ModeloDocumento modelo) {
        this.modeloDocumento = modelo;
        ExpressionResolverChain chain = ExpressionResolverChainBuilder.with(new JbpmExpressionResolver(variableTypeResolver.getVariableTypeMap(), ProcessInstance.instance().getContextInstance()))
        	.and(new SeamExpressionResolver()).build();
        mapaDeVariaveis.put(getFieldName(variavelDocumento),
                modeloDocumentoManager.evaluateModeloDocumento(modelo, chain));
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

    public boolean possuiAssinatura(String idEditor) {
        DadosDocumentoAssinavel documentoAssinavel = documentosAssinaveis
                .get(idEditor);
        if (documentoAssinavel != null) {
            return assinaturaDocumentoService
                    .isDocumentoAssinado(documentoAssinavel.getIdDocumento());
        }
        return false;
    }

    public boolean podeRenderizarApplet(String idEditor) {
        DadosDocumentoAssinavel documentoAssinavel = documentosAssinaveis.get(idEditor);
        if (documentoAssinavel != null) {
            UsuarioPerfil usuarioPerfilAtual = Authenticator.getUsuarioPerfilAtual();
            Papel papel = usuarioPerfilAtual.getPerfilTemplate().getPapel();
            ClassificacaoDocumento classificacao = documentosAssinaveis.get(idEditor).getClassificacao();
            if (classificacao != null) {
            	return assinaturaDocumentoService.podeRenderizarApplet(papel, classificacao, documentoAssinavel.getIdDocumento(), usuarioPerfilAtual.getUsuarioLogin());
            }
        }
        return false;
    }

    public Map<String, DadosDocumentoAssinavel> getDocumentosAssinaveis() {
        return documentosAssinaveis;
    }
    
    public Map<String, ClassificacaoDocumento> getClassificacoesVariaveisUpload() {
        return classificacoesVariaveisUpload;
    }
    
    @SuppressWarnings(UNCHECKED)
    public Object getValueOfVariableFromTaskInstance(String variableName) {
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
    
    public List<ClassificacaoDocumento> getUseableClassificacaoDocumento(boolean isModelo, String nomeVariavel, Integer idFluxo) {
        return classificacaoDocumentoFacade.getUseableClassificacaoDocumento(isModelo, nomeVariavel, idFluxo);
    }
    
}
