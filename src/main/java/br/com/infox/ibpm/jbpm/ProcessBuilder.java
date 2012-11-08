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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.NodeTypes;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.xml.sax.InputSource;

import br.com.infox.bpm.action.TaskPageAction;
import br.com.infox.ibpm.bean.PrazoTask;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.entity.Tarefa;
import br.com.infox.ibpm.home.FluxoHome;
import br.com.infox.ibpm.jbpm.converter.NodeConverter;
import br.com.infox.ibpm.jbpm.handler.EventHandler;
import br.com.infox.ibpm.jbpm.handler.NodeHandler;
import br.com.infox.ibpm.jbpm.handler.SwimlaneHandler;
import br.com.infox.ibpm.jbpm.handler.TaskHandler;
import br.com.infox.ibpm.jbpm.handler.TransitionHandler;
import br.com.infox.ibpm.jbpm.handler.VariableAccessHandler;
import br.com.infox.ibpm.jbpm.node.MailNode;
import br.com.infox.ibpm.type.PrazoEnum;
import br.com.infox.jbpm.layout.JbpmLayout;
import br.com.itx.util.EntityUtil;
import br.com.itx.util.FacesUtil;
import br.com.itx.util.FileUtil;
import br.com.itx.util.ReflectionsUtil;


@Name(ProcessBuilder.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class ProcessBuilder implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final LogProvider LOG = Logging.getLogProvider(ProcessBuilder.class);
	
	public static final String NAME = "processBuilder";
	public static final String POST_DEPLOY_EVENT = "postDeployEvent";
	public static final String SET_CURRENT_NODE_EVENT = "ProcessBuilder.setCurrentNode"; 
	
	private String id;
	private ProcessDefinition instance;
	private List<SwimlaneHandler> swimlanes;
	private TaskHandler startTaskHandler;
	private Map<Node, List<TaskHandler>> taskNodeMap;
	private List<Node> nodes;
	private List<SelectItem> nodesItems;
	private List<SelectItem> transitionsItems;
	private Map<Node, String> nodeMessageMap = new HashMap<Node, String>();
	private Node oldNodeTransition;
	private List<EventHandler> eventList;
	private String newNodeName;
	private String newNodeType = "Task";
	private Node newNodeAfter;
	private TransitionHandler newNodeTransition;
	private Transition currentTransition = new Transition();
	private Node currentNode;
	private TaskHandler currentTask;
	private EventHandler currentEvent;
	private SwimlaneHandler currentSwimlane;
	private NodeHandler nodeHandler;
	private boolean exists;
	private String xml;
	private List<TransitionHandler> arrivingTransitions;
	private List<TransitionHandler> leavingTransitions;
	private List<TransitionHandler> transitionList;
	private List<String[]> transitionNames;
	private List<String> typeList;
	private Properties types;
	private String tab;
	private transient JbpmLayout layout;
	private String nodeName;
	private String taskName;
	private Map<BigInteger, String> modifiedTasks = new HashMap<BigInteger, String>();
	private Map<BigInteger, String> modifiedNodes = new HashMap<BigInteger, String>();
	private boolean needToPublic;
	private Integer prazo;
	private PrazoEnum tipoPrazo;
	private Map<String, PrazoTask> prazoTaskMap = new HashMap<String, PrazoTask>();
	
	public String getId() {
		return id;
	}

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
		startTaskHandler = new TaskHandler(startTask);
		instance.getTaskMgmtDefinition().setStartTask(startTaskHandler.getTask());
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
		addEvents();
		getTasks();
		layout = null;
	}

	/**
	 * Metodo que adiciona o tratamento de eventos 
	 */
	private void addEvents() {
		for (String e : ProcessDefinition.supportedEventTypes) {
			addEvent(e, "br.com.infox.ibpm.util.JbpmEvents.raiseEvent(executionContext)", new Script());
		}
	}

	private void addEvent(String eventType, String expression, Action action) {
		Event event = instance.getEvent(eventType);
		if (event == null) {
			event = new Event(eventType);
			instance.addEvent(event);
		}
		action.setAsync(false);
		if (action instanceof Script) {
			Script script = (Script) action;
			script.setExpression(expression);
		} else {
			action.setActionExpression(expression);		
		}
		event.addAction(action);
	}

	private void clear() {
		swimlanes = null;
		taskNodeMap = null;
		currentEvent = null;
		currentNode = null;
		currentTask = null;
		eventList = null; 
		nodes = null;
		nodesItems = null;
		transitionList = null;
		transitionsItems = null;
	}

	public void load(String newId) {
		this.id = null;
		setId(newId);
		FluxoHome fluxoHome = FluxoHome.instance();
		if (fluxoHome != null && fluxoHome.isManaged()) {
			getInstance().setName(fluxoHome.getInstance().getFluxo());
			xml = fluxoHome.getInstance().getXml();
			if (xml == null) {
				this.id = newId;
				update();
			} else {
				try {
					instance = parseInstance(xml);
					layout = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
				exists = true;
				this.id = newId;
			}
		}
	}
	
	private ProcessDefinition parseInstance(String newXml) {
	    StringReader stringReader = new StringReader(newXml);
	    JpdlXmlReader jpdlReader = new JpdlXmlReader(new InputSource(stringReader));
		return jpdlReader.readProcessDefinition();
	}

	public void setId(String newId) {
		boolean changed = !newId.equals(this.id);
		this.id = newId;
		if (changed || instance == null) {
			try {
				createInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
	
	public List<EventHandler> getEventList() {
		if (eventList == null) {
			eventList = EventHandler.createList(instance);
			if (eventList.size() == 1) {
				setCurrentEvent(eventList.get(0));
			}
		}
		return eventList;
	}

	public EventHandler getCurrentEvent() {
		return currentEvent;
	}
	
	public void setCurrentEvent(EventHandler cEvent) {
		this.currentEvent = cEvent;
	}
	
	public void addEvent() {
		Event event = new Event("new-event");
		currentEvent = new EventHandler(event);
		eventList.add(currentEvent);
		instance.addEvent(event);
	}

	public void removeEvent(EventHandler e) {
		eventList.remove(e);
		instance.removeEvent(e.getEvent());
		currentEvent = null;
	}

	public String getEventType() {
		if (currentEvent == null) {
			return null;
		}
		return currentEvent.getEvent().getEventType();
	}
	
	public void setEventType(String type) {
		Event event = currentEvent.getEvent();
		instance.removeEvent(event);
		ReflectionsUtil.setValue(event, "eventType", type);
		instance.addEvent(event);
	}


	public List<String> getSupportedEventTypes() {
		List<String> list = new ArrayList<String>();
		String[] eventTypes = instance.getSupportedEventTypes();
		List<String> currentEvents = new ArrayList<String>();
		Collection<Event> values = instance.getEvents().values();
		for (Event event : values) {
			currentEvents.add(event.getEventType());
		}
		for (String type : eventTypes) {
			if (!currentEvents.contains(type)) {
				list.add(type);
			}
		}
		return list;
	}
	
	public List<SwimlaneHandler> getSwimlanes() {
		if (swimlanes == null) {
			swimlanes = SwimlaneHandler.createList(getInstance());
		}
		return swimlanes;
	}
	 	
	public List<String> getSwimlaneList() {
		Map<String, Swimlane> swimlaneList = instance.getTaskMgmtDefinition().getSwimlanes();
		if (swimlaneList == null) {
			return null;
		}
		return new ArrayList<String>(swimlaneList.keySet());
	}
	
	public SwimlaneHandler getCurrentSwimlane() {
		return currentSwimlane;
	}
	
	public void setCurrentSwimlane(SwimlaneHandler cSwimlane) {
		this.currentSwimlane = cSwimlane;
	}
	
	public void addSwimlane() {
		Swimlane s = new Swimlane("Raia " + (swimlanes.size() + 1));
		currentSwimlane = new SwimlaneHandler(s);
		instance.getTaskMgmtDefinition().addSwimlane(s);
		swimlanes.add(currentSwimlane);
	}
	
	public void removeSwimlane(SwimlaneHandler s) {
		swimlanes.remove(s);
		currentSwimlane = null;
		Map<String, Swimlane> swimlaneMap = instance.getTaskMgmtDefinition().getSwimlanes();
		swimlaneMap.remove(s.getSwimlane().getName());
	}
	
	public TaskHandler getStartTask() {
		if (startTaskHandler == null) {
			Task startTask = instance.getTaskMgmtDefinition().getStartTask();
			startTaskHandler = new TaskHandler(startTask);
		}
		return startTaskHandler;
	}

	public void update() {
		exists = true;
		FluxoHome fluxoHome = FluxoHome.instance();
		if (fluxoHome != null && fluxoHome.isManaged()) {
			String xmlDef = JpdlXmlWriter.toString(instance);
			
			String xmlFluxo = fluxoHome.getInstance().getXml();
			
			if(xmlFluxo != null && !xmlFluxo.equals(xmlDef)) {
				//verifica a consistencia do fluxo para evitar salva-lo com erros.
				parseInstance(xmlDef);
				needToPublic = true;
				modifyNodesAndTasks();
				fluxoHome.getInstance().setXml(xmlDef);
				fluxoHome.update();
			}
			
			updatePrazoTask();
			FacesMessages.instance().add("Fluxo salvo com sucesso!");
		}
		layout = null;
	}

	public void updateFluxo() {
		String xmlDef = JpdlXmlWriter.toString(instance);
		FluxoHome fluxoHome = FluxoHome.instance();
		fluxoHome.getInstance().setXml(xmlDef);
		fluxoHome.update();
		
		needToPublic = true;
	}
	
	private void modifyNodesAndTasks() {
		String update;
		Query q;
		if(modifiedNodes.size() > 0) {
			update = "update jbpm_node set name_ = :nodeName where id_ = :nodeId";
			q = JbpmUtil.getJbpmSession().createSQLQuery(update);
			for(Entry<BigInteger, String> e : modifiedNodes.entrySet()) {
				q.setParameter("nodeName", e.getValue());
				q.setParameter("nodeId", e.getKey());
				q.executeUpdate();
			}
		}
		if(modifiedTasks.size() > 0) {
			update = "update jbpm_task set name_ = :taskName where id_ = :taskId";
			q = JbpmUtil.getJbpmSession().createSQLQuery(update);
			for(Entry<BigInteger, String> e : modifiedTasks.entrySet()) {
				q.setParameter("taskName", e.getValue());
				q.setParameter("taskId", e.getKey());
				q.executeUpdate();
			}
		}
		JbpmUtil.getJbpmSession().flush();
		modifiedNodes = new HashMap<BigInteger, String>();
		modifiedTasks = new HashMap<BigInteger, String>();
	}

	public void deploy() {
		update();
		if(needToPublic) {
			try {
				JbpmUtil.getGraphSession().deployProcessDefinition(instance);
				JbpmUtil.getJbpmSession().flush();
				Events.instance().raiseEvent(POST_DEPLOY_EVENT, instance);
				FacesMessages.instance().clear();
				FacesMessages.instance().add("Fluxo publicado com sucesso!");
			} catch (Exception e) {
				e.printStackTrace();
			}
			needToPublic = false;
		}
	}
	
	private void updatePrazoTask() {
		Fluxo fluxoInstance = FluxoHome.instance().getInstance();
		Set<Entry<String,PrazoTask>> entrySet = prazoTaskMap.entrySet();
		EntityManager entityManager = EntityUtil.getEntityManager();
		for (Entry<String, PrazoTask> entry : entrySet) {
			Tarefa t = JbpmUtil.getTarefa(entry.getKey(), fluxoInstance.getFluxo());
			if(t != null) {
				PrazoTask prazoTask = entry.getValue();
				t.setPrazo(prazoTask.getPrazo());
				t.setTipoPrazo(prazoTask.getTipoPrazo());
				entityManager.merge(t);
			}
		}
		entityManager.flush();
	}

	@Factory("processNodes")
	public List<Node> getNodes() {
		if (nodes ==  null) {
			nodes = new ArrayList<Node>();
			List<Node> list = instance.getNodes();
			if (list != null) {
				for (Node node : list) {
					nodes.add(node);
				}
			}
		} 
		checkTransitions();
		return nodes;
	}
	
	public List<SelectItem> getNodesItems() {
		if(nodesItems == null) {
			List<Node> list = instance.getNodes();
			if (list != null) {
				nodesItems = new ArrayList<SelectItem>();
				nodesItems.add(new SelectItem(null, "Selecione uma tarefa..."));
				for (Node node : list) {
					nodesItems.add(new SelectItem(node.toString(), node.getName()));
				}
			}
		}
		return nodesItems;
	}
	
	public void setNodesItems(List<SelectItem> nodesList) {
		this.nodesItems = nodesList;
	}

	public List<Node> getNodes(String type) {
		List<Node> nodeList = new ArrayList<Node>(nodes);
		for (Iterator<Node> iterator = nodeList.iterator(); iterator.hasNext();) {
			Node n = iterator.next();
			if (type.equals("from") && (n instanceof EndState)) {
				iterator.remove();
			}
			if (type.equals("to") && (n instanceof StartState)) {
				iterator.remove();
			}
		}
		return nodeList;
	}
	
	public List<SelectItem> getNodesTransitionItems(String type) {
		List<SelectItem> nodeItemsList = new ArrayList<SelectItem>();
		nodeItemsList.add(new SelectItem(null, "Selecione..."));
		for (Node node : getNodes(type)) {
			nodeItemsList.add(new SelectItem(node, node.getName()));
		}
		return nodeItemsList;
	}
	
	public NodeHandler getNodeHandler() {
		return nodeHandler;
	}
	 
	public List<TransitionHandler> getArrivingTransitions() {
		if (arrivingTransitions == null) {
			if (currentNode != null && currentNode.getArrivingTransitions() != null) {
				arrivingTransitions = TransitionHandler.getList(currentNode.getArrivingTransitions());
			}
		}
		return arrivingTransitions;
	}
	
	public List<TransitionHandler> getLeavingTransitions() {
		if (leavingTransitions == null) {
			if (currentNode != null && currentNode.getLeavingTransitions() != null) {
				leavingTransitions = TransitionHandler.getList(currentNode.getLeavingTransitions());
			}
		}
		return leavingTransitions;
	}

	public void removeTransition(TransitionHandler th, String type) {
		Transition t = th.getTransition();
		if (type.equals("from") && t.getFrom() != null) {
			t.getFrom().removeLeavingTransition(t);
		} else if (type.equals("to") && t.getTo() != null) {
			t.getTo().removeArrivingTransition(t);
		}
		leavingTransitions = null;
		arrivingTransitions = null;
		currentNode.removeArrivingTransition(t);
		currentNode.removeLeavingTransition(t);
		checkTransitions();
	}
	
	public void addTransition(String type) {
		Transition t = new Transition("");
		if (type.equals("from")) {
			currentNode.addArrivingTransition(t);
			if (arrivingTransitions == null) {
				arrivingTransitions = new ArrayList<TransitionHandler>();
			}
			arrivingTransitions.add(new TransitionHandler(t));
		} else if (type.equals("to")) {
			currentNode.addLeavingTransition(t);
			if (leavingTransitions == null) {
				leavingTransitions = new ArrayList<TransitionHandler>();
			}
			leavingTransitions.add(new TransitionHandler(t));
		} 
		checkTransitions();
	}
	
	public void transitionChangeListener(ValueChangeEvent e) {
		oldNodeTransition =  NodeConverter.getAsObject((String) e.getOldValue());
	}
	
	public void changeTransition(TransitionHandler th, String type) {
		Transition t = th.getTransition();
		if (type.equals("from")) {
			if (t.getFrom() != null) {
				t.getFrom().addLeavingTransition(t);
			}
			if (oldNodeTransition != null) {
				oldNodeTransition.removeLeavingTransition(t);
			}
		} else {
			Node to = t.getTo();
			if (to != null) {
				to.addArrivingTransition(t);
			}
			if (oldNodeTransition != null) {
				oldNodeTransition.removeArrivingTransition(t);
			}
			t.setTo(to);
		}
		if (t.getName() == null || t.getName().equals("")) {
			try {
				t.setName(t.getTo().getName());
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
	public List<TaskHandler> getTasks() {
		List<TaskHandler> taskList = new ArrayList<TaskHandler>();
		if (currentNode instanceof TaskNode) {
			TaskNode node = (TaskNode) currentNode;
			if (taskNodeMap == null) {
				taskNodeMap = new HashMap<Node, List<TaskHandler>>();
			}
			taskList = taskNodeMap.get(node);
			if (taskList == null) {
				taskList = TaskHandler.createList(node);
				taskNodeMap.put(node, taskList);
			}
			if (! taskList.isEmpty() && currentTask == null) {
				setCurrentTask(taskList.get(0));
			}
		} else if (currentNode instanceof StartState) {
			Task startTask = instance.getTaskMgmtDefinition().getStartTask();
			startTaskHandler = new TaskHandler(startTask);
			taskList.add(startTaskHandler);
			if (! taskList.isEmpty() && currentTask == null) {
				setCurrentTask(taskList.get(0));
			}
		}
		return taskList;
	}
	
	public void addTask() {
		if (currentNode instanceof TaskNode) {
			getTasks();
			TaskNode tn = (TaskNode) currentNode;
			Task t = new Task();
			t.setProcessDefinition(instance);
			t.setTaskMgmtDefinition(instance.getTaskMgmtDefinition());
			List<TaskHandler> list = taskNodeMap.get(currentNode);
			t.setName(currentNode.getName());
			tn.addTask(t);
			tn.setEndTasks(true);
			t.setSwimlane(instance.getTaskMgmtDefinition().getSwimlanes().values().iterator().next());
			TaskHandler th = new TaskHandler(t);
			list.add(th);
			currentTask = th;
		}
	}

	public void removeTask(TaskHandler t) {
		if (currentNode instanceof TaskNode) {
			TaskNode tn = (TaskNode) currentNode;
			tn.getTasks().remove(t.getTask());
			taskNodeMap.remove(currentNode);
		}
	}
	
	public TaskHandler getCurrentTask() {
		return currentTask;
	}
	
	public void setCurrentTask(TaskHandler cTask) {
		this.currentTask = cTask;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getTypeList() {
		if (typeList == null) {
			String path = FacesUtil.getServletContext(null).getRealPath("/WEB-INF/xhtml/components/jbpmComponents.properties");
			types = new Properties();
			FileInputStream input = null;
			try {
				input = new FileInputStream(path);
				types.load(input);
				typeList = new ArrayList(types.keySet());
				verifyAvaliableTypes(typeList);
				Collections.sort(typeList, new Comparator<String>() {
					
					@Override
					public int compare(String o1, String o2) {
						if(o1.equals("null")) {
							return -1;
						}
						if(o2.equals("null")) {
							return 1;
						}
						return types.getProperty(o1).compareTo(types.getProperty(o2));
					}
					
				});
			} catch (Exception e) {
				FacesMessages.instance().add(Severity.ERROR, "Erro ao carregar a lista de componentes: {0}", e);
				e.printStackTrace();
			} finally {
				FileUtil.close(input);
			}
		}
		return typeList;
	}
	
	public void setTypeList(List<String> tList) {
		this.typeList = tList;
	}

	private void verifyAvaliableTypes(List<String> tList) {
		if(currentTask != null) {
			for(VariableAccessHandler vah : currentTask.getVariables()) {
				if(vah.getType().equals(TaskPageAction.TASK_PAGE_COMPONENT_NAME)) {
					removeDifferentType(TaskPageAction.TASK_PAGE_COMPONENT_NAME, tList);
					break;
				} else if(!vah.getType().equals("null")){
					tList.remove(TaskPageAction.TASK_PAGE_COMPONENT_NAME);
					break;
				}
			}
		}
	}

	private void removeDifferentType(String newName, List<String> tList) {
		for(Iterator<String> iterator = tList.iterator(); iterator.hasNext();) {
			String i = iterator.next();
			if(!i.equals(newName)) {
				iterator.remove();
			}
		}
	}

	public String getTypeLabel(String type) {
		if (types == null) {
			getTypeList();
		}
		return (String) types.get(type);
	}
	
	public Node getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(Node cNode) {
		Node lastNode = this.currentNode;
		this.currentNode = cNode;
		getTasks();
		currentTask = null;
		if (taskNodeMap != null && taskNodeMap.containsKey(cNode)) {
			List<TaskHandler> list = taskNodeMap.get(cNode);
			if (! list.isEmpty()) {
				currentTask = list.get(0);
			} 
			setPrazoTasks(lastNode, cNode);
		}
		nodeHandler = new NodeHandler(cNode);
		newNodeType = "Task";
		arrivingTransitions = null;
		leavingTransitions = null;
		setTypeList(null);
		if(currentTask != null) {
			currentTask.clearHasTaskPage();
		}
		
		Events.instance().raiseEvent(SET_CURRENT_NODE_EVENT);
	}
	
	private void setPrazoTasks(Node lastNode, Node cNode) {
		if(cNode == null) {
			prazo = null;
			tipoPrazo = null;
			return;
		}
		
		PrazoTask prazoTask = new PrazoTask();
		if(lastNode != null && prazo != null && tipoPrazo != null) {
			prazoTask.setPrazo(prazo);
			prazoTask.setTipoPrazo(tipoPrazo);
			prazoTaskMap.put(lastNode.getName(), prazoTask);
		}
		
		prazoTask = prazoTaskMap.get(cNode.getName());
		if (prazoTask != null)	{
			prazo = prazoTask.getPrazo();
			tipoPrazo = prazoTask.getTipoPrazo();
		} else {
			Tarefa t = JbpmUtil.getTarefa(cNode.getName(), 
											FluxoHome.instance().getInstance().getFluxo());
			if (t == null)	{
				prazo = null;
				tipoPrazo = null;
			} else {
				prazo = t.getPrazo();
				tipoPrazo = t.getTipoPrazo();
				prazoTask = new PrazoTask();
				prazoTask.setPrazo(prazo);
				prazoTask.setTipoPrazo(tipoPrazo);
				prazoTaskMap.put(cNode.getName(), prazoTask);
			}
		}
	}
	
	public void setCurrentNode(Transition t, String type) {
		if (type.equals("from")) {
			setCurrentNode(t.getFrom());
		} else {
			setCurrentNode(t.getTo());
		}
	}
	
	public void removeNode(Node node) {
		nodes.remove(node);
		instance.removeNode(node);
		if (node.equals(currentNode)) {
			currentNode = null;
		}
		nodeMessageMap.clear();
		for (Node n : nodes) {
			List<Transition> transitions = n.getLeavingTransitions();
			if (transitions != null) {
				for (Iterator<Transition> i = transitions.iterator(); i.hasNext();) {
					Transition t = i.next();
					if (t.getTo().equals(node)) {
						i.remove();
					}
				}
			}
			Set<Transition> transitionSet = n.getArrivingTransitions();
			if (transitionSet != null) {
				for (Iterator<Transition> i = transitionSet.iterator(); i.hasNext();) {
					Transition t = i.next();
					if (t.getFrom().equals(node)) {
						i.remove();
					}
				}
			}
		}
		checkTransitions();
	}

	private void checkTransitions() {
		transitionList = null;
		transitionsItems = null;
		nodeMessageMap.clear();
		for (Node n : nodes) {
			if (!(n instanceof EndState)) {
				List<Transition> transitions = n.getLeavingTransitions();
				if (transitions == null || transitions.isEmpty()) {
					nodeMessageMap.put(n, "Nó sem transição de saída");
				}
			}
			if (!(n instanceof StartState)) {
				Set<Transition> transitionSet = n.getArrivingTransitions();
				if (transitionSet == null || transitionSet.isEmpty()) {
					nodeMessageMap.put(n, "Nó sem transição de entrada");
				}
			}
		}
	}
	
	public void moveUp(Node node) {
		int i = nodes.indexOf(node);
		instance.reorderNode(i, i-1);
		nodes = null;
		nodesItems = null;
	}
	
	public void moveDown(Node node) {
		int i = nodes.indexOf(node);
		instance.reorderNode(i, i+1);
		nodes = null;
		nodesItems = null;
	}

	public String getNodeForm() {
		String type = null;
		if (currentNode == null) {
			return type;
		}
		switch (currentNode.getNodeType().ordinal()) {
		case 1:		// StartState
//			type = "startState";
			break;
		case 7:		// Decision
			type = "decision";
			break;
		default:	// Node (0)
			if (currentNode instanceof MailNode) {
				type = "mail";
			}
			if (currentNode instanceof ProcessState) {
				type = "processState";
			}
			break;
		}
		return type;
	}
	
	public String getIcon(Node node) {		
		String icon = node.getNodeType().name();
		if (node instanceof MailNode) {
			icon = "MailNode";
		}
		if (node instanceof ProcessState) {
			icon = "ProcessState";
		}
		return icon;
	}

	public List<String[]> getNodeTypes() {
		List<String[]> list = new ArrayList<String[]>();
		list.add(new String[] {"StartState", "Nó inicial"});
		list.add(new String[] {"Task", "Tarefa"});
		list.add(new String[] {"Decision", "Decisão"});
		list.add(new String[] {"MailNode", "Email"});
		list.add(new String[] {"Fork", "Separação"});
		list.add(new String[] {"Join", "Junção"});
		list.add(new String[] {"ProcessState", "SubProcesso"});
		list.add(new String[] {"Node", "Sistema"});
		list.add(new String[] {"EndState", "Nó Final"});
		return list;
	}
 	
	public String getNewNodeName() {
		return newNodeName;
	}
	
	public void setNewNodeName(String newName) {
		this.newNodeName = newName;
	}
	
	public String getNewNodeType() {
		return newNodeType;
	}
	
	public void setNewNodeType(String newNodeTypee) {
		this.newNodeType = newNodeTypee;
	}
		
	public void addNewNode() {
		Class<?> nodeType = NodeTypes.getNodeType(getNodeType(newNodeType));
		if (nodeType != null) {
			Node node = null;
			try {
				node = (Node)nodeType.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			node.setName(newNodeName);
			instance.addNode(node);
			nodes = instance.getNodes();
			// Se foi informado newNodeAfter, procura para inserir
			if (newNodeAfter != null) {
				int i = nodes.indexOf(newNodeAfter);
				instance.reorderNode(nodes.indexOf(node), i + 1);
			} else {
				// Senão coloca antes do primeiro EndState
				int i = nodes.size() - 1;
				do {
					i--;
				} while (nodes.get(i) instanceof EndState);
				instance.reorderNode(nodes.indexOf(node), i + 1);
			}
			// insere o novo nó entre os nós da transição selecionada
			// Se for EndState, liga apenas ao newNodeAfter			
			if (nodeType.equals(EndState.class) ) {
				Transition t = new Transition();
				t.setFrom(newNodeAfter);
				node.addArrivingTransition(t);
				t.setName(node.getName());
				newNodeAfter.addLeavingTransition(t);
			} else if (newNodeTransition != null && newNodeTransition.getTransition() != null) {
				Transition t = new Transition();
				Transition oldT = newNodeTransition.getTransition();
				t.setCondition(oldT.getCondition());
				t.setDescription(oldT.getDescription());
				Node to = newNodeTransition.getTransition().getTo();
				t.setName(to.getName());
				t.setProcessDefinition(oldT.getProcessDefinition());

				to.removeArrivingTransition(oldT);
				to.addArrivingTransition(t);
				
				node.addLeavingTransition(t);
				newNodeTransition.setName(node.getName());
				node.addArrivingTransition(oldT);
			}
			
			newNodeName = null;
			newNodeType = null;
			newNodeAfter = null;
			newNodeTransition = null;
			transitionList = null;
			transitionsItems = null;
			nodesItems = null;
			setCurrentNode(node);
			if (nodeType.equals(TaskNode.class)) {
				addTask();
			}
		}
	}
	
	public List<TransitionHandler> getTransitions() {
		if (transitionList == null) {
			transitionList = new ArrayList<TransitionHandler>();
			for (Node n : nodes) {
				if (n.getLeavingTransitions() != null) {
					transitionList.addAll(TransitionHandler.getList(n.getLeavingTransitions()));
				}
			}
		}
		return transitionList;
	}
	
	public List<SelectItem> getTransitionsItems() {
		if (transitionsItems == null) {
			transitionsItems = new ArrayList<SelectItem>();
			transitionsItems.add(new SelectItem(null, "[Selecione...]"));
			for (Node n : nodes) {
				if (n.getLeavingTransitions() != null) {
					for (TransitionHandler t : TransitionHandler.getList(n.getLeavingTransitions())) {
						transitionsItems.add(new SelectItem(t));
					}
				}
			}
		}
		return transitionsItems;
	}
	
	public void setTransitionsItems(List<SelectItem> transitionsItems) {
		this.transitionsItems = transitionsItems;
	}
	
	public List<String[]> getTransitionNames() {
		if (transitionNames == null) {
			getTransitions();
			transitionNames = new ArrayList<String[]>();
			for (TransitionHandler th : transitionList) {
				String[] names = {th.getFromName(), th.getToName()};
				transitionNames.add(names);
			}
		}
		return transitionNames;
	}
	
	public String getNodeType(String nodeType) {
		if (nodeType.equals("Task")) {
			return "task-node";
		}
		if (nodeType.equals("MailNode")) {
			return "mail-node";
		}
		if (nodeType.equals("StartState")) {
			return "start-state";
		}
		if (nodeType.equals("EndState")) {
			return "end-state";
		}
		if (nodeType.equals("ProcessState")) {
			return "process-state";
		}
		return nodeType.substring(0, 1).toLowerCase() + nodeType.substring(1);
	}
	
	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
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

	public void setNewNodeAfter(String newNodeAfter) {
		for (Node node : getNodes()) {
			if (node.toString().equals(newNodeAfter)) {
				this.newNodeAfter = node;
			}
		}
	}

	public String getNewNodeAfter() {
		return null;
	}

	public void setNewNodeTransition(String newNodeTransition) {
		if(transitionList == null) {
			getTransitions();
		}
		this.newNodeTransition = TransitionHandler.asObject(newNodeTransition, transitionList);
	}

	public String getNewNodeTransition() {
		return null;
	}

	public String getMessage(Node n) {
		return nodeMessageMap.get(n);
	}
	
	private synchronized JbpmLayout getLayout() {
		if (layout == null) {
			try {
				layout = new JbpmLayout(instance);
			} catch (Exception e) {
				LOG.error("Erro ao construir a imagem do fluxo: " +e.getMessage(), e);
			}
		}
		return layout;
	}
	
	/**
	 * Parâmetro Object obj é utilizado pela página graph.xhtml pelo componente mediaOutput
	 * @param out
	 * @param obj
	 * @throws IOException
	 */
	public void paintGraph(OutputStream out, Object obj) throws IOException {
		JbpmLayout layoutOut = getLayout();
		if (layoutOut != null) {
			layoutOut.paint(out);
		}
	}
	
	public String getMap() {
		JbpmLayout layoutOut = getLayout();
		try {
			return layoutOut != null ? layoutOut.getMap() : null;
		} catch (Exception e) {
			LOG.error("Erro ao construir a imagem do fluxo: " +e.getMessage(), e);
			return null;
		}
	}

	public boolean isGraphImage() {
		String path = FacesUtil.getServletContext(null).getRealPath("/Assunto/definicao/" + id + "/processImage.png");
		return new File(path).canRead();
	}
	
	public Integer getNodeIndex() {
		return null;
	}
	
	public void setNodeIndex(Integer i) {
		setCurrentNode(getNodes().get(i));
	}
	
	public void setTab(String tab) {
		this.tab = tab;
	}

	public String getTab() {
		return tab;
	}

	public void clearDefinition() {
		FluxoHome fluxoHome = FluxoHome.instance();
		Fluxo fluxo = fluxoHome.getInstance();
		fluxo.setXml(null);
		String id = this.id;
		clear();
		createInstance();
		load(id);
	}

	public static ProcessBuilder instance() {
		return (ProcessBuilder) Contexts.getConversationContext().get(NAME);
	}

	public void setCurrentTransition(Transition currentTransition) {
		this.currentTransition = currentTransition;
	}

	public Transition getCurrentTransition() {
		return currentTransition;
	}

	public static void main(String[] args) throws Exception {
		String xmlResource = "processdefinition.xml";
	    JpdlXmlReader jpdlReader = new JpdlXmlReader(xmlResource); 
	    ProcessDefinition pd = jpdlReader.readProcessDefinition();
		ProcessBuilder pb = new ProcessBuilder();
		pb.setInstance(pd);
		String map = pb.getLayout().getMap();
		System.out.println(map);
	}
 
	/**
	 * Método para migrar fluxos para o novo esquema de eventos
	 */
	public void migraFluxos() {
		List<Fluxo> list = EntityUtil.getEntityList(Fluxo.class);
		for (Fluxo f : list) {
			FluxoHome fluxoHome = FluxoHome.instance();
			fluxoHome.setInstance(f);
			load(f.getFluxo());
			instance.getEvents().clear();
			addEvents();
			deploy();
		}
	}

	/**
	 * Seta a #{true} na condição da transição para o botão não ser exibido na 
	 * tab de saída do fluxo.
	 * @param th
	 */
	public void setTransitionButton(TransitionHandler th) {
		if(th.getTransition().getCondition() == null) {
			th.getTransition().setCondition("#{true}");
		} else {
			th.getTransition().setCondition(null);
		}
	}

	public void setNodeName(String nodeName) {
		if(this.nodeName != null && !this.nodeName.equals(nodeName)) {
			if(currentNode != null) {
				currentNode.setName(nodeName);
				String query = "select max(id_) from jbpm_node where processdefinition_ = " +
				   			   ":idProcessDefinition and name_ = :nodeName";
				SQLQuery sql = JbpmUtil.getJbpmSession().createSQLQuery(query);
				Query param = sql.setParameter("idProcessDefinition", getIdProcessDefinition()).setParameter("nodeName", nodeName);
				List<Object> list = param.list();
				if(list != null && list.size() > 0 && list.get(0) != null) {
					modifiedNodes.put((BigInteger) list.get(0), nodeName);
				}
			}
			this.nodeName = nodeName;
		}
	}

	public String getNodeName() {
		if(currentNode != null) {
			nodeName = currentNode.getName();
		}
		return nodeName;
	}

	public void setTaskName(String taskName) {
		if(this.taskName != null && !this.taskName.equals(taskName)) {
			if(currentTask != null && currentTask.getTask() != null) {
				currentTask.getTask().setName(taskName);
				String query = "select max(id_) from jbpm_task where processdefinition_ = " +
							   ":idProcessDefinition and name_ = :taskName";
				List<Object> list = JbpmUtil.getJbpmSession().createSQLQuery(query)
											  .setParameter("idProcessDefinition", 
													  		getIdProcessDefinition())
											  .setParameter("taskName", this.taskName)
											  .list();
				if(list != null && list.size() > 0 && list.get(0) != null) {
					modifiedTasks.put((BigInteger) list.get(0), taskName);
				}
			}
			this.taskName = taskName;
		}
	}

	public String getTaskName() {
		if(currentTask != null && currentTask.getTask() != null) {
			taskName = currentTask.getTask().getName();
		}
		return taskName;
	}

	private BigInteger getIdProcessDefinition() {
		String query = "select max(id_) from jbpm_processdefinition where name_ = :pdName";
		Query param = JbpmUtil.getJbpmSession().createSQLQuery(query).setParameter("pdName", instance.getName());
		List<Object> list = param.list();
		if(list == null || list.size() == 0) {
			return null;
		}
		return (BigInteger) list.get(0);
	}

	public void setPrazo(Integer prazo) {
		this.prazo = prazo;
	}

	public Integer getPrazo() {
		return prazo;
	}

	public void setTipoPrazo(PrazoEnum tipoPrazo) {
		this.tipoPrazo = tipoPrazo;
	}

	public PrazoEnum getTipoPrazo() {
		return tipoPrazo;
	}
	
	public PrazoEnum[] getTipoPrazoList() {
		return PrazoEnum.values();
	}
	
}