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
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.JbpmException;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.documento.dao.TipoProcessoDocumentoDAO;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.entity.TipoProcessoDocumentoPapel;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.DadosDocumentoAssinavel;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.situacao.manager.SituacaoProcessoManager;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoEpaTarefaManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.action.TaskPageAction;
import br.com.infox.ibpm.task.dao.TaskConteudoDAO;
import br.com.infox.ibpm.task.entity.TaskConteudo;
import br.com.infox.ibpm.task.manager.TaskInstanceManager;
import br.com.infox.ibpm.transition.TransitionHandler;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.util.UserHandler;
import br.com.infox.jsf.function.ElFunctions;
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
    private static final LogProvider LOG = Logging
            .getLogProvider(TaskInstanceHome.class);
    private static final long serialVersionUID = 1L;

    public static final String NAME = "taskInstanceHome";

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
    @In
    private TipoProcessoDocumentoDAO tipoProcessoDocumentoDAO;
    @In
    private SituacaoProcessoManager situacaoProcessoManager;
    @In
    private ProcessoManager processoManager;
    @In
    private ProcessoEpaTarefaManager processoEpaTarefaManager;
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

    private URL urlRetornoAcessoExterno;
    private String documentoAAssinar;

    private boolean canClosePanelVal;
    private boolean taskCompleted;

    public void createInstance() {
        taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
        if (mapaDeVariaveis == null && taskInstance != null) {
            variableTypeResolver.setProcessInstance(taskInstance
                    .getProcessInstance());
            mapaDeVariaveis = new HashMap<String, Object>();
            documentosAssinaveis = new HashMap<>();
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
        final TaskVariableRetriever variableRetriever = new TaskVariableRetriever(
                variableAccess, taskInstance);
        variableRetriever.retrieveVariableContent();
        mapaDeVariaveis.put(getFieldName(variableRetriever.getName()),
                variableRetriever.getVariable());
        if (variableRetriever.isEditor()) {
            DadosDocumentoAssinavel dados = new DadosDocumentoAssinavel();
            Integer id = (Integer) taskInstance.getVariable(variableRetriever
                    .getMappedName());
            if (id != null) {
                ProcessoDocumentoManager processoDocumentoManager = ComponentUtil
                        .getComponent(ProcessoDocumentoManager.NAME);
                ProcessoDocumento pd = processoDocumentoManager.find(id);
                if (pd != null) {
                    dados.setIdDocumento(id);
                    dados.setClassificacao(pd.getTipoProcessoDocumento());
                }
            }
            documentosAssinaveis.put(getFieldName(variableRetriever.getName()),
                    dados);
        }
        setModeloWhenExists(variableRetriever);
    }

    private void setModeloWhenExists(TaskVariableRetriever variableRetriever) {
        String modelo = getModeloFromProcessInstance(variableRetriever
                .getName());
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
        // Não pode usar ProcessoHome.instance().update() porque por algum
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
        String fieldName = getFieldName(variableAccess.getMappedName().split(
                ":")[1]);
        if (documentoAAssinar != null && fieldName.equals(documentoAAssinar)) {
            documentoCorreto = true;
        }
        TaskVariableResolver variableResolver = new TaskVariableResolver(
                variableAccess, taskInstance, documentoCorreto);

        if (variableAccess.isWritable()) {
            if (variableResolver.isEditor() && variableAccess.isReadable()) {
                DadosDocumentoAssinavel dados = documentosAssinaveis
                        .get(fieldName);
                ProcessoHome processoHome = ProcessoHome.instance();
                processoHome.setTipoProcessoDocumento(dados.getClassificacao());
                processoHome.setSignature(dados.getSignature());
                processoHome.setCertChain(dados.getCertChain());
            }
            variableResolver.assignValueFromMapaDeVariaveis(mapaDeVariaveis);
            variableResolver.resolve();
            if (variableResolver.isEditor()) {
                if (documentoCorreto) {
                    if (!variableResolver.isEditorAssinado()) {
                        assinado = false;
                    } else {
                        assinado = assinado || documentoAAssinar != null;
                        DadosDocumentoAssinavel dados = documentosAssinaveis
                                .get(fieldName);
                        dados.setIdDocumento((Integer) variableResolver
                                .getValue());
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
        int idProcesso = ProcessoHome.instance().getInstance().getIdProcesso();
        String login = Authenticator.getUsuarioLogado().getLogin();
        if (processoManager.checkAccess(idProcesso, login)) {
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
        return situacaoProcessoManager.canOpenTask(currentTaskInstance.getId());
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
        int idProcesso = ProcessoHome.instance().getInstance().getIdProcesso();
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
        try {
            this.currentTaskInstance = context.getTaskInstance();
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
            ProcessoHome processoHome = ComponentUtil
                    .getComponent(ProcessoHome.NAME);

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
        } else if (situacaoProcessoManager.canOpenTask(this.currentTaskInstance
                .getId())) {
            setTaskId(currentTaskInstance.getId());
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
        ProcessoEpaTarefa pt = processoEpaTarefaManager
                .getByTaskInstance(taskInstance.getId());
        Date dtFinalizacao = taskInstance.getEnd();
        pt.setDataFim(dtFinalizacao);
        try {
            processoEpaTarefaManager.update(pt);
            processoEpaTarefaManager.updateTempoGasto(dtFinalizacao, pt);
        } catch (DAOException e) {
            LOG.error(".atualizarBam()", e);
        }
    }

    private void limparEstado(ProcessoHome processoHome) {
        this.currentTaskInstance = null;
        processoHome.setIdProcessoDocumento(null);
        processoHome.setCertChain(null);
        processoHome.setSignature(null);
        processoHome.setTipoProcessoDocumento(null);
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
            final Map<String, Object> result = processoEpaTarefaManager
                    .findProcessoEpaTarefaByIdProcessoAndIdTarefa(idProcesso,
                            idTarefa);
            taskInstanceManager.removeUsuario((Long) result
                    .get("idTaskInstance"));
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
                    Messages.instance().get("org.jboss.seam.TaskNotFound"));
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
            modelo = modeloDocumentoManager.evaluateModeloDocumento(
                    modeloDocumento, variableTypeResolver.getVariableTypeMap());
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
        mapaDeVariaveis.put(getFieldName(variavelDocumento),
                modeloDocumentoManager.evaluateModeloDocumento(modelo,
                        variableTypeResolver.getVariableTypeMap()));
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
        DadosDocumentoAssinavel documentoAssinavel = documentosAssinaveis
                .get(idEditor);
        boolean podeAssinar = false;
        if (documentoAssinavel != null) {
            UsuarioPerfil usuarioPerfilAtual = Authenticator
                    .getUsuarioPerfilAtual();
            podeAssinar = podeAssinar(idEditor, usuarioPerfilAtual)
                    && !assinaturaDocumentoService.isDocumentoAssinado(
                            documentoAssinavel.getIdDocumento(),
                            usuarioPerfilAtual.getUsuarioLogin());
        }
        return podeAssinar;
    }

    private boolean podeAssinar(String idEditor,
            UsuarioPerfil usuarioPerfilAtual) {
        boolean assinavel = false;
        TipoProcessoDocumento classificacao = documentosAssinaveis
                .get(idEditor).getClassificacao();
        if (classificacao != null) {
            List<TipoProcessoDocumentoPapel> tipoProcessoDocumentoPapeis = classificacao
                    .getTipoProcessoDocumentoPapeis();
            for (TipoProcessoDocumentoPapel tipoProcessoDocumentoPapel : tipoProcessoDocumentoPapeis) {
                if (assinavel = usuarioPerfilAtual.getPerfilTemplate().getPapel()
                        .equals(tipoProcessoDocumentoPapel.getPapel())) {
                    break;
                }
            }
        }
        return assinavel;
    }

    public Map<String, DadosDocumentoAssinavel> getDocumentosAssinaveis() {
        return documentosAssinaveis;
    }
}
