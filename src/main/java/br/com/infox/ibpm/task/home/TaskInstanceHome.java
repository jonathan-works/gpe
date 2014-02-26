package br.com.infox.ibpm.task.home;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.EditableValueHolder;
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
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.JbpmException;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.richfaces.function.RichFunction;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.context.ContextFacade;
import br.com.infox.core.exception.ApplicationException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.dao.TipoProcessoDocumentoDAO;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.situacao.manager.SituacaoProcessoManager;
import br.com.infox.epp.search.Indexer;
import br.com.infox.epp.search.Reindexer;
import br.com.infox.epp.search.SearchHandler;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoEpaTarefaManager;
import br.com.infox.ibpm.task.action.TaskPageAction;
import br.com.infox.ibpm.task.manager.TaskInstanceManager;
import br.com.infox.ibpm.util.UserHandler;
import br.com.infox.jsf.function.ElFunctions;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(TaskInstanceHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class TaskInstanceHome implements Serializable {

    private static final String MOVIMENTAR_PATH = "/Processo/movimentar.seam";
    private static final String CAN_CLOSE_PANEL = "canClosePanel";
    private static final String TASK_COMPLETED = "taskCompleted";
    private static final String ASSINATURA_OBRIGATORIA = "A assinatura é obrigatória para esta classificação de documento";
    private static final String MSG_USUARIO_SEM_ACESSO = "Você não pode mais efetuar transações "
            + "neste registro, verifique se ele não foi movimentado";
    private static final String UPDATED_VAR_NAME = "isTaskHomeUpdated";
    private static final LogProvider LOG = Logging.getLogProvider(TaskInstanceHome.class);
    private static final long serialVersionUID = 1L;
    private static final String OCCULT_TRANSITION = "#{true}";

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
    private Boolean assinar = Boolean.FALSE;
    private Boolean assinado = Boolean.FALSE;
    private TaskInstance currentTaskInstance;

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
    private URL urlRetornoAcessoExterno;

    public void createInstance() {
        taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
        if (mapaDeVariaveis == null && taskInstance != null) {
            mapaDeVariaveis = new HashMap<String, Object>();
            retrieveVariables();
        }
    }

    @SuppressWarnings(UNCHECKED)
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
        variableRetriever.searchAndAssignConteudoToVariable();
        if (variableRetriever.isEditor()) {
            putVariable(variableRetriever.evaluateWhenDocumentoAssinado());
        } else {
            putVariable(variableRetriever.evaluateWhenMonetario());
        }
        evaluateWhenModelo(variableRetriever);
        if (variableRetriever.evaluateWhenForm() != null) {
            varName = variableRetriever.getName();
        }
    }

    private void evaluateWhenModelo(TaskVariableRetriever variableRetriever) {
        String modelo = getModeloFromProcessInstance(variableRetriever.getName());
        if (modelo != null) {
            variavelDocumento = variableRetriever.getName();
            if (!variableRetriever.hasVariable()) {
                setModeloDocumento(getModeloDocumentoFromModelo(modelo));
            }
        }
    }

    private String getModeloFromProcessInstance(String variableName) {
        return (String) ProcessInstance.instance().getContextInstance().getVariable(variableName
                + "Modelo");
    }

    private ModeloDocumento getModeloDocumentoFromModelo(String modelo) {
        String s = modelo.split(",")[0].trim();
        return modeloDocumentoManager.find(Integer.parseInt(s));
    }

    private void putVariable(TaskVariableRetriever variableRetriever) {
        if (variableRetriever != null) {
            mapaDeVariaveis.put(getFieldName(variableRetriever.getName()), variableRetriever.getVariable());
        }
    }

    public Map<String, Object> getInstance() {
        createInstance();
        return mapaDeVariaveis;
    }

    // Método que será chamado pelo botão "Assinar Digitalmente"
    public void assinarDocumento() {
        assinar = Boolean.TRUE;
        this.update();
    }

    public void update() {
        prepareForUpdate();
        if (possuiTask()) {
            TaskController taskController = taskInstance.getTask().getTaskController();
            TaskPageAction taskPageAction = ComponentUtil.getComponent(TaskPageAction.NAME);
            if (taskController != null) {
                if (!taskPageAction.getHasTaskPage()) {
                    updateVariables(taskController);
                }
                completeUpdate();
            }
        }
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
        TaskVariableResolver variableResolver = new TaskVariableResolver(variableAccess, taskInstance);
        variableResolver.assignValueFromMapaDeVariaveis(mapaDeVariaveis);
        variableResolver.resolveWhenMonetario();
        if (variableAccess.isWritable()) {
            if (variableResolver.isEditor()) {
                try {
                    variableResolver.resolveWhenEditor(assinar);
                } catch (CertificadoException e) {
                    LOG.error("Falha na assinatura", e);
                    if (assinar) {
                        FacesMessages.instance().add(Messages.instance().get("assinatura.falhaAssinatura"));
                    }
                } finally {
                    assinado = assinado || assinar;
                    assinar = Boolean.FALSE;
                }
            } else {
                variableResolver.atribuirValorDaVariavelNoContexto();
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
        String conteudo = Reindexer.getTextoIndexavel(SearchHandler.getConteudo(taskInstance));
        try {
            Indexer indexer = new Indexer();
            Map<String, String> fields = new HashMap<String, String>();
            fields.put("conteudo", conteudo);
            indexer.index(taskInstance.getId() + "", new HashMap<String, String>(), fields);
        } catch (IOException e) {
            LOG.error(".updateIndex()", e);
        }
    }

    @Observer(Event.EVENTTYPE_TASK_CREATE)
    public void setCurrentTaskInstance(ExecutionContext context) {
        try {
            this.currentTaskInstance = context.getTaskInstance();
        } catch (Exception ex) {
            String action = "atribuir a taskInstance corrente ao currentTaskInstance: ";
            LOG.warn(action, ex);
            throw new ApplicationException(ApplicationException.createMessage(action
                    + ex.getLocalizedMessage(), "setCurrentTaskInstance()", "TaskInstanceHome", "BPM"), ex);
        }
    }

    public String end(String transition) {
        if (checkAccess()) {
            checkCurrentTask();
            ProcessoHome processoHome = ComponentUtil.getComponent(ProcessoHome.NAME);
            if (processoHome.getTipoProcessoDocumento() != null
                    && faltaAssinatura(processoHome.getTipoProcessoDocumento())) {
                acusarFaltaDeAssinatura();
                return null;
            }
            limparEstado(processoHome);
            update();
            finalizarTaskDoJbpm(transition);
            atualizarPaginaDeMovimentacao(processoHome);
        }
        return null;
    }

    private void atualizarPaginaDeMovimentacao(ProcessoHome processoHome) {
        EditableValueHolder taskCompleted = (EditableValueHolder) RichFunction.findComponent(TASK_COMPLETED);
        taskCompleted.setValue(true);
        if (!canClosePanel()) {
            redirectToMovimentar(processoHome);
        } else if (isUsuarioExterno()) {
            redirectToAcessoExterno();
        }
    }

    private boolean isUsuarioExterno() {
        Authenticator authenticator = ComponentUtil.getComponent("authenticator");
        return authenticator.isUsuarioExterno();
    }

    private void redirectToAcessoExterno() {
        Redirect red = Redirect.instance();
        red.setViewId("/AcessoExterno/externo.seam?urlRetorno="
                + urlRetornoAcessoExterno.toString());
        red.setConversationPropagationEnabled(false);
        red.execute();
    }

    private void redirectToMovimentar(ProcessoHome processoHome) {
        Redirect red = Redirect.instance();
        red.setViewId(MOVIMENTAR_PATH);
        red.setParameter("idProcesso", processoHome.getInstance().getIdProcesso());
        BusinessProcess.instance().getProcessId();
        red.setConversationPropagationEnabled(false);
        red.execute();
    }

    private boolean canClosePanel() {
        EditableValueHolder canClosePanelVal = (EditableValueHolder) RichFunction.findComponent(CAN_CLOSE_PANEL);
        if (this.currentTaskInstance == null) {
            canClosePanelVal.setValue(true);
            return true;
        } else if (situacaoProcessoManager.canOpenTask(this.currentTaskInstance.getId())) {
            setTaskId(currentTaskInstance.getId());
            return false;
        } else {
            canClosePanelVal.setValue(true);
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
        ProcessoEpaTarefa pt = processoEpaTarefaManager.getByTaskInstance(taskInstance.getId());
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
    }

    private void acusarFaltaDeAssinatura() {
        FacesMessages messages = FacesMessages.instance();
        messages.clearGlobalMessages();
        messages.clear();
        messages.add(Severity.ERROR, ASSINATURA_OBRIGATORIA);
    }

    private boolean faltaAssinatura(TipoProcessoDocumento tipoProcessoDocumento) {
        boolean isObrigatorio = tipoProcessoDocumentoDAO.isAssinaturaObrigatoria(tipoProcessoDocumento, Authenticator.getPapelAtual());
        return isObrigatorio && !assinado;
    }

    private void checkCurrentTask() {
        TaskInstance tempTask = org.jboss.seam.bpm.TaskInstance.instance();
        if (currentTaskInstance != null
                && (tempTask == null || tempTask.getId() != currentTaskInstance.getId())) {
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
            final Map<String, Object> result = processoEpaTarefaManager.findProcessoEpaTarefaByIdProcessoAndIdTarefa(idProcesso, idTarefa);
            taskInstanceManager.removeUsuario((Long) result.get("idTaskInstance"));
            afterLiberarTarefa();
        } catch (NoResultException e) {
            LOG.error(".removeUsuario(idProcesso, idTarefa) - Sem resultado", e);
        } catch (NonUniqueResultException e) {
            LOG.error(".removeUsuario(idProcesso, idTarefa) - Mais de um resultado", e);
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
                taskInstanceManager.removeUsuario(BusinessProcess.instance().getTaskId());
                afterLiberarTarefa();
            } catch (DAOException e) {
                LOG.error(".removeUsuario() - ", e);
            }
        } else {
            FacesMessages.instance().add(Messages.instance().get("org.jboss.seam.TaskNotFound"));
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
        ElFunctions elFunctions = (ElFunctions) Component.getInstance(ElFunctions.NAME);
        String listaModelos = elFunctions.evaluateExpression(variavel);
        return modeloDocumentoManager.getModelosDocumentoInListaModelo(listaModelos);
    }

    public void assignModeloDocumento(final String id) {
        String modelo = "";
        if (modeloDocumento != null) {
            modelo = modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento);
        }
        mapaDeVariaveis.put(id, modelo);
    }

    public static boolean hasOcculTransition(Transition transition) {
        return OCCULT_TRANSITION.equals(transition.getCondition());
    }

    @SuppressWarnings(UNCHECKED)
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
        mapaDeVariaveis.put(getFieldName(variavelDocumento), modeloDocumentoManager.evaluateModeloDocumento(modelo));
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

}
