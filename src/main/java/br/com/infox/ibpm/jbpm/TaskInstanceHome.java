/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
 */
package br.com.infox.ibpm.jbpm;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

import br.com.infox.bpm.action.TaskPageAction;
import br.com.infox.epp.manager.ProcessoEpaTarefaManager;
import br.com.infox.epp.manager.ProcessoManager;
import br.com.infox.ibpm.dao.TipoProcessoDocumentoDAO;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.home.Authenticator;
import br.com.infox.ibpm.home.ProcessoHome;
import br.com.infox.ibpm.jbpm.actions.ModeloDocumentoAction;
import br.com.infox.ibpm.manager.SituacaoProcessoManager;
import br.com.infox.ibpm.search.Reindexer;
import br.com.infox.ibpm.search.SearchHandler;
import br.com.infox.ibpm.service.AssinaturaDocumentoService;
import br.com.infox.search.Indexer;
import br.com.infox.util.constants.WarningConstants;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.Util;
import br.com.itx.exception.AplicationException;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(TaskInstanceHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class TaskInstanceHome implements Serializable {

    private static final String ASSINATURA_OBRIGATORIA = "A assinatura é obrigatória para esta classificação de documento";

    private static final String MSG_USUARIO_SEM_ACESSO = "Você não pode mais efetuar transações "
            + "neste registro, verifique se ele não foi movimentado";

    private static final LogProvider LOG = Logging
            .getLogProvider(TaskInstanceHome.class);
    private static final long serialVersionUID = 1L;
    public static final String NAME = "taskInstanceHome";

    private static final String OCCULT_TRANSITION = "#{true}";

    private TaskInstance taskInstance;
    private Map<String, Object> instance;
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
    @In private TipoProcessoDocumentoDAO tipoProcessoDocumentoDAO;
    @In private SituacaoProcessoManager situacaoProcessoManager;
    @In private ProcessoManager processoManager;
    @In private ProcessoEpaTarefaManager processoEpaTarefaManager;
    public static final String UPDATED_VAR_NAME = "isTaskHomeUpdated";

    @SuppressWarnings(WarningConstants.UNCHECKED)
	public void createInstance() {
        taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
        if (instance == null && taskInstance != null) {
            instance = new HashMap<String, Object>();
            TaskController taskController = taskInstance.getTask().getTaskController();
            if (taskController != null) {
                List<VariableAccess> list = taskController.getVariableAccesses();
                for (VariableAccess variableAccess : list) {
                    retrieveVariable(variableAccess);
                }
                // Atualizar as transições possiveis. Isso é preciso, pois as
                // condições das transições são avaliadas antes
                // deste metodo ser executado.
                updateTransitions();

            }
        }
    }
    
    private void retrieveVariable(VariableAccess variableAccess) {
        TaskVariable taskVariable = new TaskVariable(variableAccess);
        taskVariable.setVariable(JbpmUtil.instance().getConteudo(variableAccess, taskInstance));
        if (taskVariable.isEditor()) {
            evaluateWhenDocumentoAssinado(taskVariable);
        } else {
            evaluateWhenMonetario(taskVariable);
        }
        evaluateWhenModelo(taskVariable);
        evaluateWhenForm(taskVariable);
    }

    private void evaluateWhenModelo(TaskVariable taskVariable) {
        String modelo = (String) ProcessInstance.instance()
                .getContextInstance().getVariable(taskVariable.getName() + "Modelo");
        if (modelo != null) {
            variavelDocumento = taskVariable.getName();
            if (!taskVariable.hasVariable()) {
                String s = modelo.split(",")[0].trim();
                modeloDocumento = EntityUtil.getEntityManager()
                        .find(ModeloDocumento.class,
                                Integer.parseInt(s));
                setModeloDocumento(modeloDocumento);
            }
        }
    }

    private void evaluateWhenDocumentoAssinado(TaskVariable taskVariable) {
        Integer id = (Integer) taskInstance.getVariable(taskVariable.getMappedName());
        AssinaturaDocumentoService documentoService = new AssinaturaDocumentoService();
        if ((id != null) && (!documentoService.isDocumentoAssinado(id)) && taskVariable.isWritable()) {
            ProcessoHome.instance().carregarDadosFluxo(id);
            putVariable(taskVariable);
        }
    }

    private void evaluateWhenMonetario(TaskVariable taskVariable) {
        if (taskVariable.isMonetario()) {
            taskVariable.setVariable(String.format("%.2f", taskVariable.getVariable()));
        }
        putVariable(taskVariable);
    }

    private void evaluateWhenForm(TaskVariable taskVariable) {
        if (taskVariable.isForm()) {
            varName = taskVariable.getName();
            taskVariable.retrieveHomes();
        }
    }
    
    private void putVariable(TaskVariable taskVariable){
        instance.put(getFieldName(taskVariable.getName()), taskVariable.getVariable());
    }

    public Map<String, Object> getInstance() {
        createInstance();
        return instance;
    }

    // Método que será chamado pelo botão "Assinar Digitalmente"
    public void assinarDocumento() {
        assinar = Boolean.TRUE;
        this.update();
    }

    public Object getValueFromInstanceMap(String key) {
        if (instance == null) {
            return null;
        }
        Set<Entry<String, Object>> entrySet = instance.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            if (entry.getKey().split("-")[0].equals(key)
                    && entry.getValue() != null) {
                return entry.getValue();
            }
        }
        return null;
    }

	public void update() {
        modeloDocumento = null;
        taskInstance = org.jboss.seam.bpm.TaskInstance.instance();

        if ((taskInstance != null) && (taskInstance.getTask() != null)) {
            TaskController taskController = taskInstance.getTask()
                    .getTaskController();
            if (taskController != null) {
            	TaskPageAction taskPageAction = ComponentUtil.getComponent(TaskPageAction.NAME);
            	if (!taskPageAction.getHasTaskPage()) {
	                updateVariables(taskController);
            	}
                Contexts.getBusinessProcessContext().flush();
                Util.setToEventContext(UPDATED_VAR_NAME, true);
                updateIndex();
                updateTransitions();
            }
        }
    }

    @SuppressWarnings(WarningConstants.UNCHECKED)
    private void updateVariables(TaskController taskController) {
        List<VariableAccess> list = taskController
                .getVariableAccesses();
        for (VariableAccess var : list) {

            String type = var.getMappedName().split(":")[0];
            String name = var.getMappedName().split(":")[1];
            Object value = getValueFromInstanceMap(name);

            if ("numberMoney".equals(type) && value != null) {
                String val = String.valueOf(value);
                try {
                    value = Float.parseFloat(val);
                } catch (NumberFormatException e) {
                    value = Float.parseFloat(val.replace(".", "")
                            .replace(",", "."));
                }
            }

            if (var.isWritable()) {
                if (JbpmUtil.isTypeEditor(type)) {
                    Integer idDoc = null;
                    if (taskInstance.getVariable(var.getMappedName()) != null) {
                        idDoc = (Integer) taskInstance.getVariable(var
                                .getMappedName());
                    }
                    String label = JbpmUtil.instance().getMessages()
                            .get(name);
                    Integer valueInt = ProcessoHome.instance()
                            .salvarProcessoDocumentoFluxo(value, idDoc,
                                    assinar, label);
                    if (valueInt != 0) {
                        value = valueInt;
                        Contexts.getBusinessProcessContext().set(
                                var.getMappedName(), value);
                    }
                    assinado = assinado || assinar;
                    assinar = Boolean.FALSE;
                } else {
                    Contexts.getBusinessProcessContext().set(
                            var.getMappedName(), value);
                }
            }
        }
    }

    private Boolean checkAccess() {
        int idProcesso = ProcessoHome.instance().getInstance().getIdProcesso();
        String login = Authenticator.getUsuarioLogado().getLogin();
        if (processoManager.checkAccess(idProcesso, login)){
            return Boolean.TRUE;
        } else {
            FacesMessages.instance().clear();
            throw new AplicationException(MSG_USUARIO_SEM_ACESSO);
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
                    instance.put(getFieldName(varName), idObject);
                }
                update();
            }
        }
    }

    private void canDoOperation() {
        if (currentTaskInstance == null) {
            currentTaskInstance = org.jboss.seam.bpm.TaskInstance.instance();
        }
        if (currentTaskInstance != null) {
            if (situacaoProcessoManager.canOpenTask(currentTaskInstance.getId())) {
                return;
            }
            FacesMessages.instance().clear();
            throw new AplicationException(MSG_USUARIO_SEM_ACESSO);
        }
    }

    public void updateIndex() {
        String conteudo = Reindexer.getTextoIndexavel(SearchHandler
                .getConteudo(taskInstance));
        try {
            Indexer indexer = new Indexer();
            Map<String, String> fields = new HashMap<String, String>();
            fields.put("conteudo", conteudo);
            indexer.index(taskInstance.getId() + "",
                    new HashMap<String, String>(), fields);
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
            throw new AplicationException(AplicationException.createMessage(
                    action + ex.getLocalizedMessage(),
                    "setCurrentTaskInstance()", "TaskInstanceHome", "BPM"));
        }
    }
    
    public String end(String transition) {
        if (checkAccess()) {
            TaskInstance tempTask = org.jboss.seam.bpm.TaskInstance.instance();
            if (currentTaskInstance != null && (tempTask == null || tempTask.getId() != currentTaskInstance.getId())) {
                FacesMessages.instance().clear();
                throw new AplicationException(MSG_USUARIO_SEM_ACESSO);
            }
            
            ProcessoHome processoHome = ComponentUtil.getComponent(ProcessoHome.NAME);
            boolean isObrigatorio = tipoProcessoDocumentoDAO.isAssinaturaObrigatoria(processoHome.getTipoProcessoDocumento(), Authenticator.getPapelAtual());
            
            if (isObrigatorio && !(isObrigatorio && assinado)) {
                FacesMessages messages = FacesMessages.instance();
                messages.clearGlobalMessages();
                messages.clear();
                messages.add(Severity.ERROR, ASSINATURA_OBRIGATORIA);
                return null;
            }
            
            this.currentTaskInstance = null;
            processoHome.setIdProcessoDocumento(null);
            update();
            try {
                BusinessProcess.instance().endTask(transition);
            } catch (JbpmException e) {
                LOG.error(".end()", e);
            }
            EditableValueHolder canClosePanelVal = (EditableValueHolder) RichFunction.findComponent("canClosePanel");
            boolean canClosePanel = false;
            if (this.currentTaskInstance == null) {
                canClosePanelVal.setValue(true);
                canClosePanel = true;
            } else if (situacaoProcessoManager.canOpenTask(this.currentTaskInstance.getId())) {
                setTaskId(currentTaskInstance.getId());
            } else {
                canClosePanelVal.setValue(true);
                canClosePanel = true;
            }
            EditableValueHolder taskCompleted = (EditableValueHolder) RichFunction.findComponent("taskCompleted");
            taskCompleted.setValue(true);
            if (!canClosePanel) {
                Redirect red = Redirect.instance();
                red.setViewId("/Processo/movimentar.seam");
                red.setParameter("idProcesso", processoHome.getInstance().getIdProcesso());
                BusinessProcess.instance().getProcessId();
                red.setConversationPropagationEnabled(false);
                red.execute();
            }
        }
        return null;
    }

    public void removeUsuario(final Long idTaskInstance) {
        try {
            UsuarioTaskInstance uti = EntityUtil.find(UsuarioTaskInstance.class, idTaskInstance);
            if (uti!= null) {
                EntityUtil.getEntityManager().remove(uti);
                EntityUtil.getEntityManager().flush();
            }
        } catch (Exception e) {
            LOG.error("TaskInstanceHome.removeUsuario()", e);
        }
    }
    
	public void removeUsuario(final Integer idProcesso, final Integer idTarefa) {
        try {
            final Map<String,Object> result = processoEpaTarefaManager.findProcessoEpaTarefaByIdProcessoAndIdTarefa(idProcesso, idTarefa);
            removeUsuario((Long)result.get("idTaskInstance"));
        } catch (NoResultException e) {
            LOG.error(".removeUsuario(idProcesso, idTarefa) - Sem resultado", e);
        } catch (NonUniqueResultException e) {
            LOG.error(".removeUsuario(idProcesso, idTarefa) - Mais de um resultado", e);
        } catch (IllegalStateException e) {
            LOG.error(".removeUsuario(idProcesso, idTarefa) - Estado ilegal", e);
        } finally {
            Util.rollbackTransactionIfNeeded();
        }
    }
    
    public void removeUsuario() {
        removeUsuario(BusinessProcess.instance().getTaskId());
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
        if (taskId == null) {
            setTaskId(org.jboss.seam.bpm.TaskInstance.instance().getId());
        }
        List<Transition> list = new ArrayList<Transition>();
        if (availableTransitions != null && availableTransitions.size() == 0
                && taskInstance != null) {
            updateTransitions();
        }
        if (availableTransitions == null) {
            return list;
        }
        // pega da definicao para garantir a mesma ordem do XML
        for (Transition transition : leavingTransitions) {
            // POG temporario devido a falha no JBPM de avaliar as
            // avaliablesTransitions
            if (availableTransitions.contains(transition)
                    && !hasOcculTransition(transition)) {
                list.add(transition);
            }
        }
        return list;
    }

    public static boolean hasOcculTransition(Transition transition) {
        return OCCULT_TRANSITION.equals(transition.getCondition());
    }

    @SuppressWarnings(WarningConstants.UNCHECKED)
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
        this.instance = null;
        this.taskInstance = null;
    }

    public ModeloDocumento getModeloDocumento() {
        createInstance();
        return modeloDocumento;
    }

    public void setModeloDocumento(ModeloDocumento modelo) {
        this.modeloDocumento = modelo;
        instance.put(getFieldName(variavelDocumento), ModeloDocumentoAction.instance().getConteudo(modelo));
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
}