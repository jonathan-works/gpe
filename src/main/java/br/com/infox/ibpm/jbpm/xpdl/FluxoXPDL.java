package br.com.infox.ibpm.jbpm.xpdl;

import java.io.Serializable;

import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.ProcessDefinition;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.JpdlXmlWriter;
import br.com.infox.ibpm.jbpm.xpdl.activities.ActivitiesXPDL;
import br.com.infox.ibpm.jbpm.xpdl.activities.ActivityNotAllowedXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.activities.IllegalActivityXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.element.ParallelNodeXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.lane.IllegalNumberPoolsXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.lane.LanesXPDL;
import br.com.infox.ibpm.jbpm.xpdl.transition.IllegalTransitionXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.transition.TransitionsXPDL;

public class FluxoXPDL implements Serializable {

	private static final long		serialVersionUID	= 1L;
	public static final String		NO_NAME				= "Indefinido ";
	
	private ActivitiesXPDL activities;
	private LanesXPDL lanes;
	private TransitionsXPDL transitions;
	private Element root;
	
	public FluxoXPDL(Element root) throws IllegalNumberPoolsXPDLException, ActivityNotAllowedXPDLException, IllegalActivityXPDLException, IllegalTransitionXPDLException {
		this.root = root;
		lanes = new LanesXPDL(root);
		activities = new ActivitiesXPDL(root);
		transitions = new TransitionsXPDL(root);
	}

	/**
	 * Retorna o xml correspondente ao 
	 * @return
	 * @throws ParallelNodeXPDLException
	 */
	public String toJPDL() throws ParallelNodeXPDLException {
		ProcessDefinition definition = ProcessDefinition.createNewProcessDefinition();
		definition.setName(lanes.getPoolName());
		definition.setDescription("Fluxo importado via arquivo xpdl.");
		lanes.assignLanesToProcessDefinition(definition);
		transitions.createTransition(activities.getActivities());
		lanes.assignActivitiesToLane(activities.getActivities());
		activities.changeParallelNodeInForkOrJoin(transitions.getTransitions());
		transitions.assignTransitionToNode();
		activities.assignActivitiesToProcessDefinition(definition);
		activities.assignTaskToActivities(definition);
		addEvents(definition);
		return JpdlXmlWriter.toString(definition);
	}
	
	public Element getRoot() {
		return root;
	}
	
	/**
	 * Metodo que adiciona o tratamento de eventos
	 */
	private void addEvents(ProcessDefinition definition) {
		for (String e : ProcessDefinition.supportedEventTypes) {
			addEvent(e, "br.com.infox.ibpm.util.JbpmEvents.raiseEvent(executionContext)",
					new Script(), definition);
		}
	}

	private void addEvent(String eventType, String expression, Action action,
			ProcessDefinition definition) {
		Event event = definition.getEvent(eventType);
		if (event == null) {
			event = new Event(eventType);
			definition.addEvent(event);
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

	/*
	private void assignTransitionToNode(List<TransitionXPDL> transictions) {
		if (transictions == null)
			return;
		List<ActivityXPDL> empty = new ArrayList<ActivityXPDL>();
		for (TransitionXPDL activity : transictions) {
				Transition transition = activity.toTransition(empty);
				Node from = transition.getFrom();
				Node to = transition.getTo();
				from.addLeavingTransition(transition);
				to.addArrivingTransition(transition);
		}
	}
	
	private void assignTaskToActivities(List<ActivityXPDL> activities, ProcessDefinition definition) {
		if (activities == null)
			return;
		for (ActivityXPDL activity : activities) {
			if(activity instanceof AssignTask) {
				AssignTask assign = (AssignTask)activity;
				assign.assignTask(definition);
			}
		}
	}

	
	private void assignActivitiesToProcessDefinition(List<ActivityXPDL> atividades,
			ProcessDefinition definition) {
		if (atividades == null)
			return;
		for (ActivityXPDL activity : atividades) {
			Node node = activity.toNode();
			node.setProcessDefinition(definition);
			definition.addNode(node);
		}
	}

	private void changeParallelNodeInForkOrJoin(List<ActivityXPDL> activities,
			List<TransictionXPDL> transitions) throws ParallelNodeException {
		if (activities != null) {
			Map<ParallelActivityXPDL, Integer> parallels = getAllParallelXPDL(activities);
			for (ParallelActivityXPDL parallel : parallels.keySet()) {
				createForkJoinActivityXPDL(activities, parallel, parallels.get(parallel),
						transitions);
			}
		}
	}

	private List<TransictionXPDL> getAllTransitionsContainsActivity(
			List<TransictionXPDL> transitions, ParallelActivityXPDL parallel) {
		List<TransictionXPDL> lista = new ArrayList<TransictionXPDL>();
		for (TransictionXPDL transiction : transitions) {
			if (parallel.getId().equals(transiction.getFrom())
					|| parallel.getId().equals(transiction.getTo())) {
				lista.add(transiction);
			}
		}
		return lista;
	}

	private void createForkJoinActivityXPDL(List<ActivityXPDL> lista,
			ParallelActivityXPDL parallel, int position, List<TransictionXPDL> transitions)
			throws ParallelNodeException {
		List<TransictionXPDL> parallelTransitions = getAllTransitionsContainsActivity(transitions,
				parallel);
		List<ActivityXPDL> arrives = parallel.getArrives();
		List<ActivityXPDL> leaves = parallel.getLeaves();
		if (arrives != null && leaves != null && arrives.size() == 1 && leaves.size() > 1) {
			createForknode(lista, parallelTransitions, parallel, position);
		} else if (arrives != null && leaves != null && leaves.size() == 1 && arrives.size() > 1) {
			createJoinNode(lista, parallelTransitions, parallel, position);
		} else {
			throwParallelExceptionCheckImpossible(parallel);
		}
	}

	private void throwParallelExceptionCheckImpossible(ParallelActivityXPDL paralelo)
			throws ParallelNodeException {
		throw new ParallelNodeException("Impossível verificar se o Nó ("
				+ (paralelo.getName() != null ? paralelo.getName() : "Paralelo")
				+ ") é do tipo Join ou Fork.");
	}

	private void createJoinNode(List<ActivityXPDL> lista,
			List<TransictionXPDL> parallelTransitions, ParallelActivityXPDL parallel, int position) {
		JoinActivityXPDL node = new JoinActivityXPDL(parallel.getElement(), parallel.getName());
		Transition transition = null;
		for (TransictionXPDL trans : parallelTransitions) {
			if (trans.getFrom().equals(parallel.getId())) {
				transition = trans.toTransition(lista);
				transition.setFrom(node.toNode());
			} else if (trans.getTo().equals(parallel.getId())) {
				transition = trans.toTransition(lista);
				transition.setTo(node.toNode());
			}
		}
		node.setArrives(parallel.getArrives());
		node.setLeaves(parallel.getLeaves());
		lista.set(position, node);
	}

	private void createForknode(List<ActivityXPDL> lista,
			List<TransictionXPDL> parallelTransitions, ParallelActivityXPDL parallel, int position) {
		ForkActivityXPDL node = new ForkActivityXPDL(parallel.getElement(), parallel.getName());
		Transition transition = null;
		for (TransictionXPDL trans : parallelTransitions) {
			if (trans.getFrom().equals(parallel.getId())) {
				transition = trans.toTransition(lista);
				transition.setFrom(node.toNode());
			} else if (trans.getTo().equals(parallel.getId())) {
				transition = trans.toTransition(lista);
				transition.setTo(node.toNode());
			}
		}
		node.setArrives(parallel.getArrives());
		node.setLeaves(parallel.getLeaves());
		lista.set(position, node);
	}

	private Map<ParallelActivityXPDL, Integer> getAllParallelXPDL(List<ActivityXPDL> list) {
		Map<ParallelActivityXPDL, Integer> lista = null;
		lista = new HashMap<ParallelActivityXPDL, Integer>();
		int i = 0;
		for (ActivityXPDL n : list) {
			if (n instanceof ParallelActivityXPDL) {
				lista.put((ParallelActivityXPDL) n, i);
			}
			i++;
		}
		return lista;
	} 

	private void assignActivitiesToLane(List<ActivityXPDL> activities, List<LaneXPDL> lanes) {
		for (LaneXPDL lane : lanes) {
			List<ActivityXPDL> list = lane.findActivitiesBelongingToLane(activities);
			for (ActivityXPDL activity : list) {
				activity.setLane(lane);
			}
		}
	}

	private void createTransition(List<TransitionXPDL> list,
			List<ActivityXPDL> atividades) {
		if (list == null)
			return;
		for (TransitionXPDL tran : list) {
			tran.toTransition(atividades);
		}
	}
	
	private List<TransitionXPDL> createTransitionList(List<Element> list)
			throws IllegalTransitionXPDLException {
		if (list == null)
			return null;
		List<TransitionXPDL> activities = new ArrayList<TransitionXPDL>();
		for (Element ele : list) {
			activities.add(new TransitionXPDL(ele, NO_NAME + index++));
		}
		return activities;
	}

	private void assignLanesToProcessDefinition(List<LaneXPDL> lanes, ProcessDefinition definition) {
		if (lanes == null)
			return;
		for (LaneXPDL lane : lanes) {
			definition.getTaskMgmtDefinition().addSwimlane(lane.toSwimlane());
		}
	}

	
	private List<ActivityXPDL> createAtivitiesList(List<Element> list) throws ActivityNotAllowedXPDLException {
		if (list == null)
			return null;
		List<ActivityXPDL> activities = new ArrayList<ActivityXPDL>();
		for (Element ele : list) {
			activities.add(ActivityXPDLFactory.getAtividade(ele, NO_NAME + index++));
		}
		return activities;
	}

	
	private List<LaneXPDL> createLanesList(List<Element> list) {
		if (list == null)
			return null;
		List<LaneXPDL> lanes = new ArrayList<LaneXPDL>();
		for (Element ele : list) {
			LaneXPDL lane = new LaneXPDL(ele, NO_NAME + index++);
			lanes.add(lane);
		}
		return lanes;
	}
	*/
	

	
	/*
	private Element getPool(Element root) throws ImportarXPDLServiceException {
		Element pools = XmlUtil.getChildren(root, "Pools").get(0);
		List<Element> pool = XmlUtil.getChildren(pools, "Pool");
		if (pool == null || pool.size() > 2) {
			throw new ImportarXPDLServiceException("XPDL inválido. "
					+ (pool == null ? "Não há nenhuma piscina definida."
							: "Não mais de uma piscina definida."));
		}
		return pool.get(1);
	}


	public String getPoolName() {
		return name;
	} */

	/*
	 * @SuppressWarnings(RAWTYPES) private void addSwinlanes(List raias,
	 * ProcessDefinition definition) { Swimlane s = null; Element raia = null;
	 * for (int i = 0; i < raias.size(); i++) { raia = (Element) raias.get(i); s
	 * = new Swimlane(raia.getAttributeValue(NAME_ATTRIBUTE));
	 * definition.getTaskMgmtDefinition().addSwimlane(s); } }
	 * 
	 * @SuppressWarnings(RAWTYPES) private void addNodes(String name, List nos,
	 * List transicoes, ProcessDefinition definition) throws
	 * InvalidActivityException, ParallelNodeException { Map<String, Node> map =
	 * new HashMap<String, Node>(); List<Node> l = new ArrayList<Node>();
	 * boolean parallel = false; int end = 0; Node no = null; Element ele =
	 * null; for (int i = 0; i < nos.size(); i++) { ele = (Element) nos.get(i);
	 * String nome = ele.getAttributeValue(NAME_ATTRIBUTE); String id =
	 * ele.getAttributeValue("Id"); // Verificar o tipo String type =
	 * verificarTypenode(ele); if (type == null) { throw new
	 * InvalidActivityException("O Sistema e-PA não aceita o Nó do tipo: " +
	 * ele.getName()); } if (type.equals(PARALLEL)) { parallel = true; } else if
	 * (type.equals("EndState")) { end++; }
	 * 
	 * // Gerar o Nó no = gereNode(type, nome, definition); if (no != null) {
	 * map.put(id, no); l.add(no); } } // Gerando as transições for (int i = 0;
	 * i < transicoes.size(); i++) { ele = (Element) transicoes.get(i); String
	 * from = ele.getAttributeValue("From"); String to =
	 * ele.getAttributeValue("To"); String nome =
	 * ele.getAttributeValue(NAME_ATTRIBUTE); Node de = map.get(from); Node para
	 * = map.get(to); setTransiction(de, para, nome, definition); } // gerando
	 * os os Nodes Join e Fork if (parallel) { transformeParallelNode(l); } //
	 * Mais de um Nó de finalização if (end > 1) { removeExcessEndStat(l); } //
	 * Ordenação simples ordeneNodes(l); Iterator<Node> it = l.iterator(); while
	 * (it.hasNext()) { Node n = it.next(); definition.addNode(n); }
	 * definition.setDescription(name); }
	 */

	/**
	 * Método responsável por garantir apenas um End Stat
	 */
	/*
	 * private void removeExcessEndStat(List<Node> lista) { if (lista != null) {
	 * Map<Node, Integer> ends = getEndNodes(lista); Set<Node> keys =
	 * ends.keySet(); Iterator<Node> it = keys.iterator(); Node end = it.next();
	 * List<Node> endList = getAllNodeToEnd(keys); for (Node n : endList) {
	 * changeTransitionToOnlyOneEnd(n, end); } keys.remove(end); Iterator<Node>
	 * it2 = keys.iterator(); while (it2.hasNext()) { lista.remove(it2.next());
	 * } } }
	 */

	/**
	 * Método responsável por verificar se o nó possui uma transição para o
	 * estado final. Caso positivo, então ele muda a transição para o nó fim.
	 * 
	 * @param node
	 * @param keys
	 * @return
	 */
	/*
	 * private void changeTransitionToOnlyOneEnd(Node node, Node fim) {
	 * List<Transition> leaves = node.getLeavingTransitions(); for (Transition
	 * leave : leaves) { if (leave.getTo() instanceof EndState &&
	 * !leave.getTo().equals(fim)) { leave.setTo(fim); } } }
	 * 
	 * 
	 * private List<Node> getAllNodeToEnd(Set<Node> ends) { List<Node> lista =
	 * new ArrayList<Node>(); for (Node node : ends) { Set<Transition> arriving
	 * = node.getArrivingTransitions(); for (Transition t : arriving) {
	 * lista.add(t.getFrom()); } } return lista; }
	 */
	/**
	 * Transforma os Nós Parallel (Notação XPDML) em Fork ou Join Substitui
	 * todas as referências do ParallelNode presente na lista ou em Fork ou em
	 * Join
	 * 
	 * @param lista
	 * @throws ParallelNodeException
	 */
	/*
	 * private void transformeParallelNode(List<Node> lista) throws
	 * ParallelNodeException { if (lista != null) { Map<Node, Integer> parallel
	 * = getParallelNodes(lista); for (Node paralelo : parallel.keySet()) {
	 * createForkJoinNode(lista, paralelo); } } }
	 * 
	 * private void createForkJoinNode(List<Node> lista, Node paralelo) throws
	 * ParallelNodeException { Set<Transition> arrives =
	 * paralelo.getArrivingTransitions(); List<Transition> leaves =
	 * paralelo.getLeavingTransitions(); if (arrives == null && leaves == null)
	 * { throwsParallelExcption(paralelo); } else if (arrives != null &&
	 * arrives.size() > 1 && leaves != null && leaves.size() > 1) {
	 * throwsParallelExceptionTransictions(paralelo); } else if (arrives != null
	 * && leaves != null && arrives.size() == 1 && leaves.size() > 1) {
	 * createForknode(lista, paralelo, arrives, leaves); } else if (arrives !=
	 * null && leaves != null && leaves.size() == 1 && arrives.size() > 1) {
	 * createJoinNode(lista, paralelo, arrives, leaves); } else {
	 * throwParallelExcptionCheckImpossible(paralelo); } }
	 * 
	 * private void createJoinNode(List<Node> lista, Node paralelo,
	 * Set<Transition> arrives, List<Transition> leaves) { Join node = new
	 * Join(); preencherNo(lista, paralelo, arrives, leaves, node); }
	 * 
	 * private void createForknode(List<Node> lista, Node paralelo,
	 * Set<Transition> arrives, List<Transition> leaves) { Fork node = new
	 * Fork(); preencherNo(lista, paralelo, arrives, leaves, node); }
	 * 
	 * private void throwParallelExcptionCheckImpossible(ParallelActivityXPDL
	 * paralelo) throws ParallelNodeException { throw new
	 * ParallelNodeException("Impossível verificar se o Nó (" +
	 * (paralelo.getName() != null ? paralelo.getName() : "Paralelo") +
	 * ") é do tipo Join ou Fork."); }
	 * 
	 * private void throwsParallelExceptionTransictions(ParallelActivityXPDL
	 * paralelo) throws ParallelNodeException { throw new
	 * ParallelNodeException("Impossível verificar se o Nó (" +
	 * (paralelo.getName() != null ? paralelo.getName() : "Paralelo") +
	 * ") é do tipo Join ou Fork. Existem várias transições de entrada e de saída."
	 * ); }
	 * 
	 * private void throwsParallelExcption(ParallelActivityXPDL paralelo) throws
	 * ParallelNodeException { throw new
	 * ParallelNodeException("Impossível verificar se o Nó (" +
	 * (paralelo.getName() != null ? paralelo.getName() : "Paralelo") +
	 * ") é do tipo Join ou Fork. Não há nenhuma transição de entreda ou de saída."
	 * ); }
	 * 
	 * private void preencherNo(List<Node> lista, Node parallel, Set<Transition>
	 * arrives, List<Transition> leaves, Node node) {
	 * node.setName(parallel.getName());
	 * node.setProcessDefinition(parallel.getProcessDefinition()); for
	 * (Transition arrive : arrives) { node.addArrivingTransition(arrive); } for
	 * (Transition leave : leaves) { node.addLeavingTransition(leave); }
	 * lista.set(lista.indexOf(parallel), node); }
	 * 
	 * private Map<Node, Integer> getParallelNodes(List<Node> l) { Map<Node,
	 * Integer> lista = null; if (l != null) { lista = new HashMap<Node,
	 * Integer>(); int i = 0; for (Node n : l) { if (n instanceof ParallelNode)
	 * { lista.put(n, i); } i++; } } return lista; }
	 * 
	 * private Map<Node, Integer> getEndNodes(List<Node> l) { HashMap<Node,
	 * Integer> lista = null; if (l != null) { lista = new HashMap<Node,
	 * Integer>(); int i = 0; for (Node n : l) { if (n instanceof EndState) {
	 * lista.put(n, i); } i++; } } return lista; }
	 */

	/**
	 * Garantir que o Nó inicial fique no início e o Nó final no fim
	 * 
	 * @param l
	 */
	/*
	 * private void ordeneNodes(List<Node> l) { Node start = null; int ini = -1;
	 * Node end = null; int fim = -1; int i = 0; for (Node n : l) { if (n
	 * instanceof StartState) { start = n; ini = i; } else if (n instanceof
	 * EndState) { end = n; fim = i; } i++; } if (ini != 0) { Node temp =
	 * l.get(0); l.set(0, start); l.set(ini, temp); } if (fim != l.size() - 1) {
	 * Node temp = l.get(l.size() - 1); l.set(l.size() - 1, end); l.set(fim,
	 * temp); } }
	 */
	/**
	 * Transforma o tipo do XPDL para o tipo do sistema
	 * 
	 * @param ele
	 * @param type
	 * @return
	 */
	/*
	 * private String verificarTypenode(Element ele) { String tipo = null;
	 * List<Element> event = XmlUtil.getChildren(ele, "Event"); List<Element>
	 * impl = XmlUtil.getChildren(ele, "Implementation"); List<Element> route =
	 * XmlUtil.getChildren(ele, "Route"); if (event != null && !event.isEmpty())
	 * { tipo = createStartEndMailNode(event); } else if (impl != null &&
	 * !impl.isEmpty()) { tipo = createTaskProcessState(impl); } else if (route
	 * != null && !route.isEmpty()) { tipo = createParallelDecisionNode(route);
	 * } if ("ProcessState".equals(tipo)) { return null; } return tipo; }
	 * 
	 * @SuppressWarnings(RAWTYPES) private String
	 * createParallelDecisionNode(List route) { String tipo; Element temp =
	 * (Element) route.get(0); String gateway =
	 * temp.getAttributeValue("GatewayType", temp.getNamespace()); if (gateway
	 * != null && !gateway.isEmpty() && PARALLEL.equalsIgnoreCase(gateway)) {
	 * tipo = PARALLEL; } else { tipo = "Decision"; } return tipo; }
	 * 
	 * @SuppressWarnings(RAWTYPES) private String createTaskProcessState(List
	 * impl) { Element temp = (Element) impl.get(0); List task =
	 * temp.getChildren("Task", temp.getNamespace()); List sub =
	 * temp.getChildren("SubFlow", temp.getNamespace()); if (task != null &&
	 * !task.isEmpty()) { return "Task"; } else if (sub != null &&
	 * !sub.isEmpty()) { return "ProcessState"; // SUB-PROCESSO } return null; }
	 * 
	 * @SuppressWarnings(RAWTYPES) private String createStartEndMailNode(List
	 * event) { Element temp = (Element) event.get(0); List start =
	 * temp.getChildren("StartEvent", temp.getNamespace()); List end =
	 * temp.getChildren("EndEvent", temp.getNamespace()); List inter =
	 * temp.getChildren("IntermediateEvent", temp.getNamespace()); if (start !=
	 * null && !start.isEmpty()) { return "StartState"; } else if (end != null
	 * && !end.isEmpty()) { return "EndState"; } else if (inter != null &&
	 * !inter.isEmpty()) { Element temp2 = (Element) event.get(0); String value
	 * = temp2.getAttributeValue("Trigger", temp2.getNamespace()); if (value !=
	 * null && "Message".equalsIgnoreCase(value)) { return "MailNode"; } return
	 * "Node"; } return null; }
	 * 
	 * private Node gereNode(String type, String nome, ProcessDefinition
	 * definition) { Node node = null; Class<?> nodeType =
	 * retrieveNodeType(type); try { node = createNodeFromNodetype(type, nome,
	 * definition, nodeType); } catch (InstantiationException e) {
	 * LOG.error("ImportarXPDL: " + e.getMessage(), e); } catch
	 * (IllegalAccessException e) { LOG.error("ImportarXPDL: " + e.getMessage(),
	 * e); } return node; }
	 * 
	 * private Node createNodeFromNodetype(String type, String nome,
	 * ProcessDefinition definition, Class<?> nodeType) throws
	 * InstantiationException, IllegalAccessException { if (nodeType == null) {
	 * return null; } Node node = createNodefromType(type, nodeType);
	 * assignNameToNode(nome, node); assignTaksToNode(definition, node); return
	 * node; }
	 * 
	 * private void assignTaksToNode(ProcessDefinition definition, Node node) {
	 * if (node instanceof TaskNode || node instanceof StartState || node
	 * instanceof ProcessState) { addTask(node, definition); } }
	 * 
	 * private void assignNameToNode(String nome, Node node) { if (nome != null
	 * && !nome.isEmpty()) { node.setName(nome); } else { if (node instanceof
	 * StartState || node instanceof EndState) { node.setName((node instanceof
	 * StartState ? "Início" : "Fim")); } else { node.setName(NO_NAME +
	 * index++); } } }
	 * 
	 * private Node createNodefromType(String type, Class<?> nodeType) throws
	 * InstantiationException, IllegalAccessException { Node node; if
	 * (PARALLEL.equalsIgnoreCase(type)) { node = new ParallelNode(); } else {
	 * node = (Node) nodeType.newInstance(); } return node; }
	 * 
	 * private Class<?> retrieveNodeType(String type) { Class<?> nodeType =
	 * null; if (PARALLEL.equalsIgnoreCase(type)) { nodeType =
	 * ParallelNode.class; } else { nodeType =
	 * NodeTypes.getNodeType(getNodeType(type)); } return nodeType; }
	 * 
	 * private void addTask(Node node, ProcessDefinition definition) { if (node
	 * instanceof TaskNode) { TaskNode tn = (TaskNode) node; Task task = new
	 * Task(); task.setProcessDefinition(definition);
	 * task.setTaskMgmtDefinition(definition.getTaskMgmtDefinition());
	 * task.setName(node.getName());
	 * task.setSwimlane(definition.getTaskMgmtDefinition
	 * ().getSwimlanes().values().iterator() .next()); tn.setEndTasks(true);
	 * tn.addTask(task); } else if (node instanceof StartState) { Task task =
	 * new Task("Tarefa inicial");
	 * task.setSwimlane(definition.getTaskMgmtDefinition
	 * ().getSwimlanes().values() .iterator().next()); TaskHandler
	 * startTaskHandler = new TaskHandler(task);
	 * definition.getTaskMgmtDefinition
	 * ().setStartTask(startTaskHandler.getTask()); } else if (node instanceof
	 * ProcessState) { ProcessState sub = (ProcessState) node; ProcessDefinition
	 * subProc = ProcessDefinition.createNewProcessDefinition();
	 * subProc.setDescription(NO_NAME + index++); subProc.setName(NO_NAME +
	 * index++); sub.setSubProcessDefinition(subProc); } }
	 * 
	 * private void setTransiction(Node atual, Node next, String nome,
	 * ProcessDefinition definition) { Transition t = new Transition();
	 * t.setProcessDefinition(definition); next.addArrivingTransition(t);
	 * atual.addLeavingTransition(t); if (nome != null && !nome.isEmpty()) {
	 * t.setName(nome); } else { t.setName(next.getName()); } }
	 * 
	 * private String getNodeType(String nodeType) { if
	 * (nodeType.equals("Task")) { return "task-node"; } if
	 * (nodeType.equals("MailNode")) { return "mail-node"; } if
	 * (nodeType.equals("StartState")) { return "start-state"; } if
	 * (nodeType.equals("EndState")) { return "end-state"; } if
	 * (nodeType.equals("ProcessState")) { return "process-state"; } return
	 * nodeType.substring(0, 1).toLowerCase() + nodeType.substring(1); }
	 */

	/*
	private void mensagemErro(String msg) throws ImportarXPDLServiceException {
		throw new ImportarXPDLServiceException(msg);
	}
	*/
}
