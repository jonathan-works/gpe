package br.com.infox.ibpm.process.definition;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.jpdl.JpdlException;
import org.jbpm.jpdl.xml.Problem;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.richfaces.context.ExtendedPartialViewContext;
import org.xml.sax.InputSource;

import com.google.common.base.Strings;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.RaiaPerfilManager;
import br.com.infox.epp.fluxo.manager.VariavelClassificacaoDocumentoManager;
import br.com.infox.epp.fluxo.merger.model.MergePointsBundle;
import br.com.infox.epp.fluxo.merger.service.FluxoMergeService;
import br.com.infox.epp.fluxo.xpdl.FluxoXPDL;
import br.com.infox.epp.fluxo.xpdl.IllegalXPDLException;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.timer.manager.TaskExpirationManager;
import br.com.infox.epp.tarefa.manager.TarefaJbpmManager;
import br.com.infox.epp.tarefa.manager.TarefaManager;
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
import br.com.infox.ibpm.swimlane.SwimlaneInstanceSearch;
import br.com.infox.ibpm.task.dao.TaskInstanceDAO;
import br.com.infox.ibpm.task.handler.TaskHandler;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.jsf.validator.JsfComponentTreeValidator;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class ProcessBuilder implements Serializable {

    private static final String PROCESS_DEFINITION_TABPANEL_ID = ":processDefinition";
    private static final String PROCESS_DEFINITION_MESSAGES_ID = ":pageBodyDialogMessage";

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(ProcessBuilder.class);

    @Inject
    private EventFitter eventFitter;
    @Inject
    private TransitionFitter transitionFitter;
    @Inject
    private SwimlaneFitter swimlaneFitter;
    @Inject
    private TaskFitter taskFitter;
    @Inject
    private NodeFitter nodeFitter;
    @Inject
    private ProcessBuilderGraph processBuilderGraph;
    @Inject
    private JsfComponentTreeValidator jsfComponentTreeValidator;
    @Inject
    private GenericManager genericManager;
    @Inject
    private RaiaPerfilManager raiaPerfilManager;
    @Inject
    private VariavelClassificacaoDocumentoManager variavelClassificacaoDocumentoManager;
    @Inject
    private ActionMessagesService actionMessagesService;
    @Inject
    private TaskExpirationManager taskExpirationManager;
    @Inject
    private InfoxMessages infoxMessages;
    @Inject
    private FluxoMergeService fluxoMergeService;
    @Inject
    private TaskInstanceDAO taskInstanceDAO;
    @Inject
    private SwimlaneInstanceSearch swimlaneInstanceSearch;
    @Inject
    private ProcessoManager processoManager;
    @Inject
    private TarefaManager tarefaManager;
    @Inject
    private TarefaJbpmManager tarefaJbpmManager;

 
    private String id;
    private ProcessDefinition instance;
    private Map<Node, List<TaskHandler>> taskNodeMap;

    private boolean exists;
    private String xml;
    private String tab;

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
        instance.setKey("key_" + UUID.randomUUID().toString());
        Swimlane laneSolicitante = new Swimlane("solicitante");
        laneSolicitante.setKey("key_" + UUID.randomUUID().toString());
        laneSolicitante.setActorIdExpression("#{actor.id}");

        Task startTask = new Task("Tarefa inicial");
        startTask.setKey("key_" + UUID.randomUUID().toString());
        startTask.setSwimlane(laneSolicitante);
        taskFitter.setStarTaskHandler(new TaskHandler(startTask));
        instance.getTaskMgmtDefinition().setStartTask(taskFitter.getStartTaskHandler().getTask());

        StartState startState = new StartState(infoxMessages.get("process.node.first"));
        startState.setKey("key_" + UUID.randomUUID().toString());
        instance.addNode(startState);
        EndState endState = new EndState(infoxMessages.get("process.node.last"));
        endState.setKey("key_" + UUID.randomUUID().toString());
        instance.addNode(endState);
        Transition t = new Transition();
        t.setKey("key_" + UUID.randomUUID().toString());
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

    @SuppressWarnings("unchecked")
    public void load(){
    	try {
    		if(!FacesContext.getCurrentInstance().isPostback()){
    			internalLoad(getFluxo());
    		}
    	} catch (JpdlException e){
    		for (Problem problem : (List<Problem>)e.getProblems()) {
				int problemLevel = problem.getLevel();
				if (problemLevel == Problem.LEVEL_FATAL || problemLevel == Problem.LEVEL_ERROR){
					FacesMessages.instance().add(Severity.ERROR, problem.getDescription());
					LOG.error(problem);
				} else if (problemLevel == Problem.LEVEL_WARNING){
					LOG.warn(problem);
				} else {
					LOG.info(problem);
				}
			}
		} catch (Exception e) {
			LOG.error(e);
		}
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
            validateSubProcessNode();
            validateTaskExpiration();
        } catch (IllegalStateException e) {
            FacesMessages.instance().clearGlobalMessages();
            FacesMessages.instance().add(e.getMessage());
            context.getRenderIds().add(messages.getClientId(facesContext));
            throw new AbortProcessingException("processBuilder.prepareUpdate(event)", e);
        }

        context.getRenderIds().add(processDefinitionTabPanel.getClientId(facesContext));
        context.getRenderIds().add(messages.getClientId(facesContext));
    }

    private void validateTaskExpiration() {
        Set<String> taskNames = new HashSet<>();
        List<Node> nodes = instance.getNodes();
        for (Node node : nodes) {
            if (node instanceof TaskNode) {
                taskNames.add(node.getName());
            }
        }
        try {
            taskExpirationManager.clearUnusedTaskExpirations(fluxo, taskNames);
        } catch (DAOException de) {
            throw new IllegalStateException(de);
        }
    }

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
                        } else if (VariableType.NULL.name().equals(tokens[0])) {
                            throw new IllegalStateException("A variável " + tokens[1] + " da tarefa " + node.getName() + " não possui tipo");
                        } else if (VariableType.DATE.name().equals(tokens[0]) && tokens.length < 3) {
                            throw new IllegalStateException("A variável " + tokens[1] + " da tarefa " + node.getName() + " é do tipo data mas não possui tipo de validação");
                        } else if (VariableType.ENUMERATION.name().equals(tokens[0]) && tokens.length < 3) {
                            throw new IllegalStateException("A variável " + tokens[1] + " da tarefa " + node.getName() + " é do tipo lista de dados mas não possui lista de valores definida");
                        } else if (VariableType.ENUMERATION_MULTIPLE.name().equals(tokens[0]) && tokens.length < 3) {
                            throw new IllegalStateException("A variável " + tokens[1] + " da tarefa " + node.getName() + " é do tipo lista de dados (múltipla) mas não possui lista de valores definida");
                        } else if (VariableType.FRAGMENT.name().equals(tokens[0]) && tokens.length < 3) {
                            System.out.println(tokens);
                            throw new IllegalStateException(MessageFormat.format(infoxMessages.get("processDefinition.variable.list.error"), tokens[1], node.getName()));
                        } 
                    }
                }
            }
        }
    }

    private void validateMailNode() {
        List<Node> nodes = getInstance().getNodes();
        for (Node node : nodes) {
            if (node instanceof InfoxMailNode) {
                InfoxMailNode mailNode = (InfoxMailNode) node;
                if (Strings.isNullOrEmpty(mailNode.getTo())) {
                    throw new IllegalStateException("O nó de email " + mailNode.getName() + " deve possuir pelo menos um destinatário.");
                }
                if (mailNode.getModeloDocumento() == null) {
                    throw new IllegalStateException("O nó de email " + mailNode.getName() + " deve possuir um modelo de documento associado.");
                }
            }
        }
    }
    
    private void validateSubProcessNode() {
        List<Node> nodes = getInstance().getNodes();
        for (Node node : nodes) {
            if (node instanceof ProcessState) {
                ProcessState subprocess = (ProcessState) node;
                if (Strings.isNullOrEmpty(subprocess.getSubProcessName())) {
                    throw new IllegalStateException("O nó " + subprocess.getName() + " é do tipo subprocesso e deve possuir um fluxo associado.");
                }
            }
        }
    }

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
    
    @Transactional
    public void update() {
        exists = true;
        if (fluxo != null) {
            String xmlDef = JpdlXmlWriter.toString(instance);

            String xmlFluxo = fluxo.getXml();

            if (xmlFluxo == null || !xmlFluxo.equals(xmlDef)) {
                // verifica a consistencia do fluxo para evitar salva-lo com
                // erros.
                parseInstance(xmlDef);
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
    }

    private void modifyNodesAndTasks() {
        nodeFitter.modifyNodes();
        taskFitter.modifyTasks();
    }

    public boolean deploy() {
        String modifiedXml = fluxo.getXml();
        String publishedXml = fluxo.getXmlExecucao();
        boolean needToPublish = !Objects.equals(modifiedXml, publishedXml);
        if (publishedXml != null && needToPublish) {
            ProcessDefinition modifiedProcessDef = fluxoMergeService.jpdlToProcessDefinition(modifiedXml);
            ProcessDefinition publishedProcessDef = fluxoMergeService.jpdlToProcessDefinition(publishedXml);
            MergePointsBundle mergePointsBundle = fluxoMergeService.verifyMerge(publishedProcessDef, modifiedProcessDef);
            if (!mergePointsBundle.isValid()) {
                FacesMessages.instance().add("Não é possível publicar fluxo");
                fluxo.setPublicado(false);
                return false;
            }
        }
        try {
            deployActions();
        } catch (DAOException e1) {
            LOG.error(".deploy()", e1);
            FacesMessages.instance().clear();
            actionMessagesService.handleDAOException(e1);
            fluxo.setPublicado(false);
            return false;
        }
        if (needToPublish) {
            try {
                JbpmUtil.getGraphSession().deployProcessDefinition(instance);
                JbpmUtil.getJbpmSession().flush();
                fluxo.setXmlExecucao(fluxo.getXml());
                if (!fluxo.getPublicado()){
                    fluxo.setPublicado(Boolean.TRUE);
                }
                try {
                    genericManager.update(fluxo);
                } catch (DAOException e) {
                    LOG.error(".update()", e);
                }
                updatePostDeploy(instance);
                taskFitter.checkCurrentTaskPersistenceState();
                atualizarRaiaPooledActors(instance.getId());
                atualizarTimer();
                FacesMessages.instance().clear();
                FacesMessages.instance().add("Fluxo publicado com sucesso!");
            } catch (Exception e) {
                LOG.error(".deploy()", e);
                return false;
            }
        }
        return true;
    }
    
    public void updatePostDeploy(ProcessDefinition processDefinition) throws DAOException {
        processoManager.atualizarProcessos(processDefinition.getId(), processDefinition.getName());
        tarefaManager.encontrarNovasTarefas();
        tarefaJbpmManager.inserirVersoesTarefas();
    }

    private void deployActions() throws DAOException {
        raiaPerfilManager.atualizarRaias(fluxo, instance.getTaskMgmtDefinition().getSwimlanes());
        Integer idFluxo = fluxo.getIdFluxo();
        List<String> variaveis = getVariaveisDocumento();
        variavelClassificacaoDocumentoManager.removerClassificacoesDeVariaveisObsoletas(idFluxo, variaveis);
        variavelClassificacaoDocumentoManager.publicarClassificacoesDasVariaveis(idFluxo);
    }

    private void atualizarRaiaPooledActors(Long idProcessDefinition) {
       EntityManager entityManager = EntityManagerProducer.instance().getEntityManagerNotManaged();
       try {
           List<SwimlaneInstance> swimlaneInstances = swimlaneInstanceSearch.getSwimlaneInstancesByProcessDefinition(idProcessDefinition, entityManager);
           for (SwimlaneInstance swimlaneInstance : swimlaneInstances) {
               String[] pooledActorIds = swimlaneInstance.getSwimlane().getPooledActorsExpression().split(",");
               swimlaneInstance.setPooledActors(pooledActorIds);
           }
           entityManager.flush();
           entityManager.clear();
           List<TaskInstance> taskInstances = taskInstanceDAO.getTaskInstancesOpen(idProcessDefinition, entityManager);
           for (TaskInstance taskInstance : taskInstances) {
               ExecutionContext executionContext = new ExecutionContext(taskInstance.getToken());
               taskInstance.assign(executionContext);
           }
           entityManager.flush();
       } finally {
           if (entityManager.isOpen()) {
               entityManager.close();
           }
       }
	}
    
    private void atualizarTimer() throws Exception {
        JbpmUtil.instance().deleteTimers(instance);
        JbpmUtil.instance().createTimers(instance);
    }

    private List<String> getVariaveisDocumento() {
        List<String> variaveis = new ArrayList<>();
        List<Node> nodes = instance.getNodes();
        for (Node node : nodes) {
            if (!(node instanceof TaskNode)) {
                continue;
            }
            TaskNode taskNode = (TaskNode) node;
            Set<Task> tasks = taskNode.getTasks();
            for (Task task : tasks) {
                if (task.getTaskController() == null) {
                    continue;
                }
                List<VariableAccess> variableAccesses = task.getTaskController().getVariableAccesses();
                for (VariableAccess variableAccess : variableAccesses) {
                    String[] mappedName = variableAccess.getMappedName().split(":");
                    VariableType type = VariableType.valueOf(mappedName[0]);
                    if (type == VariableType.EDITOR || type == VariableType.FILE) {
                        variaveis.add(variableAccess.getVariableName());
                    }
                }
            }
        }
        return variaveis;
    }

    public void clearDefinition() {
        fluxo.setXml(null);
        load(fluxo);
    }

    public static ProcessBuilder instance() {
        return BeanManager.INSTANCE.getReference(ProcessBuilder.class);
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

    public Number getIdProcessDefinition() {
        if (instance == null || instance.getName() == null) {
            return null;
        }
        return JbpmUtil.getProcessDefinitionId(instance.getName());
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

    public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
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
        mensagensImportacao = new HashSet<>();
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
        } catch (IllegalXPDLException | IllegalArgumentException | DAOException e) {
            LOG.error("Erro ao importar arquivo XPDL. " + e.getMessage(), e);
            if ((e instanceof IllegalXPDLException || e instanceof IllegalArgumentException) && e.getMessage() != null) {
                mensagensImportacao.add(e.getMessage());
            }
            if (fluxoXPDL != null) {
                mensagensImportacao.addAll(fluxoXPDL.getMensagens());
                StringBuilder sb = new StringBuilder("Foram encontrados erros ao importar o XPDL:\n");
                for (String mensagem : mensagensImportacao) {
                    sb.append("\t");
                    sb.append(mensagem);
                    sb.append("\n");
                }
                LOG.error(sb.toString());
            }
        }
    }

    public void clearImportacao() {
        importacaoConcluida = null;
        mensagensImportacao = null;
    }

    public boolean existemProcessosAssociadosAoFluxo() {
        return fluxoMergeService.hasActiveNode(getInstance(), nodeFitter.getCurrentNode());
    }
    
    public String getTypeLabel(String type) {
        return VariableType.valueOf(type).getLabel();
    }
}
