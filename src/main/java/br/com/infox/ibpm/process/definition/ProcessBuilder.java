package br.com.infox.ibpm.process.definition;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.jpdl.JpdlException;
import org.jbpm.jpdl.xml.Problem;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.richfaces.context.ExtendedPartialViewContext;
import org.xml.sax.InputSource;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.manager.GenericManager;
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
import br.com.infox.epp.modeler.converter.BpmnJpdlService;
import br.com.infox.epp.modeler.converter.JpdlBpmnConverter;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.timer.manager.TaskExpirationManager;
import br.com.infox.epp.tarefa.manager.TarefaJbpmManager;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.jpdl.JpdlXmlWriter;
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

    private static final String MODELADOR_FORM_ID = ":modeladorForm";
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
    @Inject
    private FluxoManager fluxoManager;
    @Inject
    private BpmnJpdlService bpmnJpdlService;
    
 
    private String id;
    private ProcessDefinition instance;
    private Map<Node, List<TaskHandler>> taskNodeMap;

    private boolean exists;
    private String xml;
    private String tab;

    private Fluxo fluxo;

    public void newInstance() {
        instance = null;
    }

    public void createInstance() {
        id = null;
        exists = false;
        clear();
        instance = bpmnJpdlService.createInitialProcessDefinition(getFluxo().getFluxo());
        taskFitter.setStarTaskHandler(new TaskHandler(instance.getTaskMgmtDefinition().getStartTask()));
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
			LOG.error("Erro ao carregar o fluxo", e);
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
            if (fluxo.getBpmn() != null) {
            	BpmnModelInstance bpmnModel = Bpmn.readModelFromStream(new ByteArrayInputStream(fluxo.getBpmn().getBytes(StandardCharsets.UTF_8)));
            	bpmnJpdlService.atualizarNomeFluxo(fluxo, bpmnModel, instance);
            	fluxo.setBpmn(Bpmn.convertToString(bpmnModel));
            }
        }
        if (this.fluxo.getBpmn() == null && this.fluxo.getXml() != null) {
        	this.fluxo.setBpmn(new JpdlBpmnConverter().convert(this.fluxo.getXml()));
        	this.fluxo = fluxoManager.update(this.fluxo);
        }
        nodeFitter.clear();
        transitionFitter.clear();
    }

    private ProcessDefinition parseInstance(String newXml) {
        StringReader stringReader = new StringReader(newXml);
        InfoxJpdlXmlReader jpdlReader = new InfoxJpdlXmlReader(new InputSource(stringReader));
        try {
        	return jpdlReader.readProcessDefinition();
        } catch (JpdlException e) {
        	for (Object p : e.getProblems()) {
        		System.out.println(((Problem) p).getDescription());
        	}
        	throw e;
        }
    }

    public void prepareUpdate(ActionEvent event) {
        ExtendedPartialViewContext context = ExtendedPartialViewContext.getInstance(FacesContext.getCurrentInstance());

        try {
            validateJsfTree();
            validateTaskExpiration();
        } catch (IllegalStateException e) {
            FacesMessages.instance().clearGlobalMessages();
            FacesMessages.instance().add(e.getMessage());
            context.getRenderIds().add(PROCESS_DEFINITION_MESSAGES_ID);
            throw new AbortProcessingException("processBuilder.prepareUpdate(event)", e);
        }

        context.getRenderIds().add(PROCESS_DEFINITION_TABPANEL_ID);
        context.getRenderIds().add(MODELADOR_FORM_ID);
        context.getRenderIds().add(PROCESS_DEFINITION_MESSAGES_ID);
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
                fluxo.setXml(xmlDef);
                try {
                    fluxo = (Fluxo) genericManager.update(fluxo);
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
        fluxo.setBpmn(null);
        fluxo.setSvg(null);
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

    public ProcessBuilderGraph getProcessBuilderGraph() {
        return processBuilderGraph;
    }

    public boolean existemProcessosAssociadosAoFluxo() {
        return fluxoMergeService.hasActiveNode(getInstance(), nodeFitter.getCurrentNode());
    }
    
    public String getTypeLabel(String type) {
        return VariableType.valueOf(type).getLabel();
    }
    
    public void setIdFluxo(Integer idFluxo) {
    	if (idFluxo == null) {
    		fluxo = null;
    	} else {
    		fluxo = fluxoManager.find(idFluxo);
    	}
    }
}
