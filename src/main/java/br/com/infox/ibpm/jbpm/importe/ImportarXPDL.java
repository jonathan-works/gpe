package br.com.infox.ibpm.jbpm.importe;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activity.InvalidActivityException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.Fork;
import org.jbpm.graph.node.Join;
import org.jbpm.graph.node.NodeTypes;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jdom.Document;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.JpdlXmlWriter;
import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.infox.ibpm.jbpm.handler.TaskHandler;
import br.com.itx.util.XmlUtil;

@Name(ImportarXPDL.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class ImportarXPDL implements Serializable {

	private static final String	ERRO_MSG			= "ImportarXPDL. Erro msg: ";
	private static final String	NAME_ATTRIBUTE		= "Name";
	private static final String	PARALLEL			= "Parallel";
	private static final String	RAWTYPES			= "rawtypes";
	private static final long	serialVersionUID	= 1L;
	public static final String	NAME				= "importarXPDL";
	public static final String	POOLS				= "Pools";
	public static final String	ASSOCIATIONS		= "Associations";
	public static final String	ARTIFACTS			= "Artifacts";
	public static final String	WORKFLOW_PROCESSES	= "WorkflowProcesses";
	public static final String	EXTENDED_ATTRIBUTES	= "ExtendedAttributes";
	public static final String	ACTIVITES			= "Activities";
	public static final String	TRANSITIONS			= "Transitions";
	public static final String	NO_NAME				= "Indefinido ";
	private static Log			LOG					= Logging.getLog(ImportarXPDL.class);
	private int					index				= 1;

	public void importarXPDL(byte[] bytes, String cdFluxo) {
		if (bytes != null && cdFluxo != null && !"".equals(cdFluxo)) {
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			Document doc = XmlUtil.readDocument(stream);
			Element root = doc.getRootElement();
			List<Element> pools = obterElements(root, POOLS);
			List<Element> workFlow = obterElements(root, WORKFLOW_PROCESSES);
			gereFluxo(pools, workFlow, cdFluxo);
		}
		else {
			mensagemErro("Erro ao importar arquivo XPDL. "
					+ (cdFluxo == null || "".equals(cdFluxo) ? "Erro na gravação do fluxo."
							: (bytes == null ? "Erro no upload do arquivo." : "")));
			LOG.error("Erro ao importar arquivo XPDL.");
		}
	}

	@SuppressWarnings(RAWTYPES)
	public List<Element> obterElements(Element ele, String filter) {
		List<Element> lista = null;
		if (ele != null && filter != null) {
			lista = new ArrayList<Element>();
			List l = ele.getContent();
			Iterator it = l.iterator();
			Element e = null;
			while (it.hasNext()) {
				Object ob = it.next();
				if (ob instanceof Element) {
					e = (Element) ob;
					if (e.getName().equals(filter)) {
						lista.add(e);
					}
				}
			}
		}
		return lista;
	}

	private void gereFluxo(List<Element> poolsList, List<Element> workflowList, String cdFluxo) {
		if (poolsList == null || poolsList.size() > 1) {
			mensagemErro("XPDL inválido. "
					+ (poolsList == null ? "Não há nenhuma piscina definida."
							: "Não mais de uma piscina definida."));
			LOG.error("XPDL inválido.");
			return;
		}
		Element pools = poolsList.get(0);
		List<Element> pool = obterElements(pools, "Pool");
		String nome = pool.get(1).getAttributeValue(NAME_ATTRIBUTE);
		List<Element> raias = obterElements(pool.get(1), "Lanes");
		List<Element> raia = obterElements(raias.get(0), "Lane");
		List<Element> workflows = obterElements(workflowList.get(0), "WorkflowProcess");
		List<Element> activities = obterElements(workflows.get(1), ACTIVITES);
		List<Element> activity = obterElements(activities.get(0), "Activity");
		List<Element> transicoes = obterElements(workflows.get(1), TRANSITIONS);
		List<Element> transicao = obterElements(transicoes.get(0), "Transition");
		gereXML(nome, raia, activity, transicao, cdFluxo);
	}

	@SuppressWarnings(RAWTYPES)
	private void gereXML(String nome, List raias, List nos, List transicoes, String cdFluxo) {
		try {
			index = 1;
			ProcessDefinition definition = ProcessDefinition.createNewProcessDefinition();
			definition.setName(nome);
			addSwinlanes(raias, definition);
			addNodes(nome, nos, transicoes, definition);
			addEvents(definition);
			String xml = JpdlXmlWriter.toString(definition);
			ProcessBuilder process = ProcessBuilder.instance();
			process.setId(cdFluxo);
			Redirect redirect = Redirect.instance();
			redirect.setParameter("id", cdFluxo);
			redirect.setParameter("tab", "Propriedades");
			redirect.setParameter("nodeIndex", "0");
			redirect.setViewId("/Fluxo/definicao/processDefinition.xhtml");
			process.load(cdFluxo);
			process.setXml(xml);
			redirect.execute();
		}
		catch (InvalidActivityException e) {
			LOG.error(ERRO_MSG + e.getMessage(), e);
			mensagemErro(ERRO_MSG + e.getMessage());
		}
		catch (ParallelNodeException e) {
			LOG.error(ERRO_MSG + e.getMessage(), e);
			mensagemErro(ERRO_MSG + e.getMessage());
		}
	}

	@SuppressWarnings(RAWTYPES)
	private void addSwinlanes(List raias, ProcessDefinition definition) {
		Swimlane s = null;
		Element raia = null;
		for (int i = 0; i < raias.size(); i++) {
			raia = (Element) raias.get(i);
			s = new Swimlane(raia.getAttributeValue(NAME_ATTRIBUTE));
			definition.getTaskMgmtDefinition().addSwimlane(s);
		}
	}

	@SuppressWarnings(RAWTYPES)
	private void addNodes(String name, List nos, List transicoes, ProcessDefinition definition)
			throws InvalidActivityException, ParallelNodeException {
		Map<String, Node> map = new HashMap<String, Node>();
		List<Node> l = new ArrayList<Node>();
		boolean parallel = false;
		int end = 0;
		Node no = null;
		Element ele = null;
		for (int i = 0; i < nos.size(); i++) {
			ele = (Element) nos.get(i);
			String nome = ele.getAttributeValue(NAME_ATTRIBUTE);
			String id = ele.getAttributeValue("Id");
			// Verificar o tipo
			String type = verificarTypenode(ele);
			if (type == null) {
				throw new InvalidActivityException("O Sistema e-PA não aceita o Nó do tipo: "
						+ ele.getName());
			}
			if (type.equals(PARALLEL)) {
				parallel = true;
			}
			else if (type.equals("EndState")) {
				end++;
			}

			// Gerar o Nó
			no = gereNode(type, nome, definition);
			if (no != null) {
				map.put(id, no);
				l.add(no);
			}
		}
		// Gerando as transições
		for (int i = 0; i < transicoes.size(); i++) {
			ele = (Element) transicoes.get(i);
			String from = ele.getAttributeValue("From");
			String to = ele.getAttributeValue("To");
			String nome = ele.getAttributeValue(NAME_ATTRIBUTE);
			Node de = map.get(from);
			Node para = map.get(to);
			setTransiction(de, para, nome, definition);
		}
		// gerando os os Nodes Join e Fork
		if (parallel) {
			transformeParallelNode(l);
		}
		// Mais de um Nó de finalização
		if (end > 1) {
			removeExcessEndStat(l);
		}
		// Ordenação simples
		ordeneNodes(l);
		Iterator<Node> it = l.iterator();
		while (it.hasNext()) {
			Node n = it.next();
			definition.addNode(n);
		}
		definition.setDescription(name);
	}

	/**
	 * Método responsável por garantir apenas um End Stat
	 */
	private void removeExcessEndStat(List<Node> lista) {
		if (lista != null) {
			Map<Node, Integer> ends = getEndNodes(lista);
			Set<Node> keys = ends.keySet();
			Iterator<Node> it = keys.iterator();
			Node end = it.next();
			List<Node> endList = getAllNodeToEnd(keys);
			for (Node n : endList) {
				changeTransitionToOnlyOneEnd(n, end);
			}
			keys.remove(end);
			Iterator<Node> it2 = keys.iterator();
			while (it2.hasNext()) {
				lista.remove(it2.next());
			}
		}
	}

	/**
	 * Método responsável por verificar se o nó possui uma transição para o
	 * estado final. Caso positivo, então ele muda a transição para o nó fim.
	 * 
	 * @param node
	 * @param keys
	 * @return
	 */
	private void changeTransitionToOnlyOneEnd(Node node, Node fim) {
		List<Transition> leaves = node.getLeavingTransitions();
		for (Transition leave : leaves) {
			if (leave.getTo() instanceof EndState && !leave.getTo().equals(fim)) {
				leave.setTo(fim);
			}
		}
	}

	private List<Node> getAllNodeToEnd(Set<Node> ends) {
		List<Node> lista = new ArrayList<Node>();
		for (Node node : ends) {
			Set<Transition> arriving = node.getArrivingTransitions();
			for (Transition t : arriving) {
				lista.add(t.getFrom());
			}
		}
		return lista;
	}

	/**
	 * Transforma os Nós Parallel (Notação XPDML) em Fork ou Join Substitui
	 * todas as referências do ParallelNode presente na lista ou em Fork ou em
	 * Join
	 * 
	 * @param lista
	 * @throws ParallelNodeException
	 */
	private void transformeParallelNode(List<Node> lista) throws ParallelNodeException {
		if (lista != null) {
			Map<Node, Integer> parallel = getParallelNodes(lista);
			for (Node paralelo : parallel.keySet()) {
				createForkJoinNode(lista, paralelo);
			}
		}
	}

	private void createForkJoinNode(List<Node> lista, Node paralelo) throws ParallelNodeException {
		Set<Transition> arrives = paralelo.getArrivingTransitions();
		List<Transition> leaves = paralelo.getLeavingTransitions();
		if (arrives == null && leaves == null) {
			throwsParallelExcption(paralelo);
		}
		else if (arrives != null && arrives.size() > 1 && leaves != null && leaves.size() > 1) {
			throwsParallelExceptionTransictions(paralelo);
		}
		else if (arrives != null && leaves != null && arrives.size() == 1 && leaves.size() > 1) {
			createForknode(lista, paralelo, arrives, leaves);
		}
		else if (arrives != null && leaves != null && leaves.size() == 1 && arrives.size() > 1) {
			createJoinNode(lista, paralelo, arrives, leaves);
		}
		else {
			throwParallelExcptionCheckImpossible(paralelo);
		}
	}

	private void createJoinNode(List<Node> lista, Node paralelo, Set<Transition> arrives,
			List<Transition> leaves) {
		Join node = new Join();
		preencherNo(lista, paralelo, arrives, leaves, node);
	}

	private void createForknode(List<Node> lista, Node paralelo, Set<Transition> arrives,
			List<Transition> leaves) {
		Fork node = new Fork();
		preencherNo(lista, paralelo, arrives, leaves, node);
	}

	private void throwParallelExcptionCheckImpossible(Node paralelo) throws ParallelNodeException {
		throw new ParallelNodeException("Impossível verificar se o Nó ("
				+ (paralelo.getName() != null ? paralelo.getName() : "Paralelo")
				+ ") é do tipo Join ou Fork.");
	}

	private void throwsParallelExceptionTransictions(Node paralelo) throws ParallelNodeException {
		throw new ParallelNodeException("Impossível verificar se o Nó ("
				+ (paralelo.getName() != null ? paralelo.getName() : "Paralelo")
				+ ") é do tipo Join ou Fork. Existem várias transições de entrada e de saída.");
	}

	private void throwsParallelExcption(Node paralelo) throws ParallelNodeException {
		throw new ParallelNodeException("Impossível verificar se o Nó ("
				+ (paralelo.getName() != null ? paralelo.getName() : "Paralelo")
				+ ") é do tipo Join ou Fork. Não há nenhuma transição de entreda ou de saída.");
	}

	private void preencherNo(List<Node> lista, Node parallel, Set<Transition> arrives,
			List<Transition> leaves, Node node) {
		node.setName(parallel.getName());
		node.setProcessDefinition(parallel.getProcessDefinition());
		for (Transition arrive : arrives) {
			node.addArrivingTransition(arrive);
		}
		for (Transition leave : leaves) {
			node.addLeavingTransition(leave);
		}
		lista.set(lista.indexOf(parallel), node);
	}

	private Map<Node, Integer> getParallelNodes(List<Node> l) {
		Map<Node, Integer> lista = null;
		if (l != null) {
			lista = new HashMap<Node, Integer>();
			int i = 0;
			for (Node n : l) {
				if (n instanceof ParallelNode) {
					lista.put(n, i);
				}
				i++;
			}
		}
		return lista;
	}

	private Map<Node, Integer> getEndNodes(List<Node> l) {
		HashMap<Node, Integer> lista = null;
		if (l != null) {
			lista = new HashMap<Node, Integer>();
			int i = 0;
			for (Node n : l) {
				if (n instanceof EndState) {
					lista.put(n, i);
				}
				i++;
			}
		}
		return lista;
	}

	/**
	 * Garantir que o Nó inicial fique no início e o Nó final no fim
	 * 
	 * @param l
	 */
	private void ordeneNodes(List<Node> l) {
		Node start = null;
		int ini = -1;
		Node end = null;
		int fim = -1;
		int i = 0;
		for (Node n : l) {
			if (n instanceof StartState) {
				start = n;
				ini = i;
			}
			else if (n instanceof EndState) {
				end = n;
				fim = i;
			}
			i++;
		}
		if (ini != 0) {
			Node temp = l.get(0);
			l.set(0, start);
			l.set(ini, temp);
		}
		if (fim != l.size() - 1) {
			Node temp = l.get(l.size() - 1);
			l.set(l.size() - 1, end);
			l.set(fim, temp);
		}
	}

	@SuppressWarnings(RAWTYPES)
	/**
	 * Transforma o tipo do XPDL para o tipo do sistema
	 * @param ele
	 * @param type
	 * @return
	 */
	public String verificarTypenode(Element ele) {
		String tipo = null;
		List event = ele.getChildren("Event", ele.getNamespace());
		List impl = ele.getChildren("Implementation", ele.getNamespace());
		List route = ele.getChildren("Route", ele.getNamespace());
		if (event != null && !event.isEmpty()) {
			tipo = createStartEndMailNode(event);
		}
		else if (impl != null && !impl.isEmpty()) {
			tipo = createTaskProcessState(impl);
		}
		else if (route != null && !route.isEmpty()) {
			tipo = createParallelDecisionNode(route);
		}
		if ("ProcessState".equals(tipo)) {
			return null;
		}
		return tipo;
	}

	@SuppressWarnings(RAWTYPES)
	private String createParallelDecisionNode(List route) {
		String tipo;
		Element temp = (Element) route.get(0);
		String gateway = temp.getAttributeValue("GatewayType");
		if (gateway != null && !gateway.isEmpty() && PARALLEL.equalsIgnoreCase(gateway)) {
			tipo = PARALLEL;
		}
		else {
			tipo = "Decision";
		}
		return tipo;
	}

	@SuppressWarnings(RAWTYPES)
	private String createTaskProcessState(List impl) {
		Element temp = (Element) impl.get(0);
		List task = temp.getChildren("Task", temp.getNamespace());
		List sub = temp.getChildren("SubFlow", temp.getNamespace());
		if (task != null && !task.isEmpty()) {
			return "Task";
		}
		else if (sub != null && !sub.isEmpty()) {
			return "ProcessState"; // SUB-PROCESSO
		}
		return null;
	}

	@SuppressWarnings(RAWTYPES)
	private String createStartEndMailNode(List event) {
		Element temp = (Element) event.get(0);
		List start = temp.getChildren("StartEvent", temp.getNamespace());
		List end = temp.getChildren("EndEvent", temp.getNamespace());
		List inter = temp.getChildren("IntermediateEvent", temp.getNamespace());
		if (start != null && !start.isEmpty()) {
			return "StartState";
		}
		else if (end != null && !end.isEmpty()) {
			return "EndState";
		}
		else if (inter != null && !inter.isEmpty()) {
			Element temp2 = (Element) event.get(0);
			String value = temp2.getAttributeValue("Trigger", temp2.getNamespace());
			if (value != null && "Message".equalsIgnoreCase(value)) {
				return "MailNode";
			}
			return "Node";
		}
		return null;
	}

	private Node gereNode(String type, String nome, ProcessDefinition definition) {
		Node node = null;
		Class<?> nodeType = retrieveNodeType(type);
		try {
			node = createNodeFromNodetype(type, nome, definition, nodeType);
		}
		catch (InstantiationException e) {
			LOG.error("ImportarXPDL: " + e.getMessage(), e);
		}
		catch (IllegalAccessException e) {
			LOG.error("ImportarXPDL: " + e.getMessage(), e);
		}
		return node;
	}

	private Node createNodeFromNodetype(String type, String nome, ProcessDefinition definition,
			Class<?> nodeType) throws InstantiationException, IllegalAccessException {
		if (nodeType == null) {
			return null;
		}
		Node node = createNodefromType(type, nodeType);
		assignNameToNode(nome, node);
		assignTaksToNode(definition, node);
		return node;
	}

	private void assignTaksToNode(ProcessDefinition definition, Node node) {
		if (node instanceof TaskNode || node instanceof StartState || node instanceof ProcessState) {
			addTask(node, definition);
		}
	}

	private void assignNameToNode(String nome, Node node) {
		if (nome != null && !nome.isEmpty()) {
			node.setName(nome);
		}
		else {
			if (node instanceof StartState || node instanceof EndState) {
				node.setName((node instanceof StartState ? "Início" : "Fim"));
			}
			else {
				node.setName(NO_NAME + index++);
			}
		}
	}

	private Node createNodefromType(String type, Class<?> nodeType) throws InstantiationException,
			IllegalAccessException {
		Node node;
		if (PARALLEL.equalsIgnoreCase(type)) {
			node = new ParallelNode();
		}
		else {
			node = (Node) nodeType.newInstance();
		}
		return node;
	}

	private Class<?> retrieveNodeType(String type) {
		Class<?> nodeType = null;
		if (PARALLEL.equalsIgnoreCase(type)) {
			nodeType = ParallelNode.class;
		}
		else {
			nodeType = NodeTypes.getNodeType(getNodeType(type));
		}
		return nodeType;
	}

	public void addTask(Node node, ProcessDefinition definition) {
		if (node instanceof TaskNode) {
			TaskNode tn = (TaskNode) node;
			Task t = new Task();
			t.setProcessDefinition(definition);
			t.setTaskMgmtDefinition(definition.getTaskMgmtDefinition());
			t.setName(node.getName());
			t.setSwimlane(definition.getTaskMgmtDefinition().getSwimlanes().values().iterator()
					.next());
			tn.setEndTasks(true);
			tn.addTask(t);
		}
		else if (node instanceof StartState) {
			Task startTask = new Task("Tarefa inicial");
			startTask.setSwimlane(definition.getTaskMgmtDefinition().getSwimlanes().values()
					.iterator().next());
			TaskHandler startTaskHandler = new TaskHandler(startTask);
			definition.getTaskMgmtDefinition().setStartTask(startTaskHandler.getTask());
		}
		else if (node instanceof ProcessState) {
			ProcessState sub = (ProcessState) node;
			ProcessDefinition subProc = ProcessDefinition.createNewProcessDefinition();
			subProc.setDescription(NO_NAME + index++);
			subProc.setName(NO_NAME + index++);
			sub.setSubProcessDefinition(subProc);
		}
	}

	private void setTransiction(Node atual, Node next, String nome, ProcessDefinition definition) {
		Transition t = new Transition();
		t.setProcessDefinition(definition);
		next.addArrivingTransition(t);
		atual.addLeavingTransition(t);
		if (nome != null && !nome.isEmpty()) {
			t.setName(nome);
		}
		else {
			t.setName(next.getName());
		}
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
		}
		else {
			action.setActionExpression(expression);
		}
		event.addAction(action);
	}

	private void mensagemErro(String msg) {
		FacesMessages.instance().add(Severity.INFO, msg);
	}
}
