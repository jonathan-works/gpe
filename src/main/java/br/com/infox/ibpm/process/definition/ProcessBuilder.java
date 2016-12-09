package br.com.infox.ibpm.process.definition;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.jpdl.JpdlException;
import org.jbpm.jpdl.xml.Problem;
import org.richfaces.context.ExtendedPartialViewContext;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.fluxo.crud.FluxoController;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.list.HistoricoProcessDefinitionList;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.processo.timer.manager.TaskExpirationManager;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.jpdl.JpdlXmlWriter;
import br.com.infox.ibpm.process.definition.fitter.EventFitter;
import br.com.infox.ibpm.process.definition.fitter.NodeFitter;
import br.com.infox.ibpm.process.definition.fitter.SwimlaneFitter;
import br.com.infox.ibpm.process.definition.fitter.TaskFitter;
import br.com.infox.ibpm.process.definition.fitter.TransitionFitter;
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
    private JsfComponentTreeValidator jsfComponentTreeValidator;
    @Inject
    private TaskExpirationManager taskExpirationManager;
    @Inject
    private FluxoManager fluxoManager;
    @Inject
    private HistoricoProcessDefinitionList historicoProcessDefinitionList;
    @Inject
    private ProcessDefinitionService processDefinitionService;
    @Inject
    private ActionMessagesService actionMessagesService;
    @Inject
    private FluxoController fluxoController;
 
    private ProcessDefinition instance;
    private String tab;

    private void clear() {
        swimlaneFitter.clear();
        taskFitter.clear();
        nodeFitter.clear();
        transitionFitter.clear();
        eventFitter.clear();
    }

    public void load() {
    	try {
    		if(!FacesContext.getCurrentInstance().isPostback()){
    			load(getFluxo());
    		}
    	} catch (JpdlException e){
    		logJpdlException(e);
		} catch (Exception e) {
			LOG.error("Erro ao carregar o fluxo", e);
		}
    }

    @SuppressWarnings("unchecked")
	private void logJpdlException(JpdlException e) {
		for (Problem problem : (List<Problem>) e.getProblems()) {
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
	}

    public void load(Fluxo fluxo) {
    	try {
    		fluxoController.setFluxo(processDefinitionService.loadDefinicoes(fluxo));
    		fluxo = getFluxo();
	        clear();
	        instance = InfoxJpdlXmlReader.readProcessDefinition(fluxo.getXml());
	        taskFitter.setStarTaskHandler(new TaskHandler(instance.getTaskMgmtDefinition().getStartTask()));
	        taskFitter.getTasks();
	        historicoProcessDefinitionList.refresh();
    	} catch (JpdlException e) {
    		logJpdlException(e);
    	} catch (Exception e) {
    		LOG.error(".load(Fluxo)", e);
        	actionMessagesService.handleGenericException(e);
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
            taskExpirationManager.clearUnusedTaskExpirations(getFluxo(), taskNames);
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
    
    public void update() {
    	Fluxo fluxo = getFluxo();
        if (fluxo != null) {
            String xmlDef = JpdlXmlWriter.toString(instance);
            String xmlFluxo = fluxo.getXml();

            if (xmlFluxo == null || !xmlFluxo.equals(xmlDef)) {
                try {
                	// verifica a consistencia do fluxo para evitar salva-lo com
                    // erros.
                    InfoxJpdlXmlReader.readProcessDefinition(xmlDef);
                    fluxoController.setFluxo(processDefinitionService.atualizarDefinicao(fluxo, xmlDef, taskFitter.getTarefasModificadas()));
                    historicoProcessDefinitionList.refresh();
                    FacesMessages.instance().add("Fluxo salvo com sucesso!");
                } catch (JpdlException e) {
                    logJpdlException(e);
                } catch (Exception e) {
                	LOG.error(".update()", e);
                	actionMessagesService.handleGenericException(e);
				}
            }
        }
    }

    public void clearDefinition() {
    	Fluxo fluxo = getFluxo();
    	fluxo.setXml(null);
    	fluxo.setBpmn(null);
    	fluxo.setSvg(null);
        load(fluxo);
    }

    public static ProcessBuilder instance() {
        return BeanManager.INSTANCE.getReference(ProcessBuilder.class);
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getTab() {
        return tab;
    }

    public Number getIdProcessDefinition() {
        if (instance == null || instance.getName() == null) {
            return null;
        }
        return JbpmUtil.getProcessDefinitionId(instance.getName());
    }

    public ProcessDefinition getInstance() {
        return instance;
    }

    public TransitionFitter getTransitionFitter() {
        return transitionFitter;
    }

    public TaskFitter getTaskFitter() {
        return taskFitter;
    }

    public NodeFitter getNodeFitter() {
        return nodeFitter;
    }

    public Fluxo getFluxo() {
        return fluxoController.getFluxo();
    }

    public void setIdFluxo(Integer idFluxo) {
    	if (idFluxo == null) {
    		fluxoController.setFluxo(null);
    	} else {
    		fluxoController.setFluxo(fluxoManager.find(idFluxo));
    	}
    }
}
