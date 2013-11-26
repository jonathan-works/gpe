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
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.StartState;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.richfaces.context.ExtendedPartialViewContext;
import org.xml.sax.InputSource;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.validator.JsfComponentTreeValidator;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.xpdl.FluxoXPDL;
import br.com.infox.epp.fluxo.xpdl.IllegalXPDLException;
import br.com.infox.ibpm.jbpm.fitter.EventFitter;
import br.com.infox.ibpm.jbpm.fitter.NodeFitter;
import br.com.infox.ibpm.jbpm.fitter.SwimlaneFitter;
import br.com.infox.ibpm.jbpm.fitter.TaskFitter;
import br.com.infox.ibpm.jbpm.fitter.TransitionFitter;
import br.com.infox.ibpm.jbpm.fitter.TypeFitter;
import br.com.infox.ibpm.jbpm.handler.TaskHandler;

@Name(ProcessBuilder.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessBuilder implements Serializable {

	private static final String PROCESS_DEFINITION_BUTTONS_FORM_ID = ":processDefinitionButtonsForm";
	private static final String PROCESS_DEFINITION_TABPANEL_ID = ":processDefinition";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(ProcessBuilder.class);

	public static final String NAME = "processBuilder";
	public static final String POST_DEPLOY_EVENT = "postDeployEvent";
	
	@In	private EventFitter eventFitter;
	@In	private TransitionFitter transitionFitter;
	@In private SwimlaneFitter swimlaneFitter;
	@In private TaskFitter taskFitter;
	@In private NodeFitter nodeFitter;
	@In private TypeFitter typeFitter;
	@In	private ProcessBuilderGraph processBuilderGraph;
	@In private JsfComponentTreeValidator jsfComponentTreeValidator;
	@In private GenericManager genericManager;

	private String id;
	private ProcessDefinition instance;
	private Map<Node, List<TaskHandler>> taskNodeMap;

	private boolean exists;
	private String xml;
	private String tab;
	private boolean needToPublic;
	
	private Fluxo fluxo;

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
		
		StartState startState = new StartState("Início");
		instance.addNode(startState);
		EndState endState = new EndState("Término");
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
			try {
				instance = parseInstance(xml);
				instance.setName(fluxo.getFluxo());
			} catch (Exception e) {
			    LOG.error(".load()", e);
			}
			exists = true;
			this.id = newId;
		}
	}

	private ProcessDefinition parseInstance(String newXml) {
		StringReader stringReader = new StringReader(newXml);
		JpdlXmlReader jpdlReader = new JpdlXmlReader(new InputSource(
				stringReader));
		return jpdlReader.readProcessDefinition();
	}
	
	public void prepareUpdate(ActionEvent event) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		UIComponent processDefinitionTabPanel = facesContext.getViewRoot().findComponent(PROCESS_DEFINITION_TABPANEL_ID);
		UIComponent buttonsForm = facesContext.getViewRoot().findComponent(PROCESS_DEFINITION_BUTTONS_FORM_ID);
		ExtendedPartialViewContext context = ExtendedPartialViewContext.getInstance(facesContext);
		
		try {
			validateJsfTree();
			validateJbpmGraph();
		} catch (IllegalStateException e) {
			FacesMessages.instance().clearGlobalMessages();
			FacesMessages.instance().add(e.getMessage());
			context.getRenderIds().add(buttonsForm.getClientId(facesContext));
			throw new AbortProcessingException(e);
		}
		
		context.getRenderIds().add(processDefinitionTabPanel.getClientId(facesContext));
		context.getRenderIds().add(buttonsForm.getClientId(facesContext));
	}

	@SuppressWarnings(WarningConstants.UNCHECKED)
	private void validateJbpmGraph() {
		List<Node> nodes = getInstance().getNodes();
		for (Node node : nodes) {
			if (!node.getNodeType().equals(NodeType.EndState) && (node.getLeavingTransitions() == null || node.getLeavingTransitions().isEmpty())) {
				throw new IllegalStateException("Existe algum nó na definição que não possui transição de saída.");
			}
		}
		
		Node start = getInstance().getStartState();
		Set<Node> visitedNodes = new HashSet<>();
		if (!findPathToEndState(start, visitedNodes, false)) {
			throw new IllegalStateException("Fluxo mal-definido, não há como alcançar o nó de término.");
		}
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	private boolean findPathToEndState(Node node, Set<Node> visitedNodes, boolean hasFoundEndState) {
		if (node.getNodeType().equals(NodeType.EndState)) {
			return true;
		}
		
		if (!visitedNodes.contains(node)) {
			visitedNodes.add(node);
		
			List<Transition> transitions = node.getLeavingTransitions();
			for (Transition t : transitions) {
				hasFoundEndState = findPathToEndState(t.getTo(), visitedNodes, hasFoundEndState);
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
		clear();
		createInstance();
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
	// ------------------------------------------------ Getters and Setters -----------------------------------------------
	// ---------------------------------------------------- ~Comuns~ ------------------------------------------------------

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
	// ------------------------------------------------ Getters and Setters -----------------------------------------------
	// --------------------------------------------------- ~Especiais~ ----------------------------------------------------

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

	@SuppressWarnings(WarningConstants.UNCHECKED)
	public BigInteger getIdProcessDefinition() {
		if (instance == null) {
			return null;
		}
		String query = "select max(id_) from jbpm_processdefinition where name_ = :pdName";
		Query param = JbpmUtil.getJbpmSession().createSQLQuery(query)
				.setParameter("pdName", instance.getName());
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
	
	public String getTypeLabel(String type){
		return typeFitter.getTypeLabel(type);
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

	public TypeFitter getTypeFitter() {
		return typeFitter;
	}
	
	public Fluxo getFluxo(){
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
	
	public void importarXPDL(byte[] bytes, Fluxo fluxo) {
        try {
            load(fluxo);
            final FluxoXPDL fluxoXPDL = FluxoXPDL.createInstance(bytes);
            final String xml = fluxoXPDL.toJPDL(fluxo.getCodFluxo());
            setXml(xml); 
            updateFluxo(fluxo.getCodFluxo());
        } catch (IllegalXPDLException e) {
            LOG.error("Erro ao importar arquivo XPDL. " + e.getMessage());
        }
    }
}