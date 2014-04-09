package br.com.infox.ibpm.process.definition;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static br.com.infox.seam.messages.LocaleUtil.internacionalize;
import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.jpdl.JpdlException;
import org.jbpm.jpdl.xml.Problem;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;
import org.richfaces.context.ExtendedPartialViewContext;
import org.xml.sax.InputSource;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.xpdl.FluxoXPDL;
import br.com.infox.epp.fluxo.xpdl.IllegalXPDLException;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.jpdl.JpdlXmlWriter;
import br.com.infox.ibpm.node.InfoxMailNode;
import br.com.infox.ibpm.process.definition.fitter.EventFitter;
import br.com.infox.ibpm.process.definition.fitter.NodeFitter;
import br.com.infox.ibpm.process.definition.fitter.SwimlaneFitter;
import br.com.infox.ibpm.process.definition.fitter.TaskFitter;
import br.com.infox.ibpm.process.definition.fitter.TransitionFitter;
import br.com.infox.ibpm.process.definition.graphical.ProcessBuilderGraph;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.handler.TaskHandler;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.jsf.validator.JsfComponentTreeValidator;

import com.google.common.base.Strings;

@Name(ProcessBuilder.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessBuilder implements Serializable {

    private static final String PROCESS_DEFINITION_TABPANEL_ID = ":processDefinition";
    private static final String PROCESS_DEFINITION_MESSAGES_ID = ":pageBodyDialogMessage";

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(ProcessBuilder.class);

    public static final String NAME = "processBuilder";
    public static final String POST_DEPLOY_EVENT = "postDeployEvent";

    @In
    private EventFitter eventFitter;
    @In
    private TransitionFitter transitionFitter;
    @In
    private SwimlaneFitter swimlaneFitter;
    @In
    private TaskFitter taskFitter;
    @In
    private NodeFitter nodeFitter;
    @In
    private ProcessBuilderGraph processBuilderGraph;
    @In
    private JsfComponentTreeValidator jsfComponentTreeValidator;
    @In
    private GenericManager genericManager;
    @In
    private FluxoManager fluxoManager;

    private String id;
    private ProcessDefinition instance;
    private Map<Node, List<TaskHandler>> taskNodeMap;

    private boolean exists;
    private String xml;
    private String tab;
    private boolean needToPublic;

    private Fluxo fluxo;

    private Boolean importacaoConcluida;
    private Set<String> mensagensImportacao;

    public void newInstance() {
        instance = null;
    }

    public void createInstance() {
        id = null;
        exists = false;
        clear();
        instance = ProcessDefinition.createNewProcessDefinition();
        Swimlane laneSolicitante = new Swimlane("solicitante");
        laneSolicitante.setActorIdExpression("#{actor.id}");

        Task startTask = new Task("Tarefa inicial");
        startTask.setSwimlane(laneSolicitante);
        taskFitter.setStarTaskHandler(new TaskHandler(startTask));
        instance.getTaskMgmtDefinition().setStartTask(taskFitter.getStartTaskHandler().getTask());

        StartState startState = new StartState(internacionalize("process.node.first"));
        instance.addNode(startState);
        EndState endState = new EndState(internacionalize("process.node.last"));
        instance.addNode(endState);
        Transition t = new Transition();
        t.setName(endState.getName());
        t.setTo(endState);
        startState.addLeavingTransition(t);
        endState.addArrivingTransition(t);
        instance.getTaskMgmtDefinition().addSwimlane(laneSolicitante);
        eventFitter.addEvents();
        taskFitter.getTasks();
        processBuilderGraph.clear();
    }

    private void clear() {
        taskNodeMap = null;
        swimlaneFitter.clear();
        taskFitter.clear();
        nodeFitter.clear();
        transitionFitter.clear();
        eventFitter.clear();
    }

    public void load(Fluxo fluxo) {
        try {
            internalLoad(fluxo);
        } catch (Exception e) {
            LOG.error(".load()", e);
        }
    }

    private void internalLoad(Fluxo fluxo) throws Exception {
        this.fluxo = fluxo;
        String newId = fluxo.getCodFluxo();
        this.id = null;
        setId(newId);

        getInstance().setName(fluxo.getFluxo());
        xml = fluxo.getXml();
        if (xml == null) {
            this.id = newId;
            update();
        } else {
            instance = parseInstance(xml);
            instance.setName(fluxo.getFluxo());
            exists = true;
            this.id = newId;
        }
    }

    private ProcessDefinition parseInstance(String newXml) {
        StringReader stringReader = new StringReader(newXml);
        InfoxJpdlXmlReader jpdlReader = new InfoxJpdlXmlReader(new InputSource(stringReader));
        return jpdlReader.readProcessDefinition();
    }

    public void prepareUpdate(ActionEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent processDefinitionTabPanel = facesContext.getViewRoot().findComponent(PROCESS_DEFINITION_TABPANEL_ID);
        UIComponent messages = facesContext.getViewRoot().findComponent(PROCESS_DEFINITION_MESSAGES_ID);
        ExtendedPartialViewContext context = ExtendedPartialViewContext.getInstance(facesContext);

        try {
            validateJsfTree();
            validateJbpmGraph();
            validateMailNode();
            validateVariables();
        } catch (IllegalStateException e) {
            FacesMessages.instance().clearGlobalMessages();
            FacesMessages.instance().add(e.getMessage());
            context.getRenderIds().add(messages.getClientId(facesContext));
            throw new AbortProcessingException("processBuilder.prepareUpdate(event)", e);
        }

        context.getRenderIds().add(processDefinitionTabPanel.getClientId(facesContext));
        context.getRenderIds().add(messages.getClientId(facesContext));
    }

    @SuppressWarnings(UNCHECKED)
    private void validateVariables() {
        List<Node> nodes = getInstance().getNodes();
        for (Node node : nodes) {
            if (node.getNodeType().equals(NodeType.Task)) {
                TaskController taskController = ((TaskNode) node).getTask(node.getName()).getTaskController();
                if (taskController != null) {
                    List<VariableAccess> variables = taskController.getVariableAccesses();
                    for (VariableAccess variable : variables) {
                        String[] tokens = variable.getMappedName().split(":");
                        if (tokens.length == 1) {
                            throw new IllegalStateException("Existe uma variável sem nome na tarefa " + node.getName()); 
                        } else if ("null".equals(tokens[0])) {
                            throw new IllegalStateException("A variável " + tokens[1] + " da tarefa " + node.getName() + " não possui tipo");
                        } else if ("date".equals(tokens[0]) && tokens.length < 3) {
                            throw new IllegalStateException("A variável " + tokens[1] + " da tarefa " + node.getName() + " é do tipo data mas não possui tipo de validação");
                        } else if ("enumeracao".equals(tokens[0]) && tokens.length < 3) {
                            throw new IllegalStateException("A variável " + tokens[1] + " da tarefa " + node.getName() + " é do tipo lista de dados mas não possui lista de valores definida");
                        } 
                    }
                }
            }
        }
    }

    @SuppressWarnings(UNCHECKED)
    private void validateMailNode() {
        List<Node> nodes = getInstance().getNodes();
        for (Node node : nodes) {
            if (node instanceof InfoxMailNode) {
                InfoxMailNode mailNode = (InfoxMailNode) node;
                if (Strings.isNullOrEmpty(mailNode.getTo())) {
                    throw new IllegalStateException("O nó de email deve possuir pelo menos um destinatário.");
                }
            }
        }
    }

    @SuppressWarnings(UNCHECKED)
    private void validateJbpmGraph() {
        List<Node> nodes = getInstance().getNodes();
        for (Node node : nodes) {
            if (!node.getNodeType().equals(NodeType.EndState)
                    && (node.getLeavingTransitions() == null || node.getLeavingTransitions().isEmpty())) {
                throw new IllegalStateException("Existe algum nó na definição que não possui transição de saída.");
            }
        }

        Node start = getInstance().getStartState();
        Set<Node> visitedNodes = new HashSet<>();
        if (!findPathToEndState(start, visitedNodes, false)) {
            throw new IllegalStateException("Fluxo mal-definido, não há como alcançar o nó de término.");
        }
    }

    @SuppressWarnings(UNCHECKED)
    private boolean findPathToEndState(Node node, Set<Node> visitedNodes,
            boolean hasFoundEndState) {
        if (node.getNodeType().equals(NodeType.EndState)) {
            return true;
        }

        if (!visitedNodes.contains(node)) {
            visitedNodes.add(node);

            List<Transition> transitions = node.getLeavingTransitions();
            for (Transition t : transitions) {
                if (t.getTo() != null) {
                    hasFoundEndState = findPathToEndState(t.getTo(), visitedNodes, hasFoundEndState);
                }
            }
        }
        return hasFoundEndState;
    }

    private void validateJsfTree() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent processDefinitionTabPanel = facesContext.getViewRoot().findComponent(PROCESS_DEFINITION_TABPANEL_ID);
        if (jsfComponentTreeValidator.hasInvalidComponent(processDefinitionTabPanel)) {
            throw new IllegalStateException("O formulário possui campos inválidos, favor corrigí-los.");
        }
    }

    public void update() {
        exists = true;
        if (fluxo != null) {
            String xmlDef = JpdlXmlWriter.toString(instance);

            String xmlFluxo = fluxo.getXml();

            if (xmlFluxo == null || !xmlFluxo.equals(xmlDef)) {
                // verifica a consistencia do fluxo para evitar salva-lo com
                // erros.
                parseInstance(xmlDef);
                needToPublic = true;
                modifyNodesAndTasks();
                fluxo.setXml(xmlDef);
                try {
                    genericManager.update(fluxo);
                } catch (DAOException e) {
                    LOG.error(".update()", e);
                }
            }
            taskFitter.updateTarefas();
            FacesMessages.instance().add("Fluxo salvo com sucesso!");
        }
        processBuilderGraph.clear();
    }

    public void updateFluxo(String cdFluxo) {
        String xmlDef = JpdlXmlWriter.toString(instance);
        fluxo.setXml(xmlDef);
        try {
            genericManager.update(fluxo);
        } catch (DAOException e) {
            LOG.error(".updateFluxo()", e);
        }

        this.id = cdFluxo;
        this.exists = true;
        this.needToPublic = true;
    }

    private void modifyNodesAndTasks() {
        nodeFitter.modifyNodes();
        taskFitter.modifyTasks();
    }

    public void deploy() {
        update();
        if (needToPublic) {
            try {
                JbpmUtil.getGraphSession().deployProcessDefinition(instance);
                JbpmUtil.getJbpmSession().flush();
                Events.instance().raiseEvent(POST_DEPLOY_EVENT, instance);
                taskFitter.checkCurrentTaskPersistenceState();
                FacesMessages.instance().clear();
                FacesMessages.instance().add("Fluxo publicado com sucesso!");
            } catch (Exception e) {
                LOG.error(".deploy()", e);
            }
            needToPublic = false;
        }
    }

    public void clearDefinition() {
        fluxo.setXml(null);
        load(fluxo);
    }

    public static ProcessBuilder instance() {
        ProcessBuilder returnInstance = (ProcessBuilder) Contexts.getConversationContext().get(NAME);
        if (returnInstance == null) {
            returnInstance = (ProcessBuilder) Component.getInstance(ProcessBuilder.class);
        }
        return returnInstance;
    }

    // --------------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------ Getters and Setters
    // -----------------------------------------------
    // ---------------------------------------------------- ~Comuns~
    // ------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getTab() {
        return tab;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public Map<Node, List<TaskHandler>> getTaskNodeMap() {
        return taskNodeMap;
    }

    public void setTaskNodeMap(Map<Node, List<TaskHandler>> taskNodeMap) {
        this.taskNodeMap = taskNodeMap;
    }

    // --------------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------ Getters and Setters
    // -----------------------------------------------
    // --------------------------------------------------- ~Especiais~
    // ----------------------------------------------------

    public void setId(String newId) {
        boolean changed = !newId.equals(this.id);
        this.id = newId;
        if (changed || instance == null) {
            try {
                createInstance();
            } catch (Exception e) {
                LOG.error(".setId()", e);
            }
        }
    }

    @SuppressWarnings(UNCHECKED)
    public BigInteger getIdProcessDefinition() {
        if (instance == null) {
            return null;
        }
        String query = "select max(id_) from jbpm_processdefinition where name_ = :pdName";
        Query param = JbpmUtil.getJbpmSession().createSQLQuery(query).setParameter("pdName", instance.getName());
        List<Object> list = param.list();
        if (list == null || list.size() == 0) {
            return null;
        }
        return (BigInteger) list.get(0);
    }

    public ProcessDefinition getInstance() {
        if (instance == null) {
            createInstance();
        }
        return instance;
    }

    public void setInstance(ProcessDefinition newInstance) {
        this.instance = newInstance;
    }

    public String getXml() {
        xml = JpdlXmlWriter.toString(instance);
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
        if (xml != null && !xml.trim().equals("")) {
            instance = parseInstance(xml);
        }
        clear();
    }

    public EventFitter getEventFitter() {
        return eventFitter;
    }

    public TransitionFitter getTransitionFitter() {
        return transitionFitter;
    }

    public SwimlaneFitter getSwimlaneFitter() {
        return swimlaneFitter;
    }

    public TaskFitter getTaskFitter() {
        return taskFitter;
    }

    public NodeFitter getNodeFitter() {
        return nodeFitter;
    }

    public Fluxo getFluxo() {
        return this.fluxo;
    }

    public void getPaintedGraph() {
        try {
            getProcessBuilderGraph().paintGraph();
        } catch (IOException e) {
            throw new AbortProcessingException(e);
        }
    }

    public ProcessBuilderGraph getProcessBuilderGraph() {
        return processBuilderGraph;
    }

    public Set<String> getMensagensImportacao() {
        return mensagensImportacao;
    }

    public Boolean getImportacaoConcluida() {
        return importacaoConcluida;
    }

    @SuppressWarnings(UNCHECKED)
    public void importarXPDL(byte[] bytes, Fluxo fluxo) {
        FluxoXPDL fluxoXPDL = null;
        try {
            importacaoConcluida = false;

            fluxoXPDL = FluxoXPDL.createInstance(bytes);
            final String codFluxo = fluxo.getCodFluxo();

            final String xmlDef = fluxoXPDL.toJPDL(codFluxo);
            parseInstance(xmlDef);
            fluxo.setXml(xmlDef);
            FluxoManager fluxoManager = (FluxoManager) Component.getInstance(FluxoManager.NAME);
            fluxoManager.update(fluxo);

            this.importacaoConcluida = true;
        } catch (JpdlException e) {
            List<Problem> problems = e.getProblems();
            mensagensImportacao = new HashSet<>();
            for (Problem object : problems) {
                mensagensImportacao.add(format("{0}", object.toString()));
            }
        } catch (IllegalXPDLException | DAOException e) {
            LOG.error("Erro ao importar arquivo XPDL. " + e.getMessage(), e);
            if (fluxoXPDL != null) {
                mensagensImportacao = fluxoXPDL.getMensagens();
            }
        }
    }

    public void clearImportacao() {
        importacaoConcluida = null;
        mensagensImportacao = null;
    }

    public boolean existemProcessosAssociadosAoFluxo() {
        return fluxoManager.existemProcessosAssociadosAFluxo(getFluxo());
    }
    
    public String getTypeLabel(String type) {
        return VariableType.convertValueOf(type).getLabel();
    }
}
